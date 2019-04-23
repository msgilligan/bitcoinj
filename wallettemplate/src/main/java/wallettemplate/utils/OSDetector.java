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

package wallettemplate.utils;

/**
 * Utility class to detect host operating system
 */
public class OSDetector {
    private static final String osName = System.getProperty("os.name").toLowerCase();

    static public boolean isWindows() {
        return osName.contains("windows");
    }

    static public boolean isMac() {
        return osName.startsWith("mac") || osName.startsWith("darwin");
    }

    static public boolean isUnix() {
        return osName.contains("nix") || osName.contains("nux") || osName.contains("sunos");
    }
}
