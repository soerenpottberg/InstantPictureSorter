package org.pottberg.ips.controller;

import javafx.fxml.FXML;

import org.pottberg.ips.view.CategoryManagement;
import org.pottberg.ips.view.ImageManagement;

public class MainController {

    @FXML
    private CategoryManagement categoryManagement;

    @FXML
    private ImageManagement imageManagement;

    @FXML
    private void initialize() {
	imageManagement.yearListProperty()
	    .bind(categoryManagement.yearListProperty());
    }

}
