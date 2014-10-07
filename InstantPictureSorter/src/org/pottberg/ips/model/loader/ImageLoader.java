package org.pottberg.ips.model.loader;

import java.net.URI;

import org.pottberg.ips.model.ImageData;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class ImageLoader extends Task<Void> {

    private static final boolean LODING_CANCELLED = true;
    private static final double LOADED_COMPLETLY = 1;
    private Image image;
    private ObservableList<ImageData> imageData;

    public ImageLoader(ObservableList<ImageData> imageData) {
	this.imageData = imageData;
    }

    @Override
    protected Void call() throws Exception {
	int i = 0;
	updateProgress(i, imageData.size());
	for (ImageData data : imageData) {
	    i++;
	    if (data.getImage() != null) {
		updateProgress(i, imageData.size());
		continue;
	    }
	    loadImage(data);
	    updateProgress(i, imageData.size());
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

    private void loadImage(ImageData data) {
	URI uri = data.getUri();
	image = new Image(uri.toString(), 100, 100, true,
	    true, true);
	Platform.runLater(() -> {
	    data.setImage(image);
	});
    }

}
