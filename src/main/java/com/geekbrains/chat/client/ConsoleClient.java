package com.geekbrains.chat.client;

import java.time.LocalDateTime;
import java.util.Scanner;

import com.geekbrains.StringMessage;

public class ConsoleClient {
    public static void main(String[] args) {
        NettyNet net = new NettyNet(System.out::println);
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String msg = in.nextLine();
            net.sendMessage(new StringMessage(msg, LocalDateTime.now()));
        }
    }
}
