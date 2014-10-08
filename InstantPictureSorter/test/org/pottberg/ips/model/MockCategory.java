package org.pottberg.ips.model;

import java.nio.file.Path;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class MockCategory implements Category {

    private StringProperty nameProperty;
    private ObjectProperty<LocalDate> startDateProperty;
    private ObjectProperty<LocalDate> endDateProperty;
    private ObjectProperty<LocalDate> userDefinedStartDateProperty;
    private ObjectProperty<LocalDate> userDefinedEndDateProperty;
    private ObjectProperty<Path> directoryProperty;

    public MockCategory() {
	directoryProperty = new SimpleObjectProperty<>();
	startDateProperty = new SimpleObjectProperty<>();
	endDateProperty = new SimpleObjectProperty<>();
	userDefinedStartDateProperty = new SimpleObjectProperty<>();
	userDefinedEndDateProperty = new SimpleObjectProperty<>();
	nameProperty = new SimpleStringProperty();
    }

    @Override
    public StringProperty nameProperty() {
	return nameProperty;
    }

    @Override
    public String getName() {
	return nameProperty.get();
    }

    @Override
    public ObservableList<ImageData> getImageData() {
	return null;
    }
   
    @Override
    public void startLoadingImages() {
    }

    @Override
    public void stopLoadingImages() {
    }

    @Override
    public void startLoadingFileAttributes() {
    }

    @Override
    public void stopLoadingFileAttributes() {
    }
    
    @Override
    public ReadOnlyDoubleProperty progressProperty() {
	return null;
    }

    @Override
    public double getProgress() {
	return -1;
    }

    @Override
    public ObjectProperty<LocalDate> startDateProperty() {
	return startDateProperty;
    }

    @Override
    public ObjectProperty<LocalDate> endDateProperty() {
	return endDateProperty;
    }

    @Override
    public ObjectProperty<LocalDate> userDefinedStartDateProperty() {
	return userDefinedStartDateProperty;
    }

    @Override
    public ObjectProperty<LocalDate> userDefinedEndDateProperty() {
	return userDefinedEndDateProperty;
    }

    @Override
    public ObjectProperty<Path> directoryProperty() {
	return directoryProperty;
    }

    @Override
    public Path getDirectory() {
	return directoryProperty.get();
    }

    @Override
    public void setName(String name) {
	nameProperty.set(name);
    }

    @Override
    public void setDirtectoy(Path dir) {
	directoryProperty.set(dir);
    }

    @Override
    public String getDirectoryName() {
	return getDirectory().getFileName()
	    .toString();
    }

    @Override
    public void reset() {
    }

    public void setStartDate(LocalDate startDate) {
	startDateProperty.set(startDate);
    }

    public void setEndDate(LocalDate endDate) {
	endDateProperty.set(endDate);
    }
    
    public void setUserDefinedStartDate(LocalDate userDefinedStartDate) {
	userDefinedStartDateProperty.set(userDefinedStartDate);
    }

    public void setUserDefinedEndDate(LocalDate userDefinedEndDate) {
	userDefinedEndDateProperty.set(userDefinedEndDate);
    }
}