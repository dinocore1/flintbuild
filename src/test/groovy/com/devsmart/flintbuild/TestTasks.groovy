package com.devsmart.flintbuild

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

class TestTasks {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()

    private File mBuildGradle

    @Before
    void setup() {
        mBuildGradle = testProjectDir.newFile('build.gradle')
    }

    @Test
    void testExtention() {
        mBuildGradle << """
            apply plugin: com.devsmart.flintbuild.FlintPlugin
            
            flint {
              library {
                name 'baseline'
                gitUri 'https://github.com/dinocore1/Baseline.git'
                gitTag 'master'
              }
              
              target {
                name 'linux'
              }
              
            }
            
            task configMyLib() {
            }
        """

        Project project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .build()

        project.evaluate()

        Task acquireTask = project.tasks['acquireBaseline']
        assertNotNull(acquireTask)

        Task configTask = project.tasks['configBaselineLinux']
        assertNotNull(configTask)
    }


    @Test
    void testConfigCMakeTask() {

        mBuildGradle << """
            task configMyLib(type: ConfigCMakeProject) {
                srcDir = file('example/cmakelib')
                installDir = file('example/build')
                buildDir = file('example/build/cmakelib_build')
            }
        """

        Project project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .build()



        //def t = project.task('configMyLib')




    }
}
