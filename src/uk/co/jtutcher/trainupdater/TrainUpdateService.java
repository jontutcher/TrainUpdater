package uk.co.jtutcher.trainupdater;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AbstractScheduledService;

public class TrainUpdateService extends AbstractScheduledService {
	private JenaTrainUpdater t;
	boolean started = false;
	String url = "";
	
	public TrainUpdateService(JenaTrainUpdater tu)
	{
		super();
		//make our train updater
		t = tu;
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
    	 catch (Exception e)
    	 {
    		 System.out.println("Iteration error: ");
    		 e.printStackTrace();
    	 }
	}
	
	protected void shutDown() throws Exception {
	System.out.println("Finishing");
	t.close();
	}

	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(0, 5, TimeUnit.SECONDS);
	}


}
