package org.pottberg.ips.controller;

import java.io.File;
import java.nio.file.Path;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.ImageGroup;
import org.pottberg.ips.model.Year;
import org.pottberg.ips.model.loader.service.CategoryLoaderService;
import org.pottberg.ips.model.loader.service.ImageGroupLoaderService;
import org.pottberg.ips.view.AttributedImageListCell;
import org.pottberg.ips.view.CategoryListCell;
import org.pottberg.ips.view.ImageGroupListCell;
import org.pottberg.ips.view.ImageListCell;

public class MainController {

    private Stage primaryStage;

    @FXML
    private ComboBox<Year> yearsCombobox;

    @FXML
    private ListView<ImageGroup> imageGroupListView;

    @FXML
    private ListView<ImageData> unsortedPicturesListView;

    @FXML
    private ListView<Category> categoriesListView;

    @FXML
    private ListView<ImageData> sortedPicturesListView;

    @FXML
    private ProgressBar sortedPicturesProgressBar;

    @FXML
    private TextField categoryNameTextField;

    @FXML
    private DatePicker earliestDateDatePicker;

    private ObjectProperty<Path> selectedTargetPath;

    private ObjectProperty<Path> selectedSourcePath;

    private CategoryLoaderService categoryLoaderService;

    private ImageGroupLoaderService imageGroupLoaderService;

    private ObjectProperty<Category> selectedCategory;

    private ObjectBinding<ObservableList<ImageData>> selectedCategoryImageData;

    private ObjectProperty<ImageGroup> selectedImageGroup;

    private ObjectBinding<ObservableList<ImageData>> selectedImageGroupImageData;

    private ObjectProperty<Year> selectedYear;

    private ObjectBinding<ObservableList<Category>> selectedYearCategories;

    public MainController() {
	categoryLoaderService = new CategoryLoaderService();
	imageGroupLoaderService = new ImageGroupLoaderService();
	selectedTargetPath = new SimpleObjectProperty<>();
	selectedSourcePath = new SimpleObjectProperty<>();
	selectedCategory = new SimpleObjectProperty<>();
	selectedImageGroup = new SimpleObjectProperty<>();
	selectedYear = new SimpleObjectProperty<>();
    }

    @FXML
    private void openTargetFolderClicked(ActionEvent event) {
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	final File selectedDirectory = directoryChooser
	    .showDialog(primaryStage);

	if (selectedDirectory == null) {
	    return;
	}

	selectedTargetPath.set(selectedDirectory.toPath());
    }

    @FXML
    private void openFolderOfUnsortedPicturesClicked(ActionEvent event) {
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	final File selectedDirectory = directoryChooser
	    .showDialog(primaryStage);

	if (selectedDirectory == null) {
	    return;
	}

	selectedSourcePath.set(selectedDirectory.toPath());
    }

    public void setStage(Stage primaryStage) {
	this.primaryStage = primaryStage;
    }

    public void init() {

	selectedTargetPath.addListener((observablePath, oldPath, newPath) -> {
	    categoryLoaderService.cancel();
	    categoryLoaderService.reset();
	    categoryLoaderService.setDirectory(newPath);
	    categoryLoaderService.start();
	    categoryLoaderService.setOnSucceeded(workerEvent -> {
		yearsCombobox.setItems(categoryLoaderService.getValue());
		yearsCombobox.getSelectionModel()
		    .selectLast();
	    });
	});

	selectedSourcePath.addListener((observablePath, oldPath, newPath) -> {
	    imageGroupLoaderService.cancel();
	    imageGroupLoaderService.reset();
	    imageGroupLoaderService.setDirectory(newPath);
	    imageGroupLoaderService.start();
	    imageGroupLoaderService.setOnSucceeded(workerEvent -> {
		ObservableList<ImageGroup> imageGroups = imageGroupLoaderService.getValue();
		for (ImageGroup imageGroup : imageGroups) {
		    imageGroup.getImageData()
			.addListener(
			    (ListChangeListener.Change<? extends ImageData> item) -> {
				if (item.getList()
				    .size() == 0) {
				    imageGroupListView.getItems()
					.remove(imageGroup);
				}
			    });
		}
		imageGroupListView.setItems(imageGroups);
	    });
	});

	sortedPicturesListView.setCellFactory(param -> {
	    return new AttributedImageListCell();
	});
	imageGroupListView.setCellFactory(param -> {
	    return new ImageGroupListCell();
	});
	unsortedPicturesListView.setCellFactory(param -> {
	    return new ImageListCell();
	});
	categoriesListView.setCellFactory(param -> {
	    return new CategoryListCell();
	});

	categoriesListView.getSelectionModel()
	    .selectedItemProperty()
	    .addListener((observableCategory, oldCategory, newCategory) -> {
		if (oldCategory != null) {
		    oldCategory.stopLoadingImages();
		}
		sortedPicturesProgressBar.progressProperty()
		    .unbind();
		categoryNameTextField.setText("");
		earliestDateDatePicker.valueProperty()
		    .unbind();
		if (newCategory != null) {
		    categoryNameTextField.setText(newCategory.getLabel());
		    earliestDateDatePicker.valueProperty()
			.bind(newCategory.earliestDateProperty());
		    sortedPicturesProgressBar.progressProperty()
			.bind(newCategory.progressProperty());
		    newCategory.startLoadingImages();
		}
	    });

	imageGroupListView.getSelectionModel()
	    .selectedItemProperty()
	    .addListener(
		(observableImageGroup, oldImageGroup, newImageGroup) -> {
		    if (oldImageGroup != null) {
			oldImageGroup.stopLoadingImages();
		    }
		    if (newImageGroup != null) {
			newImageGroup.startLoadingImages();
		    }
		});

	selectedYear.bind(getSelectedItemProperty(yearsCombobox));
	selectedCategory.bind(getSelectedItemProperty(categoriesListView));
	selectedImageGroup.bind(getSelectedItemProperty(imageGroupListView));

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
		.getImageData();
	}, selectedCategory);
	
	selectedImageGroupImageData = Bindings.createObjectBinding(() -> {
	    if (selectedImageGroup.get() == null) {
		return null;
	    }
	    return selectedImageGroup.get()
		.getImageData();
	}, selectedImageGroup);

	categoriesListView.itemsProperty()
	    .bind(selectedYearCategories);
	sortedPicturesListView.itemsProperty()
	    .bind(selectedCategoryImageData);
	unsortedPicturesListView.itemsProperty()
	    .bind(selectedImageGroupImageData);
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
    private void removeClicked(ActionEvent event) {
	final int index = unsortedPicturesListView.getSelectionModel()
	    .getSelectedIndex();
	if (index != -1) {
	    unsortedPicturesListView.getItems()
		.remove(index);
	}
    }

}