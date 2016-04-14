package eu.rageproject.asset.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple publish-subscribe mechanism for assets.
 *
 * @author Ivan Martinez-Ortiz
 */
public class PubSubz {

	
	public interface TopicListener {
		void topicUpdated(String topic, Object... params);
	}
	
	private static PubSubz INSTANCE;
	
	public static final PubSubz getInstance() {
		if ( PubSubz.INSTANCE == null ) {
			PubSubz.INSTANCE = new PubSubz();
		}
		return PubSubz.INSTANCE;
	}
	
	static final void setInstance(PubSubz instance) {
		INSTANCE = instance;
	}
	
	private int subUid;
	
	private Map<String, Map<String, TopicListener>> topics;
	
	private PubSubz() {
		this.subUid = 0;
		this.topics = new HashMap<>();
	}
	
	public boolean define(String topic) {
		if (! topics.containsKey(topic)) {
			topics.put(topic,  new HashMap<String, TopicListener>());
			return true;
		}
		return false;
	}
	
	public boolean publish(String topic, Object... params) {
		if ( ! topics.containsKey(topic)) {
			return false;
		}
		
		for(Map.Entry<String, TopicListener> entry : topics.get(topic).entrySet()) {
			entry.getValue().topicUpdated(topic, params);
		}
		
		return true;
	}
	
	public String subscribe(String topic, TopicListener listener) {
		define(topic);
		
		String token = Integer.toString(++this.subUid);
		topics.get(topic).put(token, listener);
		
		return token;
	}
	
	public boolean unsubscribe(String token) {
		for (Map.Entry<String, Map<String, TopicListener>> topic: topics.entrySet() ) {
			Iterator<Map.Entry<String, TopicListener>> subscribers = topic.getValue().entrySet().iterator();
			
			while (subscribers.hasNext()) {
				Map.Entry<String, TopicListener> subscriber = subscribers.next();
				if (subscriber.getKey().equals(token)) {
					subscribers.remove();
					return true;
				}
			}
		}
		return false;
	}
}
