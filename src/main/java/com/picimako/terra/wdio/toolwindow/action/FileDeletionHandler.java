//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import java.io.IOException;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Utility class for providing an easier API for deleting files, including file existence check and exception handling,
 * also ensuring that the deletion takes place in a {@link WriteAction}.
 */
public final class FileDeletionHandler {

    /**
     * Checks if the argument file exists, and if it does, deletes the file.
     *
     * @param file          the file to delete
     * @param requestor     the requestor of the file deletion
     * @param afterDeletion the actions to run after the deletion is successful
     * @param onIOException the actions to run when the deletion resulted in an {@link IOException}
     */
    public static void checkExistenceAndHandleDeletion(VirtualFile file, Object requestor, Runnable afterDeletion, Runnable onIOException) {
        if (file.exists()) {
            handleDeletion(file, requestor, afterDeletion, onIOException);
        }
    }

    /**
     * Deletes the argument file, and handles any thrown exceptions.
     *
     * @param file          the file to delete
     * @param requestor     the requestor of the file deletion
     * @param afterDeletion the actions to run after the deletion is successful
     * @param onIOException the actions to run when the deletion resulted in an {@link IOException}
     */
    public static void handleDeletion(VirtualFile file, Object requestor, Runnable afterDeletion, Runnable onIOException) {
        try {
            //Delete the actual screenshot file from the file system
            WriteAction.run(() -> file.delete(requestor));
            afterDeletion.run();
        } catch (IOException ioe) {
            onIOException.run();
        }
    }

    private FileDeletionHandler() {
        //Utility class
    }
}
