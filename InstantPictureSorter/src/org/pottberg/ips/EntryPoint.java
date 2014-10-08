package org.pottberg.ips;

import java.io.IOException;

import org.pottberg.ips.view.Main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EntryPoint extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
	Main root = new Main(primaryStage);
	Scene scene = new Scene(root, 1200, 990);

	primaryStage.setTitle("Instant Picture Sorter");
	primaryStage.setScene(scene);
	primaryStage.show();
    }

    public static void main(String[] args) {
	launch(args);
    }
}
