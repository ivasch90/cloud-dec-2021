package com.geekbrains.netty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {
    public static void main(String[] args) {
        User user = new User();
        user.setAge(15);
        user.setName("Ivan");
        log.debug("{}", user);
    }
}
