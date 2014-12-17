package org.pottberg.ips.controller;

import java.io.File;
import java.nio.file.Path;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.pottberg.ips.model.YearDirectoy;
import org.pottberg.ips.model.command.Command;
import org.pottberg.ips.model.command.CommandExecutionException;
import org.pottberg.ips.model.command.CommandExecutor;
import org.pottberg.ips.model.loader.service.CategoryLoaderService;
import org.pottberg.ips.view.CategoryManagement;
import org.pottberg.ips.view.ErrorDialog;
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

	try {
	    verifyDoesNotContainSourceDirectory(selectedDirectory);
	    selectedTargetPathProperty.set(selectedDirectory);
	} catch (CommandExecutionException e) {
	    displayException("Change target directory to " + selectedDirectory,
		e);
	}
    }

    @FXML
    public void openSourceDirectoryClicked(ActionEvent event) {
	String title = "Open Folder Containing Unsorted Pictures";
	final Path selectedDirectory = openDirectoryChooser(title);

	if (selectedDirectory == null) {
	    return;
	}

	try {
	    verifyIsNotContainedInTargetDirectory(selectedDirectory);
	    selectedSourcePathProperty.set(selectedDirectory);
	} catch (CommandExecutionException e) {
	    displayException("Change source directory to " + selectedDirectory,
		e);
	}
    }

    private void verifyDoesNotContainSourceDirectory(
	final Path selectedDirectory) throws CommandExecutionException {
	Path sourcePath = selectedSourcePathProperty.get();
	if (sourcePath == null) {
	    return;
	}
	if (sourcePath.getNameCount() < selectedDirectory.getNameCount()) {
	    return;
	}
	if (!sourcePath.getRoot()
	    .equals(selectedDirectory.getRoot())) {
	    return;
	}
	verifyIsNotParentPath(selectedDirectory, sourcePath);
    }

    private void verifyIsNotContainedInTargetDirectory(
	final Path selectedDirectory) throws CommandExecutionException {
	Path targetPath = selectedTargetPathProperty.get();
	if (targetPath == null) {
	    return;
	}
	if (targetPath.getNameCount() > selectedDirectory.getNameCount()) {
	    return;
	}
	if (!targetPath.getRoot()
	    .equals(selectedDirectory.getRoot())) {
	    return;
	}
	verifyIsNotParentPath(targetPath, selectedDirectory);
    }

    private void verifyIsNotParentPath(final Path targetPath, Path sourcePath)
        throws CommandExecutionException {
        Path sourceSubdirectory = sourcePath.subpath(0,
            targetPath.getNameCount());
        Path targetSubdirectory = targetPath.subpath(0,
            targetPath.getNameCount());
        if (sourceSubdirectory.equals(targetSubdirectory)) {
            throw new CommandExecutionException(
        	new IllegalArgumentException(
        	    "Source path cannot be a sub directory of the target path."));
    
        }
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
	try {
	    commandExecutor.undo();
	    categoryManagement.refresh();
	} catch (CommandExecutionException e) {
	    displayException("Undo", e);
	}
    }

    public void redoClicked(ActionEvent event) {
	try {
	    commandExecutor.redo();
	    categoryManagement.refresh();
	} catch (CommandExecutionException e) {
	    displayException("Redo", e);
	}
    }

    public void executeCommand(Command command) {
	try {
	    commandExecutor.execute(command);
	} catch (CommandExecutionException e) {
	    displayException(command.getName(), e);
	}
    }

    private Window getRootWindow() {
	return root.getScene()
	    .getWindow();
    }

    private void displayException(String commandName,
	CommandExecutionException e) {
	Stage dialogStage = new Stage();
	dialogStage.initModality(Modality.WINDOW_MODAL);
	dialogStage.initOwner(getRootWindow());
	ErrorDialog dialogRoot = new ErrorDialog();
	dialogRoot.setErrorMessage("Command Failed: " + commandName);
	Scene dialogScene = new Scene(dialogRoot);
	dialogStage.setTitle("Exception: " + e.getMessage());
	dialogStage.setScene(dialogScene);
	dialogStage.show();
    }

}
