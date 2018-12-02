package com.devsmart.flintbuild;

import groovy.lang.Closure;
import org.ajoberstar.gradle.git.tasks.GitClone;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FlintExtention {

    private Project mProject;
    protected File rootDir;
    private List<Library> mLibraries = new LinkedList<>();
    private List<Target> mTargets = new LinkedList<>();

    public FlintExtention(Project project) {
        mProject = project;
    }

    public void rootDir(File path) {
        rootDir = path;
    }

    public File getRootDir() {
        if(rootDir == null) {
            String homePath = System.getenv("FLINT_HOME");
            if(homePath == null) {
                homePath = System.getProperty("user.home") + File.separator + ".flint";
            }
            rootDir = new File(homePath);
        }
        return rootDir;
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

    public List<Library> getLibraries() {
        return mLibraries;
    }

    public List<Target> getTargets() {
        return mTargets;
    }


}
