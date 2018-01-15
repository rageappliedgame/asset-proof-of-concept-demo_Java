package eu.rageproject.asset.manager;

import java.util.Map;

/**
 * Interface for asset.
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public interface IAsset {

	public String getClassName();
	
	public String getId();
	
	public Map<String, String> getDependencies();
	
	public String getMaturity();
	
	public ISettings getSettings();
	
	public void setSettings(final ISettings settings);
	
	public String getVersion();
	
	public IBridge getBridge();
	
	public void setBridge(final IBridge bridge);
}
