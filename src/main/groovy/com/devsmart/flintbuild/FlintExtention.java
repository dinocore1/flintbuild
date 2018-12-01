package com.devsmart.flintbuild;

import groovy.lang.Closure;
import org.gradle.api.Project;

import java.util.LinkedList;
import java.util.List;

public class FlintExtention {

    private Project mProject;
    private List<Library> mLibraries = new LinkedList<>();
    private List<Target> mTargets = new LinkedList<>();

    public FlintExtention(Project project) {
        mProject = project;
    }

    public void library(Closure c) {
        System.out.println("new lib");
        Library lib = new Library();
        Closure code = c.rehydrate(lib, mProject, lib);
        code.call();
        mLibraries.add(lib);
    }

    public void target(Closure c) {

    }

    public void setProject(Project project) {
        mProject = project;
    }
}
