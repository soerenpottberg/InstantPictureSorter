package org.pottberg.ips.model.loader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.SimpleCategory;
import org.pottberg.ips.model.YearDirectoy;

public class CategoryLoader extends Task<ObservableList<YearDirectoy>> {

    private final Path dir;

    public CategoryLoader(Path dir) {
	this.dir = dir;
    }

    @Override
    protected ObservableList<YearDirectoy> call() throws Exception {
	ObservableList<YearDirectoy> years = FXCollections.observableArrayList();
	try (DirectoryStream<Path> yearStream = Files.newDirectoryStream(dir,
	    "[0-9][0-9][0-9][0-9]")) {
	    for (Path yearPath : yearStream) {
		if (isCancelled()) {
		    return null;
		}
		YearDirectoy year = new YearDirectoy(yearPath);
		loadCategories(year, yearPath);
		if (isCancelled()) {
		    return null;
		}
		years.add(year);
	    }
	}
	return years;
    }

    private void loadCategories(YearDirectoy year, Path yearPath) throws IOException {
	try (DirectoryStream<Path> categoryStream = Files.newDirectoryStream(yearPath)) {
	    for (Path categoryPath : categoryStream) {
		if (isCancelled()) {
		    return;
		}
		if (!Files.isDirectory(categoryPath)) {
		    continue;
		}
		Category category = new SimpleCategory(categoryPath);
		year.addCategory(category);
	    }
	}
    }

}
