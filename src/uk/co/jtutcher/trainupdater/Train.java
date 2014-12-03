package uk.co.jtutcher.trainupdater;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Train {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public enum Dir {UP, DOWN}; 
	public String name;
	public TrackCircuit tc;
	public String code;
	public Dir dir = Dir.DOWN;
	
	public Train(String name, TrackCircuit tc)
	{
		logger.trace("New train: {}", name);
		this.name = name;
		this.tc = tc;
	}
	
//	public Statement getTCStatement()
//	{
//		return new StatementImpl(getFQName(), new URIImpl(C.NS.IS + "tcPos"), getTC());
//	}
	
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
		return new URIImpl(C.NS.RES + res);
	}
	
	public URI getTCURI()
	{
		//return new URIImpl(C.NS.RES + "TC" + Integer.toString(tc));
		return tc.uri;
	}
	
	public URI getDir()
	{
		switch(dir)
		{
		case UP:
			return C.VOCAB.UP;
		case DOWN:
			return C.VOCAB.DOWN;
		}
		return null;
	}
	
	public URI getFrom()
	{
		if(dir==Dir.UP)
			return new URIImpl("http://purl.org/rail/resource/BirminghamNewStreetBHMStation");
		else
			return new URIImpl("http://purl.org/rail/resource/CheltenhamSpaCNMStation");
	}
	
	public URI getTo()
	{
		if(dir==Dir.DOWN)
			return new URIImpl("http://purl.org/rail/resource/BirminghamNewStreetBHMStation");
		else
			return new URIImpl("http://purl.org/rail/resource/CheltenhamSpaCNMStation");
	}
	
	public String toString()
	{
		return '[' + name + ' ' + tc.name + ']';
	}
	

}
