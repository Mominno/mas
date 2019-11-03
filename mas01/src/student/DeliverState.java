package student;

import java.io.IOException;

import mas.agents.Message;
import mas.agents.StringMessage;
import mas.agents.task.mining.StatusMessage;

public class DeliverState extends State {
	
	private Agent parentAgent;
	
	public DeliverState(Agent agent) {
		parentAgent = agent;
	}
	
	@Override
	public void doAction() throws Exception {
		StatusMessage status = parentAgent.sense();
		//look if at depo
		if(status.isAtDepot()) {
			try {
				parentAgent.drop();
			} catch (IOException e) {
				e.printStackTrace();
			}
			parentAgent.changeState(new SearchState(parentAgent));
		}
		else{
			parentAgent.goToDepo();
		}

	}
	@Override
	public State replyToMessage(Message m) throws IOException {
		//no to help and receive none
		m.replyWith(new StringMessage("no"));
		return this;
	}
	@Override	
	public State replyToPercept(int x, int y, int type) {
		switch(type) {
			case StatusMessage.OBSTACLE:
				parentAgent.addObstacle(x, y);
				return this;
			default:
				return this;
		}
	}
	@Override
	public String toString() {
		return "delivery";
	}
}
