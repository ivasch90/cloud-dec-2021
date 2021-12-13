package com.geekbrains.server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.geekbrains.core.model.AbstractMessage;
import com.geekbrains.core.model.FileMessage;
import com.geekbrains.core.model.FileRequest;
import com.geekbrains.core.model.FilesListResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        currentDir = Paths.get("root");
        if (!Files.exists(currentDir)) {
            Files.createDirectory(currentDir);
        }
        ctx.writeAndFlush(new FilesListResponse(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                AbstractMessage abstractMessage) throws Exception {
        switch (abstractMessage.getType()) {
            case FILE_REQUEST:
                FileRequest request = (FileRequest) abstractMessage;
                Path file = currentDir.resolve(request.getFileName());
                ctx.writeAndFlush(new FileMessage(file));
                break;
            case FILE_RESPONSE:
                FileMessage fileMsg = (FileMessage) abstractMessage;
                Files.write(
                        currentDir.resolve(fileMsg.getFileName()),
                        fileMsg.getBytes()
                );
                ctx.writeAndFlush(new FilesListResponse(currentDir));
                break;
        }
    }
}
