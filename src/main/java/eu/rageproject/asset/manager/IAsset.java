package eu.rageproject.asset.manager;

import java.util.Map;

/**
 * Interface for asset.
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public interface IAsset {

	String getClassName();
	
	String getId();
	
	Map<String, String> getDependencies();
	
	String getMaturity();
	
	ISettings getSettings();
	
	void setSettings(ISettings settings);
	
	String getVersion();
	
	IBridge getBridge();
	
	void setBridge(IBridge bridge);
}
