package autocompressor.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import autocompressor.breaker.ContainerAutoBreaker;
import autocompressor.breaker.GuiAutoBreaker;
import autocompressor.breaker.TileEntityAutoBreaker;
import autocompressor.mk1.ContainerAutoCompressor;
import autocompressor.mk1.GuiAutoCompressor;
import autocompressor.mk1.TileEntityAutoCompressor;
import autocompressor.mk2.ContainerACMark2;
import autocompressor.mk2.GUIACMark2;
import autocompressor.mk2.TEACMark2;
import cpw.mods.fml.common.network.IGuiHandler;

public class AutoCompressorGuiHandler implements IGuiHandler {
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		// Check to make sure we have a valid entity we're being called for
		if (tileEntity == null) {
			return null;
		}

		// Handle the GUI id
		switch (id) {
			case 0: {
				if (tileEntity instanceof TileEntityAutoCompressor) {
					return new GuiAutoCompressor(player.inventory, (TileEntityAutoCompressor) tileEntity);
				}
				break;
			}
			case 1: {
				if (tileEntity instanceof TEACMark2) {
					return new GUIACMark2(player.inventory, (TEACMark2) tileEntity);
				}
				break;
			}
			case 2: {
				if (tileEntity instanceof TileEntityAutoBreaker) {
					return new GuiAutoBreaker(player.inventory, (TileEntityAutoBreaker) tileEntity);
				}
				break;
			}
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		// Check to make sure we have a valid entity we're being called for
		if (tileEntity == null) {
			return null;
		}

		// Handle the GUI id
		switch (id) {
			case 0: {
				if (tileEntity instanceof TileEntityAutoCompressor) {
					return new ContainerAutoCompressor(player.inventory, (TileEntityAutoCompressor) tileEntity);
				}
				break;
			}
			case 1: {
				if (tileEntity instanceof TEACMark2) {
					return new ContainerACMark2(player.inventory, (TEACMark2) tileEntity);
				}
				break;
			}
			case 2: {
				if (tileEntity instanceof TileEntityAutoBreaker) {
					return new ContainerAutoBreaker(player.inventory, (TileEntityAutoBreaker) tileEntity);
				}
				break;
			}
		}
		return null;
	}
}
