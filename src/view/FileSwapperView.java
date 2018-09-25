package view;

import controller.FileSwapperController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;


public class FileSwapperView extends GridPane {

    private FileSwapperController controller;


    public FileSwapperView(Stage stage){
        setPadding(new Insets(15, 12, 15, 12));
        getColumnConstraints().add(new ColumnConstraints(200));
        controller = new FileSwapperController();
        initView(stage);
    }

    private void initView(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick file to be moved");

        Button pickFileButton = new Button("Pick file");
        pickFileButton.setOnAction(event -> controller.setFileToMove(fileChooser.showOpenDialog(stage)));
        add(pickFileButton, 1, 0);

        Label pickedFileLabel = new Label();
        pickedFileLabel.textProperty().bind(controller.fileToMoveStringProperty());
        add(pickedFileLabel, 0, 0);

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Pick dir to watch");

        Button pickDirButton = new Button("Pick dir");
        pickDirButton.setOnAction(event -> controller.setDirToWatch(dirChooser.showDialog(stage)));
        add(pickDirButton, 1, 1);

        Label pickedDirLabel = new Label();
        pickedDirLabel.textProperty().bind(controller.dirToWatchStringProperty());
        add(pickedDirLabel, 0, 1);

        Button startButton = new Button("Start");
        startButton.setOnAction(event -> {
            try {
                controller.startListening();
            } catch ( IOException | InterruptedException e ) {
                e.printStackTrace();
            }
        });
        add(startButton, 1, 2);



    }
}
