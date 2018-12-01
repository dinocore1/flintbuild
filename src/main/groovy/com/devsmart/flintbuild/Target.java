package com.devsmart.flintbuild;

import java.util.Collection;
import java.util.LinkedHashSet;

public class Target {

    private String mName;
    private LinkedHashSet<CharSequence> mCMakeArgs = new LinkedHashSet<>();

    public void name(String name) {
        mName = name;
    }

    public void cmakeArgs(Collection<CharSequence> args) {
        mCMakeArgs.addAll(args);
    }

    @Override
    public String toString() {
        return mName;
    }
}
