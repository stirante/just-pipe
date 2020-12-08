package com.stirante.justpipe.exception;

import com.stirante.justpipe.function.IORunnable;

import java.io.IOException;

/**
 * Simple class, that wraps an IOException in a RuntimeException.
 */
public class RuntimeIOException extends RuntimeException {

    private final IOException exception;

    public RuntimeIOException(IOException e) {
        super(e);
        exception = e;
    }

    public IOException getException() {
        return exception;
    }

    /**
     * Catches all IOExceptions in runnable and wraps it in RuntimeIOException
     * @param r runnable
     */
    public static void wrap(IORunnable r) {
        try {
            r.run();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

}
