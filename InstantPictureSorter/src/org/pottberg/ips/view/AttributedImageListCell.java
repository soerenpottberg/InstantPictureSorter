package org.pottberg.ips.view;

import javafx.scene.control.ListCell;

import org.pottberg.ips.model.ImageData;

public class AttributedImageListCell extends ListCell<ImageData> {

    private AttributedImageView imageView = new AttributedImageView();

    @Override
    public void updateItem(final ImageData item, boolean empty) {
	super.updateItem(item, empty);

	if (empty || item == null) {
	    setText(null);
	    setGraphic(null);
	} else {
	    imageView.setImageData(item);
	    setGraphic(imageView);
	}

    }

}
