cmake_minimum_required(VERSION 3.18.1)

project("myopenGLjni")

file(GLOB allCpp
        ${CMAKE_SOURCE_DIR}/src/main/cpp/native-gl.cpp)

add_library( # Sets the name of the library.
        myopenGLjni

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${allCpp})

include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/include)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries(
        myopenGLjni

        ${log-lib})