package org.pottberg.ips.model.command;



public interface Command {
    
    public enum State {
	DONE, UNDONE, RUNNING, FAILED;
    }

    public void execute() throws CommandExecutionException;

    public void undo() throws CommandExecutionException;
    
    public boolean isDone();
    
    public boolean isUnDone();

    public boolean isRunning();
    
    public boolean isFailed();
    
    public String getName();

    public State getState();

}
