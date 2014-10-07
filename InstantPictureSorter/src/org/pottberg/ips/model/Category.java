package org.pottberg.ips.model;

import java.nio.file.Path;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;

import org.pottberg.ips.model.loader.ImageNameLoader;
import org.pottberg.ips.model.loader.service.CreationDateLoaderService;
import org.pottberg.ips.model.loader.service.ImageDataLoaderService;
import org.pottberg.ips.model.loader.service.ImageLoaderService;

public class Category {

    private StringProperty labelProperty;
    private ObservableList<ImageData> imageData;
    private ImageLoaderService imageLoaderService;
    private ImageNameLoader imageNameLoader;
    private CreationDateLoaderService fileAttributeLoaderService;
    
    private ObjectProperty<LocalDate> earliestDateProperty;
    private ObjectProperty<LocalDate> lastDateProperty;

    public Category(Path dir) {
	imageNameLoader = new ImageNameLoader(dir);

	labelProperty = new SimpleStringProperty(dir.getFileName()
	    .toString());
	imageLoaderService = new ImageLoaderService();
	fileAttributeLoaderService = new CreationDateLoaderService();
	fileAttributeLoaderService.setOnSucceeded(event -> {
	    calculateDates();
	});
	earliestDateProperty = new SimpleObjectProperty<LocalDate>();
	lastDateProperty = new SimpleObjectProperty<LocalDate>();	 
    }

    private void calculateDates() {
	LocalDate earlierstDate = null;
	LocalDate lastDate = null;
	for (ImageData data : imageData) {
	    if(earlierstDate == null || data.getCreationDate().isBefore(earlierstDate)) {
		earlierstDate = data.getCreationDate();
	    }
	    if(lastDate == null || data.getCreationDate().isAfter(lastDate)) {
		lastDate = data.getCreationDate();
	    }
	}
	earliestDateProperty.set(earlierstDate);
	lastDateProperty.set(lastDate);
    }

    public ReadOnlyStringProperty getLabelProperty() {
	return labelProperty;
    }

    public String getLabel() {
	return labelProperty.get();
    }

    public ObservableList<ImageData> getImageData() {

	if (imageData == null) {
	    calculateImageData();
	}
	return imageData;

    }

    private void calculateImageData() {

	if (imageNameLoader.getState() != State.READY) {
	    return;
	}

	imageNameLoader.setOnSucceeded(event -> {
	    startInitialService(imageLoaderService);
	    startInitialService(fileAttributeLoaderService);
	});
	new Thread(imageNameLoader).start();

	imageData = imageNameLoader.getPartialResults();

    }

    public void startLoadingImages() {
	startService(imageLoaderService);
    }

    public void stopLoadingImages() {
	stopService(imageLoaderService);
    }

    public void startLoadingFileAttributes() {
	startService(fileAttributeLoaderService);
    }

    public void stopLoadingFileAttributes() {
	stopService(fileAttributeLoaderService);
    }

    private void startInitialService(ImageDataLoaderService service) {
	if (service.getState() != State.READY) {
	    return;
	}
	service.setImageData(imageData);
	service.start();
    }

    private void startService(ImageDataLoaderService service) {
	if (imageNameLoader.getState() != State.SUCCEEDED) {
	    return;
	}
	switch (service.getState()) {
	case READY:
	    startInitialService(service);
	    break;
	case CANCELLED:
	    service.restart();
	    break;
	default:
	    break;
	}
    }

    private void stopService(ImageDataLoaderService service) {
	if (service.getState() == State.SUCCEEDED) {
	    return;
	}
	service.cancel();
    }

    public ReadOnlyDoubleProperty progressProperty() {
	return imageLoaderService.progressProperty();
    }

    public double getProgress() {
	return imageLoaderService.getProgress();
    }

    public ObjectProperty<LocalDate> earliestDateProperty() {
	return earliestDateProperty;
    }
    
    public ObjectProperty<LocalDate> lastDateProperty() {
	return lastDateProperty;
    }

}
