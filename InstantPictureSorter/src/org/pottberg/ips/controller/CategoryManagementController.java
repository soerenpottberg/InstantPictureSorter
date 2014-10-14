package org.pottberg.ips.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.YearDirectoy;
import org.pottberg.ips.view.AttributedImageListCell;
import org.pottberg.ips.view.CategoryEditForm;

public class CategoryManagementController extends CategoryBasedController {

    @FXML
    private Button openTargetFolderButton;

    @FXML
    private ComboBox<YearDirectoy> yearsCombobox;

    @FXML
    private ListView<Category> categoriesListView;

    @FXML
    private ListView<ImageData> sortedPicturesListView;

    @FXML
    private ProgressBar sortedPicturesProgressBar;

    @FXML
    private CategoryEditForm categoryEditForm;

    private ObjectBinding<ObservableList<ImageData>> selectedCategoryImageData;

    private MainController mainController;

    @FXML
    protected void initialize() {
	super.initialize();
	
	yearDirectoriesProperty.addListener((observableYearList, oldYearList,
	    newYearList) -> {
	    yearsCombobox.getSelectionModel()
		.selectLast();
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
	    if (selectedCategoryProperty.get() == null) {
		return null;
	    }
	    return selectedCategoryProperty.get()
		.getImageDataList();
	}, selectedCategoryProperty);

	sortedPicturesListView.itemsProperty()
	    .bind(selectedCategoryImageData);
    }

    @FXML
    private void openTargetDirectoryClicked(ActionEvent event) {
	mainController.openTargetDirectoryClicked(event);
    }
    
    public ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty() {
	return yearDirectoriesProperty;
    }

    public void setMainController(MainController mainController) {
	this.mainController = mainController;	
    }

}
