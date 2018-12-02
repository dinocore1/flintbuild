package com.devsmart.flintbuild;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

public class GitCloneTask extends DefaultTask {

    private String uri;
    private File dir;
    private String branchRef;


    @Input
    public String getUri() {
        return uri;
    }

    @Input
    public String getBranchRef() {
        return branchRef;
    }

    @OutputDirectory
    public File getDir() {
        return dir;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setBranchRef(String branchRef) {
        this.branchRef = branchRef;
    }

    @TaskAction
    public void cloneRepo() {
        CloneCommand cmd = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(dir)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setBranchesToClone(Arrays.asList(branchRef))
                .setBranch(branchRef)
                .setCloneAllBranches(false);

        try {
            cmd.call();
        } catch (GitAPIException e) {
            throw new GradleException("error cloning repo.", e);
        }

    }
}
