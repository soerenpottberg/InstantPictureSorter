package org.pottberg.ips.view;

import static javafx.beans.binding.Bindings.equal;
import static javafx.beans.binding.Bindings.when;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.pottberg.ips.model.ImageData;

public class ImageListCell extends ListCell<ImageData> {

    private static final String SELECTED_LIST_ITEM_STYLE_CLASS = "list-item-selected";
    private static final int COMPLETLY_LOADED = 1;
    private CheckBox checkBox;
    private ImageView imageView;
    private ProgressBar progressBar;
    private ObjectProperty<Image> imageProperty;
    private BooleanProperty selectedProperty;
    private ObservableList<ImageData> selectedItems;
    private ChangeListener<Boolean> updateSelectedItems;
    private ChangeListener<Boolean> updateStyleClass;
    private ListChangeListener<ImageData> updateSelectionProperty;

    public ImageListCell(ObservableList<ImageData> selectedItems) {
	this.selectedItems = selectedItems;
	checkBox = new CheckBox();
	imageView = new ImageView();
	progressBar = new ProgressBar();
	imageProperty = new SimpleObjectProperty<Image>();
	selectedProperty = new SimpleBooleanProperty();
	imageProperty.addListener((ChangeListener<Image>) (obervableImage,
	    oldImage, newImage) -> {
	    showImage(newImage);
	});
	checkBox.selectedProperty()
	    .bindBidirectional(selectedProperty);

	updateSelectionProperty = (ListChangeListener<ImageData>) change -> {
	    selectedProperty.removeListener(updateSelectedItems);
	    selectedProperty.set(selectedItems.contains(getItem()));
	    selectedProperty.addListener(updateSelectedItems);
	};
	selectedItems.addListener(updateSelectionProperty);

	updateSelectedItems = (observableIsSelected, oldIsSelected,
	    newIsSelected) -> {
	    if (isEmpty()) {
		return;
	    }
	    selectedItems.removeListener(updateSelectionProperty);
	    if (newIsSelected) {
		selectedItems.add(getItem());
	    } else {
		selectedItems.remove(getItem());
	    }
	    selectedItems.addListener(updateSelectionProperty);
	};
	updateStyleClass = (observableIsSelected, oldIsSelected,
	    newIsSelected) -> {
	    if (isEmpty()) {
		return;
	    }
	    if (newIsSelected) {
		getStyleClass().add(SELECTED_LIST_ITEM_STYLE_CLASS);
	    } else {
		getStyleClass().remove(SELECTED_LIST_ITEM_STYLE_CLASS);
	    }
	};
	selectedProperty.addListener(updateSelectedItems);
	selectedProperty.addListener(updateStyleClass);
	setOnMousePressed(mouseEvent -> {
	    if (isEmpty()) {
		return;
	    }
	    checkBox.setSelected(!checkBox.isSelected());
	    checkBox.requestFocus();
	    mouseEvent.consume();
	});
    }

    @Override
    public void updateItem(final ImageData item, boolean empty) {
	super.updateItem(item, empty);
	selectedProperty.removeListener(updateSelectedItems);
	selectedProperty.set(selectedItems.contains(item));
	selectedProperty.addListener(updateSelectedItems);
	if (empty || item == null) {
	    imageProperty.unbind();
	    imageProperty.set(null);
	    graphicProperty().unbind();
	    setGraphic(null);
	    getStyleClass().remove(SELECTED_LIST_ITEM_STYLE_CLASS);
	} else {
	    setGraphic(checkBox);
	    imageProperty.bind(item.imagePorperty());
	}

    }

    private void showImage(Image image) {
	if (image == null) {
	    progressBar.progressProperty()
		.unbind();
	    progressBar.setProgress(-1);
	    checkBox.graphicProperty()
		.unbind();
	    checkBox.graphicProperty()
		.set(progressBar);
	} else {
	    progressBar.progressProperty()
		.bind(image.progressProperty());
	    imageView.setImage(image);
	    checkBox.setAlignment(Pos.TOP_LEFT);
	    checkBox.graphicProperty()
		.bind(
		    when(
			equal(image.progressProperty(), COMPLETLY_LOADED))
			.then((Node) imageView)
			.otherwise(progressBar));
	}
    }

}
