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
