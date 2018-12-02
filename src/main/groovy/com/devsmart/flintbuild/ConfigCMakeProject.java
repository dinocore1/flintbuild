package com.devsmart.flintbuild;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.process.ExecSpec;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

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

    @Input
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

        Project p = getProject();
        p.exec(new Action<ExecSpec>() {
            @Override
            public void execute(ExecSpec execSpec) {

                List<String> commandLine = new LinkedList<>();
                commandLine.add("cmake");
                commandLine.add(srcDir.toString());
                commandLine.add("-DCMAKE_INSTALL_PREFIX=" + installDir.toString());
                for(Object s : variables) {
                    commandLine.add(String.format("-D%s", s));
                }

                execSpec.commandLine(commandLine);
                execSpec.workingDir(buildDir);

            }
        }).assertNormalExitValue();

    }
}
