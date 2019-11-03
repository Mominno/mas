package student;

import java.io.IOException;

import mas.agents.Message;
import mas.agents.StringMessage;
import mas.agents.task.mining.StatusMessage;

public class SearchState extends State{
	
	private Agent parentAgent;
	private int searchStep;
	
	public SearchState(Agent agent) {
		parentAgent = agent;
		searchStep = 1;
	}
		
	@Override
	public void doAction() throws Exception {
		//explore randomly
		//parentAgent.right();
		//todo
		parentAgent.exploreInCircle(searchStep);
		searchStep++;
	}
	
	@Override
	public State replyToMessage(Message m) throws Exception {
		//if help message
		String mes = m.toString();
        String[] splitList = mes.split(",");
        String message = splitList[1];
        int x;
        int y;
        switch(message) {
        case "comeHelp":
        	x = Integer.parseInt(splitList[2]);
        	y = Integer.parseInt(splitList[3]);
        	return new HelpState(x,y,m.getSender(),parentAgent);
       
        case "obstacle":
        	x = Integer.parseInt(splitList[2]);
        	y = Integer.parseInt(splitList[3]);
        	parentAgent.addObstacle(x,y);
        	break;
        
        case "depo":
        	x = Integer.parseInt(splitList[2]);
        	y = Integer.parseInt(splitList[3]);
        	parentAgent.depoFoundAt(x,y);
        	break;
        
        case "available":
        	m.replyWith(new StringMessage("yes"));
        	break;
        
        case "id":
        	int id = Integer.parseInt(splitList[2]);
        	//parentAgent.log("adding friendly agent");
        	parentAgent.addFriendlyAgent(id);
        	//m.replyWith(new StringMessage(",idReceived"));
        	break;
        case "idReceived":
        	parentAgent.responses++;
        	break;
        }
        
		return this;
	}
	@Override
	public State replyToPercept(int x, int y, int type) throws Exception {
		Message m;
		switch(type) {
		case StatusMessage.AGENT:
			return this;
		case StatusMessage.OBSTACLE:
			if(!parentAgent.knowsOfObstacle(x,y)) {
				m = new StringMessage(String.format(",obstacle,%d,%d", x,y));
				parentAgent.sendToOthers(m);
				parentAgent.addObstacle(x,y);
				parentAgent.log("I see rock");
			}
			
			return this;
		case StatusMessage.DEPOT:
			m = new StringMessage(String.format(",depo,%d,%d", x,y));
			parentAgent.sendToOthers(m);
			parentAgent.depoFoundAt(x,y);
			parentAgent.log("I see depo");
			return this;
		case StatusMessage.GOLD:
			//parentAgent.log("I see gold but no depo");
			if(parentAgent.isDepoLocated()) {
				return new PickState(x, y, parentAgent);
			}
			else {
				return this;
			}
		default:
			return this;
		}
	}
	
	@Override
	public String toString() {
		return "search";
	}
}
