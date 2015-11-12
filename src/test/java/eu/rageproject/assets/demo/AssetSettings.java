package eu.rageproject.assets.demo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;

import eu.rageproject.asset.manager.BaseSettings;

public class AssetSettings extends BaseSettings {

	@XmlElement
	private String testProperty;
	
	@XmlElement
	private boolean testReadOnly;

	@XmlElementWrapper(name="ListItem")
	@XmlList
	private String[] testList;
	
	public AssetSettings() {
		this.testProperty = "Hello Default World";
		this.testReadOnly = true;
		this.testList = new String[] { "Hello", "List", "World" }; 
	}

	public String getTestProperty() {
		return testProperty;
	}

	public void setTestProperty(String testProperty) {
		this.testProperty = testProperty;
	}

	public String[] getTestList() {
		return testList;
	}

	public void setTestList(String[] testList) {
		this.testList = testList;
	}

	public boolean isTestReadOnly() {
		return testReadOnly;
	}

}