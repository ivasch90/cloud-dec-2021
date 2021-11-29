package com.geekbrains.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Handler implements Runnable {

    private static final int SIZE = 8192;
    private Path serverDir;
    private boolean running;
    private final byte[] buf;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final Socket socket;

    public Handler(Socket socket) throws IOException {
        running = true;
        buf = new byte[SIZE];
        this.socket = socket;
        serverDir = Paths.get("server");
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                String command = is.readUTF();
                if (command.equals("quit")) {
                    os.writeUTF("Client disconnected");
                    close();
                    break;
                } else if (command.equals("#file#")) {
                    String fileName = is.readUTF();
                    long size = is.readLong();
                    try (FileOutputStream fos = new FileOutputStream(
                            serverDir.resolve(fileName).toFile())) {
                        os.writeUTF("File " + fileName + " created");
                        for (int i = 0; i < (size + SIZE - 1) / SIZE; i++) {
                            int read = is.read(buf);
                            fos.write(buf, 0 , read);
                            os.writeUTF("Uploaded " + (i + 1) + " batch");
                        }
                    }
                    os.writeUTF("File successfully uploaded.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        os.close();
        is.close();
        socket.close();
    }
}
