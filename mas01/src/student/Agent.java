package student;

import mas.agents.AbstractAgent;
import mas.agents.Message;
import mas.agents.SimulationApi;
import mas.agents.StringMessage;
import mas.agents.task.mining.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.TreeSet;



public class Agent extends AbstractAgent {
    public Agent(int id, InputStream is, OutputStream os, SimulationApi api) throws IOException, InterruptedException {
        super(id, is, os, api);
        state = new SearchState(this);
        otherAgents = new TreeSet();
        responses =0;
    }
    
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;

    // See also class StatusMessage
    public static String[] types = {
            "", "obstacle", "depot", "gold", "agent"
    };
    
    //agents representation of the map
    private int [][] agentMap;
    private int [][] tileMap;
    
    private int currentX;
    private int currentY;
    
    private TreeSet<Integer> otherAgents;
    private student.State state;
    private StatusMessage status;
    
    private boolean depoFound;
    private int depoX;
    private int depoY;
    public int responses;
    
    
    
    @Override
    public void act() throws Exception {
        sendMessage(1, new StringMessage("Hello")); 
        this.status = right();
        currentX = status.agentX;
        currentY = status.agentY;
        //initialize inner agent map - can move to unoccupied spaces
        agentMap = new int[status.width][status.height];
        for(int i=0;i<status.width;i++) {
        	for(int j=0;j<status.height;j++) {
        		agentMap[i][j] = 0;
        	}
        }
        
        while(messageAvailable()) {
    		Message m = readMessage();
    		int otherID =  m.getSender();
    		if(otherID != this.getAgentId()) {
    			otherAgents.add(otherID);
    		}
    	}
        
        for(Integer i : otherAgents) {
        	//sendMessage(i, new StringMessage(String.format(",id,%d",1)));
        	for(int j=0;j<otherAgents.size();j++) {
        		int targetID = (i+j)%(otherAgents.size()+1);
        		//if(targetID != 1) {
        			sendMessage(i, new StringMessage(String.format(",id,%d",targetID+1)));
        		//}
        	}
        }
        
        
         
        while(true) {
        	while(messageAvailable()) {
        		Message m = readMessage();
        		log("I have received " + m);
        		state = state.replyToMessage(m);
        	}
        	
        	for(Integer i : otherAgents) {
        		for(int j=0;j<otherAgents.size();j++) {
        			int targetID = (i+j)%(otherAgents.size()+1);
              			sendMessage(i, new StringMessage(String.format(",id,%d",targetID+1)));
              	}
             }
        	
        	
        	log(String.format("i know of other %d agents",otherAgents.size()));
        	state.doAction();
        	//this.doAction(nextAction);
        	
        	for(StatusMessage.SensorData data : status.sensorInput) {
        		state = state.replyToPercept(data.x, data.y, data.type);
        	//	sendMessage(i, new StringMessage(String.format(",%s,%d,%d", types[data.type], data.x, data.y)));
                		//sendMessage(i, new StringMessage(String.format(",%s,%d,%d", types[data.type], data.x, data.y)));
            }
            // REMOVE THIS BEFORE SUBMITTING YOUR SOLUTION TO BRUTE !!
            //   (this is meant just to slow down the execution a bit for demonstration purposes)
            try {
                Thread.sleep(300);
            } catch(InterruptedException ie) {}
        
        }
    }
    
    public void changeState(student.State newState) {
    	this.state = newState;
    }
    public boolean isAtLoc(int x, int y) {
    	if(currentX == x && currentY == y) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public void goToDepo() throws IOException {
    	goToTile(depoX, depoY);
    }
    
    public boolean isDepoLocated() {
    	return depoFound;
    }
    
    public void goToNextTile(int x, int y) throws IOException {
    	int relX = x - currentX;
    	int relY = y - currentY;
    	if(relX > 0) {
    		//desiredDirection =RIGHT; 
    		//moveInDirection(RIGHT);
    		status = this.right();
    	}
    	else if(relX < 0){
    		//desiredDirection =LEFT;
    		//moveInDirection(LEFT);
    		status = this.left();
    	}
    	
    	if(relY > 0) {
    		//desiredDirection =DOWN;
    		//moveInDirection(DOWN);
    		status = this.down();
    	}
    	else if(relY < 0) {
    		//desiredDirection =UP;
    		//moveInDirection(UP);
    		status = this.up();
    	}
    }
    
    public void goToTile(int x, int y) throws IOException {
    	if(false) {
    		
    	}
    	else {
    		int[][] tilemap = initializeTileMap(x,y);
    		int direction = findHighestPossibleValue(tilemap);
        	goInDirection(direction);
    	}
    	
    	//initialize tilemap
    	
    	//find highest value from neighbours
    	//if zero - recalculate tile map
    	//else go to highest value
    }
    
    private void goInDirection(int direction) throws IOException {
    	switch(direction) {
    	case 1:
    		status = right();
    		break;
    	case 2:
    		status = left();
    		break;
    	case 3:
    		status = down();
    		break;
    	case 4:
    		status = up();
    		break;
    	}
    }
    
    private int findHighestPossibleValue(int [][] tilemap) {
    	//List values = new ArrayList();
    	int val1 = 0;
    	int val2 = 0;
    	int val3 = 0;
    	int val4 = 0;
    	int comp1;
    	int comp2;
    	int direction1;
    	int direction2;
    	if(currentX != status.width-1) {
    		if(agentMap[currentX+1][currentY] != 1) {
    			//right
    			val1 = tilemap[currentX+1][currentY];
    		}
    	}
    	if(currentX != 0) {
    		if(agentMap[currentX-1][currentY] != 1) {
    			//left
    			val2 = tilemap[currentX-1][currentY];
    		}
    	}
    	if(currentY != status.height-1) {
    		if(agentMap[currentX][currentY+1] != 1) {
    			//down
    			val3 = tilemap[currentX][currentY+1];
    		}
    	}
    	if(currentY != 0) {
    		if(agentMap[currentX][currentY-1] != 1) {
    			//up
    			val4 = tilemap[currentX][currentY-1];
    		}
    	}
    	
    	if(val1 > val2) {
    		comp1 = val1;
    		direction1 = 1;
    	}
    	else {
    		comp1 = val2;
    		direction1 = 2;
    	}
    	if(val3 > val4) {
    		comp2 = val3;
    		direction2 = 3;
    	}
    	else {
    		comp2 = val4;
    		direction2 = 4;
    	}
    	if(comp1 > comp2 ) {
    		return direction1;
    	}
    	else {
    		return direction2;
    	}
    	
    }
    
    private int[][] initializeTileMap(int centerX, int centerY)	{
    	int [][] tileMap = new int[status.width][status.height];
    	//top
    	tileMap[centerX][centerY] = Math.max(status.width, status.height);
		for(int j=1;j<centerY+1;j++) {
			for(int k=-j;k<(j)+1;k++) {
				if( 0 <= centerX+k && centerX+k < status.width) {
					tileMap[centerX+k][centerY-j] = tileMap[centerX][centerY] -j;
				}
			}
		
    	}
		for(int j=1;j<centerX+1;j++) {
			for(int k=-j;k<(j)+1;k++) {
				if( 0 <= centerY+k && centerY+k < status.height) {
					tileMap[centerX-j][centerY+k] = tileMap[centerX][centerY] -j;
				}
			}
		
    	}
		for(int j=1;j<status.height-(centerY);j++) {
			for(int k=-j;k<(j)+1;k++) {
				if( 0 <= centerX+k && centerX+k < status.width) {
					tileMap[centerX+k][centerY+j] = tileMap[centerX][centerY] -j;
				}
			}
		
    	}
		for(int j=1;j<status.width - (centerX );j++) {
			for(int k=-j;k<(j)+1;k++) {
				if( 0 <= centerY+k && centerY+k < status.height) {
					tileMap[centerX+j][centerY+k] = tileMap[centerX][centerY] -j;
				}
			}
		
    	}
    	return tileMap;
    }
     
    public void addObstacle(int x, int y) {
    	agentMap[x][y] = 1;
    }
    
    public void depoFoundAt(int x, int y) {
    	depoFound = true;
    	depoX = x;
    	depoY = y;
    }
    
    public void sendToOthers(Message m) throws IOException {
    	for(Integer i : otherAgents) {
    		this.sendMessage(i, m);
    	}
    }
    
    public boolean isNextToLoc(int x, int y) {
    	int relX = Math.abs(x-currentX);
    	int relY = Math.abs(y-currentY);
    	if(relX + relY == 1) {
    		return true;
    	}
    	else {
    		return false;
    	}
    	
    }
    
    public void addFriendlyAgent(int id) {
    	otherAgents.add(id);
    }
    
    private void checkIntercepts() throws Exception {
    	for(StatusMessage.SensorData data : status.sensorInput) {
    		state = state.replyToPercept(data.x, data.y, data.type);
        }
    }
    
    public boolean knowsOfObstacle(int x, int y) {
    	if(this.agentMap[x][y] == 1) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public void exploreInCircle(int step) throws Exception {
    	if(step%2 ==0) {
    		for(int i =0;i<step;i++) {
    			status = left();
    			checkIntercepts();
    		}
    		for(int i =0;i<step;i++) {
    			status = down();
    			checkIntercepts();
    		}
    		
    	}
    	else {
    		for(int i =0;i<step;i++) {
    			status = right();
    			checkIntercepts();
    		}
    		for(int i =0;i<step;i++) {
    			status = up();
    			checkIntercepts();
    		}
    	}
    }
}
	
	
  
  

