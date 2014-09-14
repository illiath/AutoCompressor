package autocompressor.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import autocompressor.machine.ContainerAutoCompressor;
import autocompressor.machine.GuiAutoCompressor;
import autocompressor.machine.TileEntityAutoCompressor;
import autocompressor.mark2.ContainerACMark2;
import autocompressor.mark2.GUIACMark2;
import autocompressor.mark2.TEACMark2;
import cpw.mods.fml.common.network.IGuiHandler;

public class AutoCompressorGuiHandler implements IGuiHandler {
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if ((id == 0) && (tileEntity instanceof TileEntityAutoCompressor)) {
			return new GuiAutoCompressor(player.inventory, (TileEntityAutoCompressor) tileEntity);
		}
		
		if ((id == 1) && (tileEntity instanceof TEACMark2)) {
			return new GUIACMark2(player.inventory, (TEACMark2) tileEntity);
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if ((id == 0)  && (tileEntity instanceof TileEntityAutoCompressor)) {
			return new ContainerAutoCompressor(player.inventory, (TileEntityAutoCompressor) tileEntity);
		}
		if ((id == 1)  && (tileEntity instanceof TEACMark2)) {
			return new ContainerACMark2(player.inventory, (TEACMark2) tileEntity);
		}
		return null;
	}
}
