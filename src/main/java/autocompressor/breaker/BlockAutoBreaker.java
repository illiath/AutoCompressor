package autocompressor.breaker;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import autocompressor.Main;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAutoBreaker extends BlockContainer {
	@SideOnly(Side.CLIENT)
	public static IIcon	acBreakerTopIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon	acBreakerBottomIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon	acBreakerSideIcon;

	public BlockAutoBreaker() {
		super(Material.rock);

		// Set the block options
		setBlockName("blockAutoBreaker");
		setCreativeTab(Main.tabAutoCompressor);
		setHardness(2.0F);
		setHarvestLevel("pickaxe", 0); // This means the block is destroyed if we break it by hand...
	}

	public void registerBlock(BlockAutoBreaker block) {
		// Register the block with minecraft
		GameRegistry.registerBlock(block, "Auto Breaker");

		// Register the recipe with minecraft
		GameRegistry.addShapedRecipe(new ItemStack(block), new Object[] {
			"PO ",
			"CR ",
			"   ",
			'P', Blocks.piston,
			'O', Blocks.obsidian,
			'C', Blocks.chest,
			'R', Items.redstone
			}
		);

		// Register the TileEntity class with the block class
		GameRegistry.registerTileEntity(TileEntityAutoBreaker.class, "blockAutoBreaker");
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityAutoBreaker();
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

		player.openGui(Main.MODID, 2, world, x, y, z);
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconList) {
		acBreakerTopIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoBreaker_top");
		acBreakerBottomIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoCompressor_bottom");
		acBreakerSideIcon = iconList.registerIcon(Main.MODID + ":" + "blockAutoBreaker_side");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return acBreakerBottomIcon;
		} else if (side == 1) {
			return acBreakerTopIcon;
		} else {
			return acBreakerSideIcon;
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
