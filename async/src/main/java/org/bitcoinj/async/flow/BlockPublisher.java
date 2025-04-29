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

import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

/**
 * A {@link Flow.Publisher} that streams blocks. {@code T} is typically either some form of serialized block
 * data (e.g. {@link ByteBuffer}) or a deserialized {@link Block}.
 */
public interface BlockPublisher<T> extends Flow.Publisher<T> {}
