package org.pottberg.ips.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import org.pottberg.ips.controller.MainController;

public class Main extends BorderPane {

    private MainController controller;

    public Main() {
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
	    "Main.fxml"));
	fxmlLoader.setRoot(this);
	controller = new MainController();
	fxmlLoader.setController(controller);
	try {
	    fxmlLoader.load();
	} catch (IOException exception) {
	    throw new RuntimeException(exception);
	}
    }

}
