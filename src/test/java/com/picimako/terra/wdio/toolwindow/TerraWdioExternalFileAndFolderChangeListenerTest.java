//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.picimako.terra.wdio.TerraWdioFolders;
import com.picimako.terra.wdio.toolwindow.action.DeleteScreenshotsAction;
import com.picimako.terra.wdio.toolwindow.action.RenameScreenshotsAction;
import com.picimako.terra.wdio.toolwindow.action.ReplaceReferenceWithLatestAction;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;

/**
 * Unit test for {@link TerraWdioExternalFileAndFolderChangeListener}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TerraWdioExternalFileAndFolderChangeListenerTest {

    private TerraWdioExternalFileAndFolderChangeListener listener;
    @Mock
    private TerraWdioTree tree;
    @Mock
    private Project project;
    @Mock
    private TerraWdioTreeModel model;

    @Before
    public void setup() {
        when(tree.getModel()).thenReturn(model);
        listener = new TerraWdioExternalFileAndFolderChangeListener(tree, project);
    }

    @Test
    public void shouldNotUpdateTreeForContentChangeEvent() {
        List<? extends VFileEvent> events = List.of(mock(VFileContentChangeEvent.class));

        listener.after(events);

        verify(model, never()).buildTree();
        verify(tree, never()).updateUI();
    }

    @Test
    public void shouldNotUpdateTreeForDeleteScreenshotsAction() {
        shouldNotUpdateTreeForAction(DeleteScreenshotsAction.class);
    }

    @Test
    public void shouldNotUpdateTreeForRenameScreenshotsAction() {
        shouldNotUpdateTreeForAction(RenameScreenshotsAction.class);
    }

    @Test
    public void shouldNotUpdateTreeForReplaceReferenceWithLatestAction() {
        shouldNotUpdateTreeForAction(ReplaceReferenceWithLatestAction.class);
    }

    @Test
    public void shouldNotUpdateTreeForEventNotWithinWdioTestFiles() {
        VFileDeleteEvent event = mock(VFileDeleteEvent.class);
        VirtualFile file = mock(VirtualFile.class);
        when(event.getRequestor()).thenReturn(null);
        when(event.getFile()).thenReturn(file);
        List<? extends VFileEvent> events = List.of(event);

        try (var util = Mockito.mockStatic(TerraWdioFolders.class)) {
            util.when(() -> TerraWdioFolders.isInWdioFiles(file, project)).thenReturn(false);
            listener.after(events);

            verify(model, never()).buildTree();
            verify(tree, never()).updateUI();
        }
    }

    @Test
    public void shouldUpdateTree() {
        VFileDeleteEvent event = mock(VFileDeleteEvent.class);
        VirtualFile file = mock(VirtualFile.class);
        when(event.getRequestor()).thenReturn(null);
        when(event.getFile()).thenReturn(file);
        List<? extends VFileEvent> events = List.of(event);

        try (var util = Mockito.mockStatic(TerraWdioFolders.class)) {
            util.when(() -> TerraWdioFolders.isInWdioFiles(file, project)).thenReturn(true);
            listener.after(events);

            verify(model, times(1)).buildTree();
            verify(tree, times(1)).updateUI();
        }
    }

    private void shouldNotUpdateTreeForAction(Class<? extends AnAction> action) {
        VFileDeleteEvent event = mock(VFileDeleteEvent.class);
        when(event.getRequestor()).thenReturn(mock(action));
        List<? extends VFileEvent> events = List.of(event);

        listener.after(events);

        verify(model, never()).buildTree();
        verify(tree, never()).updateUI();
    }
}
