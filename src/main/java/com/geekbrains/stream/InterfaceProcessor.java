package com.geekbrains.stream;

public class InterfaceProcessor {

    void doActionWith(String arg, Action action) {
        action.doAction(arg);
    }

    String convert(Integer arg, Converter<Integer, String> converter) {
        return converter.convert(arg);
    }

}
