package org.pottberg.ips.controller;

import java.io.File;
import java.nio.file.Path;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
import org.pottberg.ips.view.CategoryListCell;

public class CategoryManagementController {

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

    private ObjectProperty<ObservableList<Year>> yearListProperty;

    private ObjectProperty<Year> selectedYear;

    private ObjectBinding<ObservableList<Category>> selectedYearCategories;

    private ObjectProperty<Category> selectedCategory;

    private ObjectBinding<ObservableList<ImageData>> selectedCategoryImageData;

    public CategoryManagementController() {
	selectedTargetPath = new SimpleObjectProperty<>();
	categoryLoaderService = new CategoryLoaderService();
	yearListProperty = new SimpleObjectProperty<>();
	selectedYear = new SimpleObjectProperty<>();
	selectedCategory = new SimpleObjectProperty<>();
    }

    @FXML
    private void initialize() {

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

	yearListProperty.addListener((observableYearList, oldYearList,
	    newYearList) -> {
	    yearsCombobox.setItems(newYearList);
	    yearsCombobox.getSelectionModel()
		.selectLast();
	});

	sortedPicturesListView.setCellFactory(param -> {
	    return new AttributedImageListCell();
	});

	categoriesListView.setCellFactory(param -> {
	    return new CategoryListCell();
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

	selectedYear.bind(getSelectedItemProperty(yearsCombobox));
	selectedCategory.bind(getSelectedItemProperty(categoriesListView));

	selectedYearCategories = Bindings.createObjectBinding(() -> {
	    if (selectedYear.get() == null) {
		return null;
	    }
	    return selectedYear.get()
		.getCategories();
	}, selectedYear);

	selectedCategoryImageData = Bindings.createObjectBinding(() -> {
	    if (selectedCategory.get() == null) {
		return null;
	    }
	    return selectedCategory.get()
		.getImageDataList();
	}, selectedCategory);

	categoriesListView.itemsProperty()
	    .bind(selectedYearCategories);
	sortedPicturesListView.itemsProperty()
	    .bind(selectedCategoryImageData);

    }

    private <T> ReadOnlyObjectProperty<T> getSelectedItemProperty(
	ComboBox<T> comboBox) {
	return comboBox.getSelectionModel()
	    .selectedItemProperty();
    }

    private <T> ReadOnlyObjectProperty<T> getSelectedItemProperty(
	ListView<T> listView) {
	return listView.getSelectionModel()
	    .selectedItemProperty();
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
