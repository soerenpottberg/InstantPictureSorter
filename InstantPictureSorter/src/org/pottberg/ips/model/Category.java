package org.pottberg.ips.model;

import java.nio.file.Path;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public interface Category {

    public StringProperty nameProperty();

    public String getName();

    public ObservableList<ImageData> getImageDataList();

    public void startLoadingImages();

    public void stopLoadingImages();

    public void startLoadingFileAttributes();

    public void stopLoadingFileAttributes();

    public ReadOnlyDoubleProperty progressProperty();

    public double getProgress();

    public ObjectProperty<LocalDate> startDateProperty();

    public ObjectProperty<LocalDate> endDateProperty();

    public ObjectProperty<LocalDate> userDefinedStartDateProperty();

    public ObjectProperty<LocalDate> userDefinedEndDateProperty();

    public ObjectProperty<Path> directoryProperty();

    public Path getDirectory();

    public void setName(String name);

    public void setDirtectoy(Path dir);

    public String getDirectoryName();

    public void reset();

    public void loadImageNames();
}