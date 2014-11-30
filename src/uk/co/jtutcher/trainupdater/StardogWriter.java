package uk.co.jtutcher.trainupdater;

import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.configuration.Configuration;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;

public class StardogWriter {
	private Configuration config;
	private AdminConnection a;
	private Connection c;
	private boolean connected = false;
	ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
	public StardogWriter(Configuration config) {
		this.config = config;
	}
	
	
	public boolean isConnected() {
		return connected;
	}
	
	public void open() throws StardogException
	{
//		a = AdminConnectionConfiguration.toServer(C.STARDOG)
//                .credentials("admin", "admin")
//                .connect();
		
		c = ConnectionConfiguration
				.to(config.getString("stardog.db"))
				.server(config.getString("stardog.url"))
				.credentials(config.getString("stardog.user"), config.getString("stardog.pass"))
				.connect();
		
		connected = true;
	}
	
//	public void addPizza() throws NoConnectionException, StardogException
//	{
//		if(!connected) throw new NoConnectionException("Not Connected!");
//		c.begin();
//		try {
//			c.add().io()
//				.format(RDFFormat.RDFXML)
//				.stream(new FileInputStream("res/pizza.rdf"));
//		} catch (FileNotFoundException e) {
//			throw new NoConnectionException();
//		}
//		c.commit();
//		
//	}
	
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

	
	public void close() throws StardogException
	{
		c.close();
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
			System.out.println("Writing Graph: " + gStage);
			c.commit();
			System.out.println("Committed Graph\n");
		} catch (StardogException e) {
			System.out.println("Error Writing");
			e.printStackTrace();
//			throw new NoConnectionException();
		}
	}
}