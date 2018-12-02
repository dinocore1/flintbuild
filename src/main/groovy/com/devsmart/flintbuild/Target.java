package com.devsmart.flintbuild;

import java.util.Collection;
import java.util.LinkedHashSet;

public class Target {

    private String mName;
    private LinkedHashSet<String> mCMakeArgs = new LinkedHashSet<>();

    public void name(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void cmakeArgs(Collection<String> args) {
        mCMakeArgs.addAll(args);
    }

    public LinkedHashSet<String> getCmakeArgs() {
        return mCMakeArgs;
    }

    @Override
    public String toString() {
        return mName;
    }
}
