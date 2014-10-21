package org.pottberg.ips.model.command;

import org.pottberg.ips.model.History;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CommandExecutor {

    private History<Command> commandHistory;

    private ObjectProperty<CommandExecutionException> exceptionProperty;

    public CommandExecutor() {
	commandHistory = new History<>();
	exceptionProperty = new SimpleObjectProperty<>();
    }

    public void execute(Command command) {
	try {
	    command.execute();
	    commandHistory.addToHistory(command);
	} catch (CommandExecutionException e) {
	    exceptionProperty.set(e);
	}
    }

    public void undo() {
	if (!commandHistory.hasPrevious()) {
	    return;
	}

	Command command = commandHistory.getPrevious();

	try {
	    command.undo();
	    commandHistory.back();
	} catch (CommandExecutionException e) {
	    exceptionProperty.set(e);
	}
    }

    public void redo() {
	if (!commandHistory.hasNext()) {
	    return;
	}

	Command command = commandHistory.getNext();

	try {
	    command.execute();
	    commandHistory.next();
	} catch (CommandExecutionException e) {
	    exceptionProperty.set(e);
	}
    }

}
