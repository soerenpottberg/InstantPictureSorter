package org.pottberg.ips.view;

import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import org.pottberg.ips.controller.CategoryEditFormController;
import org.pottberg.ips.model.Category;

public class CategoryEditForm extends GridPane {

    private final CategoryEditFormController controller;

    public CategoryEditForm() {
	controller = new CategoryEditFormController();
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
	    "CategoryEditForm.fxml"));
	fxmlLoader.setRoot(this);
	fxmlLoader.setController(controller);
	try {
	    fxmlLoader.load();
	} catch (IOException exception) {
	    throw new RuntimeException(exception);
	}
    }

    public ObjectProperty<Category> categoryProperty() {
	return controller.categoryProperty();
    }

}
