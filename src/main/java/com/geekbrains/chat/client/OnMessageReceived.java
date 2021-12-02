package com.geekbrains.chat.client;

import com.geekbrains.AbstractMessage;

public interface OnMessageReceived {

    void onReceive(AbstractMessage msg);

}
