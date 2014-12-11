package uk.co.jtutcher.trainupdater;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.configuration.Configuration;
import org.openrdf.model.Graph;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.query.QueryEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.jtutcher.trainupdater.Train.Direction;
import uk.co.jtutcher.trainupdater.exceptions.BadDBException;
import uk.co.jtutcher.trainupdater.exceptions.NoConnectionException;

import com.complexible.common.openrdf.model.Graphs;
import com.complexible.stardog.StardogException;


public class TrainUpdater {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@SuppressWarnings("unused")
	private Configuration config;
	@SuppressWarnings("unused")
	private boolean connected = false;
	private StardogWriter stardogWriter;
	public Train[] trains;
	public ArrayList<TrackCircuit> trackCircuits;
	RailwaySystem simulatedLine;
	private int currentBatch = 1;
	//TODO: Add functionality to max batch (get the maximum batch number from sql)
	private int maxBatch=84;
	
	public int getCurrentBatch() {
		return currentBatch;
	}
	public void setCurrentBatch(int currentBatch) {
		this.currentBatch = currentBatch;
	}
	
	
	/**
	 * Creates moving train system and calls update mechanisms for passing out RDF
	 * @param sw
	 * @param config
	 * @throws NoConnectionException
	 * @throws StardogException 
	 * @throws QueryEvaluationException 
	 * @throws NumberFormatException 
	 */
	public TrainUpdater(StardogWriter stardogWriter, Configuration config) throws NoConnectionException, NumberFormatException, QueryEvaluationException, StardogException
	{ 
		this.config = config;
		if(!stardogWriter.isConnected()) throw new NoConnectionException("Stardog not connected!");
		this.stardogWriter = stardogWriter;
		trackCircuits = stardogWriter.getCircuits();	//may throw exceptions if we can't get track circuits
		
		trains = new Train[Constants.TRAINIDS.length];
		simulatedLine = new RailwaySystem(trackCircuits, config.getString("railway.name"));
		logger.info("Initiated railway line {} with {} trains", simulatedLine.getName(), trains.length);
		Random rand = new Random();
		int tLoc = 0;
		boolean b = rand.nextBoolean();
		for(int i = 0; i < trains.length; i++)
		{
			//make new trains
			logger.info("Track circuit size is {}", trackCircuits.size());
			tLoc = rand.nextInt(trackCircuits.size());
			b = rand.nextBoolean();
			
			trains[i] = new Train("Train" + Constants.TRAINIDS[i], trackCircuits.get(tLoc));
			trains[i].code = Constants.TRAINIDS[i];
			if(b)
				trains[i].direction = (Direction.UP);
			else
				trains[i].direction = (Direction.DOWN);
			simulatedLine.addTrain(trains[i]);
		}
		
		
	}
	
	/**
	 * 
	 * @return Graph
	 * @throws NoConnectionException
	 */
	public Graph doAuto() throws NoConnectionException
	{
		if(!stardogWriter.isConnected()) throw new NoConnectionException("Stardog not connected!");
		simulatedLine.moveTrains();
		logger.trace("Moving Trains");
		Graph gStage = Graphs.newContextGraph();
		Iterator<Train> itr = simulatedLine.getTrains().iterator();
		
		while(itr.hasNext())
		{
			Train t = itr.next();
			//generate all RDF nodes concerning a particular train and its current location
			gStage.addAll(Helper.createStopNodes(t.getFQName(), t.getLabel(), null, t.trackCircuit, t.getDir(), t.getFrom(), t.getTo()));
		}
	
		return gStage;
	}
	
	public boolean doUpdate(int index) throws NoConnectionException, StardogException
	{
		if(!stardogWriter.isConnected()) throw new NoConnectionException("Stardog not connected!");
		Graph gStage = doAuto();
		gStage.addAll(insertProgress(index));
		logger.debug("Writing Stage {} to Triple store", index);
		stardogWriter.writeGraph(gStage, true);
		return true;
	}
	
	/**
	 * Calculate progress through the simulation, and add to triplestore!
	 * @param index
	 * @return
	 */
	private ArrayList<org.openrdf.model.Statement> insertProgress(int index) {
		logger.trace("Calculating Progress at index {})", index);
		ArrayList<org.openrdf.model.Statement> retval = new ArrayList<org.openrdf.model.Statement>();
		retval.add(new ContextStatementImpl(Constants.SERURI, Constants.PROGRESSURI, new LiteralImpl(Integer.toString(index), Constants.INTDTYPE), Constants.GRAPHS.DYNAMIC));
		retval.add(new ContextStatementImpl(Constants.SERURI, Constants.MAXURI, new LiteralImpl(Integer.toString(maxBatch), Constants.INTDTYPE), Constants.GRAPHS.DYNAMIC));
		return retval;
	}
	
	public void doNextUpdate() throws NoConnectionException, StardogException, BadDBException
	{
		logger.trace("Triggering Update {}", currentBatch);
		doUpdate(currentBatch);
		//try and do the next update
		if(++currentBatch>maxBatch)
			currentBatch=0;
	}
	
//	private org.openrdf.model.Statement textStatement(String subject,
//			String predicate, String object, String graph) 
//	{
//		//bodge to replace an unknown graph with our known one
//		if(graph.isEmpty())graph=C.GRAPH.toString();
//		
//		if(subject.isEmpty()||predicate.isEmpty()||object.isEmpty()||graph.isEmpty())
//			throw new IllegalArgumentException("Blank values in database row");
//		//check if subject and predicate are valid URIs
//		if(!TUHelper.IsValidIRI(subject)||!TUHelper.IsValidIRI(predicate)||!TUHelper.IsValidIRI(graph))
//			throw new IllegalArgumentException("Values in database row are not URIs: " + subject + " " + predicate + " " + object);
//		
//		org.openrdf.model.Statement stmt;
//		//cast the object appropriately
//		if(TUHelper.IsValidIRI(object))
//		{
//			stmt = new ContextStatementImpl(new URIImpl(subject), new URIImpl(predicate), new URIImpl(object), new URIImpl(graph));
//		} else {
//			stmt = new ContextStatementImpl(new URIImpl(subject), new URIImpl(predicate), new LiteralImpl(object), new URIImpl(graph));
//		}
//		return stmt;
//	}

}
