package org.pottberg.ips.model;

import static javafx.beans.binding.Bindings.createDoubleBinding;

import java.nio.file.Path;
import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

public class SimpleCategory extends SimpleDirectory implements Category {

    private StringProperty nameProperty;
    private ObservableList<ImageData> imageDataList;
    private ImageLoaderService imageLoaderService;
    private ImageNameLoader imageNameLoader;
    private CreationDateLoaderService fileAttributeLoaderService;

    private ObjectProperty<LocalDate> startDateProperty;
    private ObjectProperty<LocalDate> endDateProperty;
    private ObjectProperty<LocalDate> userDefinedStartDateProperty;
    private ObjectProperty<LocalDate> userDefinedEndDateProperty;
    private CategoryNameParser categoryNameParser;
    private YearDirectoy yearDirectory;
    private DoubleProperty progressProperty;
    private BooleanProperty isAutomaticLoadingProperty;
    private BooleanProperty isLoadingProperty;

    private SimpleCategory(YearDirectoy yearDirectory, String name,
	LocalDate startDate,
	LocalDate endDate, LocalDate userDefinedStartDate,
	LocalDate userDefinedEndDate, Path path) {
	super(path);
	this.yearDirectory = yearDirectory;
	imageDataList = FXCollections.observableArrayList();
	startDateProperty = new SimpleObjectProperty<>(startDate);
	endDateProperty = new SimpleObjectProperty<>(endDate);
	userDefinedStartDateProperty = new SimpleObjectProperty<>(
	    userDefinedStartDate);
	userDefinedEndDateProperty = new SimpleObjectProperty<>(
	    userDefinedEndDate);
	nameProperty = new SimpleStringProperty(name);
	progressProperty = new SimpleDoubleProperty(1);
	isAutomaticLoadingProperty = new SimpleBooleanProperty();
	isLoadingProperty = new SimpleBooleanProperty();
    }

    public SimpleCategory(YearDirectoy yearDirectory, Path path) {
	this(yearDirectory, null, null, null, null, null, path);
	final SimpleCategory category = this;
	imageNameLoader = new ImageNameLoader(path, imageDataList) {

	    @Override
	    public ImageData createImageData(Path file) {
		return new ImageData(file.getFileName(), category);
	    }

	};
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
	parseDirectory();
	isLoadingProperty.bind(imageNameLoader.runningProperty()
	    .or(imageLoaderService.runningProperty())
	    .or(fileAttributeLoaderService.runningProperty()));
    }

    public SimpleCategory(YearDirectoy yearDirectory, String name,
	LocalDate date) {
	this(yearDirectory, name, date, date, date, date, null);
	CategoryNameBuilder categoryNameBuilder = new CategoryNameBuilder(this);
	String completeName = categoryNameBuilder.getSuggestedName();
	Path parentDirectory = yearDirectory.getPath();
	setPath(parentDirectory.resolve(completeName));
	isLoadingProperty.set(false);
    }

    private void parseDirectory() {
	if (categoryNameParser == null) {
	    categoryNameParser = new CategoryNameParser(pathProperty());
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
	    startService(fileAttributeLoaderService);
	    if (isAutomaticLoadingProperty.get()) {
		startService(imageLoaderService);
	    }
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
    public void setName(String name) {
	nameProperty.set(name);
    }

    @Override
    public String getDirectoryName() {
	return getPath().getFileName()
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

    public BooleanProperty isAutomaticLoadingProperty() {
	return isAutomaticLoadingProperty;
    }

    @Override
    public ReadOnlyBooleanProperty isLoadingProperty() {
	return isLoadingProperty;
    }

}