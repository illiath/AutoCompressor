package autocompressor;

import java.lang.ref.WeakReference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import autocompressor.breaker.BlockAutoBreaker;
import autocompressor.gui.AutoCompressorGuiHandler;
import autocompressor.mk1.BlockAutoCompressor;
import autocompressor.mk2.BlockACMark2;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Main.MODID, version = Main.VERSION, guiFactory = "autocompressor.gui.ACGUIFactory")
public class Main {
	public static final String					MODID				= "autocompressor";
	public static final String					VERSION				= "0.4";

	public WeakReference<EntityPlayer>			AutoCompressorPlayer;

	@Instance("illiath_autocompressor")
	public static Main							instance;

	public static BlockAutoCompressor			BlockAutoCompressor;
	public static BlockACMark2					BlockACMark2;
	public static BlockAutoBreaker				BlockAutoBreaker;

	public static CreativeTabs					tabAutoCompressor	= new CreativeTabs("tabAutoCompressor") {
																		public Item getTabIconItem() {
																			return Items.bed;
																		}
																	};

	public static final SimpleNetworkWrapper	INSTANCE			= NetworkRegistry.INSTANCE
																			.newSimpleChannel(MODID.toLowerCase());

	// Configuration file stuffs
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config.init(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		// Register GUI handler
		NetworkRegistry.INSTANCE.registerGuiHandler(MODID, new AutoCompressorGuiHandler());

		// Set up & register the Auto Compressor Mk1
		BlockAutoCompressor = new BlockAutoCompressor();
		BlockAutoCompressor.registerBlock(BlockAutoCompressor);

		if (config.enableWIPBlocks) {
			// Set up the Mark 2 Block
			BlockACMark2 = new BlockACMark2();
			BlockACMark2.registerBlock(BlockACMark2, BlockAutoCompressor);
		}

		// Set up the Auto Breaker Block
		BlockAutoBreaker = new BlockAutoBreaker();
		BlockAutoBreaker.registerBlock(BlockAutoBreaker);
	}
}
