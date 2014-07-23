	package uk.co.jtutcher.trainupdater;

import org.openrdf.model.URI;

public class TrackCircuit {
	public URI uri;
	public String name;
	public double minMiles=-1, maxMiles=-1;
	
	public String toString() {
		return uri.toString() + ' ' + minMiles + ' ' + maxMiles;
	}
	
	public TrackCircuit(URI uri, double minMiles, double maxMiles, String name)
	{
		this.uri = uri;
		this.name = name;
		this.minMiles = minMiles;
		this.maxMiles = maxMiles;
	}
	
	public double getMid()
	{
		if(maxMiles>0&&minMiles>0)
			return (maxMiles + minMiles) / 2;
		return -1;
	}
}
