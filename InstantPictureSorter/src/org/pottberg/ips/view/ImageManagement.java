package org.pottberg.ips.view;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;

import org.pottberg.ips.controller.CategoryBasedController;
import org.pottberg.ips.controller.ImageManagementController;

public class ImageManagement extends CategoryBasedView {

    private ImageManagementController controller;

    @Override
    protected CategoryBasedController getController() {
	if(controller == null) {
	    controller = new ImageManagementController();
	}
	return controller;
    }

    public ObjectProperty<Path> selectedSourcePathProperty() {
	return controller.selectedSourcePathProperty();
    }

}
