/*
 * Copyright 2021 Tamás Balog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.picimako.terra.wdio;

/**
 * Utility class for building screenshot paths based on screenshot types.
 */
public final class ScreenshotTypeHelper {

    public static String reference(String path) {
        return "tests/wdio/__snapshots__/reference" + path;
    }

    public static String diff(String path) {
        return "tests/wdio/__snapshots__/diff" + path;
    }

    public static String latest(String path) {
        return "tests/wdio/__snapshots__/latest" + path;
    }

    private ScreenshotTypeHelper() {
    }
}
