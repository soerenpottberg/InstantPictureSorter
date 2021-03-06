package org.pottberg.ips.model;

import static javafx.beans.binding.Bindings.createObjectBinding;
import static org.pottberg.ips.bindings.Binder.bindProperty;

import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

public class ImageData implements Comparable<ImageData> {

    private ObjectProperty<Image> imageProperty;
    private Path fileName;
    private ObjectProperty<LocalDate> creationDateProperty;
    private ObjectProperty<Directory> directoryProperty;
    private ObjectProperty<Path> directoryPathProperty;
    private ObjectProperty<Path> pathPropety;

    public URI getUri() {
	return getPath().toUri();
    }

    public ImageData(Path fileName, Directory directory) {
	this.fileName = fileName;
	creationDateProperty = new SimpleObjectProperty<>();
	imageProperty = new SimpleObjectProperty<>();
	directoryProperty = new SimpleObjectProperty<>(directory);
	pathPropety = new SimpleObjectProperty<>();
	directoryPathProperty = new SimpleObjectProperty<>();
	
	bindProperty(directoryProperty,
	    directoryPathProperty, Directory::pathProperty);

	pathPropety.bind(createObjectBinding(() -> {
	    return getDirectoryPath().resolve(getFileName());
	}, directoryPathProperty));
    }

    public Path getDirectoryPath() {
	return getDirectory().getPath();
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
	return pathPropety.get();
    }

    public ReadOnlyObjectProperty<Path> pathPropety() {
	return pathPropety;
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

    public ObjectProperty<Directory> directoryProperty() {
	return directoryProperty;
    }

    public void setDirectory(Directory directory) {
	directoryProperty.set(directory);
    }

    public Directory getDirectory() {
	return directoryProperty.get();
    }

    public Path getFileName() {
	return fileName;
    }

}
