package org.pottberg.ips.controller;

import java.nio.file.Path;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.YearDirectoy;
import org.pottberg.ips.view.CategoryListCell;

public abstract class CategoryBasedController {

    @FXML
    protected ComboBox<YearDirectoy> yearsCombobox;

    @FXML
    protected ListView<Category> categoriesListView;

    protected ObjectProperty<Path> selectedTargetPathProperty;

    protected ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty;

    protected ObjectProperty<YearDirectoy> selectedYearDirectoryProperty;

    protected ObjectBinding<ObservableList<Category>> selectedYearCategories;

    protected ObjectProperty<Category> selectedCategoryProperty;

    public CategoryBasedController() {
	selectedTargetPathProperty = new SimpleObjectProperty<>();
	yearDirectoriesProperty = new SimpleObjectProperty<>();
	selectedYearDirectoryProperty = new SimpleObjectProperty<>();
	selectedCategoryProperty = new SimpleObjectProperty<>();
    }

    @FXML
    protected void initialize() {
	yearDirectoriesProperty.addListener((observableYearList, oldYearList,
	    newYearList) -> {
	    yearsCombobox.setItems(newYearList);
	    yearsCombobox.getSelectionModel()
		.selectLast();
	});

	categoriesListView.setCellFactory(param -> {
	    return new CategoryListCell();
	});

	selectedYearDirectoryProperty.bind(getSelectedItemProperty(yearsCombobox));
	selectedCategoryProperty.bind(getSelectedItemProperty(categoriesListView));

	selectedYearCategories = Bindings.createObjectBinding(() -> {
	    if (selectedYearDirectoryProperty.get() == null) {
		return null;
	    }
	    return selectedYearDirectoryProperty.get()
		.getCategories();
	}, selectedYearDirectoryProperty);

	categoriesListView.itemsProperty()
	    .bind(selectedYearCategories);
    }

    protected <T> ReadOnlyObjectProperty<T> getSelectedItemProperty(
	ComboBox<T> comboBox) {
	return comboBox.getSelectionModel()
	    .selectedItemProperty();
    }

    protected <T> ReadOnlyObjectProperty<T> getSelectedItemProperty(
	ListView<T> listView) {
	return listView.getSelectionModel()
	    .selectedItemProperty();
    }

    public ObjectProperty<Path> selectedTargetPathProperty() {
	return selectedTargetPathProperty;
    }

    public ObjectProperty<ObservableList<YearDirectoy>> yearDirectoriesProperty() {
	return yearDirectoriesProperty;
    }

}
