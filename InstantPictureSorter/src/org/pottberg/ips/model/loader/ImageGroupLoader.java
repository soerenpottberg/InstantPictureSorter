package org.pottberg.ips.model.loader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.ImageGroup;
import org.pottberg.ips.model.SimpleDirectory;

public class ImageGroupLoader extends Task<ObservableList<ImageGroup>> {

    private final Path dir;

    public ImageGroupLoader(Path dir) {
	this.dir = dir;
    }

    @Override
    protected ObservableList<ImageGroup> call() throws Exception {
	updateProgress(-1, -1);
	try {
	    ObservableList<ImageData> imageData = createImageData();

	    loadCreationDate(imageData);

	    ObservableList<ImageGroup> imageGroups = createImageGroups(imageData);

	    return imageGroups;

	} catch (Exception e) {
	    updateProgress(0, 1);
	    e.printStackTrace();
	}

	return null;
    }

    private ObservableList<ImageData> createImageData() throws IOException {
	// TODO use ImageNameLoader
	ObservableList<ImageData> imageData = FXCollections.observableArrayList();
	try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
	    "*.{jpg,JPG,jpeg,JPEG}")) {
	    for (Path imagePath : stream) {
		imageData.add(new ImageData(imagePath, new SimpleDirectory(dir)));
	    }
	}
	return imageData;
    }

    private void loadCreationDate(ObservableList<ImageData> imageDataList)
	throws Exception {
	CreationDateLoader creationDateLoader = new CreationDateLoader(
	    imageDataList, false);
	creationDateLoader.progressProperty()
	    .addListener(
		(observableProgress, oldProgress, newProgress) -> {
		    updateProgress(creationDateLoader.getWorkDone(),
			creationDateLoader.getTotalWork());
		});
	creationDateLoader.loadAttributes();
    }

    private ObservableList<ImageGroup> createImageGroups(
	List<ImageData> imageData) {
	ObservableList<ImageGroup> imageGroups = FXCollections
	    .observableArrayList();

	Collections.sort(imageData);
	ImageGroup currentGroup = null;
	for (ImageData data : imageData) {
	    if (!data.fitInImageGroup(currentGroup)) {
		currentGroup = ImageGroup.forImageData(data, dir);
		imageGroups.add(currentGroup);
	    }
	    currentGroup.addImageData(data);
	}
	return imageGroups;
    }

}
