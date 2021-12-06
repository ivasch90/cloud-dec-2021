package com.geekbrains.stream;

@FunctionalInterface
public interface Foo {

    void foo();

    default void bar() {
        foo();
    }

}
