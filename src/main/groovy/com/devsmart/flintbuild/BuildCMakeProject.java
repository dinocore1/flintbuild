package com.devsmart.flintbuild;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.process.ExecSpec;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class BuildCMakeProject extends DefaultTask {

    private File buildDir;
    private String target;

    @InputDirectory
    public File getBuildDir() {
        return buildDir;
    }

    public void setBuildDir(File buildDir) {
        this.buildDir = buildDir;
    }

    @Optional
    @Input
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }


    @TaskAction
    public void execute(IncrementalTaskInputs inputs) {


        Project p = getProject();
        p.exec(new Action<ExecSpec>() {
            @Override
            public void execute(ExecSpec execSpec) {

                List<String> commandLine = new LinkedList<>();
                commandLine.add("cmake");
                commandLine.add("--build");
                commandLine.add(buildDir.toString());

                if(target != null) {
                    commandLine.add("--target");
                    commandLine.add(target);
                }

                execSpec.commandLine(commandLine);

            }
        }).assertNormalExitValue();

    }
}
