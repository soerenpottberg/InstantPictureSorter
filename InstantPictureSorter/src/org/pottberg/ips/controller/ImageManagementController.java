package org.pottberg.ips.controller;

import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.beans.binding.Bindings.createStringBinding;
import static javafx.beans.binding.Bindings.when;

import java.nio.file.Path;
import java.time.LocalDate;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.CategoryNameBuilder;
import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.ImageGroup;
import org.pottberg.ips.model.SimpleCategory;
import org.pottberg.ips.model.YearDirectoy;
import org.pottberg.ips.model.command.Command;
import org.pottberg.ips.model.command.ComplexCommand;
import org.pottberg.ips.model.command.CreateCategoryCommand;
import org.pottberg.ips.model.command.CreateYearDirectoryCommand;
import org.pottberg.ips.model.command.MoveImagesCommand;
import org.pottberg.ips.model.loader.ImageLoader;
import org.pottberg.ips.model.loader.service.ImageGroupLoaderService;
import org.pottberg.ips.view.ImageGroupListCell;
import org.pottberg.ips.view.ImageListCell;

public class ImageManagementController extends CategoryBasedController {

    @FXML
    private Button openSourceFolderButton;

    @FXML
    private ListView<ImageGroup> imageGroupListView;

    @FXML
    private ListView<ImageData> unsortedPicturesListView;

    @FXML
    private ToggleGroup categoryToggleGroup;

    @FXML
    private RadioButton suggestedCategoryRadioButton;

    @FXML
    private RadioButton newCategoryRadioButton;

    @FXML
    private RadioButton userDefinedCategoryRadioButton;

    @FXML
    private TextField newCategoryNameTextField;

    @FXML
    private Label suggestedCategoryPreviewLabel;

    @FXML
    private Label newCategoryPreviewLabel;

    @FXML
    private Label userDefinedCategoryPreviewLabel;

    @FXML
    private Label categoryPreviewLabel;

    @FXML
    private Button moveToSelectedCategoryButton;

    private ObjectProperty<Path> selectedSourcePathProperty;

    private ImageGroupLoaderService imageGroupLoaderService;

    private ObjectProperty<ImageGroup> selectedImageGroupProperty;

    private ObjectBinding<ObservableList<ImageData>> selectedImageGroupImageData;

    private ObjectBinding<LocalDate> selectedImageGroupDate;

    private BooleanBinding isMoveable;

    private ObservableList<ImageData> selectedUnsortedImageData;

    private ObjectProperty<Category> suggestedCategoryProperty;

    private ChangeListener<String> selectNewCategoryListener;

    private ChangeListener<Category> selectUserDefinedCategoryListener;

    private DoubleProperty selectedImageGroupProgressProperty;

    public ImageManagementController() {
	selectedSourcePathProperty = new SimpleObjectProperty<>();
	imageGroupLoaderService = new ImageGroupLoaderService();
	selectedImageGroupProperty = new SimpleObjectProperty<>();
	selectedUnsortedImageData = FXCollections.observableArrayList();
	suggestedCategoryProperty = new SimpleObjectProperty<>();
	selectedImageGroupProgressProperty = new SimpleDoubleProperty();
    }

    @Override
    @FXML
    protected void initialize() {
	super.initialize();

	isMoveable = createBooleanBinding(
	    () -> !selectedUnsortedImageData.isEmpty(),
	    selectedUnsortedImageData).and(
	    selectedImageGroupProgressProperty.isEqualTo(ImageLoader.LOADED_COMPLETLY, 1e-16));

	moveToSelectedCategoryButton.disableProperty()
	    .bind(isMoveable.not());

	yearDirectoriesProperty.addListener((observableYearList, oldYearList,
	    newYearList) -> {
	    selectedCategoryProperty.removeListener(selectUserDefinedCategoryListener);
	    yearsCombobox.getSelectionModel()
		.selectLast();
	    selectedCategoryProperty.addListener(selectUserDefinedCategoryListener);
	});

	selectedImageGroupDate = createObjectBinding(() -> {
	    ImageGroup selectedImageGroup = selectedImageGroupProperty.get();
	    if (selectedImageGroup == null) {
		return null;
	    }
	    return selectedImageGroup.getCreationDate();
	}, selectedImageGroupProperty);

	suggestedCategoryProperty.bind(createObjectBinding(() -> {
	    if (selectedImageGroupDate.get() == null) {
		return null;
	    }
	    LocalDate creationDate = selectedImageGroupDate.get();
	    int year = creationDate.getYear();
	    YearDirectoy yearDirectory = getYearDirectory(year);
	    if (yearDirectory == null) {
		return null;
	    }
	    for (Category category : yearDirectory.getCategories()) {
		if (category.containsDate(creationDate)) {
		    return category;
		}
	    }
	    return null;
	}, selectedUnsortedImageData, selectedUnsortedImageData));

	selectedSourcePathProperty.addListener((observablePath, oldPath,
	    newPath) -> {
	    imageGroupLoaderService.setDirectory(newPath);
	    imageGroupLoaderService.setOnSucceeded(workerEvent -> {
		ObservableList<ImageGroup> imageGroups = imageGroupLoaderService.getValue();
		for (ImageGroup imageGroup : imageGroups) {
		    imageGroup.getImageDataList()
			.addListener(
			    (ListChangeListener<ImageData>) change -> {
				ObservableList<ImageGroup> items = imageGroupListView.getItems();
				if (change.getList()
				    .size() == 0) {
				    items.remove(imageGroup);
				} else if (!items.contains(imageGroup)) {
				    items.add(imageGroup);
				    FXCollections.sort(items);
				}
			    });
		}
		imageGroupListView.setItems(imageGroups);
	    });
	    imageGroupLoaderService.restart();
	});

	imageGroupListView.setCellFactory(param -> {
	    return new ImageGroupListCell();
	});

	unsortedPicturesListView.setCellFactory(param -> {
	    return new ImageListCell(selectedUnsortedImageData);
	});

	imageGroupListView.getSelectionModel()
	    .selectedItemProperty()
	    .addListener(
		(observableImageGroup, oldImageGroup, newImageGroup) -> {
		    if (oldImageGroup != null) {
			oldImageGroup.stopLoadingImages();
			newCategoryNameTextField.textProperty()
			    .removeListener(selectNewCategoryListener);
		    }
		    suggestedCategoryRadioButton.setSelected(true);
		    newCategoryNameTextField.setText("");
		    if (newImageGroup != null) {
			newImageGroup.startLoadingImages();
			newCategoryNameTextField.textProperty()
			    .addListener(selectNewCategoryListener);
		    }
		});

	selectedImageGroupProperty.bind(getSelectedItemProperty(imageGroupListView));

	selectedImageGroupImageData = Bindings.createObjectBinding(() -> {
	    if (selectedImageGroupProperty.get() == null) {
		return null;
	    }
	    return selectedImageGroupProperty.get()
		.getImageDataList();
	}, selectedImageGroupProperty);

	selectedImageGroupProperty.addListener((observableImageGroup,
	    oldImageGroup, newImageGroup) -> {
	    selectedUnsortedImageData.clear();
	    selectedImageGroupProgressProperty.unbind();
	    if (newImageGroup != null) {
		selectedImageGroupProgressProperty.bind(newImageGroup.progressProperty());
	    }
	});

	unsortedPicturesListView.itemsProperty()
	    .bind(selectedImageGroupImageData);

	suggestedCategoryPreviewLabel.textProperty()
	    .bind(createStringBinding(() -> {
		if (suggestedCategoryProperty.get() == null) {
		    return "No Suggestion";
		}
		return suggestedCategoryProperty.get()
		    .getDirectory()
		    .getFileName()
		    .toString();
	    }, suggestedCategoryProperty));

	newCategoryPreviewLabel.textProperty()
	    .bind(
		CategoryNameBuilder.createSuggestedNameBinding(
		    selectedImageGroupDate,
		    selectedImageGroupDate,
		    newCategoryNameTextField.textProperty()));

	userDefinedCategoryPreviewLabel.textProperty()
	    .bind(createStringBinding(() -> {
		if (selectedCategoryProperty.get() == null) {
		    return "No Category Selected";
		}
		return selectedCategoryProperty.get()
		    .getDirectory()
		    .getFileName()
		    .toString();
	    }, selectedCategoryProperty));

	categoryPreviewLabel.textProperty()
	    .bind(when(categoryToggleGroup.selectedToggleProperty()
		.isEqualTo(suggestedCategoryRadioButton))
		.then(suggestedCategoryPreviewLabel.textProperty())
		.otherwise(when(categoryToggleGroup.selectedToggleProperty()
		    .isEqualTo(newCategoryRadioButton))
		    .then(newCategoryPreviewLabel.textProperty())
		    .otherwise(userDefinedCategoryPreviewLabel.textProperty())));

	selectNewCategoryListener = (observableText, oldText, newText) -> {
	    newCategoryRadioButton.setSelected(true);
	};

	selectUserDefinedCategoryListener = (observableCategory, oldCategory,
	    newCategory) -> {
	    userDefinedCategoryRadioButton.setSelected(true);
	};
    }

    @FXML
    private void openSourceDirectoryClicked(ActionEvent event) {
	mainController.openSourceDirectoryClicked(event);
    }

    @FXML
    private void moveToSelectedCategoryClicked(ActionEvent event) {
	Category targetCategory = null;
	Command createYearDirectoryCommand = null;
	Command createCategoryCommand = null;
	if (categoryToggleGroup.getSelectedToggle() == suggestedCategoryRadioButton) {
	    targetCategory = suggestedCategoryProperty.get();
	} else if (categoryToggleGroup.getSelectedToggle() == newCategoryRadioButton) {
	    String newCategoryName = newCategoryNameTextField.getText();
	    LocalDate creationDate = selectedImageGroupProperty.get()
		.getCreationDate();
	    int year = creationDate.getYear();
	    YearDirectoy yearDirectory = getYearDirectory(year);
	    if (yearDirectory == null) {
		Path parentDirectory = selectedTargetPathProperty.get();
		yearDirectory = new YearDirectoy(parentDirectory, year);
		createYearDirectoryCommand = new CreateYearDirectoryCommand(
		    yearDirectoriesProperty.get(),
		    yearDirectory);
	    }
	    targetCategory = new SimpleCategory(yearDirectory, newCategoryName,
		creationDate);
	    createCategoryCommand = new CreateCategoryCommand(
		yearDirectory, targetCategory);
	} else if (categoryToggleGroup.getSelectedToggle() == userDefinedCategoryRadioButton) {
	    targetCategory = selectedCategoryProperty.get();
	}

	if (selectedUnsortedImageData.isEmpty() || targetCategory == null) {
	    return;
	}
	MoveImagesCommand moveImagemsCommand = new MoveImagesCommand(null,
	    selectedUnsortedImageData, selectedImageGroupProperty.get(),
	    targetCategory);
	selectedUnsortedImageData.clear();

	if (createCategoryCommand != null || createYearDirectoryCommand != null) {
	    ComplexCommand complexCommand = new ComplexCommand() {

		@Override
		public String getName() {
		    return moveImagemsCommand.getName();
		}
	    };
	    if (createYearDirectoryCommand != null) {
		complexCommand.addCommand(createYearDirectoryCommand);
	    }
	    if (createCategoryCommand != null) {
		complexCommand.addCommand(createCategoryCommand);
	    }
	    complexCommand.addCommand(moveImagemsCommand);
	    mainController.getCommandExecutor()
		.execute(complexCommand);
	} else {
	    mainController.getCommandExecutor()
		.execute(moveImagemsCommand);
	}
    }

    private YearDirectoy getYearDirectory(int year) {
	if (yearDirectoriesProperty.get() == null) {
	    return null;
	}
	for (YearDirectoy yearDirectory : yearDirectoriesProperty.get()) {
	    if (yearDirectory.getYear() == year) {
		return yearDirectory;
	    }
	}
	return null;
    }

    @FXML
    private void openTargetDirectoryClicked(ActionEvent event) {
	mainController.openTargetDirectoryClicked(event);
    }

    @FXML
    private void selectAllClicked(ActionEvent event) {
	if (selectedImageGroupImageData.get() == null) {
	    return;
	}
	selectedUnsortedImageData.setAll(selectedImageGroupImageData.get());
    }

    @FXML
    private void unselectAllClicked(ActionEvent event) {
	selectedUnsortedImageData.clear();
    }

    public ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty() {
	return yearDirectoriesProperty;
    }

    public ObjectProperty<Path> selectedSourcePathProperty() {
	return selectedSourcePathProperty;
    }

}
