package org.pottberg.ips.controller;

import java.io.File;
import java.nio.file.Path;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import org.pottberg.ips.model.YearDirectoy;
import org.pottberg.ips.model.command.CommandExecutor;
import org.pottberg.ips.model.loader.service.CategoryLoaderService;
import org.pottberg.ips.view.CategoryManagement;
import org.pottberg.ips.view.ImageManagement;
import org.pottberg.ips.view.Main;

public class MainController implements Controller {

    @FXML
    private CategoryManagement categoryManagement;

    @FXML
    private ImageManagement imageManagement;

    private CategoryLoaderService categoryLoaderService;

    private ObjectProperty<Path> selectedTargetPathProperty;

    private ObjectProperty<Path> selectedSourcePathProperty;

    private ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty;

    private Main root;

    private final DirectoryChooser directoryChooser;

    private CommandExecutor commandExecutor;

    public MainController(Main main) {
	this.root = main;
	categoryLoaderService = new CategoryLoaderService();
	selectedTargetPathProperty = new SimpleObjectProperty<>();
	selectedSourcePathProperty = new SimpleObjectProperty<>();
	yearDirectoriesProperty = new SimpleObjectProperty<>();
	directoryChooser = new DirectoryChooser();
	commandExecutor = new CommandExecutor();
    }

    @FXML
    private void initialize() {

	selectedTargetPathProperty.addListener((observablePath, oldPath,
	    newPath) -> {
	    categoryLoaderService.setDirectory(newPath);
	    categoryLoaderService.setOnSucceeded(workerEvent -> {
		yearDirectoriesProperty.set(categoryLoaderService.getValue());
	    });
	    categoryManagement.progressProperty()
		.bind(categoryLoaderService.progressProperty());
	    categoryLoaderService.restart();
	});

	imageManagement.yearDirectoriesProperty()
	    .bindBidirectional(yearDirectoriesProperty);
	categoryManagement.yearDirectoriesProperty()
	    .bindBidirectional(yearDirectoriesProperty);

	imageManagement.selectedTargetPathProperty()
	    .bindBidirectional(selectedTargetPathProperty);
	categoryManagement.selectedTargetPathProperty()
	    .bindBidirectional(selectedTargetPathProperty);

	imageManagement.selectedSourcePathProperty()
	    .bindBidirectional(selectedSourcePathProperty);

	categoryManagement.setMainController(this);
	imageManagement.setMainController(this);
    }

    @FXML
    public void openTargetDirectoryClicked(ActionEvent event) {
	String title = "Open Target Folder";
	final Path selectedDirectory = openDirectoryChooser(title);

	if (selectedDirectory == null) {
	    return;
	}

	selectedTargetPathProperty.set(selectedDirectory);
    }

    @FXML
    public void openSourceDirectoryClicked(ActionEvent event) {
	String title = "Open Folder Containing Unsorted Pictures";
	final Path selectedDirectory = openDirectoryChooser(title);

	if (selectedDirectory == null) {
	    return;
	}

	selectedSourcePathProperty.set(selectedDirectory);
    }

    private Path openDirectoryChooser(String title) {
	directoryChooser.setTitle(title);
	final File selectedDirectory = directoryChooser
	    .showDialog(getRootWindow());
	if (selectedDirectory == null) {
	    return null;
	}
	return selectedDirectory.toPath();
    }

    @FXML
    public void onExitClicked(ActionEvent event) {
	Platform.exit();
    }

    @FXML
    public void undoClicked(ActionEvent event) {
	commandExecutor.undo();
    }

    public void redoClicked(ActionEvent event) {
	commandExecutor.redo();
    }

    private Window getRootWindow() {
	return root.getScene()
	    .getWindow();
    }

    public CommandExecutor getCommandExecutor() {
	return commandExecutor;
    }

}
