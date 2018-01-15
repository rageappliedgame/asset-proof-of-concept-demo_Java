package eu.rageproject.asset.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public class PubSubz {

	
	public static interface TopicEvent {
		public void topicUpdated(final String topic, final Object... params);
	}
	
	private static PubSubz INSTANCE;
	
	public static final PubSubz getInstance() {
		if ( PubSubz.INSTANCE == null ) {
			PubSubz.INSTANCE = new PubSubz();
		}
		return PubSubz.INSTANCE;
	}
	
	static final void setInstance(final PubSubz instance) {
		INSTANCE = instance;
	}
	
	private int subUid;
	
	private Map<String, Map<String, TopicEvent>> topics;
	
	
	private PubSubz() {
		this.subUid = 0;
		this.topics = new HashMap<>();
	}
	
	public boolean define(String topic) {
		if (! topics.containsKey(topic)) {
			topics.put(topic,  new HashMap<String, TopicEvent>());
			return true;
		}
		return false;
	}
	
	public boolean publish(final String topic, final Object... params) {
		if ( ! topics.containsKey(topic)) {
			return false;
		}
		
		for(Map.Entry<String, TopicEvent> entry : topics.get(topic).entrySet()) {
			entry.getValue().topicUpdated(topic, params);
		}
		
		return true;
	}
	
	public String subscribe(final String topic, final TopicEvent listener) {
		define(topic);
		
		String token = Integer.toString(++this.subUid);
		topics.get(topic).put(token, listener);
		
		return token;
	}
	
	public boolean unsubscribe(final String token) {
		for (Map.Entry<String, Map<String, TopicEvent>> topic: topics.entrySet() ) {
			Iterator<Map.Entry<String, TopicEvent>> subscribers = topic.getValue().entrySet().iterator();
			
			while (subscribers.hasNext()) {
				Map.Entry<String, TopicEvent> subscriber = subscribers.next();
				if (subscriber.getKey().equals(token)) {
					subscribers.remove();
					return true;
				}
			}
		}
		return false;
	}
}
