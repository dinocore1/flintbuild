package com.devsmart.flintbuild;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

import java.io.File;
import java.util.*;

public class ConfigCMakeProject extends DefaultTask {


    private File srcDir;
    private File buildDir;
    private File installDir;
    private LinkedHashSet<Object> variables = new LinkedHashSet<>();


    @InputDirectory
    public File getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    @OutputDirectory
    public File getBuildDir() {
        return buildDir;
    }

    public void setBuildDir(File buildDir) {
        this.buildDir = buildDir;
    }

    public File getInstallDir() {
        return installDir;
    }

    public void setInstallDir(File installDir) {
        this.installDir = installDir;
    }

    @Optional
    @Input
    public LinkedHashSet<Object> getVariables() {
        return variables;
    }

    public void setVariables(LinkedHashSet<Object> variables) {
        this.variables = variables;
    }


    @TaskAction
    public void execute(IncrementalTaskInputs inputs) {


        if(!buildDir.exists()) {
            if(!buildDir.mkdirs()){
                throw new GradleException("cannot create build dir: " + buildDir);
            }
        }

        List<String> commandLine = new LinkedList<>();
        commandLine.add("cmake");
        commandLine.add(srcDir.toString());
        commandLine.add("-DCMAKE_INSTALL_PREFIX=" + installDir.toString());
        for(Object s : variables) {
            commandLine.add(String.format("-D%s", s));
        }

        try {
            System.out.println("Running Process: " + commandLine + " in dir: " + buildDir);
            Process process = new ProcessBuilder()
                    .command(commandLine)
                    .directory(buildDir)
                    .inheritIO()
                    .start();

            int exitCode = process.waitFor();
            if(exitCode != 0) {
                throw new GradleException("cmake exited with code: " + exitCode);
            }

        } catch (Exception e) {
            throw new GradleException("error launching cmake: " + e.getMessage(), e);
        }

    }
}
