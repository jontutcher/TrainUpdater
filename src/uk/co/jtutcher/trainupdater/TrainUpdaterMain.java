package uk.co.jtutcher.trainupdater;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.query.QueryEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.jtutcher.trainupdater.exceptions.FailedToCloseException;
import uk.co.jtutcher.trainupdater.exceptions.NoConnectionException;

import com.complexible.stardog.StardogException;

public class TrainUpdaterMain {
	private static final Logger logger = LoggerFactory.getLogger(TrainUpdaterMain.class);
	
	public static void main(String[] args) {
		TrainUpdaterMain me = new TrainUpdaterMain();
		int exitCode = me.run();
		System.exit(exitCode);
	}

	public TrainUpdaterMain()
	{
		
	}
	
	/**
	 * instantiates and runs entire application.
	 * @return
	 */
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
		
		if(!AppConfig.validate(config))
		{
			logger.error("Invalid configuration file, quitting.");
			return 1;
		}
		
		StardogWriter stardogWriter = new StardogWriter(config);	//create stardog writer object
		JenaTrainUpdater updater;	//train updater class is what actually updates the train locations
		try {
			stardogWriter.open();
			updater = new JenaTrainUpdater(stardogWriter, config);
		} catch (NumberFormatException | QueryEvaluationException
				| NoConnectionException | StardogException e1) {
			logger.error("Cannot create updater object or connect to triple store", e1);
			return 1;
		} //create updater
		
		//if we're successful, make new updater service
		TrainUpdateService scheduler = new TrainUpdateService(updater, config);	// async update service
		scheduler.startAsync();	//start updates
		System.out.println("Press Enter to exit");
		try {
			System.in.read();
			//wait for user input before quitting
		} catch (IOException e) {
			logger.error("User input error", e);
			return 2;
		} finally {
			scheduler.stopAsync();
			try {
				stardogWriter.close();
			} catch (FailedToCloseException e) {
				logger.error("Could not close triple store", e);
				return 3;
			}
		}
		return 0;
	}
}
