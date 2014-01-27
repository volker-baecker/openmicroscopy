/*
 * Copyright (C) 2014 University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package ome.formats.importer.transfers;

import java.io.FileInputStream;
import java.io.IOException;

import omero.ServerError;
import omero.api.RawFileStorePrx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base {@link FileTransfer} implementation primarily providing the
 * {@link #start(TransferState)} and {@link #finish(TransferState, long)}
 * methods. Also used as the factory for {@link FileTransfer} implementations
 * via {@link #createTransfer(String)}.
 *
 * @since 5.0
 */
public abstract class AbstractFileTransfer implements FileTransfer {

    /**
     * Enum of well-known {@link FileTransfer} names.
     */
    public enum Transfers {
        ln(HardlinkFileTransfer.class),
        ln_rm(MoveFileTransfer.class),
        ln_s(SymlinkFileTransfer.class),
        upload(UploadFileTransfer.class);
        Class<?> kls;
        Transfers(Class<?> kls) {
            this.kls = kls;
        }
    }

    /**
     * Factory method for instantiating {@link FileTransfer} objects from
     * a string. Supported values can be found in the {@link Transfers} enum.
     * Otherwise, a FQN for a class on the classpath should be passed in.
     * @param arg non-null
     */
    public static FileTransfer createTransfer(String arg) {
        Logger tmp = LoggerFactory.getLogger(AbstractFileTransfer.class);
        tmp.debug("Loading file transfer class {}", arg);
        try {
            try {
                return (FileTransfer) Transfers.valueOf(arg).kls.newInstance();
            } catch (Exception e) {
                // Assume not in the enum
            }
            Class<?> c = Class.forName(arg);
            return (FileTransfer) c.newInstance();
        } catch (Exception e) {
            tmp.error("Failed to load file transfer class " + arg);
            throw new RuntimeException(e);
        }
    }

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Minimal start method which logs the file, calls
     * {@link TransferState#start()}, and loads the {@link RawFileStorePrx}
     * which any implementation will need.
     *
     * @param state non-null.
     * @return
     * @throws ServerError
     */
    protected RawFileStorePrx start(TransferState state) throws ServerError {
        log.info("Transferring {}...", state.getFile());
        state.start();
        return state.getUploader();
    }

    /**
     * Save the current state to disk and finish all timing and logging.
     *
     * @param state non-null
     * @param offset total length transferred.
     * @return client-side digest string.
     * @throws ServerError
     */
    protected String finish(TransferState state, long offset) throws ServerError {
        state.start();
        state.save();
        state.stop();
        state.uploadComplete(offset);
        return state.getChecksum();
    }

    /**
     * Utility method for closing resources.
     *
     * @param rawFileStore possibly null
     * @param stream possibly null
     * @throws ServerError
     */
    protected void cleanupUpload(RawFileStorePrx rawFileStore,
            FileInputStream stream) throws ServerError {
        try {
            if (rawFileStore != null) {
                try {
                    rawFileStore.close();
                } catch (Exception e) {
                    log.error("error in closing raw file store", e);
                }
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error("I/O in error closing stream", e);
                }
            }
        }

    }
}
