package eu.rageproject.asset.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
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

	public static final String LOGGER_KEY = "eu.rageproject.assetManager";

	private static final Pattern CLASS_PATTERN = Pattern.compile("^([^_]+)_\\d+$");

	private static final Logger log = Logger.getLogger(LOGGER_KEY);

	private static AssetManager INSTANCE;

	public static AssetManager getInstance() {
		if (INSTANCE == null) {
			AssetManager.INSTANCE = new AssetManager();
		}
		return AssetManager.INSTANCE;
	}
	
	static void setInstance(AssetManager instance) {
		INSTANCE = instance;
	}
	
	private int idGenerator;

	private Map<String, IAsset> assets;

	private IBridge bridge;

	/**
	 * Avoid manual instantiation
	 */
	private AssetManager() {
		this.idGenerator = 0;
		this.assets = new HashMap<>();
		initEventSystem();
	}

	private void initEventSystem() {
		PubSubz.getInstance().define("EventSystem.Init");
		PubSubz.getInstance().publish("EventSystem.Init', 'hello event!");
	}

	@SuppressWarnings("unchecked")
	public <T> T findAssetByClass(String clazz) {
		for (Map.Entry<String, IAsset> e : this.assets.entrySet()) {
			Matcher m = CLASS_PATTERN.matcher(e.getKey());
			if (m.matches() && m.group(1).equals(clazz)) {
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
	public IAsset findAssetById(String id) {
		return this.assets.get(id);
	}

	/**
	 * Searches for assets by class.
	 * 
	 * @param clazz
	 *            The
	 * @return
	 */
	public Iterable<IAsset> findAssetsByClass(String clazz) {
		List<IAsset> results = new LinkedList<>();
		for (Map.Entry<String, IAsset> e : this.assets.entrySet()) {
			Matcher m = CLASS_PATTERN.matcher(e.getKey());
			if (m.matches() && m.group(1).equals(clazz)) {
				results.add(e.getValue());
			}
		}
		return results;
	}

	public String registerAssetInstance(IAsset asset, String clazz) {
		for (Map.Entry<String, IAsset> e : assets.entrySet()) {
			if (e.getValue() == asset) {
				return e.getKey();
			}
		}

		String id = String.format("%s_%d", clazz, idGenerator++);

		log.info(String.format("Registering Asset %s/%s as %s", asset.getClassName(), clazz, id));

		assets.put(id, asset);

		log.finest(String.format("Registered %d Asset(s)", assets.size()));

		return id;
	}

	public IBridge getBridge() {
		return bridge;
	}

	public void setBridge(IBridge bridge) {
		this.bridge = bridge;
	}

	/**
	 * Reports version and dependencies.
	 * 
	 * @return The version and dependencies report.
	 */
    public String getVersionAndDependenciesReport() {
            int col1w = 40;
            int col2w = 32;

            StringBuilder report = new StringBuilder();

            // Get system dependant end of line separators
            String eol = System.getProperty("line.separator");
            
            report.append(padRight("Asset", col1w - "Asset".length()));
            report.append(padRight("| Depends on", col2w)).append(eol);
            report.append(padRight("", col1w, '-'));
            report.append("+");
            report.append(padRight("", col2w-1, '-')).append(eol);

            for (Map.Entry<String, IAsset> e : this.assets.entrySet()) {
            	IAsset asset = e.getValue();
            	String artifact = String.format("%s v%s", asset.getClassName(), asset.getVersion());
                report.append(padRight(artifact, col1w - artifact.length()));

                int cnt = 0;
                for (Map.Entry<String, String> dependency : asset.getDependencies().entrySet()) {
                    //! Better version matches (see Microsoft).
                    // 
                    //! https://msdn.microsoft.com/en-us/library/system.version(v=vs.110).aspx
                    //
                    //! dependency.value has min-max format (inclusive) like:
                    // 
                    //? v1.2.3-*        (v1.2.3 or higher)
                    //? v0.0-*          (all versions)
                    //? v1.2.3-v2.2     (v1.2.3 or higher less than or equal to v2.1)
                    //
                	String depencyVersion = dependency.getValue();
                    String[] vrange = depencyVersion.split("-");

                    Version low = null;

                    Version hi = null;

                    switch (vrange.length) {
                        case 1:
                            low = new Version(vrange[0]);
                            hi = low;
                            break;
                        case 2:
                            low = new Version(vrange[0]);
                            if ("*".equals(vrange[1])) {
                                hi = new Version(99, 99);
                            } else {
                                hi = new Version(vrange[1]);
                            }
                            break;

                        default:
                            break;
                    }

                    Boolean found = false;

                    if (low != null) {
                        for (IAsset dep : findAssetsByClass(dependency.getKey())) {
                            Version vdep = new Version(dep.getVersion());
                            if (low.compareTo(vdep) <= 0 && vdep.compareTo(hi) <= 0) {
                                found = true;
                                break;
                            }
                        }

                        report.append(String.format("| %s v%s [%s]", dependency.getKey(), dependency.getValue(), found ? "resolved" : "missing")).append(eol);
                    } else {
                        report.append("error");
                    }

                    if (cnt != 0) {
                        report.append(padRight("", col1w - 1));
                    }

                    cnt++;
                }

                if (cnt == 0) {
                    report.append(String.format("| %s", "No dependencies")).append(eol);
                }
            }

            report.append(padRight("", col1w, '-'));
            report.append("+");
            report.append(padRight("", col2w-1, '-')).append(eol);

            return report.toString();
    }

	private String padRight(String base, int quantity) {
		return padRight(base, quantity, ' ');
	}

	private String padRight(String base, int quantity, char paddingChar) {
		StringBuilder buffer = new StringBuilder(base.length() + quantity);
		buffer.append(base);
		for (int i = 0; i < quantity; i++) {
			buffer.append(paddingChar);
		}

		return buffer.toString();
	}

	/**
	 * Mimics required C#'s System.Version class functionality
	 * 
	 * @author Ivan Martinez-Ortiz
	 *
	 */
	private static class Version implements Comparable<Version> {

		private final int major;

		private final int minor;

		private final int build;

		private final int revision;

		public Version() {
			this(0, 0, -1, -1);
		}

		public Version(int major, int minor) {
			this(major, minor, -1, -1);
		}

		public Version(int major, int minor, int build) {
			this(major, minor, build, -1);
		}

		public Version(int major, int minor, int build, int revision) {
			this.major = major;
			this.minor = minor;
			this.build = build;
			this.revision = revision;
		}
		
		protected Version(int[] version) {
			this(version[0], version[1], version[2], version[3]);
		}

		public Version(String version) {
    		this(doParse(version));
    	}

		private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+)(?:\\.(\\d+))?)?$");
		
		private static int[] doParse(String version) {
			int[] versionArray = new int[] {0, 0, -1, -1};
			
			Matcher m = VERSION_PATTERN.matcher(version);
			if (m.matches()) {
				versionArray[0] = Integer.parseInt(m.group(1), 10);
				versionArray[1] = Integer.parseInt(m.group(2), 10);
				if (m.groupCount() > 3 && m.group(3) != null) {
					versionArray[2] = Integer.parseInt(m.group(3), 10);
				}
				if (m.groupCount() > 4 && m.group(4) != null) {
					versionArray[3] = Integer.parseInt(m.group(4), 10);
				}
			}
			return versionArray;
		}

		public static Version parse(String version) {
			return new Version(version);
		}

		/**
		 * The components of Version in decreasing order of importance are:
		 * major, minor, build, and revision. An unknown component is assumed to
		 * be older than any known component. For example:
		 * 
		 * <ul>
		 * <li>Version 1.1 is older than version 1.1.0.</li>
		 * <li>Version 1.1 is older than version 1.1.1.</li>
		 * <li>Version 1.1 is older than version 1.1.2.3.</li>
		 * <li>Version 1.1.2 is older than version 1.1.2.4.</li>
		 * <li>Version 1.2.5 is newer than version 1.2.3.4.</li>
		 * 
		 * @param v
		 *            {@code Version} to compare to.
		 * 
		 * @return -1 if this {@code Version} is older than {@code v}, 0 if
		 *         this {@code Version} is the same than {@code v} and 1 if this
		 *         {@code Version} is newer than {@code v} or {@code v} is
		 *         {@code null}.
		 * @throws ClassCastException
		 */
		@Override
		public int compareTo(Version v) throws ClassCastException {
			return -1;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + build;
			result = prime * result + major;
			result = prime * result + minor;
			result = prime * result + revision;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Version other = (Version) obj;
			if (build != other.build)
				return false;
			if (major != other.major)
				return false;
			if (minor != other.minor)
				return false;
			if (revision != other.revision)
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(this.major).append('.').append(this.minor);
			if (this.build != -1) {
				builder.append('.').append(this.build);
			}
			if (this.revision != -1) {
				builder.append('.').append(this.revision);
			}
			return builder.toString();
		}
	}
}
