package eu.rageproject.asset.manager;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A base settings.
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
@XmlRootElement(name="settings")
public class BaseSettings implements ISettings {

	public BaseSettings() {
    }
}
