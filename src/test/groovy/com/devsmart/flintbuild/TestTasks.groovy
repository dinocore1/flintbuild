package com.devsmart.flintbuild

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

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
                name 'foo'
              }
              
              target {
                name 'linux'
              }
              
              target {
                name 'android'
                cmakeArgs ([
                    "CMAKE_TOOLCHAIN_FILE=${System.env.ANDROID_NDK}/build/cmake/android.toolchain.cmake",
                    'ANDROID_ABI=arm64-v8a'
                    ])
              }
            }
            
            task configMyLib() {
            }
        """
        Project project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .build()

        project.evaluate()

        def p = project.extensions.findByName("FlintPlugin");

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
