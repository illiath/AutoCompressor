package autocompressor;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class config {
	// Configuration Data
	public static Configuration					configFile;
	public static int							itemRFCost			= 100;
	public static int							totalRFStored		= 5000;
	public static boolean						enableWIPBlocks		= false;

	public static void init(FMLPreInitializationEvent event) {
		// Register Event Handler
		FMLCommonHandler.instance().bus().register(new config());

		configFile = new Configuration(event.getSuggestedConfigurationFile());

		syncConfig();
	}
	
	public static void syncConfig() {
		// configFile.getInt(name, category, defaultValue, minValue, maxValue, comment);
		itemRFCost = configFile.getInt("RF Cost Per Item", Configuration.CATEGORY_GENERAL, itemRFCost, 0, Integer.MAX_VALUE,
				"Set the RF Cost per block crafted with.");
		totalRFStored = configFile.getInt("Block RF Storage", Configuration.CATEGORY_GENERAL, totalRFStored, 0, Integer.MAX_VALUE,
				"How much RF Stored in a block.");
		enableWIPBlocks = configFile.getBoolean("Enable WIP Blocks", Configuration.CATEGORY_GENERAL, enableWIPBlocks,
				"Enable Work In Progress Blocks (RESTART REQUIRED!)");

		if (configFile.hasChanged()) {
			configFile.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.modID.equals(Main.MODID))
			syncConfig();
	}

}
