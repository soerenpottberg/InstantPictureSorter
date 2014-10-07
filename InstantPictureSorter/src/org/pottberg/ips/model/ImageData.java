package org.pottberg.ips.model;

import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

public class ImageData implements Comparable<ImageData> {

    private ObjectProperty<Image> imageProperty;
    private Path path;
    private ObjectProperty<LocalDate> creationDateProperty;

    public URI getUri() {
	return path.toUri();
    }

    public ImageData(LocalDate creationDate, Path path, Image image) {
	this.path = path;
	creationDateProperty = new SimpleObjectProperty<LocalDate>(creationDate);
	imageProperty = new SimpleObjectProperty<Image>(image);
    }

    public ImageData(LocalDate creationDate, Path path) {
	this(creationDate, path, null);
    }

    public ImageData(Path path) {
	this(null, path, null);
    }

    @Override
    public String toString() {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	return getCreationDate().format(formatter);
    }

    @Override
    public int compareTo(ImageData other) {
	return getCreationDate().compareTo(other.getCreationDate());
    }

    public boolean fitInImageGroup(ImageGroup currentGroup) {
	if (currentGroup == null) {
	    return false;
	}
	return getCreationDate().equals(currentGroup.getCreationDate());
    }

    public Path getPath() {
	return path;
    }

    public ObjectProperty<Image> imagePorperty() {
        return imageProperty;
    }

    public void setImage(Image image) {
        imageProperty.set(image);
    }

    public Image getImage() {
        return imageProperty.get();
    }

    public boolean hasCreationDate() {
        return getCreationDate() != null;
    }

    public ObjectProperty<LocalDate> creationDateProperty() {
	return creationDateProperty;
    }

    public void setCreationDate(LocalDate creationDate) {
	creationDateProperty.set(creationDate);
    }

    public LocalDate getCreationDate() {
        return creationDateProperty.get();
    }


}
