package org.pottberg.ips.view;

import javafx.scene.control.ListCell;

import org.pottberg.ips.model.Category;

public class CategoryListCell extends ListCell<Category> {

    @Override
    public void updateItem(final Category item, boolean empty) {
	super.updateItem(item, empty);

	if (empty || item == null) {
	    textProperty().unbind();
	    setText(null);
	    setGraphic(null);
	} else {
	    textProperty().bind(item.getLabelProperty());
	}

    }

}
