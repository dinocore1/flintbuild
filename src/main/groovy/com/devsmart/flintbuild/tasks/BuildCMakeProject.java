package com.devsmart.flintbuild.tasks;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.process.ExecSpec;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class BuildCMakeProject extends DefaultTask {

    private final DirectoryProperty buildDir;
    private String target;
    private File srcDir;

    public BuildCMakeProject() {
        Project project = getProject();
        buildDir = project.getObjects().directoryProperty();
    }

    @InputDirectory
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

    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    public Callable<FileTree> getSrcFiles() {
        return new Callable<FileTree>() {
            @Override
            public FileTree call() throws Exception {
                if(srcDir != null) {
                    HashMap<String, Object> config = new HashMap<String, Object>();
                    config.put("dir", srcDir);
                    config.put("include", new String[]{
                            "**/*.cpp", "**/*.c", "**/*.cc", "**/*.h", "**/*.hpp", "**/*.hh"
                    });

                    return getProject().fileTree(config);
                } else {
                    return null;
                }
            }
        };
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
