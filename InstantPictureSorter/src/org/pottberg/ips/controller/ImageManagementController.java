package org.pottberg.ips.controller;

import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.beans.binding.Bindings.createStringBinding;
import static javafx.beans.binding.Bindings.when;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.stage.DirectoryChooser;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.CategoryNameBuilder;
import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.ImageGroup;
import org.pottberg.ips.model.SimpleCategory;
import org.pottberg.ips.model.YearDirectoy;
import org.pottberg.ips.model.command.Command;
import org.pottberg.ips.model.command.CreateCategoryCommand;
import org.pottberg.ips.model.command.CreateYearDirectoryCommand;
import org.pottberg.ips.model.command.MoveImagesCommand;
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

    private ObjectProperty<Path> selectedSourcePath;

    private ImageGroupLoaderService imageGroupLoaderService;

    private ObjectProperty<ImageGroup> selectedImageGroupProperty;

    private ObjectBinding<ObservableList<ImageData>> selectedImageGroupImageData;

    private ObjectBinding<LocalDate> selectedImageGroupDate;

    private ObservableList<ImageData> selectedUnsortedImageData;

    private ObjectProperty<Category> suggestedCategoryProperty;

    public ImageManagementController() {
	selectedSourcePath = new SimpleObjectProperty<>();
	imageGroupLoaderService = new ImageGroupLoaderService();
	selectedImageGroupProperty = new SimpleObjectProperty<>();
	selectedUnsortedImageData = FXCollections.observableArrayList();
	suggestedCategoryProperty = new SimpleObjectProperty<>();
    }

    @Override
    @FXML
    protected void initialize() {
	super.initialize();

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

	selectedSourcePath.addListener((observablePath, oldPath, newPath) -> {
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
		    }
		    if (newImageGroup != null) {
			newImageGroup.startLoadingImages();
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

	selectedImageGroupProperty.addListener((observableList, oldList,
	    newList) -> {
	    selectedUnsortedImageData.clear();
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
    }

    @FXML
    private void openFolderOfUnsortedPicturesClicked(ActionEvent event) {
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	final File selectedDirectory = directoryChooser
	    .showDialog(openSourceFolderButton.getScene()
		.getWindow());

	if (selectedDirectory == null) {
	    return;
	}

	selectedSourcePath.set(selectedDirectory.toPath());
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

    @FXML
    private void moveToSelectedCategoryClicked(ActionEvent event) {
	Category targetCategory = null;
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
		Command createYearDirectoryCommand = new CreateYearDirectoryCommand(
		    null, yearDirectoriesProperty.get(),
		    yearDirectory);
		createYearDirectoryCommand.execute();
	    }
	    targetCategory = new SimpleCategory(yearDirectory, newCategoryName,
		creationDate);
	    Command createCategoryCommand = new CreateCategoryCommand(null,
		yearDirectory, targetCategory);
	    createCategoryCommand.execute();
	} else if (categoryToggleGroup.getSelectedToggle() == userDefinedCategoryRadioButton) {
	    targetCategory = selectedCategoryProperty.get();
	}

	if (selectedUnsortedImageData.isEmpty() || targetCategory == null) {
	    return;
	}
	MoveImagesCommand command = new MoveImagesCommand(null,
	    selectedUnsortedImageData, selectedImageGroupProperty.get(),
	    targetCategory);
	selectedUnsortedImageData.clear();
	command.execute();
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
    private void selectAllClicked(ActionEvent event) {
	selectedUnsortedImageData.setAll(selectedImageGroupImageData.get());
    }

    @FXML
    private void unselectAllClicked(ActionEvent event) {
	selectedUnsortedImageData.clear();
    }

    public ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty() {
	return yearDirectoriesProperty;
    }

}
