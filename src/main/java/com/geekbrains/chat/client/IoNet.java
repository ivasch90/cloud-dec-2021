package com.geekbrains.chat.client;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoNet implements Closeable {

    private final Callback callback;
    private final Socket socket;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final byte[] buf;

    public IoNet(Callback callback,
                 Socket socket) throws IOException {
        this.callback = callback;
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        buf = new byte[8192];
        Thread readThread = new Thread(this::readMessages);
        readThread.setDaemon(true);
        readThread.start();
    }

    public void writeUtf(String msg) throws IOException {
        os.writeUTF(msg);
        os.flush();
    }

    public void writeLong(long size) throws IOException {
        os.writeLong(size);
        os.flush();
    }

    public void writeBytes(byte[] bytes, int off, int cnt) throws IOException {
        os.write(bytes, off, cnt);
        os.flush();
    }

    public void sendMsg(String msg) throws IOException {
        os.write(msg.getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    private void readMessages() {
        try {
            while (true) {
                String msg = is.readUTF();
                callback.onReceive(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        os.close();
        is.close();
        socket.close();
    }
}
