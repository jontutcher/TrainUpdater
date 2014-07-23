package uk.co.jtutcher.trainupdater;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.*;
import org.openrdf.query.QueryEvaluationException;

import uk.co.jtutcher.trainupdater.Train.Dir;

import com.complexible.common.openrdf.model.Graphs;
import com.complexible.stardog.StardogException;


public class JenaTrainUpdater {
	
	private boolean connected = false;
	private Connection sqlConn = null;
	private StardogWriter s;
	public Train[] trains;
	public ArrayList<TrackCircuit> tcs;
	TrainSystem line;
	private int currentBatch = 1;
	//TODO: Add functionality to max batch (get the maximum batch number from sql)
	private int maxBatch=84;
	
	public int getCurrentBatch() {
		return currentBatch;
	}
	public void setCurrentBatch(int currentBatch) {
		this.currentBatch = currentBatch;
	}
	
	
	public JenaTrainUpdater(StardogWriter sw) throws NoConnectionException
	{ 
		if(!sw.isConnected()) throw new NoConnectionException("Stardog not connected!");
		s = sw;
		try {
			tcs = s.getCircuits();
			//TODO
		} catch (NumberFormatException | QueryEvaluationException
				| StardogException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		trains = new Train[C.TRAINIDS.length];
		line = new TrainSystem(tcs);
		Random rand = new Random();
		int tLoc = 0;
		boolean b = rand.nextBoolean();
		for(int i = 0; i < trains.length; i++)
		{
			//make new trains
			tLoc = rand.nextInt(tcs.size());
			b = rand.nextBoolean();
			
			trains[i] = new Train("Train" + C.TRAINIDS[i], tcs.get(tLoc));
			trains[i].code = C.TRAINIDS[i];
			if(b)
				trains[i].dir = (Dir.UP);
			else
				trains[i].dir = (Dir.DOWN);
			line.addTrain(trains[i]);
		}
		
		
	}
	public boolean connect() throws ClassNotFoundException, SQLException
	{
		Class.forName(C.JDBC_DRIVER);
		try {
			sqlConn = DriverManager.getConnection(C.DB_URL,C.USER,C.PASS);
			s.open();
			connected = true;
			return true;
		} catch (SQLException | StardogException e) {
			e.printStackTrace();
			return false;
		} finally {
			//reset back to default
		}
	}
	
	/**
	 * Update the triplestore as necessary! returns true if update happened, or false if there were no results.
	 * @param index
	 * @throws Exception 
	 */
	
	public Graph doAuto() throws NoConnectionException
	{
		if(!connected) throw new NoConnectionException();
		line.moveTrains();
		
		Graph gStage = Graphs.newContextGraph();
		Iterator<Train> itr = line.getTrains().iterator();
		while(itr.hasNext())
		{
			Train t = itr.next();
			gStage.addAll(TUHelper.createStopNodes(t.getFQName(), t.getLabel(), null, t.tc, t.getDir(), t.getFrom(), t.getTo()));
		}
		return gStage;
	}
	
//	public Graph doSQLUpdate(int index) throws NoConnectionException, SQLException, StardogException
//	{
//		//get us out of here if we're not connected
//		if(!connected) throw new NoConnectionException();
//		//cast index to Integer just for convenience
//		Integer qIndex = Integer.valueOf(index);
//		// create a SQL statement object to execute
//		Statement stmt;
//		stmt = sqlConn.createStatement();
//		// send our query, using the constant defined above, with the index as the 'BatchID' variable.
//		ResultSet rs = stmt.executeQuery(String.format(C.QUERY, qIndex.toString()));
//
//		//check if we have any results; if not, return false as we have nothing to do
//		if (!rs.isBeforeFirst() ) {    
//			 System.out.println("No data");
//			 throw new SQLException();
//		} 
//		//list of statements to add
//		Graph gStage = Graphs.newGraph();
//		while(rs.next())
//		{
//			try {
//				org.openrdf.model.Statement triple = textStatement(rs.getString("subject"), rs.getString("predicate"), rs.getString("object"), rs.getString("graph"));
//				gStage.add(triple);
//			} catch (IllegalArgumentException e) {
//				System.out.println(e.getMessage());
//			}
//		}
//		return gStage;
//	}
	
	public boolean doUpdate(int index) throws NoConnectionException, SQLException, StardogException
	{
		Graph gStage = doAuto();
		gStage.addAll(insertProgress(index));
		System.out.println("Writing Stage " + index + " to Stardog");
		s.writeGraph(gStage, true);
		return true;
	}
	
	private ArrayList<org.openrdf.model.Statement> insertProgress(int index) {
		ArrayList<org.openrdf.model.Statement> retval = new ArrayList<org.openrdf.model.Statement>();
		retval.add(new ContextStatementImpl(C.SERURI, C.PROGRESSURI, new LiteralImpl(Integer.toString(index), C.INTDTYPE), C.GRAPHS.DYNAMIC));
		retval.add(new ContextStatementImpl(C.SERURI, C.MAXURI, new LiteralImpl(Integer.toString(maxBatch), C.INTDTYPE), C.GRAPHS.DYNAMIC));
		return retval;
	}
	
	public void doNextUpdate() throws NoConnectionException, SQLException, StardogException, BadDBException
	{
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
	
	public boolean close()
	{
		if(!connected) return false;
		try {
			if(!sqlConn.isClosed())
				sqlConn.close();
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
