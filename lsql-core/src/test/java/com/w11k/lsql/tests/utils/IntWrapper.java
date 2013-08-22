package com.w11k.lsql.tests.utils;

public class IntWrapper {

    private final int i;

    public IntWrapper(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntWrapper that = (IntWrapper) o;
        return i == that.i;
    }

    @Override
    public int hashCode() {
        return i;
    }

    @Override public String toString() {
        return "IntWrapper{" +
                "i=" + i +
                '}';
    }
}
