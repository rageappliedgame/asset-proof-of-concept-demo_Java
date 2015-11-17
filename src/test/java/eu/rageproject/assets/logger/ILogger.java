package eu.rageproject.assets.logger;

/**
 * Interface for logger.
 */
public interface ILogger {
	
	public String getPrefix();
	
    public void setPrefix(String prefix);

    /**
     * Logs a message.
     * 
     * @param msg Message to log
     */
    public void doLog(String msg);
}
