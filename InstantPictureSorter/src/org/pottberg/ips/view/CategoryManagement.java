package org.pottberg.ips.view;

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

}
