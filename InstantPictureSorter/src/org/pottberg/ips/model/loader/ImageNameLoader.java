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

public class ImageNameLoader extends Task<ObservableList<ImageData>> {
    private ReadOnlyObjectWrapper<ObservableList<ImageData>> imageData = new ReadOnlyObjectWrapper<>(
	FXCollections.observableArrayList());

    private final Path dir;

    public ImageNameLoader(Path dir) {
	this.dir = dir;
    }

    @Override
    protected ObservableList<ImageData> call() throws Exception {

	try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
	    "*.{jpg,JPG,jpeg,JPEG}")) {
	    for (Path file : stream) {
		if (!Files.isRegularFile(file)) {
		    continue;
		}
		final ImageData data = new ImageData(file);
		Platform.runLater(() -> {
		    getPartialResults().add(data);
		});
	    }
	} catch (DirectoryIteratorException e) {
	    throw e.getCause();
	}
	return getPartialResults();
    }

    public final ReadOnlyObjectProperty<ObservableList<ImageData>> partialResultsProperty() {
	return imageData.getReadOnlyProperty();
    }

    public final ObservableList<ImageData> getPartialResults() {
	return imageData.get();
    }

}
