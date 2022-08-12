/*
 * Copyright 2014 bitcoinj project
 * Copyright 2019 Tim Strasser
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

package org.bitcoinj.core;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.bitcoinj.base.Base58;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

import static org.bitcoinj.base.utils.ByteUtils.HEX;
import static org.junit.Assert.assertEquals;

public class EncodedPrivateKeyTest {
    private static class EncodedPrivateKeyToTest extends EncodedPrivateKey {
        public EncodedPrivateKeyToTest(NetworkParameters params, byte[] bytes) {
            super(params, bytes);
        }

        @Override
        public String toString() {
            return Base58.encodeChecked(params.getAddressHeader(), bytes);
        }
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(EncodedPrivateKey.class)
                .withPrefabValues(NetworkParameters.class, MainNetParams.get(), TestNet3Params.get())
                .suppress(Warning.NULL_FIELDS)
                .suppress(Warning.TRANSIENT_FIELDS)
                .usingGetClass()
                .verify();
    }

    @Test
    public void stringification() {
        EncodedPrivateKey a = new EncodedPrivateKeyToTest(TestNet3Params.get(), HEX.decode("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc"));
        assertEquals("n4eA2nbYqErp7H6jebchxAN59DmNpksexv", a.toString());
    }
}
