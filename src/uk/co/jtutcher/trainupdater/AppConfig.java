package uk.co.jtutcher.trainupdater;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class AppConfig {
	public AppConfig(String fName)
	{
		try {
			Configuration config = new PropertiesConfiguration(fName);
			System.out.println("Config: " + config.getInt("stardog.url"));
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static boolean validate(Configuration c) {
		if(!c.containsKey("app.trains")) return false;
		if(!c.containsKey("app.mintc")) return false;
		if(!c.containsKey("app.maxtc")) return false;
		if(!c.containsKey("app.refresh")) return false;

		if(!c.containsKey("stardog.url")) return false;
		if(!c.containsKey("stardog.db")) return false;
		if(!c.containsKey("stardog.user")) return false;
		if(!c.containsKey("stardog.pass")) return false;

		if(!c.containsKey("mysql.driver")) return false;
		if(!c.containsKey("mysql.url")) return false;
		if(!c.containsKey("mysql.user")) return false;
		if(!c.containsKey("mysql.pass")) return false;

		return true;
	}
}
