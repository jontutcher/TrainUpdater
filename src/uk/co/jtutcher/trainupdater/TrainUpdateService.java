package uk.co.jtutcher.trainupdater;

import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.jtutcher.trainupdater.exceptions.BadDBException;
import uk.co.jtutcher.trainupdater.exceptions.NoConnectionException;

import com.complexible.stardog.StardogException;
import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * Scheduled service that fires update methods at a preset interval, when trains need to be moved.
 * @author Jon
 *
 */
public class TrainUpdateService extends AbstractScheduledService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public Configuration config;
	private JenaTrainUpdater t;
	boolean started = false;
	
	/**
	 * Creates object and loads config
	 * @param tu
	 * @param config
	 */
	public TrainUpdateService(JenaTrainUpdater tu, Configuration config)
	{
		super();
		logger.trace("Creating new train updater service");
		//make our train updater
		t = tu;
		this.config = config;
		//do whatever it does
		
	}
	

	protected void startUp() throws Exception {
		//try to connect, and throw exception upwards if fail
		logger.info("Train updater service started");
		started = true;
	}
	
	/**
	 * Triggers each iteration update by calling the train updater
	 */
	protected void runOneIteration(){
    	 try {
			t.doNextUpdate();
		} catch (NoConnectionException | StardogException | BadDBException e) {
			logger.error("Problem connecting to data store whilst updating", e);
		}
	}
	
	protected void shutDown() throws Exception {
	logger.info("Train updater service stopped");
	}

	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(0, config.getInt("app.refresh"), TimeUnit.SECONDS);
	}


}
