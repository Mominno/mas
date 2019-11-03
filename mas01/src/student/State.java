package student;

import java.io.IOException;

import mas.agents.Message;

public abstract class State {
	
	public abstract void doAction() throws Exception;
	public abstract State replyToMessage(Message m) throws Exception;
	public abstract State replyToPercept(int x, int y, int type) throws Exception;

}
