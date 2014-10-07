package org.pottberg.ips.model.loader.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;

import org.pottberg.ips.model.ImageData;

public abstract class ImageDataLoaderService extends Service<Void> {

    protected ObjectProperty<ObservableList<ImageData>> imageDataProperty = new SimpleObjectProperty<>();

    public ObjectProperty<ObservableList<ImageData>> imageDataProperty() {
	return imageDataProperty;
    }
    
    public void setImageData(ObservableList<ImageData> imageData) {
	imageDataProperty.set(imageData);
    }

}
