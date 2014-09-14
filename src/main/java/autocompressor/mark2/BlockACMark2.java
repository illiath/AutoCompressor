package autocompressor.mark2;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import autocompressor.Main;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockACMark2 extends BlockContainer {
	@SideOnly(Side.CLIENT)
	public static IIcon	acBlockTopIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon	acBlockBottomIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon	acBlockSideIcon;

	static {
		Main.INSTANCE.registerMessage(ACGUIMark2MessageHandler.class, ACGUIMark2MessageHandler.class, 1, Side.SERVER);
	}

	public BlockACMark2() {
		super(Material.rock);

		// Set the block options
		setBlockName("blockAutoCompressorMk2");
		setCreativeTab(Main.tabAutoCompressor);
		setHardness(2.0F);
		setHarvestLevel("pickaxe", 0); // This means the block is destroyed if we break it by hand...
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TEACMark2();
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float fl1, float fl2,
			float fl3) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null) {
			return false;
		}

		if (player.isSneaking()) {
			// Maybe we'll do something here...
			return false;
		}

		player.openGui(Main.MODID, 1, world, x, y, z);
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconList) {
		acBlockTopIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoCompressorMk2_top");
		acBlockBottomIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoCompressor_bottom");
		acBlockSideIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoCompressorMk2_side");
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
	public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, block, par6);
	}

	private void dropItems(World world, int x, int y, int z) {
		Random rand = new Random();

		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(item.getItem(), item.stackSize,
						item.getItemDamage()));

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				}

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}
}
