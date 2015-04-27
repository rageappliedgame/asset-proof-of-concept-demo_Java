package eu.rageproject.assets.demo;

import eu.rageproject.assets.Asset;
import eu.rageproject.assets.AssetManager;
import eu.rageproject.assets.BaseAsset;
import eu.rageproject.assets.logger.Logger;

public class DemoAsset extends BaseAsset {

	protected DemoAsset() {
	}
	
    public void publicMethod(String msg)
    {
        //! TODO Nicer would be to return the correct type of Asset.
        //
        Iterable<Asset> loggers = AssetManager.getInstance().findAssetsByClass("Logger");

        for(Asset asset : loggers) {
        	((Logger)asset).log(asset.getId() + " - " + msg);
        }
    }
}
