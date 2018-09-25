package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.FileSwapperView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("File Swapper");
        primaryStage.setScene(new Scene(new FileSwapperView(primaryStage), 300, 100));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
