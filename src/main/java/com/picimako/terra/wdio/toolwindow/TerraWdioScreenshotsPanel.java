//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import java.awt.*;
import java.util.Map;
import javax.swing.*;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.TreeUIHelper;
import com.intellij.ui.components.JBScrollPane;

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
        TreeUIHelper.getInstance().installTreeSpeedSearch(tree);
    }

    public TerraWdioTree getTree() {
        return tree;
    }

    /**
     * Registers actions and listeners to the tool window tree.
     * <p>
     * Registering menu actions happens from code instead of the plugin.xml, because this tool window may not be used,
     * so that when it is not opened, actions are not registered unnecessarily.
     * <p>
     * @see <a href="https://jetbrains.org/intellij/sdk/docs/tutorials/action_system/grouping_action.html#implementing-custom-action-group-classes">Grouping actions</a>
     */
    private void registerActionsAndListenersForTree() {
        var actionManager = ActionManager.getInstance();

        //Add action popup menu to the tree component
        var actionPopupMenu = actionManager
            .createActionPopupMenu("TerraWdioToolWindow", (DefaultActionGroup) actionManager.getAction(SCREENSHOT_ACTIONS_GROUP));
        actionPopupMenu.setTargetComponent(tree);
        final var actionPopupMenus = Map.of("Screenshot", actionPopupMenu);
        ToolWindowPopupMenuInvoker menuInvoker = new ToolWindowPopupMenuInvoker(tree, actionPopupMenus);

        tree.addMouseListener(new MouseListeningPopupMenuInvoker(menuInvoker));
        tree.addKeyListener(new KeyboardListeningPopupMenuInvoker(menuInvoker));
        tree.addKeyListener(new ShortcutKeyListeningScreenshotNodeActionInvoker(project, tree));
        tree.addMouseListener(new MouseListeningScreenshotNodeActionInvoker(project, tree));
    }
}
