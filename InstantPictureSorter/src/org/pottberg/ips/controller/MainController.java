package org.pottberg.ips.controller;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import org.pottberg.ips.model.YearDirectoy;
import org.pottberg.ips.model.loader.service.CategoryLoaderService;
import org.pottberg.ips.view.CategoryManagement;
import org.pottberg.ips.view.ImageManagement;

public class MainController {

    @FXML
    private CategoryManagement categoryManagement;

    @FXML
    private ImageManagement imageManagement;

    private CategoryLoaderService categoryLoaderService;

    private ObjectProperty<Path> selectedTargetPathProperty;

    private ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty;

    public MainController() {
	categoryLoaderService = new CategoryLoaderService();
	selectedTargetPathProperty = new SimpleObjectProperty<>();
	yearDirectoriesProperty = new SimpleObjectProperty<>();
    }

    @FXML
    private void initialize() {

	selectedTargetPathProperty.addListener((observablePath, oldPath,
	    newPath) -> {
	    categoryLoaderService.cancel();
	    categoryLoaderService.reset();
	    categoryLoaderService.setDirectory(newPath);
	    categoryLoaderService.start();
	    categoryLoaderService.setOnSucceeded(workerEvent -> {
		yearDirectoriesProperty.set(categoryLoaderService.getValue());
	    });
	});

	imageManagement.yearDirectoriesProperty()
	    .bindBidirectional(yearDirectoriesProperty);
	categoryManagement.yearDirectoriesProperty()
	    .bindBidirectional(yearDirectoriesProperty);

	imageManagement.selectedTargetPathProperty()
	    .bindBidirectional(selectedTargetPathProperty);
	categoryManagement.selectedTargetPathProperty()
	    .bindBidirectional(selectedTargetPathProperty);
    }

}
