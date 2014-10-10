package org.pottberg.ips.model;

import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class YearDirectoy {

    private Path directory;
    private int year;
    private ObservableList<Category> categories;

    public YearDirectoy(Path directory) {
	this.directory = directory;
	year = Integer.parseInt(directory.getFileName()
	    .toString());
	categories = FXCollections.observableArrayList();
    }

    public YearDirectoy(Path parentDirectory, int year) {
	this.directory = parentDirectory.resolve(String.valueOf(year));
	this.year = year;
	categories = FXCollections.observableArrayList();
    }

    public Path getDirectory() {
	return directory;
    }

    public void setDirectory(Path directory) {
	this.directory = directory;
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
