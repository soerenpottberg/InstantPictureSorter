package org.pottberg.ips.model.command;

import java.io.IOException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class SimpleCommand implements Command {

    protected DoubleProperty progressProperty;
    private ObjectProperty<State> stateProperty;
    private DoubleProperty totalWorkProperty;
    private DoubleProperty workDoneProperty;

    public SimpleCommand() {
	progressProperty = new SimpleDoubleProperty();
	stateProperty = new SimpleObjectProperty<>(State.UNDONE);
	totalWorkProperty = new SimpleDoubleProperty(1);
	workDoneProperty = new SimpleDoubleProperty(-1);
	progressProperty.bind(workDoneProperty.divide(totalWorkProperty));
    }

    protected abstract void updateApplicationState();

    protected abstract void updateFileSystem() throws IOException;

    protected abstract void revertApplicationState();

    protected abstract void revertFileSystem() throws IOException;

    @Override
    public void execute() throws CommandExecutionException {
	if (!isUnDone()) {
	    throw new IllegalStateException();
	}
	stateProperty.set(State.RUNNING);
	updateApplicationState();
	try {
	    updateFileSystem();
	} catch (IOException e) {
	    revertApplicationState();
	    stateProperty.set(State.UNDONE);
	    throw new CommandExecutionException(e);
	}
	stateProperty.set(State.DONE);
    }

    @Override
    public void undo() throws CommandExecutionException {
	if (!isDone()) {
	    throw new IllegalStateException();
	}
	stateProperty.set(State.RUNNING);
	revertApplicationState();
	try {
	    revertFileSystem();
	} catch (IOException e) {
	    updateApplicationState();
	    stateProperty.set(State.DONE);
	    throw new CommandExecutionException(e);
	}
	stateProperty.set(State.UNDONE);
    }

    @Override
    public State getState() {
	return stateProperty.get();
    }

    @Override
    public boolean isDone() {
	return getState() == State.DONE;
    }

    @Override
    public boolean isUnDone() {
	return getState() == State.UNDONE;
    }

    @Override
    public boolean isRunning() {
	return getState() == State.RUNNING;
    }

    @Override
    public boolean isFailed() {
	return getState() == State.FAILED;
    }

}
