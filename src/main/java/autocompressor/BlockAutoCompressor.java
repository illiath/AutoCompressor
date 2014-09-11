package autocompressor;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAutoCompressor extends BlockContainer {
	@SideOnly(Side.CLIENT)
	public static IIcon	acBlockTopIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon	acBlockBottomIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon	acBlockSideIcon;

	public BlockAutoCompressor() {
		super(Material.rock);

		// Set the block options
		setBlockName("blockAutoCompressor");
		setCreativeTab(Main.tabAutoCompressor);
		setHardness(2.0F);
		setHarvestLevel("pickaxe", 0); // This means the block is destroyed if we break it by hand...
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconList) {
		acBlockTopIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoCompressor_top");
		acBlockBottomIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoCompressor_bottom");
		acBlockSideIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoCompressor_side");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return acBlockBottomIcon;
		} else if (side == 1) {
			return acBlockTopIcon;
		} else {
			return acBlockSideIcon;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityAutoCompressor();
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float fl1, float fl2,
			float fl3) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null) {
			return false;
		}

		if (player.isSneaking()) {
			// DebugOut.debugMessage("onBlockActivated", "Hey, sneaky player!");
			// Maybe we'll do something here...
			return false;
		}

		player.openGui(Main.MODID, 0, world, x, y, z);
		return true;
	}
}
