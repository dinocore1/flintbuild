package com.devsmart.flintbuild

import com.devsmart.flintbuild.tasks.BuildCMakeProject
import com.devsmart.flintbuild.tasks.ConfigCMakeProject
import com.devsmart.flintbuild.tasks.GitCloneTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class FlintPlugin implements Plugin<Project> {

    protected Project project
    private FlintExtention config

    @Override
    void apply(Project project) {
        this.project = project
        config = project.extensions.create("flint", FlintExtention, project)
        project.afterEvaluate{
            createFlintTasks()
        }

    }

    final void createFlintTasks() {

        if(config.targets.isEmpty()) {
            throw new GradleException("must have at least one target")
        }

        for(Library l : config.libraries) {
            if(l.gitUri != null) {
                createGitCloneTask(l)
            }

        }

        for(Target target : config.targets) {

            List<Task> installTasks = []

            for(Library lib : config.libraries) {

                String comboName = lib.name.capitalize() + target.name.capitalize()

                File installDir = new File(config.rootDir, "install")
                installDir = new File(installDir, target.name)

                File buildDir = new File(project.file('build'), target.name)
                buildDir = new File(buildDir, lib.name)

                LinkedHashSet<String> cmakeArgs = new LinkedHashSet<>()
                cmakeArgs.addAll(target.cmakeArgs)
                cmakeArgs.addAll(lib.cmakeArgs)

                ConfigCMakeProject configTask = project.tasks.create("config${comboName}", ConfigCMakeProject)
                configTask.dependsOn("acquire${lib.name.capitalize()}")
                configTask.buildDir = buildDir
                configTask.srcDir = getLibrarySrcDir(lib)
                configTask.installDir = installDir
                configTask.variables = cmakeArgs

                BuildCMakeProject buildTask = project.tasks.create("build${comboName}", BuildCMakeProject)
                buildTask.dependsOn(configTask)
                buildTask.buildDir = configTask.buildDir

                BuildCMakeProject installTask = project.tasks.create("install${comboName}", BuildCMakeProject)
                installTasks.add(installTask)
                installTask.dependsOn(buildTask)
                installTask.buildDir = configTask.buildDir
                installTask.target = 'install'

            }

            LinkedHashSet<String> cmakeArgs = new LinkedHashSet<>()
            cmakeArgs.addAll(target.cmakeArgs)

            String comboName = project.name.capitalize() + target.name.capitalize()

            File installDir = new File(config.rootDir, "install")
            installDir = new File(installDir, target.name)

            File buildDir = new File(project.file('build'), target.name)
            buildDir = new File(buildDir, project.name)

            cmakeArgs.add("FLINT_BUILDROOT="+installDir)

            ConfigCMakeProject configTask = project.tasks.create("config${comboName}", ConfigCMakeProject)
            configTask.dependsOn(installTasks)
            configTask.buildDir = buildDir
            configTask.srcDir = project.file('.')
            configTask.installDir = installDir
            configTask.variables = cmakeArgs

            BuildCMakeProject buildTask = project.tasks.create("build${comboName}", BuildCMakeProject)
            buildTask.dependsOn(configTask)
            buildTask.buildDir = configTask.buildDir


            project.tasks.create("build${target.name}").dependsOn(buildTask)


        }
    }

    protected File getLibrarySrcDir(Library lib) {
        File srcDir = new File(config.rootDir, "src")
        srcDir = new File(srcDir, "${lib.name}-${lib.version}")
        return srcDir
    }

    protected void createGitCloneTask(Library lib) {
        GitCloneTask cloneTask = project.tasks.create("acquire${lib.name.capitalize()}", GitCloneTask)

        File srcDir = getLibrarySrcDir(lib)
        cloneTask.uri = lib.gitUri
        cloneTask.branchRef = lib.gitTag
        cloneTask.dir = srcDir
        cloneTask.enabled = !srcDir.exists()
    }
}
