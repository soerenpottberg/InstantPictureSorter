package org.pottberg.ips.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import org.pottberg.ips.model.loader.ImageLoader;
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
    private ProgressBar categoryManagementProgressBar;

    @FXML
    private CategoryEditForm categoryEditForm;

    private ObjectBinding<ObservableList<ImageData>> selectedCategoryImageData;

    private DoubleProperty progressProperty;

    private BooleanBinding isMoveable;

    private DoubleProperty selectedCategoryProgressProperty;
    
    public CategoryManagementController() {
	progressProperty = new SimpleDoubleProperty();
	selectedCategoryProgressProperty = new SimpleDoubleProperty();
    }

    @FXML
    protected void initialize() {
	super.initialize();
	
	selectedCategoryProperty.addListener((observableImageGroup,
	    oldImageGroup, newImageGroup) -> {
	    selectedCategoryProgressProperty.unbind();
	    if (newImageGroup != null) {
		selectedCategoryProgressProperty.bind(newImageGroup.progressProperty());
	    }
	});
	
	isMoveable = selectedCategoryProperty.isNotNull().and(
	    selectedCategoryProgressProperty.isEqualTo(
		ImageLoader.LOADED_COMPLETLY, 1e-16));

	categoryEditForm.disableProperty().bind(isMoveable.not());

	categoryManagementProgressBar.progressProperty()
	    .bind(progressProperty);

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
		    progressProperty.unbind();
		    if (newCategory != null) {
			progressProperty.bind(newCategory.progressProperty());
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

    @Override
    public void setMainController(MainController mainController) {
	super.setMainController(mainController);
	categoryEditForm.setMainController(mainController);
    }
    
    public DoubleProperty progressProperty() {
	return progressProperty;
    }
}
