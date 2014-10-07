package org.pottberg.ips.view;

import static javafx.beans.binding.Bindings.equal;
import static javafx.beans.binding.Bindings.when;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.pottberg.ips.model.ImageData;

public class ImageListCell extends ListCell<ImageData> {

    private static final int COMPLETLY_LOADED = 1;
    private ImageView imageView = new ImageView();
    private ProgressBar progressBar = new ProgressBar();
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();

    @Override
    public void updateItem(final ImageData item, boolean empty) {
	super.updateItem(item, empty);

	if (empty || item == null) {
	    imageProperty.unbind();
	    imageProperty.set(null);
	    setText(null);
	    graphicProperty().unbind();
	    setGraphic(null);
	} else {
	    imageProperty.bind(item.imagePorperty());
	    imageProperty.addListener((ChangeListener<Image>) (obervableImage,
		oldImage, newImage) -> {
		showImage(newImage, item);
	    });
	    showImage(imageProperty.get(), item);

	}

    }

    private void showImage(Image image, ImageData data) {
	if (image == null) {
	    progressBar.progressProperty()
		.unbind();
	    progressBar.setProgress(-1);
	    graphicProperty().unbind();
	    graphicProperty().set(progressBar);
	} else {
	    progressBar.progressProperty()
		.bind(image.progressProperty());
	    imageView.setImage(image);
	    graphicProperty().bind(
		when(
		    equal(image.progressProperty(), COMPLETLY_LOADED))
		    .then((Node) imageView)
		    .otherwise(progressBar));
	}
    }

}
