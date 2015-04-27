package eu.rageproject.assets;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * 
 * <strong>IMPLEMENTATION NOTE</strong>
 * 
 * This class is not thread-safe
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public final class AssetManager {

	public static final String LOGGER_KEY = "eu.rageproject.assets";

	private static final Pattern CLASS_PATTERN = Pattern
			.compile("^[^_]+$");

	private static final Logger log = Logger.getLogger(LOGGER_KEY);

	private static AssetManager INSTANCE;

	private static int idGenerator = 0;

	public static AssetManager getInstance() {
		if (INSTANCE == null) {
			AssetManager.INSTANCE = new AssetManager();
		}
		return AssetManager.INSTANCE;
	}

	private Map<String, Asset> assets;

	/**
	 * Avoid manual instantiation
	 */
	private AssetManager() {
		this.assets = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <T> T findAssetByClass(String clazz) {
		for (Map.Entry<String, Asset> e : this.assets.entrySet()) {
			if (CLASS_PATTERN.matcher(e.getKey()).matches()) {
				return (T) e.getValue();
			}
		}
		return null;
	}

	/**
	 * Searches for the first <code>Asset</code> by identifier.
	 * 
	 * @param id
	 *            The asset identifier.
	 * 
	 * @return return the <code>Asset</code> or <code>null</code> if the
	 *         <code>id</code> is not found.
	 */
	public Asset findAssetById(String id) {
		return this.assets.get(id);
	}

	/**
	 * Searches for assets by class.
	 * 
	 * @param clazz
	 *            The
	 * @return
	 */
	public Iterable<Asset> findAssetsByClass(String clazz) {
		List<Asset> results = new LinkedList<>();
		for (Map.Entry<String, Asset> e : this.assets.entrySet()) {
			if (CLASS_PATTERN.matcher(e.getKey()).matches()) {
				results.add(e.getValue());
			}
		}
		return results;
	}

	public String registerAssetInstance(Asset asset, String clazz) {
		for (Map.Entry<String, Asset> e : assets.entrySet()) {
			if (e.getValue() == asset) {
				return e.getKey();
			}
		}

		String id = String.format("%s_%d", clazz, idGenerator++);

		log.info(String.format("Registering Asset %s/%s as %s", asset
				.getClassName(), clazz, id));

		assets.put(id, asset);

		log.finest(String.format("Registered %d Asset(s)", assets.size()));

		return id;
	}
}
