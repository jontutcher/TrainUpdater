package uk.co.jtutcher.trainupdater;

import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;

import com.google.common.util.concurrent.AbstractScheduledService;

public class TrainUpdateService extends AbstractScheduledService {
	public Configuration config;
	private JenaTrainUpdater t;
	boolean started = false;
	String url = "";
	
	public TrainUpdateService(JenaTrainUpdater tu, Configuration config)
	{
		super();
		//make our train updater
		t = tu;
		this.config = config;
		//do whatever it does
		
	}
	
	protected void startUp() throws Exception {
		//try to connect, and throw exception upwards if fail
		System.out.println("Service Connected");
		url = this.url;
		started = true;
	}
	
	protected void runOneIteration() throws Exception {
	     try {
	    	 t.doNextUpdate();
	     }
    	 catch (NoConnectionException e)
    	 {
    		 System.out.println("Connection Failed to SQL Database");
  
    	 }
	}
	
	protected void shutDown() throws Exception {
	System.out.println("Finishing");
	t.close();
	}

	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(0, config.getInt("app.refresh"), TimeUnit.SECONDS);
	}


}
