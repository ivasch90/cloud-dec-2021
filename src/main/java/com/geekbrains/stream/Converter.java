package com.geekbrains.stream;

public interface Converter<T, V> {

    V convert(T arg);

}
