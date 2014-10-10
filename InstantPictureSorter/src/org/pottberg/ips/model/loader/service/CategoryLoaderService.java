package org.pottberg.ips.model.loader.service;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.pottberg.ips.model.YearDirectoy;
import org.pottberg.ips.model.loader.CategoryLoader;

public class CategoryLoaderService extends Service<ObservableList<YearDirectoy>> {

    protected ObjectProperty<Path> dir = new SimpleObjectProperty<>();

    public ObjectProperty<Path> dirProperty() {
	return dir;
    }

    public void setDirectory(Path dir) {
	dirProperty().set(dir);
    }

    @Override
    protected Task<ObservableList<YearDirectoy>> createTask() {
	return new CategoryLoader(dir.get());
    }

}
