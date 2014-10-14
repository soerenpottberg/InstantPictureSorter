package org.pottberg.ips.view;

import java.io.IOException;
import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import org.pottberg.ips.controller.CategoryManagementController;
import org.pottberg.ips.controller.MainController;
import org.pottberg.ips.model.YearDirectoy;

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
    
    public ObjectProperty<Path> selectedTargetPathProperty() {
	return controller.selectedTargetPathProperty();
    }
    
    public ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty() {
	return controller.yearDirectoriesProperty();
    }

    public void setMainController(MainController mainController) {
	controller.setMainController(mainController);
    }
}
