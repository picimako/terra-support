//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.event;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import lombok.RequiredArgsConstructor;

/**
 * Listens for the Microsoft Context menu keyboard button to invoke popup menus.
 */
@RequiredArgsConstructor
public final class KeyboardListeningPopupMenuInvoker extends KeyAdapter {
    private final ToolWindowPopupMenuInvoker menuInvoker;

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
            Component component = e.getComponent();
            menuInvoker.invokePopup(component, component.getX(), component.getY());
        }
    }
}
