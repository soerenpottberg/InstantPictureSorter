package org.pottberg.ips.model.loader.service;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.pottberg.ips.model.Year;
import org.pottberg.ips.model.loader.CategoryLoader;

public class CategoryLoaderService extends Service<ObservableList<Year>> {

    protected ObjectProperty<Path> dir = new SimpleObjectProperty<>();

    public ObjectProperty<Path> dirProperty() {
	return dir;
    }

    public void setDirectory(Path dir) {
	dirProperty().set(dir);
    }

    @Override
    protected Task<ObservableList<Year>> createTask() {
	return new CategoryLoader(dir.get());
    }

}
