package com.devsmart.flintbuild

import org.ajoberstar.gradle.git.tasks.GitClone
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

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

        for(Target t : config.targets) {

            for(Library l : config.libraries) {

                String comboName = l.name.capitalize() + t.name.capitalize()

                File installDir = new File(config.rootDir, "install")
                installDir = new File(installDir, t.name)

                LinkedHashSet<String> cmakeArgs = new LinkedHashSet<>()
                cmakeArgs.addAll(t.cmakeArgs)
                cmakeArgs.addAll(l.cmakeArgs)

                ConfigCMakeProject configTask = project.tasks.create("config${comboName}", ConfigCMakeProject)
                configTask.dependsOn("clone${l.name.capitalize()}")
                configTask.buildDir = new File(project.file('build'), comboName)
                configTask.srcDir = getLibrarySrcDir(l)
                configTask.installDir = installDir
                configTask.variables = cmakeArgs

            }
        }
    }

    protected File getLibrarySrcDir(Library lib) {
        File srcDir = new File(config.rootDir, "src")
        srcDir = new File(srcDir, "${lib.name}-${lib.version}")
        return srcDir
    }

    protected void createGitCloneTask(Library lib) {
        GitClone cloneTask = project.tasks.create("clone${lib.name.capitalize()}", GitClone)

        File srcDir = getLibrarySrcDir(lib)

        cloneTask.uri = lib.gitUri
        cloneTask.branch = lib.gitTag
        cloneTask.destinationPath = srcDir
        cloneTask.cloneAllBranches = false
        cloneTask.enabled = !srcDir.exists()
    }
}
