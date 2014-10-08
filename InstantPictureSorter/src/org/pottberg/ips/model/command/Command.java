package org.pottberg.ips.model.command;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Worker;

public abstract class Command implements Worker<Void> {

    protected DoubleProperty progressProperty;
    private Command previousCommand;
    private Command nextCommand;
    private ObjectProperty<Throwable> exceptionProperty;
    private ObjectProperty<State> stateProperty;
    private DoubleProperty totalWorkProperty;
    private StringProperty titleProperty;
    private ObjectProperty<Void> valueProperty;
    private DoubleProperty workDoneProperty;
    private BooleanProperty runningProperty;
    private StringProperty messageProperty;

    public Command(Command previousCommand) {
	this.previousCommand = previousCommand;
	if (previousCommand != null) {
	    previousCommand.setNextCommand(this);
	}
	progressProperty = new SimpleDoubleProperty();
	exceptionProperty = new SimpleObjectProperty<>();
	stateProperty = new SimpleObjectProperty<>(State.READY);
	totalWorkProperty = new SimpleDoubleProperty(1);
	titleProperty = new SimpleStringProperty();
	valueProperty = new SimpleObjectProperty<>();
	workDoneProperty = new SimpleDoubleProperty(-1);
	runningProperty = new SimpleBooleanProperty();
	messageProperty = new SimpleStringProperty();
	progressProperty.bind(workDoneProperty.divide(totalWorkProperty));
    }

    protected abstract void updateApplicationState();

    protected abstract void updateFileSystem() throws IOException;

    protected abstract void revertApplicationState();

    protected abstract void revertFileSystem() throws IOException;

    public void execute() {
	if (getState() != State.READY) {
	    throw new IllegalStateException();
	}
	stateProperty.set(State.SCHEDULED);
	stateProperty.set(State.RUNNING);
	updateApplicationState();
	try {
	    updateFileSystem();
	} catch (IOException e) {
	    setException(e);
	    revertApplicationState();
	}
	stateProperty.set(State.SUCCEEDED);
    }

    public void undo() {
	if (getState() != State.SUCCEEDED) {
	    throw new IllegalStateException();
	}
	setReady();
	stateProperty.set(State.SCHEDULED);
	stateProperty.set(State.RUNNING);
	revertApplicationState();
	try {
	    revertFileSystem();
	} catch (IOException e) {
	    setException(e);
	    updateApplicationState();
	}
	stateProperty.set(State.SUCCEEDED);
	setReady();
    }

    private void setReady() {
	updateProgress(-1, 1);
	stateProperty.set(State.READY);
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

    private void setException(Exception e) {
	stateProperty.set(State.FAILED);
	exceptionProperty.set(e);
    }

    public boolean hasPreviousCommand() {
	return previousCommand != null;
    }

    public boolean hasNextCommand() {
	return nextCommand != null;
    }

    public Command getPreviousCommand() {
	return previousCommand;
    }

    public Command getNextCommand() {
	return nextCommand;
    }

    private void setNextCommand(Command nextCommand) {
	this.nextCommand = nextCommand;
    }

    @Override
    public boolean cancel() {
	stateProperty.set(State.CANCELLED);
	return true;
    }

    @Override
    public ReadOnlyObjectProperty<Throwable> exceptionProperty() {
	return exceptionProperty;
    }

    @Override
    public Throwable getException() {
	return exceptionProperty.get();
    }

    @Override
    public String getMessage() {
	return messageProperty.get();
    }

    @Override
    public double getProgress() {
	return progressProperty.get();
    }

    @Override
    public State getState() {
	return stateProperty.get();
    }

    @Override
    public String getTitle() {
	return titleProperty.get();
    }

    @Override
    public double getTotalWork() {
	return totalWorkProperty.get();
    }

    @Override
    public Void getValue() {
	return null;
    }

    @Override
    public double getWorkDone() {
	return workDoneProperty.get();
    }

    @Override
    public boolean isRunning() {
	return runningProperty.get();
    }

    @Override
    public ReadOnlyStringProperty messageProperty() {
	return messageProperty;
    }

    @Override
    public ReadOnlyDoubleProperty progressProperty() {
	return progressProperty;
    }

    @Override
    public ReadOnlyBooleanProperty runningProperty() {
	return runningProperty;
    }

    @Override
    public ReadOnlyObjectProperty<javafx.concurrent.Worker.State> stateProperty() {
	return stateProperty;
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
	return titleProperty;
    }

    @Override
    public ReadOnlyDoubleProperty totalWorkProperty() {
	return totalWorkProperty;
    }

    @Override
    public ReadOnlyObjectProperty<Void> valueProperty() {
	return valueProperty;
    }

    @Override
    public ReadOnlyDoubleProperty workDoneProperty() {
	return workDoneProperty;
    }

}
