package eu.rageproject.assets.logger;

import eu.rageproject.asset.manager.BaseAsset;

/**
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public class Logger extends BaseAsset {

	public static final String LOGGER_KEY = "eu.rageproject.assets.logger";

	private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LOGGER_KEY);
	
	public Logger() {
	}
	
	public void log(final String msg) {
        //! See what bridge code to call, Asset, Asset Manager or just expose Default behavior (if any).
        // 
        ILogger logger = getInterface(ILogger.class);
        if (logger != null) {
            // Use a supplied bridge.
            logger.doLog(msg);
        } else {
            Logger.logger.fine(msg);
        }
	}
}
