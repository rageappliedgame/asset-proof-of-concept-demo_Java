package eu.rageproject.asset.manager;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Information about the rage version.
 * 
 * <p>
 * <strong>VERSION INFO EXAMPLE</strong>
 * </p>
 * {@code
   <version>
     <id>asset</id>
     <major>1</major>
     <minor>2</minor>
     <build>3</build>
     <revision></revision>
     <maturity>alpha</maturity>
     <dependencies>
       <depends minVersion = "1.2.3" > Logger </ depends >
     </dependencies >
    </version>
    }
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "version")
public class RageVersionInfo {

	@XmlElement(name = "id")
	private String id;

	@XmlElement(name = "major")
	private int major;

	@XmlElement(name = "minor")
	private int minor;

	@XmlElement(name = "build")
	private int build;

	@XmlElement(name = "revision", required=false)
	private int revision;

	@XmlElement(name = "maturity")
	private String maturity;

	@XmlElementWrapper(name = "dependencies")
	@XmlElement(name = "depends")
	private List<Dependency> dependencies;

	/**
	 * Initializes a new instance of the {@link #AssetManagerPackage
	 * .RageVersionInfo} class.
	 */
	public RageVersionInfo() {
		this.id = null;
		this.major = -1;
		this.minor = -1;
		this.build = -1;
		this.revision = -1;
		this.dependencies = new LinkedList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getBuild() {
		return build;
	}

	public void setBuild(int build) {
		this.build = build;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String getMaturity() {
		return maturity;
	}

	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * Loads version information..
	 * 
	 * @param xml
	 *            The XML version representation.
	 * 
	 * @return the loaded version info
	 */
	public static RageVersionInfo loadVersionInfo(String xml) {
		RageVersionInfo info = JAXB.unmarshal(new StringReader(xml), RageVersionInfo.class);
		return info;
	}

	/**
	 * Saves the version information.
	 * 
	 * @return the XML version info.
	 */
	public String saveVersionInfo() {
		StringWriter buffer = new StringWriter();
		JAXB.marshal(this, buffer);
		return buffer.toString();
	}

	/**
	 * A dependency.
	 * 
	 * <p>
	 * <strong>DEPENDENCY EXAMPLE</strong>
	 * </p>
	 * {@code
	   <depends minVersion = "1.2.3" > Logger </ depends >
	    }
	 * 
	 * @author Ivan Martinez-Ortiz
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "depends")
	public static class Dependency {
		
		@XmlAttribute(name="minVersion", required=true)
		private String minVersion;
		
		@XmlAttribute(name="maxVersion", required=false)
		private String maxVersion;
		
		@XmlValue
		private String name;
		
		/**
		 * Initializes a new dependency
		 */
		public Dependency() {
		}

		public String getMinVersion() {
			return minVersion;
		}

		public void setMinVersion(String minVersion) {
			this.minVersion = minVersion;
		}

		public String getMaxVersion() {
			return maxVersion;
		}

		public void setMaxVersion(String maxVersion) {
			this.maxVersion = maxVersion;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.major).append(".").append(this.minor).append(".").append(this.build);
		if (this.revision >= 0) {
			builder.append(".").append(this.revision);
		}
		return builder.toString();
	}
}