package com.devsmart.flintbuild.tasks;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.process.ExecSpec;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class BuildCMakeProject extends DefaultTask {

    private final DirectoryProperty buildDir;
    private final Property<String> target;

    public BuildCMakeProject() {
        Project project = getProject();
        buildDir = project.getObjects().directoryProperty();
        target = project.getObjects().property(String.class);
    }

    @Internal
    public DirectoryProperty getBuildDir() {
        return buildDir;
    }

    @Optional
    @Input
    public Property<String> getTarget() {
        return target;
    }

    @TaskAction
    public void execute(IncrementalTaskInputs inputs) {
        final String theTarget = target.get();

        Project p = getProject();
        p.exec(new Action<ExecSpec>() {
            @Override
            public void execute(ExecSpec execSpec) {

                List<String> commandLine = new LinkedList<>();
                commandLine.add("cmake");
                commandLine.add("--build");
                commandLine.add(buildDir.toString());

                if(theTarget != null) {
                    commandLine.add("--target");
                    commandLine.add(theTarget);
                }

                execSpec.commandLine(commandLine);

            }
        }).assertNormalExitValue();

    }
}
