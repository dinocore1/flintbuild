### Flint Build ###

[![Build Status](https://travis-ci.com/dinocore1/flintbuild.svg?branch=master)](https://travis-ci.com/dinocore1/flintbuild)

Flint is a plugin for Gradle that makes building cross-platform C/C++ projects with
lots of library dependencies much easier. With Flint, you define your project's
dependencies and the architecture/os that you are targeting.


```

buildscript {
  repositories {
      mavenCentral()
  }

  dependencies { 
      classpath 'com.dev-smart:flintbuild:0.0.1'
  }
}

apply plugin: com.devsmart.flintbuild.FlintPlugin

def android_ndk = System.getenv('ANDROID_NDK')

flint {

  library {
    name 'foo'
    gitUri 'https://github.com/dinocore1/foo.git'
    gitTag 'v1.0.2'
    cmakeArgs (['BUILD_TESTS=OFF'])
  }
  
  target {
    name 'linux'
  }
  
  target {
    name 'android'
    cmakeArgs ([
                "CMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake",
                'ANDROID_ABI=arm64-v8a',
                'ANDROID_NATIVE_API_LEVEL=23'
                ])
  }

}

task deleteBuildDir(type: Delete) {
    delete 'build'
}


```

In your local CMakeLists.txt you will want to add:

```

include_directories(${FLINT_BUILDROOT}/include)
link_directories(${FLINT_BUILDROOT}/lib)

```

### Options

Env. var FLINT_HOME -- set the install dir for build. Default is user's home dir.
