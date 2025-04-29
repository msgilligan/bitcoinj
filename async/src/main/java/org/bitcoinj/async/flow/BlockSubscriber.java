/*
 * Copyright by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.async.flow;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.utils.ContextPropagatingThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  A sample Block subscriber that parses each {@link ByteBuffer} into a {@link Block} on a private
 *  {@link ThreadPoolExecutor} instance before allowing the {@code Block}s to be garbage-collected. Blocks
 *  are counted, with the current count available via {@link #currentCount()} and the final count available via
 *  {@link #totalCount()}
 */
public class BlockSubscriber implements Flow.Subscriber<ByteBuffer> {
    private static final Logger log = LoggerFactory.getLogger(BlockSubscriber.class);
    private final ThreadPoolExecutor deserializationExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(16, new ContextPropagatingThreadFactory("Deserializer"));
    private final int MAX_WORK_QUEUE_SIZE = 1024;
    private Flow.Subscription subscription;


    private final AtomicInteger count = new AtomicInteger(0);
    private final CompletableFuture<Integer> totalCount = new CompletableFuture<>();

    public AtomicInteger currentCount() {
        return count;
    }

    public CompletableFuture<Integer> totalCount() {
        return totalCount;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(MAX_WORK_QUEUE_SIZE);
    }

    @Override
    public void onNext(ByteBuffer item) {
        count.incrementAndGet();
        if (count.get() % 25_000 == 0) {
            log.info("Count: {}", count.get());
        }

        CompletableFuture<Block> fb = process(item, deserializationExecutor);
        fb.whenComplete((b, t) -> {
            if (t != null) {
                log.error("Error parsing", t);
            }
            if (deserializationExecutor.getQueue().size() < MAX_WORK_QUEUE_SIZE) {
                subscription.request(96);
            }
        });
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error", throwable);
        totalCount.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        log.info("Complete (received {} blocks)", count.get());
        totalCount.complete(count.get());
    }

    private CompletableFuture<Block> process(ByteBuffer bb, Executor executor) {
        CompletableFuture<Block> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                Block result = Block.read(bb);
                future.complete(result);
            } catch (ProtocolException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
