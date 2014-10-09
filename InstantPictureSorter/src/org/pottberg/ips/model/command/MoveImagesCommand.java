package org.pottberg.ips.model.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.ImageGroup;

public class MoveImagesCommand extends Command {

    private List<Command> commands;

    public MoveImagesCommand(Command previousCommand,
	List<ImageData> imageDataList, ImageGroup source, Category target) {
	super(previousCommand);
	commands = new ArrayList<>();
	for (ImageData imageData : imageDataList) {
	    commands.add(new MoveImageCommand(null, imageData, source, target));
	}
    }

    @Override
    protected void updateApplicationState() {
	for (Command command : commands) {
	    command.updateApplicationState();
	}
    }

    @Override
    protected void updateFileSystem() throws IOException {
	for (Command command : commands) {
	    try {
		command.updateFileSystem();
	    } catch (IOException e) {
		tryRevertFileSystem();
		throw e;
	    }
	}
    }

    private void tryRevertFileSystem() {
	for (Command command : commands) {
	    try {
		command.revertFileSystem();
	    } catch (IOException e) {
	    }
	}
    }

    @Override
    protected void revertApplicationState() {
	for (Command command : commands) {
	    command.revertApplicationState();
	}
    }

    @Override
    protected void revertFileSystem() throws IOException {
	for (Command command : commands) {
	    try {
		command.revertFileSystem();
	    } catch (IOException e) {
		tryUpdateFileSystem();
		throw e;
	    }
	}
    }

    private void tryUpdateFileSystem() {
	for (Command command : commands) {
	    try {
		command.updateFileSystem();
	    } catch (IOException e) {
	    }
	}
    }

}
