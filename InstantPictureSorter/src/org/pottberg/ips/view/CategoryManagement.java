package org.pottberg.ips.view;

import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import org.pottberg.ips.controller.CategoryManagementController;
import org.pottberg.ips.model.Year;

public class CategoryManagement extends BorderPane {

    private final CategoryManagementController controller;

    public CategoryManagement() {
	controller = new CategoryManagementController();
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
	    "CategoryManagement.fxml"));
	fxmlLoader.setRoot(this);
	fxmlLoader.setController(controller);
	try {
	    fxmlLoader.load();
	} catch (IOException exception) {
	    throw new RuntimeException(exception);
	}
    }
    
    public ObjectProperty<ObservableList<Year>> yearListProperty() {
	return controller.yearListProperty();
    }
}
