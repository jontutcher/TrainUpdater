package uk.co.jtutcher.trainupdater;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

public class C {
	
	
	
	public C()
	{
		
	}
	
	public static class NS {
		public static final String U = "http://purl.org/ub/upper/";
		public static final String DOC = "http://purl.org/ub/doc/";
		public static final String P4D = "http://purl.org/ub/upper/p4d/";
		public static final String CORE = "http://purl.org/rail/core/";
		public static final String VOCAB = "http://purl.org/rail/core/vocab/";
		public static final String IS = "http://purl.org/rail/is/";
		public static final String TT = "http://purl.org/rail/tt/";
		public static final String RS = "http://purl.org/rail/rs/";
		public static final String DEMO = "http://purl.org/ub/demo/ontology/";
		public static final String TLOC = "http://purl.org/ub/demo/resource/";
		public static final String RES = "http://purl.org/rail/resource/";
		public static final String DC = "http://purl.org/dc/elements/1.1/";
		public static final String DCAM = "http://purl.org/dc/dcam/";
		public static final String DCT = "http://purl.org/dc/terms/";
		public static final String GEO = "http://www.opengis.net/ont/geosparql#";
		public static final String GML = "http://www.opengis.net/ont/gml#";
		public static final String OWL = "http://www.w3.org/2002/07/owl#";
		public static final String PROV = "http://www.w3.org/ns/prov#";
		public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
		public static final String SF = "http://www.opengis.net/ont/sf#";
		public static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
		public static final String TIME = "http://www.w3.org/2006/time#";
		public static final String TZONT = "http://www.w3.org/2006/timezone#";
		public static final String UNIT = "http://qudt.org/vocab/unit#";
		public static final String WGSPOS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
		public static final String XML = "http://www.w3.org/XML/1998/namespace";
		public static final String XSD = "http://www.w3.org/2001/XMLSchema#";
	}
	
	public static class VOCAB {
		public static final URI SERVICENODE = new URIImpl(NS.TT + "ServiceNode");
		public static final URI SERVICENODEPROP = new URIImpl(NS.TT + "serviceNode");
		public static final URI SERVICEINSTANCE = new URIImpl(NS.TT + "ServiceInstance");
		public static final URI TTORDER = new URIImpl(NS.TT + "ttOrder");
		public static final URI TCPOS = new URIImpl(NS.IS + "tcPos");
		public static final URI TYPE = new URIImpl(NS.RDF + "type");
		public static final URI UP = new URIImpl(NS.TT + "UpDirection");
		public static final URI DOWN = new URIImpl(NS.TT + "DownDirection");
		public static final URI DIR = new URIImpl(NS.TT + "direction");
		public static final URI LABEL = new URIImpl(NS.RDFS + "label");
		public static final URI ORIGIN = new URIImpl(NS.TT + "origin");
		public static final URI DESTINATION = new URIImpl(NS.TT+ "destination");
		public static final URI MILEAGE = new URIImpl(NS.IS+ "mileage");
		
	}
	
	public static class GRAPHS {
		public final static Resource ABOX = ValueFactoryImpl.getInstance().createURI("http://purl.org/ub/demo/graph/dynamic");
		public final static Resource TRACK = ValueFactoryImpl.getInstance().createURI("http://purl.org/ub/demo/graph/track");
		public final static Resource MILES = ValueFactoryImpl.getInstance().createURI("http://purl.org/ub/demo/graph/miles");
		public final static Resource DYNAMIC = ABOX;
	}
//	public static final int TRAINS=20;
//	public static final int MINTC=1;
//	public static final int MAXTC=83;
//	public static final String STARDOG = "http://localhost:5820/";
//	public static final String STARDOGDB = "trains";

	//constants for TrainUpdater
//	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
//	public static final String DB_URL = "jdbc:mysql://localhost:3306/";
//	public static final String USER = "root";
//	public static final String PASS = "";
	public static final String QUERY = "SELECT subject, predicate, object, graph FROM traintimes.times WHERE batchid=%s;";
	public static final URI GRAPH = new URIImpl("http://purl.org/ub/demo/graph/dynamic");
	public static final URI SERURI = new URIImpl("http://purl.org/ub/demo/resource/TrainLocator");
	public static final URI PROGRESSURI = new URIImpl("http://purl.org/ub/demo/ontology/progress");
	public static final URI MAXURI = new URIImpl("http://purl.org/ub/demo/ontology/maxProgress");
	public static final URI INTDTYPE = new URIImpl("http://www.w3.org/2001/XMLSchema#integer");
//	public static final int REFRESHTIME = 10;
	public static final String[] TRAINIDS = {"1X34", "2N99", "2U40", "2R03"};// "1M53", "2U64"// "2N71", "1M87", "1V10", "2N62", "2N74", "1M89", "2R12"};
}
