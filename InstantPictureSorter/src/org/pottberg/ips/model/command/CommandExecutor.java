package org.pottberg.ips.model.command;

import org.pottberg.ips.model.History;

public class CommandExecutor {

    private History<Command> commandHistory;

    public CommandExecutor() {
	commandHistory = new History<>();
    }

    public void execute(Command command) throws CommandExecutionException {
	command.execute();
	commandHistory.addToHistory(command);
    }

    public void undo() throws CommandExecutionException {
	if (!commandHistory.hasPrevious()) {
	    return;
	}

	Command command = commandHistory.getPrevious();

	command.undo();
	commandHistory.back();
    }

    public void redo() throws CommandExecutionException {
	if (!commandHistory.hasNext()) {
	    return;
	}

	Command command = commandHistory.getNext();

	command.execute();
	commandHistory.next();

    }

}
