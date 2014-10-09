package org.pottberg.ips.controller;

import java.io.File;
import java.nio.file.Path;

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
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;

import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.ImageGroup;
import org.pottberg.ips.model.Year;
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
    
    private ObjectProperty<Path> selectedSourcePath;

    private ImageGroupLoaderService imageGroupLoaderService;

    private ObjectProperty<ImageGroup> selectedImageGroup;

    private ObjectBinding<ObservableList<ImageData>> selectedImageGroupImageData;

    private ObservableList<ImageData> selectedUnsortedImageData;

    public ImageManagementController() {
	selectedSourcePath = new SimpleObjectProperty<>();
	imageGroupLoaderService = new ImageGroupLoaderService();
	selectedImageGroup = new SimpleObjectProperty<>();
	selectedUnsortedImageData = FXCollections.observableArrayList();
    }

    @Override
    @FXML
    protected void initialize() {
	super.initialize();
	
	selectedSourcePath.addListener((observablePath, oldPath, newPath) -> {
	    imageGroupLoaderService.cancel();
	    imageGroupLoaderService.reset();
	    imageGroupLoaderService.setDirectory(newPath);
	    imageGroupLoaderService.start();
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

	selectedImageGroup.bind(getSelectedItemProperty(imageGroupListView));

	selectedImageGroupImageData = Bindings.createObjectBinding(() -> {
	    if (selectedImageGroup.get() == null) {
		return null;
	    }
	    return selectedImageGroup.get()
		.getImageDataList();
	}, selectedImageGroup);

	selectedImageGroup.addListener((observableList, oldList,
	    newList) -> {
	    selectedUnsortedImageData.clear();
	});

	unsortedPicturesListView.itemsProperty()
	    .bind(selectedImageGroupImageData);
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
	if (selectedUnsortedImageData.isEmpty()
	    || selectedCategory.get() == null) {
	    return;
	}
	MoveImagesCommand command = new MoveImagesCommand(null,
	    selectedUnsortedImageData, selectedImageGroup.get(),
	    selectedCategory.get());
	selectedUnsortedImageData.clear();
	command.execute();
    }

    @FXML
    private void selectAllClicked(ActionEvent event) {
	selectedUnsortedImageData.setAll(selectedImageGroupImageData.get());
    }

    @FXML
    private void unselectAllClicked(ActionEvent event) {
	selectedUnsortedImageData.clear();
    }
    
    public ObjectProperty<ObservableList<Year>> yearListProperty() {
	return yearListProperty;
    }

}
