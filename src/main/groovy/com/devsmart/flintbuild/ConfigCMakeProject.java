package com.devsmart.flintbuild;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConfigCMakeProject extends DefaultTask {


    private File srcDir;
    private File buildDir;
    private File installDir;
    private Map<String, String> variables = new HashMap<>();


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

    @InputDirectory
    public File getInstallDir() {
        return installDir;
    }

    public void setInstallDir(File installDir) {
        this.installDir = installDir;
    }

    @Optional
    @Input
    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
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
        for(Map.Entry<String, String> e : variables.entrySet()) {
            commandLine.add(String.format("-D%s=%s", e.getKey(), e.getValue()));
        }

        try {
            System.out.println("Running Process: " + commandLine);
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
