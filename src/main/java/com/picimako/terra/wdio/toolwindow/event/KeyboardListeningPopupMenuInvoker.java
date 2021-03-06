/*
 * Copyright 2020 Tamás Balog
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

package com.picimako.terra.wdio.toolwindow.event;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Listens for the Microsoft Context menu keyboard button to invoke popup menus.
 */
public final class KeyboardListeningPopupMenuInvoker extends KeyAdapter {
    private final ToolWindowPopupMenuInvoker menuInvoker;

    public KeyboardListeningPopupMenuInvoker(ToolWindowPopupMenuInvoker menuInvoker) {
        this.menuInvoker = menuInvoker;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
            Component component = e.getComponent();
            menuInvoker.invokePopup(component, component.getX(), component.getY());
        }
    }
}
