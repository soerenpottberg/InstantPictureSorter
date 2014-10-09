package org.pottberg.ips.controller;

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
import org.pottberg.ips.model.Year;
import org.pottberg.ips.view.CategoryListCell;

public abstract class CategoryBasedController {

    @FXML
    protected ComboBox<Year> yearsCombobox;

    @FXML
    protected ListView<Category> categoriesListView;

    protected ObjectProperty<ObservableList<Year>> yearListProperty;

    protected ObjectProperty<Year> selectedYear;

    protected ObjectBinding<ObservableList<Category>> selectedYearCategories;

    protected ObjectProperty<Category> selectedCategory;

    public CategoryBasedController() {
	yearListProperty = new SimpleObjectProperty<>();
	selectedYear = new SimpleObjectProperty<>();
	selectedCategory = new SimpleObjectProperty<>();
    }

    @FXML
    protected void initialize() {
	yearListProperty.addListener((observableYearList, oldYearList,
	    newYearList) -> {
	    yearsCombobox.setItems(newYearList);
	    yearsCombobox.getSelectionModel()
		.selectLast();
	});
	
	categoriesListView.setCellFactory(param -> {
	    return new CategoryListCell();
	});

	selectedYear.bind(getSelectedItemProperty(yearsCombobox));
	selectedCategory.bind(getSelectedItemProperty(categoriesListView));

	selectedYearCategories = Bindings.createObjectBinding(() -> {
	    if (selectedYear.get() == null) {
		return null;
	    }
	    return selectedYear.get()
		.getCategories();
	}, selectedYear);

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
    
    public ObjectProperty<ObservableList<Year>> yearListProperty() {
	return yearListProperty;
    }

}
