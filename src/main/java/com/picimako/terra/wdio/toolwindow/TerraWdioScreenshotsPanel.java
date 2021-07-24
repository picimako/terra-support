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

package com.picimako.terra.wdio.toolwindow;

import java.awt.*;
import java.util.Map;
import javax.swing.*;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;

import com.picimako.terra.wdio.toolwindow.action.TerraWdioScreenshotActionsGroup;
import com.picimako.terra.wdio.toolwindow.event.KeyboardListeningPopupMenuInvoker;
import com.picimako.terra.wdio.toolwindow.event.MouseListeningPopupMenuInvoker;
import com.picimako.terra.wdio.toolwindow.event.MouseListeningScreenshotNodeActionInvoker;
import com.picimako.terra.wdio.toolwindow.event.ShortcutKeyListeningScreenshotNodeActionInvoker;
import com.picimako.terra.wdio.toolwindow.event.ToolWindowPopupMenuInvoker;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;

/**
 * A custom tool window panel to contain and display the Terra wdio spec files and screenshots in a custom view.
 * <p>
 * Context menu (mouse right-click) actions, and shortcut keys are assigned to the spec and screenshot nodes, providing various options to
 * manipulate those files and folders, and display them in custom views.
 * <p>
 * For how the structure displayed in the tool window corresponds to the original test folder structure,
 * head over to the class-level javadoc of {@link TerraWdioTreeModel}.
 * <p>
 * This panel, based on a particular project's folder structure, can collect and display specs and screenshot from various
 * pre-defined wdio test root folders. For the pre-defined set of roots see {@link com.picimako.terra.wdio.TerraWdioFolders}.
 */
public class TerraWdioScreenshotsPanel extends JPanel {

    private static final String SCREENSHOT_ACTIONS_GROUP = "terra.wdio.toolwindow.ScreenshotActionsGroup";
    private final Project project;
    private TerraWdioTree tree;

    public TerraWdioScreenshotsPanel(Project project) {
        this.project = project;
        buildGUI();
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new TerraWdioExternalFileAndFolderChangeListener(tree, project));
    }

    /**
     * Builds the tree model for the tool window, registers actions and listeners, and assembles the layout of the tool window.
     */
    private void buildGUI() {
        setLayout(new BorderLayout());
        tree = new TerraWdioTree(new TerraWdioTreeModel(project));
        registerActionsAndListenersForTree();
        add(new JBScrollPane(tree));
        new TreeSpeedSearch(tree);
    }

    public TerraWdioTree getTree() {
        return tree;
    }

    /**
     * Registers actions and listeners to the tool window tree.
     * <p>
     * Registering menu actions happens from code instead of the plugin.xml, because this tool window may not be used,
     * so that when the it is not opened, actions are not registered unnecessarily.
     * <p>
     * https://jetbrains.org/intellij/sdk/docs/tutorials/action_system/grouping_action.html#implementing-custom-action-group-classes
     */
    private void registerActionsAndListenersForTree() {
        ActionManager actionManager = ActionManager.getInstance();

        //List of action groups to register
        //Since ActionManager seems to be operating on application level, and not project level, this check makes sure
        //that in case multiple projects are open, this tool window doesn't try to register the same action group twice,
        //which would lead to exception thrown, and to a crashing tool window.
        if (actionManager.getAction(SCREENSHOT_ACTIONS_GROUP) == null) {
            actionManager.registerAction(SCREENSHOT_ACTIONS_GROUP, new TerraWdioScreenshotActionsGroup());
        }

        //Add action popup menu to the tree component
        ActionPopupMenu actionPopupMenu = actionManager
            .createActionPopupMenu("TerraWdioToolWindow", (DefaultActionGroup) actionManager.getAction(SCREENSHOT_ACTIONS_GROUP));
        actionPopupMenu.setTargetComponent(tree);
        final Map<String, ActionPopupMenu> actionPopupMenus = Map.of("Screenshot", actionPopupMenu);
        ToolWindowPopupMenuInvoker menuInvoker = new ToolWindowPopupMenuInvoker(tree, actionPopupMenus);

        tree.addMouseListener(new MouseListeningPopupMenuInvoker(menuInvoker));
        tree.addKeyListener(new KeyboardListeningPopupMenuInvoker(menuInvoker));
        tree.addKeyListener(new ShortcutKeyListeningScreenshotNodeActionInvoker(project, tree));
        tree.addMouseListener(new MouseListeningScreenshotNodeActionInvoker(project, tree));
    }
}
