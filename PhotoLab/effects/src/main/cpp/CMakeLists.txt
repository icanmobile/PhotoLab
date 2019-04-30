##
# Created by JONG HO BAEK on 20,February,2019
# email: icanmobile@gmail.com
##

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Werror -Wno-unused-function")

add_library(ndk_effects SHARED
        ${CMAKE_CURRENT_SOURCE_DIR}/NativePhotoEffect.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/ldr/LDR.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/ldr/ToneMappingParameters.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/ldr/ToneMappingBase.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/ldr/ToneMappingFloat.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/ldr/ToneMappingInt.cpp)

# Include libraries needed for plasma lib
target_link_libraries(ndk_effects
                      android
                      jnigraphics
                      log
                      m)
