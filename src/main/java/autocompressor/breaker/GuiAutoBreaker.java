package autocompressor.breaker;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import autocompressor.Main;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class GuiAutoBreaker extends GuiContainer {
	private ResourceLocation			GUITexture	= new ResourceLocation(Main.MODID, "textures/gui/gui-mk1.png");

	private TileEntityAutoBreaker	tileEntityAC;
	
	public GuiAutoBreaker(InventoryPlayer inventoryPlayer, TileEntityAutoBreaker te) {
		super(new ContainerAutoBreaker(inventoryPlayer, te));
		tileEntityAC = te;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// Name of the block in the top left corner
		fontRendererObj.drawString(LanguageRegistry.instance().getStringLocalization("tile.blockAutoBreaker.name"), 8, 6,
				4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		this.mc.renderEngine.bindTexture(GUITexture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
