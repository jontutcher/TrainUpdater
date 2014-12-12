package uk.co.jtutcher.trainupdater;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.jtutcher.trainupdater.Train.Direction;

/**
 * Class for a 'railway system' --- a line, with track circuits, and locations, and trains, and
 * their behaviour in normal operation. 
 * Provides basic train movement functionality.
 * @author Jon Tutcher
 *
 */
public class RailwaySystem {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ArrayList<Train> trains;
	ArrayList<TrackCircuit> circuits;
	private String name;
	
	public String getName() {
		return name;
	}

	/** 
	 * Add a train to the railway line
	 * @param train Train to add. 
	 */
	public void addTrain(Train train)
	{
		logger.trace("Adding train {} to railway {}", train, name);
		trains.add(train);
	}
	
	/**
	 * Remove train from the railway line
	 * @param train Train to remove
	 */
	public void removeTrain(Train train)
	{
		trains.remove(train);
	}
	
	/**
	 * Construct new railway system(line) using a list of track circuits
	 * @param circuits Array of track circuits used to construct the line
	 * (assumes ordered list with all connected)
	 * @param name Name of railway line (currently for debugging only)
	 */
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
	
	/**
	 * Move trains along track according to defined behaviour.
	 * Usually, on each step, each train will move one track circuit.
	 * Trains reaching the end of the line will turn round
	 * 
	 */
	public void moveTrains()
	{
		logger.trace("Moving trains on railway {}", name);
		String log = "";
		Iterator<Train> itr = trains.iterator();
		//go through each train
		while(itr.hasNext())
		{
			Train train = itr.next();
			int curIndex=0;
			switch(train.direction)
			{
			// handle trains going in both directions. Verbose way of doing it, but works
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
			//compile list of where we've put each train!
			log += '[' + train.code + ' ' + train.trackCircuit.getMid() + ']';
			
		}
		logger.info("Moving trains on railway {}: {} ", name, log);
	}
}

	