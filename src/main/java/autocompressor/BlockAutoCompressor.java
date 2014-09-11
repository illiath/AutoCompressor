package autocompressor;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAutoCompressor extends BlockContainer {
	public BlockAutoCompressor() {
		super(Material.rock);

		// Set it to have some real hardness
		setHardness(2.0F);

		// Set it so any pick axe can break the block quickly.
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityAutoCompressor();
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int metadata, float what, float these,
			float are) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}

		player.openGui(Main.MODID, 0, world, x, y, z);
		return true;
	}

	/*
    //If the block's drop is a block.
    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }
    */
}
