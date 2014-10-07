package org.pottberg.ips.model.loader.service;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.pottberg.ips.model.ImageGroup;
import org.pottberg.ips.model.loader.ImageGroupLoader;

public class ImageGroupLoaderService extends Service<ObservableList<ImageGroup>> {

    protected ObjectProperty<Path> dir = new SimpleObjectProperty<>();

    public ObjectProperty<Path> dirProperty() {
	return dir;
    }

    public void setDirectory(Path dir) {
	dirProperty().set(dir);
    }

    @Override
    protected Task<ObservableList<ImageGroup>> createTask() {
	return new ImageGroupLoader(dir.get());
    }

}
