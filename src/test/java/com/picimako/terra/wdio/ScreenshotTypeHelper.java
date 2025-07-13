//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
