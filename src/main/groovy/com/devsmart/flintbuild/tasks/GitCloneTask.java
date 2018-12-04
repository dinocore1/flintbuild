package com.devsmart.flintbuild.tasks;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

public class GitCloneTask extends DefaultTask {

    private final Property<String> uri;
    private final DirectoryProperty dir;
    private final Property<String> branchRef;


    public GitCloneTask() {
        Project project = getProject();
        uri = project.getObjects().property(String.class);
        dir = project.getObjects().directoryProperty();
        branchRef = project.getObjects().property(String.class);
    }

    @Input
    public Property<String> getUri() {
        return uri;
    }

    @Input
    public Property<String> getBranchRef() {
        return branchRef;
    }

    @OutputDirectory
    public DirectoryProperty getDir() {
        return dir;
    }

    @TaskAction
    public void cloneRepo() {
        final String theUri = uri.get();
        final String theBranch = branchRef.get();
        final File theDir = dir.getAsFile().get();

        CloneCommand cmd = Git.cloneRepository()
                .setURI(theUri)
                .setDirectory(theDir)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setBranchesToClone(Arrays.asList(theBranch))
                .setBranch(theBranch)
                .setCloneAllBranches(false);

        try {
            Git git = cmd.call();
            git.close();
        } catch (GitAPIException e) {
            throw new GradleException("error cloning repo.", e);
        }

    }
}
