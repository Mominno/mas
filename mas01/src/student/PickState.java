package student;

import java.io.IOException;

import mas.agents.Message;
import mas.agents.ReadyMessage;
import mas.agents.StringMessage;

public class PickState extends State {
	
	private int x;
	private int y;
	private Agent parentAgent;
	private int helperID;
	private int timer;
	
	public PickState(int goldX, int goldY, Agent agent) {
		x = goldX;
		y = goldY;
		parentAgent = agent;
		helperID = -1;
		timer = 0;
	}
	
	@Override
	public void doAction() throws Exception {
		//if waiting 
		if(parentAgent.isAtLoc(x,y)) {
			if(timer < 10) {
				Message m = new StringMessage(String.format("available"));
				parentAgent.sendToOthers(m);
				parentAgent.log(String.format("im waiting for another %d",timer));
				timer++;
			}
			else {
				parentAgent.changeState(new SearchState(parentAgent));
			}
		}
		else {
			//go to loc
			parentAgent.goToTile(x, y);
		}
		
	}
	
	@Override
	public State replyToMessage(Message m) throws IOException {
		//check replies to cries for help
		// if positive - wait to recieve ready msg
		//on ready msg - pick - change to delivery state and send msg to the other agent to free it
		//if help message
		String mes = m.toString();
        String[] splitList = mes.split(",");
        String message = splitList[1];
        
        if(message == "obstacle") {
        	int x = Integer.parseInt(splitList[2]);
        	int y = Integer.parseInt(splitList[3]);
        	parentAgent.addObstacle(x,y);
        }
        else if(message == "available") {
        	if(timer>5) {
        		m.replyWith(new StringMessage(",yes"));
        	}
        }
        else if(message == "comeHelp") {
        	if(timer > 5) {
        		int x = Integer.parseInt(splitList[2]);
        		int y = Integer.parseInt(splitList[3]);
        		return new HelpState(x,y,m.getSender(),parentAgent);
        	}
        }
        else if(message == "yes") {
        	if(this.helperID == -1) {
        		StringMessage newMessage = new StringMessage(String.format(",comeHelp,%d,%d",x,y));
        	    m.replyWith(newMessage);
        		this.helperID = m.getSender();
        	}
        }
        else if(m instanceof ReadyMessage) {
        	parentAgent.pick();
        	StringMessage newMessage = new StringMessage(",goldPicked");
        	m.replyWith(newMessage);
        	return new DeliverState(parentAgent);
        }
		return this;
	}
	
	@Override
	public String toString() {
		return "pick";
	}
	
	@Override
	public State replyToPercept(int x, int y, int type) {
		return this;
	}
}
