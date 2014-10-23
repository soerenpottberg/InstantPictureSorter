package org.pottberg.ips.model.loader;

import java.net.URI;

import org.pottberg.ips.model.ImageData;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class ImageLoader extends Task<Void> {

    private static final boolean LODING_CANCELLED = true;
    public static final double LOADED_COMPLETLY = 1;
    private Image image;
    private ObservableList<ImageData> imageDataList;

    public ImageLoader(ObservableList<ImageData> imageDataList) {
	this.imageDataList = FXCollections.observableArrayList(imageDataList);
    }

    @Override
    protected Void call() throws Exception {
	int i = 0;
	updateProgress(i, imageDataList.size());
	for (ImageData imageData : imageDataList) {
	    i++;
	    if (imageData.getImage() != null) {
		updateProgress(i, imageDataList.size());
		continue;
	    }
	    loadImage(imageData);
	    updateProgress(i, imageDataList.size());
	    if(awaitImageLoading() == LODING_CANCELLED) {
		return null;
	    }
	}
	return null;
    }

    private boolean awaitImageLoading() {
	while (image.getProgress() != LOADED_COMPLETLY && !image.isError()) {
	    if (isCancelled()) {
		return true; 
	    }
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		if (isCancelled()) {
		    return true;
		}
	    }
	}
	return false;
    }

    private void loadImage(ImageData imageData) {
	URI uri = imageData.getUri();
	image = new Image(uri.toString(), 100, 100, true,
	    true, true);
	Platform.runLater(() -> {
	    imageData.setImage(image);
	});
    }

}
