package com.sjgilbert.unanimus;

import android.os.Bundle;

/**
 * 9/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
interface IContainer {
    Bundle getAsBundle() throws NotSetException;

    void commit() throws NotSetException;

    void setDefault() throws NotSetException;

    void setFromBundle(Bundle bundle) throws NotSetException;

    boolean isSet();

    class NotSetException extends IllegalStateException {
        NotSetException() {
            super();
        }

        protected NotSetException(String message) {
            super(message);
        }

        protected NotSetException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

