package org.pottberg.ips.controller;

import static org.pottberg.ips.bindings.Binder.bindProperty;
import static org.pottberg.ips.bindings.Binder.createBinding;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;

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

    @FXML
    private ToggleButton automaticLoadingToggleButton;

    @FXML
    private Button stopLoadingButton;

    @FXML
    private Button restartLoadingButton;

    private ObjectBinding<ObservableList<ImageData>> selectedCategoryImageData;

    private DoubleProperty progressProperty;

    private BooleanBinding isMoveable;

    private DoubleProperty selectedCategoryProgressProperty;

    private BooleanProperty selectedCategoryIsLoadingProperty;

    private BooleanProperty isAutomaticLoadingProperty;

    public CategoryManagementController() {
	progressProperty = new SimpleDoubleProperty();
	selectedCategoryProgressProperty = new SimpleDoubleProperty();
	isAutomaticLoadingProperty = new SimpleBooleanProperty(true);
	selectedCategoryIsLoadingProperty = new SimpleBooleanProperty(true);
    }

    @FXML
    protected void initialize() {
	super.initialize();

	automaticLoadingToggleButton.selectedProperty()
	    .bindBidirectional(isAutomaticLoadingProperty);

	bindProperty(selectedCategoryProperty,
	    selectedCategoryIsLoadingProperty, Category::isLoadingProperty);

	bindProperty(selectedCategoryProperty,
	    selectedCategoryProgressProperty, Category::progressProperty);

	stopLoadingButton.disableProperty()
	    .bind(selectedCategoryProperty.isNull()
		.or(selectedCategoryIsLoadingProperty.not()));

	restartLoadingButton.disableProperty()
	    .bind(
		selectedCategoryProperty.isNull()
		    .or(
			selectedCategoryIsLoadingProperty.or(selectedCategoryProgressProperty.isEqualTo(
			    ImageLoader.LOADED_COMPLETLY, 1e-16))));

	isMoveable = selectedCategoryProperty.isNotNull()
	    .and(selectedCategoryIsLoadingProperty.not());

	categoryEditForm.disableProperty()
	    .bind(isMoveable.not());

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
			newCategory.isAutomaticLoadingProperty()
			    .bindBidirectional(isAutomaticLoadingProperty);
			newCategory.loadImageNames();
			if (isAutomaticLoadingProperty.get()) {
			    newCategory.startLoadingImages();
			}
		    }
		});
	
	selectedCategoryImageData = createBinding(selectedCategoryProperty,
	    Category::getImageDataList);

	sortedPicturesListView.itemsProperty()
	    .bind(selectedCategoryImageData);
    }

    @FXML
    private void stopLoadingButtonClicked(ActionEvent event) {
	Category category = selectedCategoryProperty.get();
	if (category == null) {
	    return;
	}
	category.stopLoadingImages();
    }

    @FXML
    private void restartLoadingButtonClicked(ActionEvent event) {
	Category category = selectedCategoryProperty.get();
	if (category == null) {
	    return;
	}
	category.startLoadingImages();
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
