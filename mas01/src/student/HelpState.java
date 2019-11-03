package student;

import java.io.IOException;

import mas.agents.Message;
import mas.agents.ReadyMessage;

public class HelpState extends State{
	
	private Agent parentAgent;
	private int x;
	private int y;
	private int ID;
	
	public HelpState(int goldX, int goldY, int requesterID, Agent agent) {
		x = goldX;
		y = goldY;
		ID = requesterID;
		parentAgent = agent;
	}
	
	@Override
	public void doAction() throws IOException {
		
		if(parentAgent.isNextToLoc(x,y)) {
			// send confirm message to request agent
			ReadyMessage m = new ReadyMessage();
			parentAgent.sendMessage(ID, m);
			//change state
		}
		else {
			parentAgent.goToTile(x, y);
			//go to loc
		}
	}
	
	@Override
	public State replyToMessage(Message m) {
		//helping other agent, reply with negative to help requests
		String mes = m.toString();
        String[] splitList = mes.split(",");
        String message = splitList[1];
		if(message == "obstacle") {
        	int x = Integer.parseInt(splitList[2]);
        	int y = Integer.parseInt(splitList[3]);
        	parentAgent.addObstacle(x,y);
		}
		else if(message == "goldPicked") {
			return new SearchState(parentAgent);
		}
		return this;
		
	}
	@Override
	public String toString() {
		return "help";
	}
	
	@Override
	public State replyToPercept(int perceptX, int perceptY, int type) {
		//StatusMessage status = parentAgent.sense();
		//handle percepts - if no agent is at goldx,y, change to search state
		return this;
	}

}
