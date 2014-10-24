package org.pottberg.ips.view;

import javafx.beans.property.DoubleProperty;

import org.pottberg.ips.controller.CategoryBasedController;
import org.pottberg.ips.controller.CategoryManagementController;

public class CategoryManagement extends CategoryBasedView {

    private CategoryManagementController controller;

    @Override
    protected CategoryBasedController getController() {
	if (controller == null) {
	    controller = new CategoryManagementController();
	}
	return controller;
    }
    
    public DoubleProperty progressProperty() {
	return controller.progressProperty();
    }

}
