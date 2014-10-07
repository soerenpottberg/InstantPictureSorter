package org.pottberg.ips.model;

import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Year {

    private Path entry;
    private int year;
    private ObservableList<Category> categories;

    public Year(Path entry) {
	this.setEntry(entry);
	this.setYear(Integer.parseInt(entry.getFileName()
	    .toString()));
	categories = FXCollections.observableArrayList();
    }

    public Path getEntry() {
	return entry;
    }

    public void setEntry(Path entry) {
	this.entry = entry;
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

    public ObservableList<Category> getCategories() {
	return categories;
    }

}
