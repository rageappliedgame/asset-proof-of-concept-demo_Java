package eu.rageproject.assets.logger;

import java.util.LinkedList;
import java.util.List;

import eu.rageproject.assets.BaseAsset;

/**
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public class Logger extends BaseAsset {

	public interface LoggerListener {
		public void logEvent(String msg);
	}
	
	
	private List<LoggerListener> listeners;
	
	protected Logger(String id) {
		super(id);
		this.listeners = new LinkedList<>();
		this.listeners.add(new DefaultLoggerListener());
	}
	
	public void log(String msg) {
		for(LoggerListener listener: this.listeners) {
			listener.logEvent(msg);				
		}
	}
	
	private void doLog(String msg) {
		//! If we're the only subscriber, we expose default behavior.
		if (this.listeners.size() == 1) {
			System.out.println(msg);			
		}
	}

	private class DefaultLoggerListener implements LoggerListener {

		@Override
		public void logEvent(String msg) {
			doLog(msg);
		}
		
	}
}
