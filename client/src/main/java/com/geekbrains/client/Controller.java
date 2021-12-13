package com.geekbrains.client;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.geekbrains.core.model.AbstractMessage;
import com.geekbrains.core.model.FileMessage;
import com.geekbrains.core.model.FilesListResponse;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class Controller implements Initializable {

    public ListView<String> clientFiles;
    public ListView<String> serverFiles;
    private Path currentDir;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;

    public void upload(ActionEvent actionEvent) throws IOException {
        Path file = currentDir.resolve(clientFiles.getSelectionModel().getSelectedItem());
        if (!Files.isDirectory(file)) {
            os.writeObject(new FileMessage(file));
        }
    }

    public void download(ActionEvent actionEvent) {

    }

    private void refreshView(List<String> files, ListView<String> view) {
        Platform.runLater(() -> {
            view.getItems().clear();
            view.getItems().addAll(files);
        });
    }

    private List<String> getFilesInCurrentDir() throws IOException {
        return Files.list(currentDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    private String resolveType(Path p) {
        if (Files.isDirectory(p)) {
            return " [DIR]";
        } else {
            return " " + p.toFile().length() + " bytes";
        }
    }

    private void read() {
        try {
            while (true) {
                AbstractMessage msg = (AbstractMessage) is.readObject();
                switch (msg.getType()) {
                    case FILES_LIST_RESPONSE:
                        refreshView(((FilesListResponse) msg).getFiles(), serverFiles);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            currentDir = Paths.get(System.getProperty("user.home"));
            refreshView(getFilesInCurrentDir(), clientFiles);
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread t = new Thread(this::read);
            t.setDaemon(true);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
