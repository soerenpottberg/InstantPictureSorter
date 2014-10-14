package org.pottberg.ips.view;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import org.pottberg.ips.controller.Controller;

public abstract class View extends BorderPane {

    public View() {
	load();
    }

    protected void load() {
	FXMLLoader fxmlLoader = new FXMLLoader(getFxmlUrl());
	fxmlLoader.setRoot(this);
	fxmlLoader.setController(getController());
	try {
	    fxmlLoader.load();
	} catch (IOException exception) {
	    throw new RuntimeException(exception);
	}
    }

    protected abstract Controller getController();

    private URL getFxmlUrl() {
	return getClass().getResource(getFxmlFileName());
    }

    private String getFxmlFileName() {
	return getClass().getSimpleName() + ".fxml";
    }

}
