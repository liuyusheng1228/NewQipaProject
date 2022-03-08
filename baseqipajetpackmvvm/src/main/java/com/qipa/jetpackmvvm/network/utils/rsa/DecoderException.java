package com.qipa.jetpackmvvm.network.utils.rsa;

public class DecoderException extends IllegalStateException {
    private Throwable cause;

    public DecoderException(String var1, Throwable var2) {
        super(var1);
        this.cause = var2;
    }


    public Throwable getCause() {
        return this.cause;
    }
}
