package com.qipa.jetpackmvvm.network.utils.rsa.commons;

public interface BinaryEncoder extends Encoder {
    byte[] encode(byte[] var1) throws EncoderException;
}

