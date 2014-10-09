package org.pottberg.ips.controller;

import java.io.File;
import java.nio.file.Path;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.Year;
import org.pottberg.ips.model.loader.service.CategoryLoaderService;
import org.pottberg.ips.view.AttributedImageListCell;
import org.pottberg.ips.view.CategoryEditForm;

public class CategoryManagementController extends CategoryBasedController {

    @FXML
    private Button openTargetFolderButton;

    @FXML
    private ComboBox<Year> yearsCombobox;

    @FXML
    private ListView<Category> categoriesListView;

    @FXML
    private ListView<ImageData> sortedPicturesListView;

    @FXML
    private ProgressBar sortedPicturesProgressBar;

    @FXML
    private CategoryEditForm categoryEditForm;

    private ObjectProperty<Path> selectedTargetPath;

    private CategoryLoaderService categoryLoaderService;

    private ObjectBinding<ObservableList<ImageData>> selectedCategoryImageData;

    public CategoryManagementController() {
	selectedTargetPath = new SimpleObjectProperty<>();
	categoryLoaderService = new CategoryLoaderService();
    }

    @FXML
    protected void initialize() {
	super.initialize();
	
	selectedTargetPath.addListener((observablePath, oldPath, newPath) -> {
	    categoryLoaderService.cancel();
	    categoryLoaderService.reset();
	    categoryLoaderService.setDirectory(newPath);
	    categoryLoaderService.start();
	    categoryLoaderService.setOnSucceeded(workerEvent -> {
		yearListProperty.set(categoryLoaderService.getValue());
		yearsCombobox.setItems(categoryLoaderService.getValue());
		yearsCombobox.getSelectionModel()
		    .selectLast();
	    });
	});

	sortedPicturesListView.setCellFactory(param -> {
	    return new AttributedImageListCell();
	});

	categoryEditForm.categoryProperty()
	    .bind(categoriesListView.getSelectionModel()
		.selectedItemProperty());

	categoriesListView.getSelectionModel()
	    .selectedItemProperty()
	    .addListener(
		(observableCategory, oldCategory, newCategory) -> {
		    if (oldCategory != null) {
			oldCategory.stopLoadingImages();
		    }
		    sortedPicturesProgressBar.progressProperty()
			.unbind();
		    if (newCategory != null) {
			sortedPicturesProgressBar.progressProperty()
			    .bind(newCategory.progressProperty());
			newCategory.loadImageNames();
			newCategory.startLoadingImages();
		    }
		});

	selectedCategoryImageData = Bindings.createObjectBinding(() -> {
	    if (selectedCategory.get() == null) {
		return null;
	    }
	    return selectedCategory.get()
		.getImageDataList();
	}, selectedCategory);

	sortedPicturesListView.itemsProperty()
	    .bind(selectedCategoryImageData);

    }

    @FXML
    private void openTargetFolderClicked(ActionEvent event) {
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	final File selectedDirectory = directoryChooser
	    .showDialog(openTargetFolderButton.getScene()
		.getWindow());

	if (selectedDirectory == null) {
	    return;
	}

	selectedTargetPath.set(selectedDirectory.toPath());
    }
    
    public ObjectProperty<ObservableList<Year>> yearListProperty() {
	return yearListProperty;
    }

}
