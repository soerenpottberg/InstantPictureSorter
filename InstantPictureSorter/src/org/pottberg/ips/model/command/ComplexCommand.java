package org.pottberg.ips.model.command;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.pottberg.ips.model.History;

public abstract class ComplexCommand implements Command {

    private History<Command> commandList;

    protected DoubleProperty progressProperty;
    private ObjectProperty<State> stateProperty;
    private DoubleProperty totalWorkProperty;
    private DoubleProperty workDoneProperty;

    public ComplexCommand() {
	commandList = new History<>();
	progressProperty = new SimpleDoubleProperty();
	stateProperty = new SimpleObjectProperty<>(State.UNDONE);
	totalWorkProperty = new SimpleDoubleProperty(1);
	workDoneProperty = new SimpleDoubleProperty(-1);
	progressProperty.bind(workDoneProperty.divide(totalWorkProperty));
    }

    public void addCommand(Command command) {
	if (!isUnDone()) {
	    throw new IllegalStateException();
	}
	commandList.addToFuture(command);
    }

    @Override
    public void execute() throws CommandExecutionException {
	if (!isUnDone()) {
	    throw new IllegalStateException();
	}
	try {
	    stateProperty.set(State.RUNNING);
	    executeCommands();
	    stateProperty.set(State.DONE);
	} finally {
	    if (!isDone()) {
		stateProperty.set(State.FAILED);
		try {
		    undoCommands();
		    stateProperty.set(State.UNDONE);
		} catch (CommandExecutionException e) {
		}
	    }
	}
    }

    @Override
    public void undo() throws CommandExecutionException {
	if (!isDone()) {
	    throw new IllegalStateException();
	}
	try {
	    stateProperty.set(State.RUNNING);
	    undoCommands();
	    stateProperty.set(State.UNDONE);
	} finally {
	    if (!isUnDone()) {
		stateProperty.set(State.FAILED);
		try {
		    executeCommands();
		    stateProperty.set(State.DONE);
		} catch (CommandExecutionException e) {
		}
	    }
	}
    }

    private void executeCommands() throws CommandExecutionException {
	updateProgress(0, commandList.getLastIndex());
	while (commandList.hasNext()) {
	    Command command = commandList.getNext();
	    command.execute();
	    commandList.next();
	    updateProgress(commandList.getPreviousIndex(),
		commandList.getLastIndex());
	}
    }

    private void undoCommands() throws CommandExecutionException {
	while (commandList.hasPrevious()) {
	    Command command = commandList.getPrevious();
	    command.undo();
	    commandList.back();
	    updateProgress(commandList.getPreviousIndex(),
		commandList.getLastIndex());
	}
    }

    private void updateProgress(double workDone, double totalWork) {
	if (Platform.isFxApplicationThread()) {
	    workDoneProperty.set(workDone);
	    totalWorkProperty.set(totalWork);
	} else {
	    Platform.runLater(() -> {
		workDoneProperty.set(workDone);
		totalWorkProperty.set(totalWork);
	    });
	}

    }

    public double getTotalWork() {
	return totalWorkProperty.get();
    }

    public double getWorkDone() {
	return workDoneProperty.get();
    }

    public ReadOnlyDoubleProperty progressProperty() {
	return progressProperty;
    }

    public ReadOnlyDoubleProperty totalWorkProperty() {
	return totalWorkProperty;
    }

    public ReadOnlyDoubleProperty workDoneProperty() {
	return workDoneProperty;
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
