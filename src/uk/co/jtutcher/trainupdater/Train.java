package uk.co.jtutcher.trainupdater;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to store train properties within simulator (location, direction, name, etc)
 * @author Jon Tutcher
 *
 */
public class Train {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public enum Direction {UP, DOWN}; 
	public String name;
	public TrackCircuit trackCircuit;
	public String code;
	public Direction direction = Direction.DOWN;
	
	/**
	 * Create train based on name and current location
	 * @param name Train name
	 * @param tc Train track circuit
	 */
	public Train(String name, TrackCircuit tc)
	{
		logger.trace("New train: {}", name);
		this.name = name;
		this.trackCircuit = tc;
	}
	
	public String getLabel()
	{
		return name;
	}
	
	public URI getFQName()
	{
		return makeURI(name);
	}
	
	private URI makeURI(String res)
	{
		return new URIImpl(Constants.NS.RES + res);
	}
	
	public URI getTCURI()
	{
		return trackCircuit.uri;
	}
	
	public URI getDir()
	{
		switch(direction)
		{
		case UP:
			return Constants.VOCAB.UP;
		case DOWN:
			return Constants.VOCAB.DOWN;
		}
		return null;
	}
	
	public URI getFrom()
	{
		if(direction==Direction.UP)
			return new URIImpl("http://purl.org/rail/resource/BirminghamNewStreetBHMStation");
		else
			return new URIImpl("http://purl.org/rail/resource/CheltenhamSpaCNMStation");
	}
	
	public URI getTo()
	{
		if(direction==Direction.DOWN)
			return new URIImpl("http://purl.org/rail/resource/BirminghamNewStreetBHMStation");
		else
			return new URIImpl("http://purl.org/rail/resource/CheltenhamSpaCNMStation");
	}
	
	public String toString()
	{
		return '[' + name + ' ' + trackCircuit.name + ']';
	}
	
}
