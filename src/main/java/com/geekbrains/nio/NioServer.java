package com.geekbrains.nio;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class NioServer {

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buf;
    private Path serverPath;

    public NioServer(int port) throws IOException {

        buf = ByteBuffer.allocate(5);
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverPath = Paths.get("server");

        while (serverChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            try {
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        handleAccept();
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    iterator.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        StringBuilder msg = new StringBuilder();
        while (true) {
            int read = channel.read(buf);
            if (read == -1) {
                channel.close();
                return;
            }
            if (read == 0) {
                break;
            }
            buf.flip();
            while (buf.hasRemaining()) {
                msg.append((char) buf.get());
            }
            buf.clear();
        }
        processMessage(channel, msg.toString());
        // String response = "Hello " + msg + key.attachment();
        // channel.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
    }

    private void processMessage(SocketChannel channel, String msg) throws IOException {
        // TODO: 29.11.2021
        if (msg.startsWith("touch")) {
            String[] arr = msg.split("\\s");
            String str = arr[1];
            Path path = Paths.get(serverPath.toString(), str);
            if (!path.toFile().exists()) {
                Files.createFile(path);
                channel.write(ByteBuffer.wrap(("File " + str + " created.\n").getBytes(StandardCharsets.UTF_8)));
            } else {
                channel.write(ByteBuffer.wrap(("ERROR: File " + str + " exists.\n").getBytes(StandardCharsets.UTF_8)));
            }
        }
        if (msg.startsWith("mkdir")) {
            String[] arr = msg.split("\\s");
            String str = arr[1];
            Path path = Paths.get(serverPath.toString(), str);

            if (!path.toFile().exists()) {
                Files.createDirectory(path);
                channel.write(ByteBuffer.wrap(("Dir " + str + " created.\n").getBytes(StandardCharsets.UTF_8)));
            } else {
                channel.write(ByteBuffer.wrap(("ERROR: dir " + str + " exists.\n").getBytes(StandardCharsets.UTF_8)));
            }
        }
        if (msg.contains("ls")) {
            String[] list = serverPath.toFile().list();
            String string = "";
            for (String s : list) {
                string = s;
                channel.write(ByteBuffer.wrap((string + " ").getBytes(StandardCharsets.UTF_8)));
            }
            channel.write(ByteBuffer.wrap(("\n").getBytes(StandardCharsets.UTF_8)));
        }
        if (msg.startsWith("cd")) {
            String[] strings = msg.split("\\s");
            String str = strings[1];
            if (str.contentEquals("/")) {
                serverPath = Paths.get("server");
            }
            String[] list = serverPath.toFile().list();
            for (int i = 0; i < list.length; i++) {
                if (str.contentEquals(list[i]) && Files.isDirectory(serverPath.resolve(list[i]))) {
                    serverPath = serverPath.resolve(str);
                    break;
                }
            }
        }
        if (msg.startsWith("cat")) {
            String[] strings = msg.split("\\s");
            String str = strings[1];
            String[] list = serverPath.toFile().list();
            Path resolve = serverPath.resolve(str);
            for (int i = 0; i < list.length; i++) {
                if (str.contentEquals(list[i])) {
                    byte[] bytes = Files.readAllBytes(resolve);
                    channel.write(ByteBuffer.wrap(bytes));
                    channel.write(ByteBuffer.wrap(("\n").getBytes(StandardCharsets.UTF_8)));
                }
            }
        }
    }

    private void handleAccept() throws IOException {
        System.out.println("Client accepted...");
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, "Hello world!");
    }

    public static void main(String[] args) throws IOException {
        new NioServer(8189);
    }

}
