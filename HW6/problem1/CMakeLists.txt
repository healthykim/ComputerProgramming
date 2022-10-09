cmake_minimum_required(VERSION 3.0.0)
project(problem1 VERSION 0.1.0)

include(CTest)
enable_testing()

add_executable(problem1_main test.cpp ui.cpp ui.h admin_ui.cpp admin_ui.h client_ui.cpp client_ui.h shopping_db.cpp shopping_db.h
        product.cpp product.h user.cpp user.h)
add_executable(problem1_test test.cpp ui.cpp ui.h admin_ui.cpp admin_ui.h client_ui.cpp client_ui.h shopping_db.cpp shopping_db.h
        product.cpp product.h user.cpp user.h)

target_compile_definitions(problem1_main PRIVATE MAIN)
target_compile_definitions(problem1_test PRIVATE TEST)

file(REMOVE_RECURSE ${CMAKE_BINARY_DIR}/test)
file(COPY test DESTINATION .)

set(CPACK_PROJECT_NAME ${PROJECT_NAME})
set(CPACK_PROJECT_VERSION ${PROJECT_VERSION})
set(CMAKE_CXX_FLAGS "-std=c++17")
include(CPack)
