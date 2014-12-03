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

public class StardogWriter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Configuration config;
//	private AdminConnection a;
	private Connection c;
	private boolean connected = false;
	ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
	public StardogWriter(Configuration config) {
		this.config = config;
	}
	
	
	public boolean isConnected() {
		return connected;
	}
	
	public void open() throws NoConnectionException
	{
//		a = AdminConnectionConfiguration.toServer(C.STARDOG)
//                .credentials("admin", "admin")
//                .connect();
		
		try {
			c = ConnectionConfiguration
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
	
	public ArrayList<TrackCircuit> getCircuits() throws StardogException, NumberFormatException, QueryEvaluationException
	{
		ArrayList<TrackCircuit> tcs = new ArrayList<TrackCircuit>();
		SelectQuery s = c.select("SELECT ?tc (COALESCE(?label, ?tc) as ?name) ?min ?max WHERE {?tc a is:TrackCircuit ; is:minLocation [is:mileage ?min] ; is:maxLocation [is:mileage ?max] ; u:id ?id . OPTIONAL {?tc rdfs:label ?label}} ORDER BY ASC(?id)");
		s.limit(100);
		TupleQueryResult res = s.execute();
		while(res.hasNext())
		{
			BindingSet b = res.next();
			TrackCircuit tct = new TrackCircuit(vf.createURI(b.getValue("tc").stringValue()),
					Double.parseDouble(b.getValue("min").stringValue()),
					Double.parseDouble(b.getValue("max").stringValue()),
					b.getValue("name").stringValue());
			
			tcs.add(tct);
		}
		return tcs;
	}

	
	public void close() throws FailedToCloseException
	{
		
		if(!connected) return;
		
		try {
		logger.info("Closing stardog writer");
		c.close();
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
			
			c.begin();
			if(clear)
			{
				c.remove().context(C.GRAPHS.DYNAMIC);
				c.remove().context(C.GRAPHS.MILES);
				c.remove().context(C.GRAPHS.TRACK);
			}
			//hope for a context aware graph!
			c.add().graph(gStage);
			logger.info("Writing graph {}", TUHelper.miniTruncate(gStage.toString()));
			c.commit();
			logger.info("Committed graph");
		} catch (StardogException e) {
			logger.error("Could not write to Stardog", e);
//			throw new NoConnectionException();
		}
	}
}