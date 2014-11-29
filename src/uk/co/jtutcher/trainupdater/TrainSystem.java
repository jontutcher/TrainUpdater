package uk.co.jtutcher.trainupdater;

import java.util.ArrayList;
import java.util.Iterator;

import uk.co.jtutcher.trainupdater.Train.Dir;

public class TrainSystem {
	private ArrayList<Train> trains;
	ArrayList<TrackCircuit> circuits;
	private Track track;
	
	public void addTrain(Train t)
	{
		trains.add(t);
	}
	
	public void removeTrain(Train t)
	{
		trains.remove(t);
	}
	
	public TrainSystem(ArrayList<TrackCircuit> circuits)
	{
		this.trains = new ArrayList<Train>();
		this.circuits = circuits;
	}
	
	public ArrayList<Train> getTrains()
	{
		return trains;
	}
	
	public void moveTrains()
	{
		Iterator<Train> itr = trains.iterator();
		String log = "";
		while(itr.hasNext())
		{
			Train t = itr.next();
			//t.doMove();
			int curIndex=0;
			switch(t.dir)
			{
			case UP:
				//get the index of where we currently are
				curIndex = circuits.indexOf(t.tc);
				//we want to move up
				if(curIndex<(circuits.size()-1))
				{
					t.tc = circuits.get(curIndex+1);
				} else {
					t.dir = Dir.DOWN;
					t.tc = circuits.get(circuits.size()-1);
				}
				break;
			case DOWN:
				//get the index of where we currently are
				curIndex = circuits.indexOf(t.tc);
				//we want to move down
				if(curIndex>0)
				{
					t.tc = circuits.get(curIndex-1);
				} else {
					t.dir = Dir.UP;
					t.tc = circuits.get(0);
				}
			}
			log += '[' + t.code + ' ' + t.tc.name + ' ' + t.tc.getMid() + ']';
			
		}
		System.out.println("Moving trains: " + log);
		
	}
}

	