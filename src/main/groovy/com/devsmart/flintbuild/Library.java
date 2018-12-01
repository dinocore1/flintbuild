package com.devsmart.flintbuild;

public class Library {

    private String mName;

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void name(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }
}
