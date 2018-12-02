package com.devsmart.flintbuild


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

        for(Target t : config.targets) {

            List<Task> installTasks = []

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
            cmakeArgs.addAll(t.cmakeArgs)

            String comboName = project.name.capitalize() + t.name.capitalize()

            File installDir = new File(config.rootDir, "install")
            installDir = new File(installDir, t.name)

            ConfigCMakeProject configTask = project.tasks.create("config${comboName}", ConfigCMakeProject)
            configTask.dependsOn(installTasks)
            configTask.buildDir = new File(project.file('build'), comboName)
            configTask.srcDir = project.file('.')
            configTask.installDir = installDir
            configTask.variables = cmakeArgs

            BuildCMakeProject buildTask = project.tasks.create("build${comboName}", BuildCMakeProject)
            buildTask.dependsOn(configTask)
            buildTask.buildDir = configTask.buildDir


            project.tasks.create("build${t.name}").dependsOn(buildTask)


        }
    }

    protected File getLibrarySrcDir(Library lib) {
        File srcDir = new File(config.rootDir, "src")
        srcDir = new File(srcDir, "${lib.name}-${lib.version}")
        return srcDir
    }

    protected void createGitCloneTask(Library lib) {
        GitCloneTask cloneTask = project.tasks.create("clone${lib.name.capitalize()}", GitCloneTask)

        File srcDir = getLibrarySrcDir(lib)
        cloneTask.uri = lib.gitUri
        cloneTask.branchRef = lib.gitTag
        cloneTask.dir = srcDir
        cloneTask.enabled = !srcDir.exists()
    }
}
