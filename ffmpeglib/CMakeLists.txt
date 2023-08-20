cmake_minimum_required(VERSION 3.18.1)

project("ffmpegjni")

file(GLOB allCpp
        ${CMAKE_SOURCE_DIR}/src/main/cpp/include/*.cpp
        ${CMAKE_SOURCE_DIR}/src/main/cpp/include/*.c
        ${CMAKE_SOURCE_DIR}/src/main/cpp/fftools/*.c
        ${CMAKE_SOURCE_DIR}/src/main/cpp/fftools/*.cpp
        ${CMAKE_SOURCE_DIR}/src/main/cpp/*.cpp)

add_library( # Sets the name of the library.
        ffmpegjni

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${allCpp})
include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/inc)
include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/include)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/src/main/jniLibs/${CMAKE_ANDROID_ARCH_ABI}")

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

set(lib_path ${CMAKE_SOURCE_DIR}/libs)
# 添加三方的so库
#add_library(fmod
#        SHARED
#        IMPORTED)
#
## 指名第三方库的绝对路径
#set_target_properties(fmod
#        PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libfmod.so )

#add_library(fmodL
#        SHARED
#        IMPORTED)
#
#set_target_properties(fmodL
#        PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libfmodL.so )

add_subdirectory(${CMAKE_SOURCE_DIR}/src/main/cpp/librtmp)


target_link_libraries(
        ffmpegjni
        rtmp
#        fmod
#        fmodL
        -Wl,--start-group
        avfilter avformat avcodec avutil postproc swresample swscale #avdevice
        -Wl,--end-group
        z
        android
        ${log-lib})