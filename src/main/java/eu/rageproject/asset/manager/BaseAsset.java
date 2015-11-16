package eu.rageproject.asset.manager;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.xml.bind.JAXB;

import eu.rageproject.asset.manager.RageVersionInfo.Dependency;

/**
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public abstract class BaseAsset implements IAsset {

	private String id;

	private IBridge bridge;

	private RageVersionInfo versionInfo;

	private ISettings settings;

	protected BaseAsset() {
		this.id = AssetManager.getInstance().registerAssetInstance(this, this.getClassName());
		String xml = getVersionAndDependencies();
		if (!"".equals(xml)) {
			this.versionInfo = RageVersionInfo.loadVersionInfo(xml);
		} else {
			this.versionInfo = new RageVersionInfo();
		}

	}

	/**
	 * Version and dependencies file content.
	 */
	private String getVersionAndDependencies() {
		// ! <package>.Resources.<AssetType>.VersionAndDependencies.xml
		String xml = getEmbeddedResource(getClass().getPackage().getName(),
				String.format("%s.VersionAndDependencies.xml", getClass().getSimpleName()));
		return xml;
	}

	/**
	 * Gets embedded resource.
	 * 
	 * @param pkg
	 *            The package.
	 * @param res
	 *            The resource name.
	 *
	 * @return The embedded resource.
	 */
	protected String getEmbeddedResource(String pkg, String res) {
		String path = String.format("/%s/%s", pkg.replaceAll("\\.", "/"), res);

		InputStream in = getClass().getResourceAsStream(path);
		if (in != null) {
			try (Scanner s = new Scanner(in)) {
				return s.useDelimiter("\\Z").next();
			}
		}

		return "";
	}

	public BaseAsset(IBridge bridge) {
		this();
		this.bridge = bridge;
	}

	@Override
	public String getClassName() {
		return getClass().getSimpleName();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public IBridge getBridge() {
		return this.bridge;
	}

	@Override
	public void setBridge(IBridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public String getMaturity() {
		return this.versionInfo.getMaturity();
	}

	public ISettings getSettings() {
		return settings;
	}

	public void setSettings(ISettings settings) {
		this.settings = settings;
	}

	/**
	 * Checks if the asset has settings
	 * 
	 * @return {@code true} if this {@link Asset} has settings, {@code false}
	 *         otherwhise.
	 */
	public boolean hasSettings() {
		return this.settings != null;
	}

	@Override
	public String getVersion() {
		return this.versionInfo.toString();
	}

	public RageVersionInfo getVersionInfo() {
		return this.versionInfo;
	}

	private void setVersionInfo(RageVersionInfo versionInfo) {
		this.versionInfo = versionInfo;
	}

	public Map<String, String> getDependencies() {
		Map<String, String> result = new TreeMap<>();

		for (Dependency dep : this.versionInfo.getDependencies()) {
			String minv = "0.0";
			String depMinv = dep.getMinVersion();
			if (depMinv != null) {
				minv = depMinv;
			}
			String maxv = "*";
			String depMaxv = dep.getMaxVersion();
			if (depMaxv != null) {
				maxv = depMaxv;
			}

			result.put(dep.getName(), String.format("%s-%s", minv, maxv));
		}

		return result;
	}

	/**
	 * Returns an object which is an instance of the given class associated with
	 * this object. Returns null if no such object can be found.
	 * 
	 * @param adapter
	 *            the adapter class to look up
	 * 
	 * @return a object castable to the given class, or null if this object does
	 *         not have an adapter for the given class
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getInterface(Class<T> adapter) {
		if (this.bridge != null && adapter.isAssignableFrom(this.bridge.getClass())) {
			return (T) this.bridge;
		}

		IBridge assetManagerBridge = AssetManager.getInstance().getBridge();
		if (assetManagerBridge != null && adapter.isAssignableFrom(assetManagerBridge.getClass())) {
			return (T) assetManagerBridge;
		}

		return null;
	}

	/**
	 * Loads Settings object from Default (Design-time) Settings.
	 *
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public Boolean loadDefaultSettings() {
		IDefaultSettings ds = getInterface(IDefaultSettings.class);

		if (ds != null && hasSettings() && ds.hasDefaultSettings(getClassName(), getId())) {
			String xml = ds.loadDefaultSettings(getClassName(), getId());
			this.settings = settingsFromXml(xml);
			return true;
		}

		return false;
	}

	/**
	 * Loads Settings object as Run-time Settings.
	 * 
	 * @param filename
	 *            Filename of the file.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public Boolean loadSettings(String filename) {
		IDataStorage ds = getInterface(IDataStorage.class);

		if (ds != null && hasSettings() && ds.exists(filename)) {
			String xml = ds.load(filename);
			this.settings = settingsFromXml(xml);
			return true;
		}

		return false;
	}

	/**
	 * Saves Settings object as Default (Design-time) Settings.
	 * 
	 * @param force
	 *            Force to save settings even if the asset has default settings.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public Boolean saveDefaultSettings(boolean force) {
		IDefaultSettings ds = getInterface(IDefaultSettings.class);

		if (ds != null && hasSettings() && (force || !ds.hasDefaultSettings(getClassName(), getId()))) {
			ds.saveDefaultSettings(getClassName(), getId(), settingsToXml());

			return true;
		}

		return false;
	}

	/**
	 * Save asset's settings.
	 * 
	 * @param force
	 *            Force to save settings even if the asset has default settings.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public Boolean SaveSettings(String filename) {
		IDataStorage ds = getInterface(IDataStorage.class);

		if (ds != null && hasSettings()) {
			ds.save(filename, settingsToXml());

			return true;
		}

		return false;
	}

	/**
	 * 
	 * <strong>IMPLEMENTATION NOTE</strong>
	 * <p>
	 * ISettings implementations must be annotated and support JAXB required
	 * contracts to marshall and unmarshall classes.
	 * </p>
	 * 
	 * @param xml Xml to unmarshal.
	 * 
	 * @return a {@link ISettings} object implementation. 
	 */
	protected ISettings settingsFromXml(String xml) {
		return JAXB.unmarshal(new StringReader(xml), this.settings.getClass());
	}

	/**
	 * 
	 * <strong>IMPLEMENTATION NOTE</strong>
	 * <p>
	 * ISettings implementations must be annotated and support JAXB required
	 * contracts to marshall and unmarshall classes.
	 * </p>
	 * 
	 * @return Xml representation.
	 */
	protected String settingsToXml() {
		StringWriter writer = new StringWriter();
		JAXB.marshal(this.settings, writer);
		return writer.toString();
	}

}
