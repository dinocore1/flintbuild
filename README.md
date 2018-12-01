### Flint Build ###

Flint is a plugin for Gradle that makes building cross-platform C/C++ projects with
lots of library dependencies much easier. With Flint, you define your project's
dependencies and the architecture/os that you are targeting.


```

apply plugin: Flint

flint {

  library {
    name 'foo'
    gitRepo 'https://github.com/dinocore1/foo.git'
    gitTag 'master'
    
  }
  
  target {
    name 'linux'
  }
  
  target {
    name 'android'
    cmakeArgs = [
                "CMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake",
                'ANDROID_ABI=arm64-v8a',
                'ANDROID_NATIVE_API_LEVEL=23'
                ]
  }

}


```