package org.pottberg.ips.model;

import static javafx.beans.binding.Bindings.format;
import static javafx.beans.binding.Bindings.when;

import java.nio.file.Path;
import java.time.LocalDate;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;

import org.pottberg.ips.model.loader.service.ImageDataLoaderService;
import org.pottberg.ips.model.loader.service.ImageLoaderService;

public class ImageGroup implements Comparable<ImageGroup> {

    private LocalDate creationDate;
    private ObservableList<ImageData> imageData;
    private StringProperty labelProperty;
    private ObjectProperty<ObservableList<ImageData>> imageDataPorperty;
    private ImageLoaderService imageLoaderService;

    public ImageGroup(LocalDate creationDate, Path dir) {
	imageLoaderService = new ImageLoaderService();
	this.creationDate = creationDate;
	imageDataPorperty = new SimpleObjectProperty<>();
	imageData = FXCollections.observableArrayList();
	imageDataPorperty.set(imageData);
	labelProperty = new SimpleStringProperty();

	bindLabelProperty();
    }

    private void bindLabelProperty() {
	IntegerBinding groupSize = Bindings.createIntegerBinding(() -> {
	    return imageData.size();
	}, imageData);
	StringBinding description = when(groupSize.isEqualTo(1))
	    .then("Picture")
	    .otherwise("Pictures");
	StringExpression label = format("%1$td.%1$tm.%1$tY (%2$d %3$s)",
	    creationDate, groupSize,
	    description);
	labelProperty.bind(label);
    }

    public ObjectProperty<ObservableList<ImageData>> getImageDataProperty() {
	return imageDataPorperty;
    }

    @Override
    public String toString() {
	return getLabel();
    }

    public LocalDate getCreationDate() {
	return creationDate;
    }

    public static ImageGroup forImageData(ImageData data, Path dir) {
	return new ImageGroup(data.getCreationDate(), dir);
    }

    public void addImageData(ImageData data) {
	imageData.add(data);
    }

    public ObservableList<ImageData> getImageDataList() {
	return imageData;
    }

    public StringProperty getlabelProperty() {
	return labelProperty;
    }

    public String getLabel() {
	return labelProperty.get();
    }

    public void startLoadingImages() {
	startService(imageLoaderService);
    }

    public void stopLoadingImages() {
	stopService(imageLoaderService);
    }

    private void startService(ImageDataLoaderService service) {
	if (service.getState() == State.SUCCEEDED) {
	    return;
	}
	service.setImageData(imageData);
	service.restart();
    }

    private void stopService(ImageDataLoaderService service) {
	if (service.getState() == State.SUCCEEDED) {
	    return;
	}
	service.cancel();
    }

    @Override
    public int compareTo(ImageGroup other) {
	return getCreationDate().compareTo(other.getCreationDate());
    }

    public ReadOnlyDoubleProperty progressProperty() {
	return imageLoaderService.progressProperty();
    }

}
