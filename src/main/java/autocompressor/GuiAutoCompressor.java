package autocompressor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class GuiAutoCompressor extends GuiContainer {
	public ResourceLocation	GUITexture	= new ResourceLocation(Main.MODID, "textures/gui/gui.png");

	public GuiCheckBox testBox1;
	public GuiButton testButton1;
	
	public GuiAutoCompressor(InventoryPlayer inventoryPlayer, TileEntityAutoCompressor tileEntity) {
		super(new ContainerAutoCompressor(inventoryPlayer, tileEntity));
		buttonList.add(new GuiButton(1,10,15,20,20,"+"));
		
		testButton1 = new GuiButton(2,10,25,20,20,"+");
		testBox1 = new GuiCheckBox(3, 10,30, "2x2", true);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// Name of the block in the top left corner
		fontRendererObj.drawString(LanguageRegistry.instance().getStringLocalization("tile.blockAutoCompressor.name"), 8, 6,
				4210752);

		// List "Inventory" or the locale equivalent over the player inventory
		// spaces
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, 38, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		this.mc.renderEngine.bindTexture(GUITexture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		// make buttons
		// id, x, y, width, height, text
		buttonList.add(new GuiButton(1, 10, 15, 20, 20, "+"));
	}

	protected void actionPerformed(GuiButton guibutton) {
		// id is the id you give your button
		switch (guibutton.id) {
			case 1:
				// activeRecipes[0] = 1;
				break;
			case 2:
				// activeRecipes[0] = 0;
		}

		// Packet code here
		// PacketDispatcher.sendPacketToServer(packet); //send packet
	}

}
