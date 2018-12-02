package com.devsmart.flintbuild;

import org.gradle.util.VersionNumber;

import java.util.Collection;
import java.util.LinkedHashSet;

public class Library {

    private String mName;
    private VersionNumber mVersion = VersionNumber.UNKNOWN;
    private String mGitUri;
    private String mGitTag = "master";
    private LinkedHashSet<String> mCMakeArgs = new LinkedHashSet<>();

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void name(String name) {
        mName = name;
    }

    public void version(String version) {
        mVersion = VersionNumber.parse(version);
    }

    public VersionNumber getVersion() {
        return mVersion;
    }

    public void gitUri(String uri) {
        mGitUri = uri;
    }

    public String getGitUri() {
        return mGitUri;
    }

    public void gitTag(String tag) {
        mGitTag = tag;
    }

    public String getGitTag() {
        return mGitTag;
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
