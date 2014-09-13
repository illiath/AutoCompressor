package autocompressor;

import java.lang.ref.WeakReference;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import autocompressor.machine.AutoCompressorGuiHandler;
import autocompressor.machine.BlockAutoCompressor;
import autocompressor.machine.TileEntityAutoCompressor;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
	public static final String					MODID				= "autocompressor";
	public static final String					VERSION				= "0.2";

	public WeakReference<EntityPlayer>			AutoCompressorPlayer;

	@Instance("illiath_autocompressor")
	public static Main							instance;

	public static Block							BlockAutoCompressor;

	public static CreativeTabs					tabAutoCompressor	= new CreativeTabs("tabAutoCompressor") {
																		public Item getTabIconItem() {
																			return Items.bed;
																		}
																	};

	public static final SimpleNetworkWrapper	INSTANCE			= NetworkRegistry.INSTANCE
																			.newSimpleChannel(MODID.toLowerCase());

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Set up the base block
		BlockAutoCompressor = (new BlockAutoCompressor());

		// Register and add recipe information
		GameRegistry.registerBlock(BlockAutoCompressor, "Auto Compressor");
		GameRegistry.addShapedRecipe(new ItemStack(BlockAutoCompressor), new Object[] { "POP", "ORO", "POP", 'O', Blocks.obsidian,
				'P', Blocks.piston, 'R', Items.redstone });
		GameRegistry.registerTileEntity(TileEntityAutoCompressor.class, "blockAutoCompressor");
		NetworkRegistry.INSTANCE.registerGuiHandler(MODID, new AutoCompressorGuiHandler());
	}
}
