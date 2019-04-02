package view;

import controller.FileSwapperController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;


public class FileSwapperView extends GridPane {

    private FileSwapperController controller;


    public FileSwapperView(Stage stage){
        setPadding(new Insets(8, 8, 8, 8));
        getColumnConstraints().add(new ColumnConstraints(65));
        setHgap(5);
        setVgap(5);

        controller = new FileSwapperController();
        initView(stage);
    }

    private void initView(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick file to be moved");

        Button pickFileButton = new Button("Pick file");
        pickFileButton.setOnAction(event -> {
            controller.setFileToMove(fileChooser.showOpenDialog(stage));
            stage.sizeToScene();
    });
        add(pickFileButton, 0, 0);

        Label pickedFileLabel = new Label();
        pickedFileLabel.textProperty().bind(controller.fileToMoveStringProperty());
        add(pickedFileLabel, 1, 0);

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Pick dir to watch");

        Button pickDirButton = new Button("Pick dir");
        pickDirButton.setOnAction(event -> {
            controller.setDirToWatch(dirChooser.showDialog(stage));
            stage.sizeToScene();
        });
        add(pickDirButton, 0, 1);

        Label pickedDirLabel = new Label();
        pickedDirLabel.textProperty().bind(controller.dirToWatchStringProperty());
        add(pickedDirLabel, 1, 1);

        Button startButton = new Button("Start");
        startButton.setOnAction(event -> {
            try {
                controller.startListening();
                stage.sizeToScene();
            } catch ( IOException | InterruptedException e ) {
                e.printStackTrace();
            }
        });
        add(startButton, 0, 2);

        Label processStartedLabel = new Label();
        processStartedLabel.textProperty().bind(controller.processInfoStringPropery());
        processStartedLabel.setTextFill(Color.GREEN);
        add(processStartedLabel, 1, 2);

    }
}
