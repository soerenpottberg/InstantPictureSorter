package org.pottberg.ips.model;

import static javafx.beans.binding.Bindings.createDoubleBinding;

import java.nio.file.Path;
import java.time.LocalDate;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;

import org.pottberg.ips.model.loader.ImageNameLoader;
import org.pottberg.ips.model.loader.service.CreationDateLoaderService;
import org.pottberg.ips.model.loader.service.ImageDataLoaderService;
import org.pottberg.ips.model.loader.service.ImageLoaderService;

public class SimpleCategory implements Category {

    private StringProperty nameProperty;
    private ObservableList<ImageData> imageDataList;
    private ImageLoaderService imageLoaderService;
    private ImageNameLoader imageNameLoader;
    private CreationDateLoaderService fileAttributeLoaderService;

    private ObjectProperty<LocalDate> startDateProperty;
    private ObjectProperty<LocalDate> endDateProperty;
    private ObjectProperty<LocalDate> userDefinedStartDateProperty;
    private ObjectProperty<LocalDate> userDefinedEndDateProperty;
    private ObjectProperty<Path> directoryProperty;
    private CategoryNameParser categoryNameParser;
    private YearDirectoy yearDirectory;
    private DoubleProperty progressProperty;

    private SimpleCategory(YearDirectoy yearDirectory, String name,
	LocalDate startDate,
	LocalDate endDate, LocalDate userDefinedStartDate,
	LocalDate userDefinedEndDate) {
	this.yearDirectory = yearDirectory;
	imageDataList = FXCollections.observableArrayList();
	directoryProperty = new SimpleObjectProperty<>();
	startDateProperty = new SimpleObjectProperty<>(startDate);
	endDateProperty = new SimpleObjectProperty<>(endDate);
	userDefinedStartDateProperty = new SimpleObjectProperty<>(
	    userDefinedStartDate);
	userDefinedEndDateProperty = new SimpleObjectProperty<>(
	    userDefinedEndDate);
	nameProperty = new SimpleStringProperty(name);
	progressProperty = new SimpleDoubleProperty(1);
    }

    public SimpleCategory(YearDirectoy yearDirectory, Path directory) {
	this(yearDirectory, null, null, null, null, null);
	imageNameLoader = new ImageNameLoader(directory, imageDataList);
	imageLoaderService = new ImageLoaderService();
	fileAttributeLoaderService = new CreationDateLoaderService();
	fileAttributeLoaderService.setOnSucceeded(event -> {
	    calculateDates();
	});
	progressProperty.bind(createDoubleBinding(() -> {
	    Double sum = 0d;
	    if (imageLoaderService.getProgress() == -1
		&& fileAttributeLoaderService.getProgress() == -1) {
		return -1d;
	    }
	    if (imageLoaderService.getProgress() != -1) {
		sum += imageLoaderService.getProgress();
	    }
	    if (fileAttributeLoaderService.getProgress() != -1) {
		sum += fileAttributeLoaderService.getProgress();
	    }
	    return sum / 2d;
	}, imageLoaderService.progressProperty(),
	    fileAttributeLoaderService.progressProperty())
	    );
	setDirtectoy(directory);
	parseDirectory();
    }

    public SimpleCategory(YearDirectoy yearDirectory, String name,
	LocalDate date) {
	this(yearDirectory, name, date, date, date, date);
	CategoryNameBuilder categoryNameBuilder = new CategoryNameBuilder(this);
	String completeName = categoryNameBuilder.getSuggestedName();
	Path parentDirectory = yearDirectory.getDirectory();
	setDirtectoy(parentDirectory.resolve(completeName));
    }

    private void parseDirectory() {
	if (categoryNameParser == null) {
	    categoryNameParser = new CategoryNameParser(directoryProperty);
	}
	nameProperty.set(categoryNameParser.getName());
	userDefinedStartDateProperty.set(categoryNameParser.getStartDate());
	userDefinedEndDateProperty.set(categoryNameParser.getEndDate());
    }

    private void calculateDates() {
	LocalDate earlierstDate = null;
	LocalDate lastDate = null;
	for (ImageData data : imageDataList) {
	    if (earlierstDate == null || data.getCreationDate()
		.isBefore(earlierstDate)) {
		earlierstDate = data.getCreationDate();
	    }
	    if (lastDate == null || data.getCreationDate()
		.isAfter(lastDate)) {
		lastDate = data.getCreationDate();
	    }
	}
	startDateProperty.set(earlierstDate);
	endDateProperty.set(lastDate);
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
    public ObservableList<ImageData> getImageDataList() {
	return imageDataList;
    }

    @Override
    public void loadImageNames() {
	if (imageNameLoader == null) {
	    return;
	}
	if (imageNameLoader.getState() != State.READY) {
	    return;
	}

	imageNameLoader.setOnSucceeded(event -> {
	    startService(imageLoaderService);
	    startService(fileAttributeLoaderService);
	});
	new Thread(imageNameLoader).start();
    }

    @Override
    public void startLoadingImages() {
	startService(imageLoaderService);
    }

    @Override
    public void stopLoadingImages() {
	stopService(imageLoaderService);
    }

    @Override
    public void startLoadingFileAttributes() {
	startService(fileAttributeLoaderService);
    }

    @Override
    public void stopLoadingFileAttributes() {
	stopService(fileAttributeLoaderService);
    }

    private void startService(ImageDataLoaderService service) {
	if (service == null) {
	    return;
	}
	if (imageNameLoader.getState() != State.SUCCEEDED) {
	    return;
	}
	if (service.getState() == State.SUCCEEDED) {
	    return;
	}
	service.setImageData(imageDataList);
	service.restart();
    }

    private void stopService(ImageDataLoaderService service) {
	if (service == null) {
	    return;
	}
	if (service.getState() == State.SUCCEEDED) {
	    return;
	}
	service.cancel();
    }

    @Override
    public ReadOnlyDoubleProperty progressProperty() {
	return progressProperty;
    }

    @Override
    public double getProgress() {
	return progressProperty().get();
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
	parseDirectory();
    }

    @Override
    public YearDirectoy getYearDirectory() {
	return yearDirectory;
    }

    @Override
    public boolean containsDate(LocalDate date) {
	if (userDefinedStartDateProperty.get() == null
	    || date.isBefore(userDefinedStartDateProperty.get())) {
	    return false;
	}
	if (userDefinedEndDateProperty.get() == null
	    || date.isAfter(userDefinedEndDateProperty.get())) {
	    return false;
	}
	return true;
    }

}