package org.pottberg.ips.model.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.pottberg.ips.model.Category;

public class RenameCategoryCommand extends Command{

    private Category category;
    private Path newDirectoy;
    private Path oldDirectory;

    public RenameCategoryCommand(Command previousCommand, Category category, Path newName) {
	super(previousCommand);
	this.category = category;
	oldDirectory = category.getDirectory();
	Path parentDirectory = oldDirectory.getParent();
        newDirectoy = parentDirectory.resolve(newName);	
    }

    @Override
    protected void updateApplicationState() {
	 category.setDirtectoy(newDirectoy);
    }

    @Override
    protected void updateFileSystem() throws IOException {
	Files.move(oldDirectory, newDirectoy);
    }

    @Override
    protected void revertApplicationState() {
	category.setDirtectoy(oldDirectory);
    }

    @Override
    protected void revertFileSystem() throws IOException {
	Files.move(newDirectoy, oldDirectory);
    }

}
