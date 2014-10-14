package org.pottberg.ips.view;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

import org.pottberg.ips.controller.CategoryBasedController;
import org.pottberg.ips.controller.MainController;
import org.pottberg.ips.model.YearDirectoy;

public abstract class CategoryBasedView extends View {

    @Override
    protected abstract CategoryBasedController getController();

    public ObjectProperty<Path> selectedTargetPathProperty() {
	return getController().selectedTargetPathProperty();
    }

    public ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty() {
	return getController().yearDirectoriesProperty();
    }

    public void setMainController(MainController mainController) {
	getController().setMainController(mainController);
    }
}
