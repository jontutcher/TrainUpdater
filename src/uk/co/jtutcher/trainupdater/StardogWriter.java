package uk.co.jtutcher.trainupdater;

import java.util.ArrayList;

import org.apache.commons.configuration.Configuration;
import org.openrdf.model.Graph;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.jtutcher.trainupdater.exceptions.FailedToCloseException;
import uk.co.jtutcher.trainupdater.exceptions.NoConnectionException;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;

/**
 * Object to read/write from/to stardog database, with particular methods for writing train simulator data
 * @author Jon Tutcher
 *
 */
public class StardogWriter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Configuration config;
	private Connection connection;
	private boolean connected = false;
	ValueFactoryImpl valueFactory = ValueFactoryImpl.getInstance();
	public StardogWriter(Configuration config) {
		this.config = config;
	}
	
	
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Open connection to Stardog db
	 * @throws NoConnectionException
	 */
	public void open() throws NoConnectionException
	{	
		try {
			connection = ConnectionConfiguration
					.to(config.getString("stardog.db"))
					.server(config.getString("stardog.url"))
					.credentials(config.getString("stardog.user"), config.getString("stardog.pass"))
					.connect();
		} catch (StardogException e) {
			// TODO Auto-generated catch block
			logger.error("Cannot connect to Stardog", e);
			throw new NoConnectionException("Cannot connect to Stardog");
		}
		connected = true;
	}
	
	/**
	 * get all track circuits in stardog for simulation
	 * @return ArrayList of track circuits found (in order)
	 * @throws StardogException
	 * @throws NumberFormatException
	 * @throws QueryEvaluationException
	 */
	public ArrayList<TrackCircuit> getCircuits() throws StardogException, NumberFormatException, QueryEvaluationException
	{
		ArrayList<TrackCircuit> trackCircuits = new ArrayList<TrackCircuit>();
		SelectQuery trackCircuitsQuery = connection.select("SELECT ?tc (COALESCE(?label, ?tc) as ?name) ?min ?max WHERE {?tc a is:TrackCircuit ; is:minLocation [is:mileage ?min] ; is:maxLocation [is:mileage ?max] ; u:id ?id . OPTIONAL {?tc rdfs:label ?label}} ORDER BY ASC(?id)");
		trackCircuitsQuery.limit(100);
		TupleQueryResult trackCircuitQueryResult = trackCircuitsQuery.execute();
		while(trackCircuitQueryResult.hasNext())
		{
			BindingSet bindings = trackCircuitQueryResult.next();
			TrackCircuit trackCircuit = new TrackCircuit(valueFactory.createURI(bindings.getValue("tc").stringValue()),
					Double.parseDouble(bindings.getValue("min").stringValue()),
					Double.parseDouble(bindings.getValue("max").stringValue()),
					bindings.getValue("name").stringValue());
			
			trackCircuits.add(trackCircuit);
		}
		if(trackCircuits.size()<1)throw new StardogException("No Track Circuits recieved from Stardog");
		return trackCircuits;
	}

	/**
	 * Close stardog connection
	 * @throws FailedToCloseException
	 */
	public void close() throws FailedToCloseException
	{
		if(!connected) return;
		try {
		logger.info("Closing stardog writer");
		connection.close();
		logger.info("Stardog writer closed");
		} catch (StardogException e) {
			logger.error("Stardog failed to close", e);
			throw new FailedToCloseException("Could not close triplestore");
		}
		return;
	}

	/**
	 * Writes an update to the triplestore, given a graph
	 * @param gStage Sesame graph to stream write
	 * @param clear Whether to remove entire graph before write (true to do so)
	 * @throws NoConnectionException
	 */
	public void writeGraph(Graph gStage, boolean clear) throws NoConnectionException {
		if(!connected) throw new NoConnectionException("Not Connected!");
		try {
			connection.begin();
			if(clear)
			{
				connection.remove().context(Constants.GRAPHS.DYNAMIC);
				connection.remove().context(Constants.GRAPHS.MILES);
				connection.remove().context(Constants.GRAPHS.TRACK);
			}
			connection.add().graph(gStage);
			logger.info("Writing graph {}", Helper.miniTruncate(gStage.toString()));
			connection.commit();
			logger.info("Committed graph");
		} catch (StardogException e) {
			logger.error("Could not write to Stardog", e);
		}
	}
}