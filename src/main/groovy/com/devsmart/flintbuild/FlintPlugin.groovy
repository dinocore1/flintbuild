package com.devsmart.flintbuild

import com.devsmart.flintbuild.tasks.BuildCMakeProject
import com.devsmart.flintbuild.tasks.ConfigCMakeProject
import com.devsmart.flintbuild.tasks.GitCloneTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.file.archive.ZipFileTree
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip

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

            File deployDir = new File(project.file('build'), "install")
            deployDir = new File(deployDir, target.name)

            List<Task> installTasks = []

            for(Library lib : config.libraries) {

                String comboName = lib.name.capitalize() + target.name.capitalize()

                File installDir = new File(config.rootDir, "install")
                installDir = new File(installDir, target.name)
                installDir = new File(installDir, "${lib.name}-${lib.version}")

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
                if(lib.generator != null) {
                    configTask.generator = lib.generator
                }

                BuildCMakeProject buildTask = project.tasks.create("build${comboName}", BuildCMakeProject)
                buildTask.dependsOn(configTask)
                buildTask.buildDir = configTask.buildDir
                buildTask.srcDir = configTask.srcDir

                BuildCMakeProject installTask = project.tasks.create("install${comboName}", BuildCMakeProject)
                installTask.dependsOn(buildTask)
                installTask.buildDir = configTask.buildDir
                installTask.target = 'install'



                Zip archiveTask = project.tasks.create([type: Zip, name: "archive${comboName}", dependsOn: installTask], {
                    from installDir
                    baseName = lib.name
                    version = lib.version

                    File archiveDir = new File(config.rootDir, "archives")
                    destinationDir = new File(archiveDir, target.name)
                })

                Copy deployTask = project.tasks.create([type: Copy, name: "deploy${comboName}", dependsOn: archiveTask], {
                    from project.zipTree(archiveTask.archivePath)
                    into deployDir
                })
                installTasks.add(deployTask)

            }

            LinkedHashSet<String> cmakeArgs = new LinkedHashSet<>()
            cmakeArgs.addAll(target.cmakeArgs)

            String comboName = project.name.capitalize() + target.name.capitalize()

            File installDir = new File(config.rootDir, "install")
            installDir = new File(installDir, target.name)

            File buildDir = new File(project.file('build'), target.name)
            buildDir = new File(buildDir, project.name)

            cmakeArgs.add("FLINT_DEPLOYROOT="+deployDir)
            cmakeArgs.add("FLINT_TARGET="+target.name)

            ConfigCMakeProject configTask = project.tasks.create("config${comboName}", ConfigCMakeProject)
            configTask.dependsOn(installTasks)
            configTask.buildDir = buildDir
            configTask.srcDir = project.file('.')
            configTask.installDir = installDir
            configTask.variables = cmakeArgs

            BuildCMakeProject buildTask = project.tasks.create("build${comboName}", BuildCMakeProject)
            buildTask.dependsOn(configTask)
            buildTask.buildDir = configTask.buildDir
            buildTask.srcDir = configTask.srcDir


            project.tasks.create("build${target.name}").dependsOn(buildTask)


        }
    }

    protected File getLibrarySrcDir(Library lib) {
        File srcDir = new File(config.rootDir, "src")
        srcDir = new File(srcDir, "${lib.name}-${lib.version}")
        return srcDir
    }

    protected GitCloneTask createGitCloneTask(Library lib) {
        GitCloneTask cloneTask = project.tasks.create("acquire${lib.name.capitalize()}", GitCloneTask)

        File srcDir = getLibrarySrcDir(lib)
        cloneTask.uri = lib.gitUri
        cloneTask.branchRef = lib.gitTag
        cloneTask.dir = srcDir
        cloneTask.enabled = !srcDir.exists()

        return cloneTask
    }
}
