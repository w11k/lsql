package com.w11k.lsql.relational;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Blob {

    private byte[] data;

    public Blob(byte[] data) {
        this.data = data;
    }

    public Blob(InputStream input) {
        try {
            this.data = ByteStreams.toByteArray(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getData() {
        return data;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(getData());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blob blob = (Blob) o;
        return Arrays.equals(data, blob.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
