package org.pottberg.ips.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

import org.pottberg.ips.controller.AttributedImageViewController;
import org.pottberg.ips.model.ImageData;

public class AttributedImageView extends BorderPane {

    private final AttributedImageViewController controller;

    public AttributedImageView() {
	controller = new AttributedImageViewController();
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
	    "AttributedImageView.fxml"));
	fxmlLoader.setRoot(this);
	fxmlLoader.setController(controller);
	try {
	    fxmlLoader.load();
	} catch (IOException exception) {
	    throw new RuntimeException(exception);
	}
    }

    public void setImage(Image image) {
	controller.setImage(image);
    }

    public void setName(String name) {
	controller.setName(name);
    }

    public void setImageData(ImageData data) {
	controller.setImageData(data);
    }

}
