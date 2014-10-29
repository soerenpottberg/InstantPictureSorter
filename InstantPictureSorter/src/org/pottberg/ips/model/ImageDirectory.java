package org.pottberg.ips.model;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;

public interface ImageDirectory {

    public ObjectProperty<Path> pathProperty();

    public void setPath(Path path);

    public Path getPath();

}
