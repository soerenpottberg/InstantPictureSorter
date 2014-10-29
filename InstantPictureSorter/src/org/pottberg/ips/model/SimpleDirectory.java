package org.pottberg.ips.model;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SimpleDirectory implements Directory {
    
    private ObjectProperty<Path> pathProperty;
    
    public SimpleDirectory(Path path) {
	pathProperty = new SimpleObjectProperty<>(path);
    }

    public ObjectProperty<Path> pathProperty() {
	return pathProperty;
    }

    public void setPath(Path path) {
	pathProperty.set(path);
    }
    
    public Path getPath() {
	return pathProperty.get();
    }

}
