package uk.co.jtutcher.trainupdater;

import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores track circuit details and methods
 * @author Jon Tutcher
 *
 */
public class TrackCircuit {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public URI uri;
	public String name;
	public double minMiles=-1, maxMiles=-1;
	
	public String toString() {
		return uri.toString() + ' ' + minMiles + ' ' + maxMiles;
	}
	
	public TrackCircuit(URI uri, double minMiles, double maxMiles, String name)
	{
		logger.trace("New track circuit: {}", uri);
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
