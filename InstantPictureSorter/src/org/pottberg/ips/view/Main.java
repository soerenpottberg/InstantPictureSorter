package org.pottberg.ips.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import org.pottberg.ips.controller.MainController;

public class Main extends BorderPane {

    public Main() {
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
	    "Main.fxml"));
	fxmlLoader.setRoot(this);
	fxmlLoader.setController(new MainController());
	try {
	    fxmlLoader.load();
	} catch (IOException exception) {
	    throw new RuntimeException(exception);
	}
    }

}
