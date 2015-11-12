package eu.rageproject.assets.demo;

import eu.rageproject.asset.manager.AssetManager;
import eu.rageproject.asset.manager.BaseAsset;
import eu.rageproject.asset.manager.IAsset;
import eu.rageproject.assets.logger.Logger;

public class DemoAsset extends BaseAsset {

	protected DemoAsset() {
	}
	
    public void publicMethod(String msg)
    {
        //! TODO Nicer would be to return the correct type of Asset.
        //
        Iterable<IAsset> loggers = AssetManager.getInstance().findAssetsByClass("Logger");

        for(IAsset asset : loggers) {
        	((Logger)asset).log(asset.getId() + " - " + msg);
        }
    }
}
