package uk.co.jtutcher.trainupdater;

import java.sql.Time;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;


/**
 * Helper functions for the Train Simulator application
 * @author Jon
 *
 */
public final class Helper {
	
	/**
	 * Truncate string to line wrap length for logging
	 * @param truncString
	 * @return
	 */
	public static String miniTruncate(String truncString)
	{
		return truncString.toString().substring(0, Constants.TRUNC-3) + "...";
		
	}
	
	/**
	 * Check if a string is a valid Jena URI by attempting to make it into a Jena Node (URIImpl)
	 * @param iri
	 * @return true if valid, false if invalid
	 */
	public static boolean IsValidIRI(String iri)
	{
		try {
			@SuppressWarnings(value = { "unused" })
			URI node = new URIImpl(iri);
			return true;
		}
		catch (IllegalArgumentException f){
			return false;
		}
	}
	
	/**
	 * Create all the RDF nodes for a particular train 'stop' (i.e. a location it is in at a time)
	 * @param subj
	 * @param label
	 * @param time
	 * @param trackCircuit
	 * @param trainDirection
	 * @param from
	 * @param to
	 * @return an array of Jena statements for each train
	 */
	public static ArrayList<Statement> createStopNodes(URI subj, String label, Time time, TrackCircuit trackCircuit, URI trainDirection, URI from, URI to)
	{
		ValueFactoryImpl valueFactory = ValueFactoryImpl.getInstance();
		ArrayList<Statement> stopNodes = new ArrayList<Statement>();
		URI newNode = new URIImpl(Constants.NS.RES + UUID.randomUUID().toString());
		stopNodes.add(new ContextStatementImpl(newNode, Constants.VOCAB.TYPE, Constants.VOCAB.SERVICENODE, Constants.GRAPHS.DYNAMIC));
		stopNodes.add(new ContextStatementImpl(newNode, Constants.VOCAB.LABEL, valueFactory.createLiteral("Service Node for " + label), Constants.GRAPHS.DYNAMIC));
		stopNodes.add(new ContextStatementImpl(newNode, Constants.VOCAB.TCPOS, trackCircuit.uri, Constants.GRAPHS.TRACK));
		stopNodes.add(new ContextStatementImpl(newNode, Constants.VOCAB.MILEAGE, valueFactory.createLiteral(trackCircuit.getMid()), Constants.GRAPHS.MILES));
		stopNodes.add(new ContextStatementImpl(subj, Constants.VOCAB.DIR, trainDirection, Constants.GRAPHS.DYNAMIC));
//		ret.add(new ContextStatementImpl(newNode, C.VOCAB.TTORDER));
		stopNodes.add(new ContextStatementImpl(subj, Constants.VOCAB.SERVICENODEPROP, newNode, Constants.GRAPHS.DYNAMIC));
		stopNodes.add(new ContextStatementImpl(subj, Constants.VOCAB.TYPE, Constants.VOCAB.SERVICEINSTANCE, Constants.GRAPHS.DYNAMIC));
		stopNodes.add(new ContextStatementImpl(subj, Constants.VOCAB.LABEL, valueFactory.createLiteral(label), Constants.GRAPHS.DYNAMIC));
		stopNodes.add(new ContextStatementImpl(subj, Constants.VOCAB.ORIGIN, from, Constants.GRAPHS.DYNAMIC));
		stopNodes.add(new ContextStatementImpl(subj, Constants.VOCAB.DESTINATION, to, Constants.GRAPHS.DYNAMIC));
		return stopNodes;
	}

	/**
	 * Validates a TrainUpdater config file by checking that it has all necessary properties
	 * @param c
	 * @return
	 */
	public static boolean validate(Configuration c) {
		boolean vres = true;
		
		if(!c.containsKey("app.trains")) vres = false;
		if(!c.containsKey("app.mintc")) vres = false;
		if(!c.containsKey("app.maxtc")) vres = false;
		if(!c.containsKey("app.refresh")) vres = false;
	
		if(!c.containsKey("stardog.url")) vres = false;
		if(!c.containsKey("stardog.db")) vres = false;
		if(!c.containsKey("stardog.user")) vres = false;
		if(!c.containsKey("stardog.pass")) vres = false;
	
		if(!c.containsKey("mysql.driver")) vres = false;
		if(!c.containsKey("mysql.url")) vres = false;
		if(!c.containsKey("mysql.user")) vres = false;
		if(!c.containsKey("mysql.pass")) vres = false;
		if(!c.containsKey("railway.name")) vres = false;
		return vres;
	}
}
