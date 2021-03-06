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

import com.intellij.ui.PopupHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A custom popup handler that handles mouse events on the JTree displayed in the tool window.
 * <p>
 * When the JTree encounters a mouse right-click, it does the following:
 * <ul>
 *     <li>selects the tree node at the click's location if it's a screenshot, so that a visual confirmation is in place that
 *     context menu actions are supposed to happen on that particular node,</li>
 *     <li>displays the context menu (popup menu) aligned to the click's location</li>
 * </ul>
 * <p>
 * <b>Related articles</b>
 * <p>
 * <ul>
 *     <li><a href=https://intellij-support.jetbrains.com/hc/en-us/community/posts/206140779-Context-menu-howto">Context-menu-howto</a></li>
 *     <li><a href=https://stackoverflow.com/questions/27468337/jtree-select-item-on-right-click">JTree select item on right-click</a></li>
 *     <li><a href=https://stackoverflow.com/questions/517704/right-click-context-menu-for-java-jtree">Right-click context menu for java JTree</a></li>
 *     <li><a href=https://stackoverflow.com/questions/38468270/how-to-implement-a-popup-menu-in-swing-that-works-both-under-windows-and-linux">Swing popup menu for Windows and Linux</a></li>
 *     <li><a href=https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html#popup">Oracle popup menu tutorial</a></li>
 * </ul>
 */
public final class MouseListeningPopupMenuInvoker extends PopupHandler {
    private final ToolWindowPopupMenuInvoker menuInvoker;

    public MouseListeningPopupMenuInvoker(@NotNull ToolWindowPopupMenuInvoker menuInvoker) {
        this.menuInvoker = menuInvoker;
    }

    @Override
    public void invokePopup(Component comp, int x, int y) {
        menuInvoker.invokePopup(comp, x, y);
    }
}
