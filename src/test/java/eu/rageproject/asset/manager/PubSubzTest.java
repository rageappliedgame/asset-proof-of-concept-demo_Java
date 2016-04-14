package eu.rageproject.asset.manager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import eu.rageproject.asset.manager.PubSubz.TopicListener;

public class PubSubzTest {

	@Test
	public void testPubSubOnlyRegisteredToTopic() {
		// Given
		TopicListener listener1 = mock(TopicListener.class);
		TopicListener listener2 = mock(TopicListener.class);

		PubSubz cut = PubSubz.getInstance();

		cut.subscribe("topic1", listener1);
		cut.subscribe("topic2", listener2);

		// When
		cut.publish("topic1");

		// Then
		verify(listener1, times(1)).topicUpdated(anyString(), anyVararg());
		verify(listener2, never()).topicUpdated(anyString(), anyVararg());
	}

	@Test
	public void testPubSubSeveralRegisteredListeners() {
		// Given
		TopicListener listener1 = mock(TopicListener.class);
		TopicListener listener2 = mock(TopicListener.class);

		PubSubz cut = PubSubz.getInstance();

		cut.subscribe("topic1", listener1);
		cut.subscribe("topic1", listener2);

		// When
		cut.publish("topic1");

		// Then
		verify(listener1, times(1)).topicUpdated(anyString(), anyVararg());
		verify(listener2, times(1)).topicUpdated(anyString(), anyVararg());
	}
}
