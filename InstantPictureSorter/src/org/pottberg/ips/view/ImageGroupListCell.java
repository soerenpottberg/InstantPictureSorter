package org.pottberg.ips.view;

import javafx.scene.control.ListCell;

import org.pottberg.ips.model.ImageGroup;

public class ImageGroupListCell extends ListCell<ImageGroup> {

    @Override
    public void updateItem(final ImageGroup item, boolean empty) {
	super.updateItem(item, empty);

	if (empty || item == null) {
	    textProperty().unbind();
	    setText(null);
	    setGraphic(null);
	} else {
	    textProperty().bind(item.getlabelProperty());
	}

    }

}
