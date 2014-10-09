package org.pottberg.ips.model.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.ImageGroup;

public class MoveImageCommand extends Command {

    private ImageGroup source;
    private Category target;
    private Path sourceDirectory;

    private Path targetDirectory;
    private ImageData imageData;
    private Path sourcePath;
    private Path targetPath;

    public MoveImageCommand(Command previousCommand, ImageData imageData,
	ImageGroup source, Category target) {
	super(previousCommand);
	this.imageData = imageData;
	this.source = source;
	this.target = target;
	Path filePath = imageData.getPath();
	Path fileName = filePath.getFileName();
	sourceDirectory = filePath.getParent();
	targetDirectory = target.getDirectory();
	sourcePath = sourceDirectory.resolve(fileName);
	targetPath = targetDirectory.resolve(fileName);
    }

    @Override
    protected void updateApplicationState() {
	source.getImageDataList()
	    .remove(imageData);
	target.getImageDataList()
	    .add(imageData);
	imageData.setPath(targetPath);
    }

    @Override
    protected void updateFileSystem() throws IOException {
	Files.move(sourcePath, targetPath);
    }

    @Override
    protected void revertApplicationState() {
	target.getImageDataList()
	    .remove(imageData);
	source.getImageDataList()
	    .add(imageData);
	imageData.setPath(sourcePath);
    }

    @Override
    protected void revertFileSystem() throws IOException {
	Files.move(targetPath, sourcePath);
    }
}