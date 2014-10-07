package org.pottberg.ips.view;
	
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.pottberg.ips.controller.MainController;

public class Main extends BorderPane {

    private final MainController controller;

	public Main(Stage primaryStage) {
		controller = new MainController();
		controller.setStage(primaryStage);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(controller);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}		
	}

	public void init() {
		controller.init();
	}
    
}
