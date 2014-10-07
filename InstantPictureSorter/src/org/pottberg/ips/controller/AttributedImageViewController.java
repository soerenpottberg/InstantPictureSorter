package org.pottberg.ips.controller;

import static javafx.beans.binding.Bindings.notEqual;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import org.pottberg.ips.model.ImageData;

public class AttributedImageViewController {

    private static final int COMPLETLY_LOADED = 1;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();

    @FXML
    private ImageView imageView;

    @FXML
    private ProgressBar imageProgress;

    @FXML
    private Text name;

    @FXML
    private Text creationDateText;

    @FXML
    private Text lastModifiedDateText;

    public AttributedImageViewController() {
	imageProperty.addListener((ChangeListener<Image>) (obervableImage,
	    oldImage, newImage) -> {
	    showImage(newImage);
	});
    }

    public void setImage(Image image) {
	imageView.setImage(image);
    }

    public void setName(String name) {
	this.name.setText(name);
    }

    public void setImageData(ImageData data) {
	imageProperty.bind(data.imagePorperty());
	showImage(imageProperty.get());
	setName(data.getPath()
	    .getFileName()
	    .toString());
	StringExpression creationDate = Bindings.format("%1$td.%1$tm.%1$tY",
	    data.creationDateProperty());
	creationDateText.textProperty()
	    .bind(creationDate);
    }

    private void showImage(Image image) {
	if (image == null) {
	    imageView.setImage(null);
	    imageProgress.progressProperty()
		.unbind();
	    imageProgress.setProgress(-1);
	    imageProgress.visibleProperty()
		.unbind();
	    imageProgress.setVisible(true);
	} else {
	    imageProgress.progressProperty()
		.bind(image.progressProperty());
	    imageView.setImage(image);
	    imageProgress.visibleProperty()
		.bind(notEqual(image.progressProperty(), COMPLETLY_LOADED));
	}
    }

}
