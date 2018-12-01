package com.devsmart.flintbuild;

import groovy.lang.Closure;
import org.gradle.api.GradleException;
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
        c.rehydrate(lib, mProject, lib).run();
        mLibraries.add(lib);
    }

    public void target(Closure c) {
        Target target = new Target();
        c.rehydrate(target, mProject, target).run();
        mTargets.add(target);
    }

    public void createTasks() {
        if(mTargets.isEmpty()) {
            throw new GradleException("must have at least one target");
        }
    }
}
