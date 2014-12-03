package uk.co.jtutcher.trainupdater;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AppConfig verifies the Train Updater application's configuration file through its static 'validate' method
 * @author Jon
 *
 */
public class AppConfig {
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	/**
	 * Validates a TrainUpdater config file by checking that it has all necessary properties
	 * @param c
	 * @return
	 */
	public static boolean validate(Configuration c) {
		logger.trace("Validating Config file");
		boolean vres = true;
		
		if(!c.containsKey("app.trains")) vres = false;
		if(!c.containsKey("app.mintc")) vres = false;
		if(!c.containsKey("app.maxtc")) vres = false;
		if(!c.containsKey("app.refresh")) vres = false;

		if(!c.containsKey("stardog.url")) vres = false;
		if(!c.containsKey("stardog.db")) vres = false;
		if(!c.containsKey("stardog.user")) vres = false;
		if(!c.containsKey("stardog.pass")) vres = false;

		if(!c.containsKey("mysql.driver")) vres = false;
		if(!c.containsKey("mysql.url")) vres = false;
		if(!c.containsKey("mysql.user")) vres = false;
		if(!c.containsKey("mysql.pass")) vres = false;
		if(!c.containsKey("railway.name")) vres = false;

		if(!vres)logger.info("Configuration file found to be invalid");
		return vres;
	}
}
