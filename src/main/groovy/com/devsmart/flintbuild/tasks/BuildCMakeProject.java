package com.devsmart.flintbuild.tasks;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.process.ExecSpec;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class BuildCMakeProject extends DefaultTask {

    private final DirectoryProperty buildDir;
    private String target;

    public BuildCMakeProject() {
        Project project = getProject();
        buildDir = project.getObjects().directoryProperty();
    }

    @Internal
    public DirectoryProperty getBuildDir() {
        return buildDir;
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

        final File theBuildDir = buildDir.getAsFile().get();

        Project p = getProject();
        p.exec(new Action<ExecSpec>() {
            @Override
            public void execute(ExecSpec execSpec) {

                List<String> commandLine = new LinkedList<>();
                commandLine.add("cmake");
                commandLine.add("--build");
                commandLine.add(theBuildDir.toString());

                if(target != null) {
                    commandLine.add("--target");
                    commandLine.add(target);
                }

                execSpec.commandLine(commandLine);

            }
        }).assertNormalExitValue();

    }
}
