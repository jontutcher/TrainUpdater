package uk.co.jtutcher.trainupdater;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.query.QueryEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.jtutcher.trainupdater.exceptions.FailedToCloseException;
import uk.co.jtutcher.trainupdater.exceptions.NoConnectionException;

import com.complexible.stardog.StardogException;

/**
 * Starter class for FuTRO Train Simulator
 * @author Jon Tutcher
 *
 */
public class TrainSimulator {
	private static final Logger logger = LoggerFactory.getLogger(TrainSimulator.class);
	private StardogWriter stardogWriter; 
	private UpdateService updateService;
	private TrainUpdater trainUpdater;	//train updater class is what actually updates the train locations
	private static boolean stop = false;
	
	//if we're called with 'start', then call start function, otherwise call stop.
	/**
	 * Main function for starting Train Simulator. 
	 * @param args "start" for service start, "stop" for service stop.
	 */
	public static void main(String[] args) {
		if(args.length<1)
			logger.info("No service parameters found; starting anyway");
			start();
        if ("start".equals(args[0])) {
        	start();
        } else if ("stop".equals(args[0])) {
            stop();	//call static stop function
        }
	}

	/**
	 * Empty constructor
	 */
	public TrainSimulator()
	{
	}
	
	//run and loop until stop
	/**
	 * Static service start method. Instantiates a train simulator, and runs it. 
	 */
	public static void start()
	{
		TrainSimulator me = new TrainSimulator();
		System.exit(me.run());
	}
	
	/**
	 * Static service stop method. Sets boolean to true and exits (service stops in its own time)
	 */
	public static void stop()
	{
		logger.info("Stopping...");
		stop = true;
	}
	
	/**
	 * instantiates and runs entire application.
	 * @return
	 */
	
	
	// run the service
	public int run()
	{	
		logger.info("Starting Up Train Updater RDF Tool");
		// TODO Auto-generated method stub
//		static final Logger logger = Logger.getLogger(TrainUpdaterMain.class)
		Configuration config;
		try {
			config = new PropertiesConfiguration("trainupdater.properties");
		} catch (ConfigurationException e1) {
			logger.error("Could not parse configuration file, quitting.", e1);
			return 1;
		}
		
		if(!Helper.validate(config))
		{
			logger.error("Invalid configuration file, quitting.");
			return 1;
		}
		
		stardogWriter = new StardogWriter(config);	//create stardog writer object
		try {
			stardogWriter.open();
			trainUpdater = new TrainUpdater(stardogWriter, config);
		} catch (NumberFormatException | QueryEvaluationException
				| NoConnectionException | StardogException e1) {
			logger.error("Cannot create updater object or connect to triple store", e1);
			return 1;
		} //create updater
		
		//if we're successful, make new updater service
		updateService = new UpdateService(trainUpdater, config);	// async update service
		updateService.startAsync();	//start updates
		
		//wait for stop
		while(!stop) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
		
		//stop has been received
		updateService.stopAsync();
		
		try {
			stardogWriter.close();
		} catch (FailedToCloseException e) {
			logger.error("Could not close triple store", e);
		}
		return 0;
	}
}
