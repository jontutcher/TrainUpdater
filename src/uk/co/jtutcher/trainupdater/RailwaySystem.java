package uk.co.jtutcher.trainupdater;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.jtutcher.trainupdater.Train.Direction;

public class RailwaySystem {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ArrayList<Train> trains;
	ArrayList<TrackCircuit> circuits;
//	private Track track;
	private String name;
	
	public String getName() {
		return name;
	}

	public void addTrain(Train train)
	{
		logger.trace("Adding train {} to railway {}", train, name);
		trains.add(train);
	}
	
	public void removeTrain(Train t)
	{
		trains.remove(t);
	}
	
	public RailwaySystem(ArrayList<TrackCircuit> circuits, String name)
	{
		this.name = name;
		this.trains = new ArrayList<Train>();
		this.circuits = circuits;
	}
	
	public ArrayList<Train> getTrains()
	{
		return trains;
	}
	
	public void moveTrains()
	{
		logger.trace("Moving trains on railway {}", name);
		String log = "";
		Iterator<Train> itr = trains.iterator();
		while(itr.hasNext())
		{
			Train train = itr.next();
			int curIndex=0;
			switch(train.direction)
			{
			case UP:
				//get the index of where we currently are
				curIndex = circuits.indexOf(train.trackCircuit);
				//we want to move up
				if(curIndex<(circuits.size()-1))
				{
					train.trackCircuit = circuits.get(curIndex+1);
				} else {
					train.direction = Direction.DOWN;
					train.trackCircuit = circuits.get(circuits.size()-1);
				}
				break;
			case DOWN:
				//get the index of where we currently are
				curIndex = circuits.indexOf(train.trackCircuit);
				//we want to move down
				if(curIndex>0)
				{
					train.trackCircuit = circuits.get(curIndex-1);
				} else {
					train.direction = Direction.UP;
					train.trackCircuit = circuits.get(0);
				}
			}
			log += '[' + train.code + ' ' + train.trackCircuit.getMid() + ']';
			
		}
		logger.info("Moving trains on railway {}: {} ", name, log);
	}
}

	