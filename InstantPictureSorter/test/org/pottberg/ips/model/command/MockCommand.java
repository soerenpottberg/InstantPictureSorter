package org.pottberg.ips.model.command;

public class MockCommand implements Command {

    private State state;

    @Override
    public void execute() throws CommandExecutionException {
	state = State.DONE;
    }

    @Override
    public void undo() throws CommandExecutionException {
	state = State.UNDONE;
    }

    @Override
    public boolean isDone() {
	return state == State.DONE;
    }

    @Override
    public boolean isUnDone() {
	return state == State.UNDONE;
    }

    @Override
    public boolean isRunning() {
	return state == State.RUNNING;
    }

    @Override
    public boolean isFailed() {
	return state == State.FAILED;
    }

    @Override
    public String getName() {
	return null;
    }

    @Override
    public State getState() {
	return state;
    }

}
