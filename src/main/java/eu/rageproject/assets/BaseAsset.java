package eu.rageproject.assets;

import eu.rageproject.assets.PubSubz.TopicEvent;

/**
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public abstract class BaseAsset implements Asset {

	private String id;
	
	private String testSubscription;

	protected BaseAsset(String id) {
        this.id = AssetManager.getInstance().registerAssetInstance(this, this.getClassName());


        testSubscription = PubSubz.getInstance().subscribe("EventSystem.Init", new TopicEvent() {
        	public void topicUpdated(String topic, Object... params) {
        		System.out.printf("[{0}].{1}: {2}", BaseAsset.this.id, topic, params);
        	}
        });
	}
	
	@Override
	public String getClassName() {
		return getClass().getName();
	}
	
	@Override
	public String getId() {
		return id;
	}
}
