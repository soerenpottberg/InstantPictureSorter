package org.pottberg.ips.model.command;

import java.io.IOException;
import java.nio.file.Files;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.YearDirectoy;

public class CreateCategoryCommand extends SimpleCommand {

    private YearDirectoy yearDirectory;
    private Category category;

    public CreateCategoryCommand(YearDirectoy yearDirectory, Category category) {
	this.yearDirectory = yearDirectory;
	this.category = category;
    }

    @Override
    protected void updateApplicationState() {
	yearDirectory.addCategory(category);
    }

    @Override
    protected void updateFileSystem() throws IOException {
	Files.createDirectory(category.getDirectory());
    }

    @Override
    protected void revertApplicationState() {
	yearDirectory.removeCategory(category);
    }

    @Override
    protected void revertFileSystem() throws IOException {
	Files.delete(category.getDirectory());
    }

    @Override
    public String getName() {
	return String.format("Create Category \"%s\"", category.getName());
    }

}
