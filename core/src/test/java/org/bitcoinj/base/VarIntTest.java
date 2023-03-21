/*
 * Copyright 2011 Google Inc.
 * Copyright 2018 Andreas Schildbach
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

package org.bitcoinj.base;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class VarIntTest {

    @Test
    @Parameters(method = "integerTestVectors")
    public void testIntCreation(int value, int size) {
        VarInt a = new VarInt(value);
        assertEquals(value, a.intValue());
        assertEquals(size, a.getSizeInBytes());
        assertEquals(size, a.getOriginalSizeInBytes());
        assertEquals(size, a.encode().length);
        assertEquals(value, new VarInt(a.encode(), 0).intValue());
    }

    @Test(expected = RuntimeException.class)
    @Parameters(method = "longTestVectors")
    public void testIntGetErr(int value, int size) {
        VarInt a = new VarInt(value);
        a.intValue();
    }

    @Test(expected = RuntimeException.class)
    @Parameters(method = "longTestVectors")
    public void testIntGetErr2(int value, int size) {
        VarInt a = new VarInt(value);
        new VarInt(a.encode(), 0).intValue();
    }

    @Test
    @Parameters(method = "longTestVectors")
    public void testLongCreation(long value, int size) {
        VarInt a = new VarInt(value);
        assertEquals(value, a.longValue());
        assertEquals(size, a.getSizeInBytes());
        assertEquals(size, a.getOriginalSizeInBytes());
        assertEquals(size, a.encode().length);
        assertEquals(value, new VarInt(a.encode(), 0).longValue());
    }

    private Object[] integerTestVectors() {
        return new Object[]{
                new Object[]{ 0, 1},
                new Object[]{ 10, 1},
                new Object[]{ 252, 1},
                new Object[]{ 253, 3},
                new Object[]{ 64000, 3},
                new Object[]{ 0x7FFF, 3},
                new Object[]{ 0x8000, 3},
                new Object[]{ 0x10000, 5},
                new Object[]{ Integer.MIN_VALUE, 9},
                new Object[]{ Integer.MAX_VALUE, 5},
                // -1 shouldn't normally be passed, but at least stay consistent (bug regression test)
                new Object[]{ -1, 9}
        };
    }

    private Object[] longTestVectors() {
        return new Object[]{
                new Object[]{ 0x7FFFL, 3},
                new Object[]{ 0x8000L, 3},
                new Object[]{ 0xFFFFL, 3},
                new Object[]{ 0x10000L, 5},
                new Object[]{ 0xAABBCCDDL, 5},
                new Object[]{ 0xFFFFFFFFL, 5},
                new Object[]{ 0xCAFEBABEDEADBEEFL, 9},
                new Object[]{ Long.MIN_VALUE, 9},
                new Object[]{ Long.MAX_VALUE, 9},
                // -1 shouldn't normally be passed, but at least stay consistent (bug regression test)
                new Object[]{ -1L, 9}
        };
    }
}
