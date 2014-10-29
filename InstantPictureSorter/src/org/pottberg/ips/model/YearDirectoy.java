package org.pottberg.ips.model;

import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class YearDirectoy extends SimpleDirectory {

    private int year;
    private ObservableList<Category> categories;

    public YearDirectoy(Path path) {
	super(path);
	year = Integer.parseInt(getPath().getFileName()
	    .toString());
	categories = FXCollections.observableArrayList();
    }

    public YearDirectoy(Path parentDirectory, int year) {
	super(parentDirectory.resolve(String.valueOf(year)));
	this.year = year;
	categories = FXCollections.observableArrayList();
    }

    public int getYear() {
	return year;
    }

    public void setYear(int year) {
	this.year = year;
    }

    @Override
    public String toString() {
	return String.valueOf(year);
    }

    public void addCategory(Category category) {
	getCategories().add(category);
    }

    public void removeCategory(Category category) {
        getCategories().remove(category);
    }

    public ObservableList<Category> getCategories() {
	return categories;
    }
}
