package uk.co.jtutcher.trainupdater;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.complexible.stardog.StardogException;

public class TrainUpdaterMain {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration config;
		try {
			config = new PropertiesConfiguration("config.properties");
			if(!AppConfig.validate(config)) throw new Exception("Invalid Configuration File");	//if the config file doesn't validate then quit
		} catch (Exception e1) {
			System.out.println("Exception, program quitting: " + e1.getMessage());
			return;	//exit if we can't find the properties
		}
		
		try {
			
			StardogWriter stardog = new StardogWriter(config);
			//w needs opening and closing!
			stardog.open();
			JenaTrainUpdater updater = new JenaTrainUpdater(stardog, config);
//			t.doUpdate(0);
			updater.connect();
			TrainUpdateService scheduler = new TrainUpdateService(updater, config);
			scheduler.startAsync();
			System.out.println("Press Enter to exit");
			System.in.read();
			scheduler.stopAsync();
			stardog.close();
			updater.close();
		} catch (StardogException e) {
			// TODO Auto-generated catch block
			if(e.getMessage()!=null)
				System.out.println("Connection to Stardog Failed: " + e.getMessage());
			else
				System.out.println("Connection to Stardog Failed...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
