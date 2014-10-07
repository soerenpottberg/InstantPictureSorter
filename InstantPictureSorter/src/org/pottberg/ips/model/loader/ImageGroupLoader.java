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

public class ImageGroupLoader extends Task<ObservableList<ImageGroup>> {

    private final Path dir;

    public ImageGroupLoader(Path dir) {
	this.dir = dir;
    }

    @Override
    protected ObservableList<ImageGroup> call() throws Exception {

	try {
	ObservableList<ImageData> imageData = createImageData();
	
	loadCreationDate(imageData);

	ObservableList<ImageGroup> imageGroups = createImageGroups(imageData);
	
	return imageGroups;
	
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	return null;
    }

    private ObservableList<ImageData> createImageData() throws IOException {
        ObservableList<ImageData> imageData = FXCollections.observableArrayList();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
            "*.{jpg,JPG,jpeg,JPEG}")) {
            for (Path imagePath : stream) {
        	imageData.add(new ImageData(imagePath));
            }
        }
        return imageData;
    }

    private void loadCreationDate(ObservableList<ImageData> imageData)
	throws Exception {
	CreationDateLoader.loadFileAttributes(imageData);
    }

    private ObservableList<ImageGroup> createImageGroups(
	List<ImageData> imageData) {
	ObservableList<ImageGroup> imageGroups = FXCollections
	    .observableArrayList();

	Collections.sort(imageData);
	ImageGroup currentGroup = null;
	for (ImageData data : imageData) {
	    if (!data.fitInImageGroup(currentGroup)) {
		currentGroup = ImageGroup.forImageData(data);
		imageGroups.add(currentGroup);
	    }
	    currentGroup.addImageData(data);
	}
	return imageGroups;
    }

}
