package eu.rageproject.assets.demo;

import java.util.Map;
import java.util.TreeMap;

import eu.rageproject.asset.manager.AssetManager;
import eu.rageproject.asset.manager.BaseAsset;
import eu.rageproject.asset.manager.IAsset;
import eu.rageproject.asset.manager.IDataStorage;
import eu.rageproject.assets.logger.Logger;

public class DemoAsset extends BaseAsset {
	
	private String fData = "Hello Storage World";
	
	private String fId1 = "Hello1.txt";
	
	private String fId2 = "Hello2.txt";

	/**
	 * The file storage.
     */
	private Map<String, String> fileStorage;

	/**
	 * Options for controlling the operation.
	 */
	private AssetSettings settings;

	protected DemoAsset() {
		this.fileStorage = new TreeMap<>();
		settings = new AssetSettings();
		settings.setTestProperty(settings.getTestProperty()+"test");
		super.setSettings(this.settings);
	}

	/**
	 * Executes the remove operation.
	 */
	public void doArchive() {
		IDataArchive ds = getInterface(IDataArchive.class);

		if (ds != null) {
			ds.archive(fId2);
		} else {
			fileStorage.remove(fId2);
		}
	}

	/**
	 * Executes the list operation.
	 * 
	 * @return a list of files
	 */
	public String[] doList() {
		IDataStorage ds = getInterface(IDataStorage.class);

		if (ds != null) {
			return ds.files();
		} else {
			return fileStorage.keySet().toArray(new String[0]);
		}
	}

	/**
	 * Executes the load operation.
	 * 
	 * @param filename
	 *            The filename.
	 * 
	 * @return a string.
	 */
	public String doLoad(String filename) {
		IDataStorage ds = getInterface(IDataStorage.class);

		if (ds != null) {
			return ds.load(filename);
		} else {
			return fileStorage.get(filename);
		}
	}

	/**
	 * Executes the remove operation.
	 */
	public void doRemove() {
		IDataStorage ds = getInterface(IDataStorage.class);

		if (ds != null) {
			ds.delete(fId1);
		} else {
			fileStorage.remove(fId1);
		}
	}

	/**
	 * Executes the store operation.
	 */
	public void doStore() {
		IDataStorage ds = getInterface(IDataStorage.class);

		if (ds != null) {
			ds.save(fId1, fData);
			ds.save(fId2, fData);
		} else {
			fileStorage.put(fId1, fData);
			fileStorage.put(fId2, fData);
		}
	}

	public void publicMethod(String msg) {
		// ! TODO Nicer would be to return the correct type of Asset.
		//
		Iterable<IAsset> loggers = AssetManager.getInstance().findAssetsByClass("Logger");

		for (IAsset asset : loggers) {
			((Logger) asset).log(asset.getId() + " - " + msg);
		}
	}
}
