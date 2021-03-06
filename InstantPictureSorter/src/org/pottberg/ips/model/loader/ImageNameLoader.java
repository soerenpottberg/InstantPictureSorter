package org.pottberg.ips.model.loader;

import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.pottberg.ips.model.ImageData;

public abstract class ImageNameLoader extends Task<ObservableList<ImageData>> {
    private ReadOnlyObjectWrapper<ObservableList<ImageData>> imageData;

    private final Path dir;

    public ImageNameLoader(Path dir) {
	this.dir = dir;
	imageData = new ReadOnlyObjectWrapper<>(FXCollections.observableArrayList());
    }

    public ImageNameLoader(Path dir, ObservableList<ImageData> imageDataList) {
	this.dir = dir;
	imageData = new ReadOnlyObjectWrapper<>(imageDataList);
    }

    @Override
    protected ObservableList<ImageData> call() throws Exception {

	try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
	    "*.{jpg,JPG,jpeg,JPEG}")) {
	    for (Path file : stream) {
		if (!Files.isRegularFile(file)) {
		    continue;
		}
		final ImageData data = createImageData(file);
		Platform.runLater(() -> {
		    getPartialResults().add(data);
		});
	    }
	} catch (DirectoryIteratorException e) {
	    throw e.getCause();
	}
	return getPartialResults();
    }

    public abstract ImageData createImageData(Path file);

    public final ReadOnlyObjectProperty<ObservableList<ImageData>> partialResultsProperty() {
	return imageData.getReadOnlyProperty();
    }

    public final ObservableList<ImageData> getPartialResults() {
	return imageData.get();
    }

}
