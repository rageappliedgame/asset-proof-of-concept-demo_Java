package eu.rageproject.asset.manager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import eu.rageproject.assets.demo.DemoAsset;
import eu.rageproject.assets.demo.IDataArchive;
import eu.rageproject.assets.dialogue.DialogueAsset;
import eu.rageproject.assets.logger.ILogger;
import eu.rageproject.assets.logger.Logger;

/**
 * Unit test for the demo
 */
public class AssetManagerTest {

	@Before
	public void setUp() {
		// Reset the AssetManager
		AssetManager.setInstance(null);
	}

	@Test
	public void testSetupAssetManager() {
		// Given
		DemoAsset asset1 = new DemoAsset();
		DemoAsset asset2 = new DemoAsset();
		Logger asset3 = new Logger();
		Logger asset4 = new Logger();
		DialogueAsset asset5 = new DialogueAsset();

		// When

		// Then
		assertThat(asset1.getId(), equalTo("DemoAsset_0"));
		assertThat(asset2.getId(), equalTo("DemoAsset_1"));
		assertThat(asset3.getId(), equalTo("Logger_2"));
		assertThat(asset4.getId(), equalTo("Logger_3"));
		assertThat(asset5.getId(), equalTo("DialogueAsset_4"));
	}

	@Test
	public void versionAndDependencyReport() {
		// Given
		DemoAsset asset1 = new DemoAsset();
		DemoAsset asset2 = new DemoAsset();
		Logger asset3 = new Logger();
		Logger asset4 = new Logger();
		DialogueAsset asset5 = new DialogueAsset();

		// When
		System.out.println(AssetManager.getInstance().getVersionAndDependenciesReport());

		// Then
		assertThat(asset3.getDependencies().size(), equalTo(0));
		assertThat(asset4.getDependencies().size(), equalTo(0));

		assertThat(asset5.getDependencies().size(), equalTo(1));
		assertThat(asset5.getDependencies().get("Logger"), equalTo("0.0-*"));

		assertThat(asset1.getDependencies().size(), equalTo(1));
		assertThat(asset1.getDependencies().get("Logger"), equalTo("1.2.3-*"));

		assertThat(asset2.getDependencies().size(), equalTo(1));
		assertThat(asset2.getDependencies().get("Logger"), equalTo("1.2.3-*"));
	}

	@Test
	public void testAssetToAssetCoordination() {
		// Given
		DemoAsset asset = new DemoAsset();

		Logger logger = mock(Logger.class, withSettings().extraInterfaces(IAsset.class));
		AssetManager.getInstance().registerAssetInstance((IAsset) logger, "Logger");

		// When
		asset.publicMethod("Hello World");

		// Then
		verify(logger, times(1)).log(anyString());
	}

	@Test
	public void testAssetToAssetManagerBridgeDelegation() {
		// Given
		DemoAsset asset = new DemoAsset();
		@SuppressWarnings("unused")
		Logger loggerAsset = new Logger();

		IBridge assetManagerBridge = mock(IBridge.class, withSettings().extraInterfaces(ILogger.class));
		AssetManager.getInstance().setBridge(assetManagerBridge);

		// When
		asset.publicMethod("Hello world");

		// Then
		verify((ILogger) assetManagerBridge, times(1)).doLog(anyString());
	}

	@Test
	public void testAssetToAssetBridgeDelegation() {
		// Given
		DemoAsset asset = new DemoAsset();
		@SuppressWarnings("unused")
		Logger loggerAsset = new Logger();
		Logger loggerAsset2 = new Logger();

		IBridge assetManagerBridge = mock(IBridge.class, withSettings().extraInterfaces(ILogger.class));
		AssetManager.getInstance().setBridge(assetManagerBridge);

		IBridge loggerAsset2Bridge = mock(IBridge.class, withSettings().extraInterfaces(ILogger.class));
		loggerAsset2.setBridge(loggerAsset2Bridge);

		// When
		asset.publicMethod("Hello world");

		// Then
		verify((ILogger) assetManagerBridge, times(1)).doLog(anyString());
		verify((ILogger) loggerAsset2Bridge, times(1)).doLog(anyString());
	}

	@Test
	public void testTryToRegisterAgainAnAsset() {
		// Given
		DemoAsset asset = new DemoAsset();

		// When
		String id = AssetManager.getInstance().registerAssetInstance(asset, "DemoAsset");

		// Then
		// the id of the already registered asset is returned
		assertThat(id, equalTo("DemoAsset_0"));
	}

	@Test
	public void tetDialogueAsset() throws Exception {

		// Given
		DialogueAsset asset = new DialogueAsset();
		asset.loadScript("me", AssetManagerTest.class.getResourceAsStream("/script.txt"));

		// Interacting using ask/tell

		asset.interact("me", "player", "banana");

		// Interacting using branches
		//
		asset.interact("me", "player");
		asset.interact("me", "player", 2); // Answer id 2

		asset.interact("me", "player");
		asset.interact("me", "player", 6); // Answer id 6

		asset.interact("me", "player");
	}

	/**
	 * Sample {@link eu.rageproject.asset.manager.IBridge} implementation
	 */
	static class Bridge implements IBridge, ILogger, IDataStorage, IDataArchive {

		private Path StorageDir = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "DataStorage");

		private Path ArchiveDir = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "Archive");

		private String prefix;

		public Bridge() {
			this("");
		}

		/**
		 * 
		 * @param prefix
		 *            Logger prefix
		 */

		public Bridge(String prefix) {
			this.prefix = prefix;

			if (!Files.exists(StorageDir)) {
				try {
					Files.createDirectory(StorageDir);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if (!Files.exists(ArchiveDir)) {
				try {
					Files.createDirectory(ArchiveDir);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		public void doLog(String msg) {
			System.out.println(this.prefix + msg);
		}

		@Override
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public String getPrefix() {
			return this.prefix;
		}

		@Override
		public boolean exists(String fileId) {
			return Files.exists(StorageDir.resolve(fileId));
		}

		@Override
		public String[] files() {
			return StorageDir.toFile().list();
		}

		@Override
		public void save(String fileId, String fileData) {
			try {
				Files.write(StorageDir.resolve(fileId), fileData.getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String load(String fileId) {
			try {
				return new String(Files.readAllBytes(StorageDir.resolve(fileId)), Charset.forName("UTF-8"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean delete(String fileId) {
			if (exists(fileId)) {
				try {
					Files.delete(StorageDir.resolve(fileId));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return true;
			}

			return false;
		}

		@Override
		public boolean archive(String fileId) {
			if (Files.exists(StorageDir.resolve(fileId))) {
				if (Files.exists(ArchiveDir.resolve(fileId))) {
					try {
						Files.delete(ArchiveDir.resolve(fileId));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

				String stampName = String.format("%s-%s.%s", getFileNameWithoutExtension(fileId),
						new SimpleDateFormat("yyyy-MM-dd [HH mm ss SSS]").format(new Date()), getFileExtension(fileId));

				try {
					Files.move(StorageDir.resolve(fileId), ArchiveDir.resolve(stampName));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return true;
			}

			return false;
		}

		private String getFileNameWithoutExtension(String fileId) {
			String fileNameWithoutExtension = fileId;
			int pos = fileId.lastIndexOf(".");
			if (pos != -1) {
				fileNameWithoutExtension = fileId.substring(0, pos);
			}

			return fileNameWithoutExtension;
		}

		private String getFileExtension(String fileId) {
			String fileExtension = "";
			int pos = fileId.lastIndexOf(".");
			if (pos != -1) {
				fileExtension = fileId.substring(pos + 1);
			}

			return fileExtension;
		}
	}
}