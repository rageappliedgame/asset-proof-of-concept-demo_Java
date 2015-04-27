package eu.rageproject.assets.demo;

import java.io.IOException;

import eu.rageproject.assets.AssetManager;
import eu.rageproject.assets.PubSubz;
import eu.rageproject.assets.PubSubz.TopicEvent;
import eu.rageproject.assets.logger.Logger;
import eu.rageproject.assets.logger.Logger.LoggerListener;

/**
 * Techical demo
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public class App {
	public static void main( String[] args ) throws IOException
    {
    	//! Add assets and automatically create the Asset Manager. 
        // 
		DemoAsset asset1 = new DemoAsset();
        DemoAsset asset2 = new DemoAsset();
        Logger asset3 = new Logger();
        Logger asset4 = new Logger();
        DialogueAsset asset5 = new DialogueAsset();

        asset3.log("Asset1: " + asset1.getClassName() + ", " + asset1.getId());
        asset3.log("Asset2: " + asset2.getClassName() + ", " + asset2.getId());
        asset3.log("Asset3: " + asset3.getClassName() + ", " + asset3.getId());
        asset3.log("Asset4: " + asset4.getClassName() + ", " + asset4.getId());
        asset3.log("Asset5: " + asset5.getClassName() + ", " + asset5.getId());

        // Use the new Logger directly. 
        // 
        asset3.log("LogByLogger: " + asset3.getClassName() + ", " + asset3.getId());

        // Test if asset1 can find the Logger (asset3) thru the AssetManager. 
        // 
        asset1.publicMethod("Hello World (console.log)");

        //! TODO Implement replacing method behavior.
        //
        // Replace the 2nd Logger's log method by a native version supplied by the Game Engine. 
        asset4.addLoggerListener(new MyLogger()); //or cc.log in Cocos2D-html5; 

        // Check the results for both Loggers differ (one message goes to the console, the other shows as an alert). 
        // 
        asset1.publicMethod("Hello Different World (Mixed Logging)");

        //! Event Subscription.
        // 
        // Define an event, subscribe to it and fire the event. 
        // 
        PubSubz pubsubz = PubSubz.getInstance();
        pubsubz.define("EventSystem.Msg");

        //! Using a method.
        // 
        {
            String eventId = pubsubz.subscribe("EventSystem.Msg", new MyEventHandler());

            pubsubz.publish("EventSystem.Msg", "hello", "from", "demo.html!");

            pubsubz.unsubscribe(eventId);
        }

        //! Using anonymous delegate.
        // 
        {
            String eventId = pubsubz.subscribe("EventSystem.Msg", new TopicEvent() {
            	@Override
            	public void topicUpdated(String topic, Object... params) {
            		System.out.printf("[demo.html].{%s}: [{%s}] (anonymous inner class)\n", topic, argsToString(params));
            	}
            });

            pubsubz.publish("EventSystem.Msg", "hello", "from", "demo.html!");

            pubsubz.unsubscribe(eventId);
        }

        //! Check if id and class can still be changed (shouldn't). 
        // 
        //asset4.Id = "XYY1Z"; 
        //asset4.Class = "test"; 
        //asset4.log("Asset4: " + asset4.Class + ", " + asset4.Id); 

        //! Test if we can re-register without creating new stuff in the register (i.e. get the existing unique id returned). 
        // 
        System.out.printf("Trying to re-register: %s\n", AssetManager.getInstance().registerAssetInstance(asset4, asset4.getClassName()));

        //! DialogAsset.
        // 
        asset5.loadScript("me", App.class.getResourceAsStream("/script.txt"));

        // Interacting using ask/tell 

        asset5.interact("me", "player", "banana");

        // Interacting using branches 
        // 
        asset5.interact("me", "player");
        asset5.interact("me", "player", 2); //Answer id 2 

        asset5.interact("me", "player");
        asset5.interact("me", "player", 6); //Answer id 6 

        asset5.interact("me", "player");

        System.in.read();
    }


	public static String argsToString(Object... params) {
		StringBuilder buffer = new StringBuilder();
		int cont = 0;
		for (Object a : params) {
			if (cont > 0) {
				buffer.append(";");
			}
			buffer.append(a);
		}
		return buffer.toString();
	}
	
	private static class MyLogger implements LoggerListener {

		@Override
		public void logEvent(String msg) {
			System.out.printf("Custom Logging: %s\n", msg);
		}

	}

	private static class MyEventHandler implements TopicEvent {

		@Override
		public void topicUpdated(String topic, Object... params) {
			System.out.printf("[demo.html].{%s}: [{%s}]\n", topic, argsToString(params));
		}
	}
}
