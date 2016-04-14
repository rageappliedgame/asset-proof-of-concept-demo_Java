package eu.rageproject.asset.manager;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A base settings object.
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
@XmlRootElement(name="settings")
public class BaseSettings implements ISettings {

	public BaseSettings() {
    }
}
