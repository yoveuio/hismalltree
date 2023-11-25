package com.hismalltree.core.exception;

/**
 * @author fangpeiyu.py
 */
public class HismalltreeException extends RuntimeException {

    public HismalltreeException(String message) {
        super(message);
    }

    public HismalltreeException(String message, Throwable cause) {
        super(message, cause);
    }
}
