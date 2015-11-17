package eu.rageproject.asset.manager;

/**
 * Interface for default settings.
 * <p>
 * This Interface is used to:
 * </p>
 * <ul>
 * <li>Check if an asset has default (application) settings that override
 * build-in default settings.</li>
 * <li>Load these settings from the game environment.</li>
 * <li>In certain environments write the actual settings as application
 * defaults. This could for instance be Unity in editor mode.</li
 * </ul>
 * 
 * Default settings and application default settings are read-only at run-time.
 * If modification and storage is needed at run-time, the {@link #IDataStorage}
 * interface could be used i.c.m. {@link #ISettings} methods.
 */
public interface IDefaultSettings {

	/**
	 * Check if a {@code clazz} with {@code id} has default settings.
	 *
	 * @param clazz
	 *            The classname.
	 * @param id
	 *            The identifier.
	 *
	 * @return {@code true} if default settings, {@code false}
	 *         otherwise.
	 */
	boolean hasDefaultSettings(String clazz, String id);

	/**
	 * Loads default settings for a {@code clazz} with {@code id}.
	 * 
	 * @param clazz
	 *            The classname.
	 * @param id
	 *            The identifier.
	 * 
	 * @return The default settings.
	 */
	String loadDefaultSettings(String clazz, String id);

	/**
	 * Saves a default settings for a {@code clazz} with {@code id}.
	 * 
	 * @param clazz
	 *            The classname.
	 * @param id
	 *            The identifier.
	 * @param fileData
	 *            Data to save.
	 */
	void saveDefaultSettings(String Class, String Id, String fileData);
}
