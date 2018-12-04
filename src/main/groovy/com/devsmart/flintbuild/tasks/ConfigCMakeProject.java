package com.devsmart.flintbuild.tasks;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.process.ExecSpec;

import java.io.File;
import java.util.*;

public class ConfigCMakeProject extends DefaultTask {


    private File srcDir;
    private DirectoryProperty buildDir;
    private DirectoryProperty installDir;
    private Property<LinkedHashSet> variables;

    public ConfigCMakeProject() {
        Project project = getProject();
        buildDir = project.getObjects().directoryProperty();
        installDir = project.getObjects().directoryProperty();
        variables = project.getObjects().property(LinkedHashSet.class);
    }

    @Internal
    public File getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    @InputFiles
    public FileTree getCMakeFiles() {
        HashMap config = new HashMap<String, Object>();
        config.put("dir", srcDir);
        config.put("include", "**/CMakeLists.txt");
        ConfigurableFileTree cmakeFiles = getProject().fileTree(config);
        return cmakeFiles;
    }

    @OutputDirectory
    public DirectoryProperty getBuildDir() {
        return buildDir;
    }

    @Internal
    public DirectoryProperty getInstallDir() {
        return installDir;
    }

    @Optional
    @Input
    public Property<LinkedHashSet> getVariables() {
        return variables;
    }

    @TaskAction
    public void execute(IncrementalTaskInputs inputs) {
        Project p = getProject();

        final LinkedHashSet theCmakeVarables = variables.get();
        final File theInstallDir = installDir.getAsFile().get();
        final File theBuildDir = buildDir.getAsFile().get();

        p.exec(new Action<ExecSpec>() {
            @Override
            public void execute(ExecSpec execSpec) {

                List<String> commandLine = new LinkedList<>();
                commandLine.add("cmake");
                commandLine.add(srcDir.toString());
                commandLine.add("-DCMAKE_INSTALL_PREFIX=" + theInstallDir.toString());
                for(Object s : theCmakeVarables) {
                    commandLine.add(String.format("-D%s", s));
                }

                execSpec.commandLine(commandLine);
                execSpec.workingDir(theBuildDir);

            }
        }).assertNormalExitValue();

    }
}
