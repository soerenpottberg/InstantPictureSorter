package org.pottberg.ips.model.command;

import java.io.IOException;
import java.nio.file.Files;

import javafx.collections.ObservableList;

import org.pottberg.ips.model.YearDirectoy;

public class CreateYearDirectoryCommand extends Command{

    
    private ObservableList<YearDirectoy> yearDirectories;
    private YearDirectoy yearDirectory;

    public CreateYearDirectoryCommand(Command previousCommand, ObservableList<YearDirectoy> yearDirectories, YearDirectoy yearDirectory) {
	super(previousCommand);
	this.yearDirectories = yearDirectories;
	this.yearDirectory = yearDirectory;	
    }

    @Override
    protected void updateApplicationState() {
	 yearDirectories.add(yearDirectory);
    }

    @Override
    protected void updateFileSystem() throws IOException {
	Files.createDirectory(yearDirectory.getDirectory());
    }

    @Override
    protected void revertApplicationState() {
	yearDirectories.remove(yearDirectory);
    }

    @Override
    protected void revertFileSystem() throws IOException {
	Files.delete(yearDirectory.getDirectory());
    }

}
