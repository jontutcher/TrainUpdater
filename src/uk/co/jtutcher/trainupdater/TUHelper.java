package uk.co.jtutcher.trainupdater;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

import uk.co.jtutcher.trainupdater.Train.Dir;

import com.complexible.common.openrdf.model.Graphs;

public class TUHelper {
	public static boolean IsValidIRI(String iri)
	{
		try {
			URI node = new URIImpl(iri);
			return true;
		}
		catch (IllegalArgumentException f){
			return false;
		}
	}
	
	public static ArrayList<Statement> createStopNodes(URI subj, String label, Time time, TrackCircuit tc, URI dir, URI from, URI to)
	{
		ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
		ArrayList<Statement> ret = new ArrayList<Statement>();
		URI newNode = new URIImpl(C.NS.RES + UUID.randomUUID().toString());
		ret.add(new ContextStatementImpl(newNode, C.VOCAB.TYPE, C.VOCAB.SERVICENODE, C.GRAPHS.DYNAMIC));
		ret.add(new ContextStatementImpl(newNode, C.VOCAB.LABEL, vf.createLiteral("Service Node for " + label), C.GRAPHS.DYNAMIC));
		ret.add(new ContextStatementImpl(newNode, C.VOCAB.TCPOS, tc.uri, C.GRAPHS.TRACK));
		ret.add(new ContextStatementImpl(newNode, C.VOCAB.MILEAGE, vf.createLiteral(tc.getMid()), C.GRAPHS.MILES));
		ret.add(new ContextStatementImpl(subj, C.VOCAB.DIR, dir, C.GRAPHS.DYNAMIC));
//		ret.add(new ContextStatementImpl(newNode, C.VOCAB.TTORDER));
		ret.add(new ContextStatementImpl(subj, C.VOCAB.SERVICENODEPROP, newNode, C.GRAPHS.DYNAMIC));
		ret.add(new ContextStatementImpl(subj, C.VOCAB.TYPE, C.VOCAB.SERVICEINSTANCE, C.GRAPHS.DYNAMIC));
		ret.add(new ContextStatementImpl(subj, C.VOCAB.LABEL, vf.createLiteral(label), C.GRAPHS.DYNAMIC));
		ret.add(new ContextStatementImpl(subj, C.VOCAB.ORIGIN, from, C.GRAPHS.DYNAMIC));
		ret.add(new ContextStatementImpl(subj, C.VOCAB.DESTINATION, to, C.GRAPHS.DYNAMIC));
		return ret;
	}
}
