package autocompressor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class GuiAutoCompressor extends GuiContainer {
	public ResourceLocation				GUITexture	= new ResourceLocation(Main.MODID, "textures/gui/gui.png");

	public GuiButton					recipeButton1;
	public GuiButton					recipeButton2;
	public GuiButton					recipeButton3;
	public GuiButton					recipeButton4;
	public GuiButton					recipeButton5;

	private TileEntityAutoCompressor	tileEntityAC;

	public GuiAutoCompressor(InventoryPlayer inventoryPlayer, TileEntityAutoCompressor tileEntity) {
		super(new ContainerAutoCompressor(inventoryPlayer, tileEntity));
		this.tileEntityAC = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// Name of the block in the top left corner
		fontRendererObj.drawString(LanguageRegistry.instance().getStringLocalization("tile.blockAutoCompressor.name"), 8, 6,
				4210752);

		// List "Inventory" or the locale equivalent over the player inventory spaces
		// Disabled for now while we sort out the GUI look otherwise.
		// fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, 38, 4210752);
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

		int baseX = this.guiLeft + 7;
		int baseY = this.guiTop + 48;
		int buttonGap = 23;

		this.buttonList.add(this.recipeButton1 = new GuiButton(0, baseX, baseY, 21, 20, "-"));
		this.buttonList.add(this.recipeButton2 = new GuiButton(1, baseX + buttonGap, baseY, 21, 20, "-"));
		this.buttonList.add(this.recipeButton3 = new GuiButton(2, baseX + (buttonGap * 2), baseY, 21, 20, "-"));
		this.buttonList.add(this.recipeButton4 = new GuiButton(3, baseX + (buttonGap * 3), baseY, 21, 20, "-"));
		this.buttonList.add(this.recipeButton5 = new GuiButton(4, baseX + (buttonGap * 4), baseY, 21, 20, "-"));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		/*
		System.out.println("updateScreen: acRecipeList[0]:" + tileEntityAC.acRecipeList[0]);
		System.out.println("updateScreen: acRecipeList[1]:" + tileEntityAC.acRecipeList[1]);
		System.out.println("updateScreen: acRecipeList[2]:" + tileEntityAC.acRecipeList[2]);
		System.out.println("updateScreen: acRecipeList[3]:" + tileEntityAC.acRecipeList[3]);
		System.out.println("updateScreen: acRecipeList[4]:" + tileEntityAC.acRecipeList[4]);
		*/

		// Update with button information
		if (tileEntityAC.acRecipeList[0] == 1) {
			recipeButton1.displayString = "*";
		} else {
			recipeButton1.displayString = "-";
		}

		if (tileEntityAC.acRecipeList[1] == 1) {
			recipeButton2.displayString = "*";
		} else {
			recipeButton2.displayString = "-";
		}

		if (tileEntityAC.acRecipeList[2] == 1) {
			recipeButton3.displayString = "*";
		} else {
			recipeButton3.displayString = "-";
		}

		if (tileEntityAC.acRecipeList[3] == 1) {
			recipeButton4.displayString = "*";
		} else {
			recipeButton4.displayString = "-";
		}

		if (tileEntityAC.acRecipeList[4] == 1) {
			recipeButton5.displayString = "*";
		} else {
			recipeButton5.displayString = "-";
		}

		// Update the button list
		this.buttonList.set(0, this.recipeButton1);
		this.buttonList.set(1, this.recipeButton2);
		this.buttonList.set(2, this.recipeButton3);
		this.buttonList.set(3, this.recipeButton4);
		this.buttonList.set(4, this.recipeButton5);
	}

	protected void actionPerformed(GuiButton guibutton) {
		try {
			switch (guibutton.id) {
				case 0:
					// DebugOut.debugMessage("actionPerformed", "Button 0 Pressed");
					Main.INSTANCE.sendToServer(new MessageACGUI(tileEntityAC, 0));
					break;
				case 1:
					// DebugOut.debugMessage("actionPerformed", "Button 1 Pressed");
					Main.INSTANCE.sendToServer(new MessageACGUI(tileEntityAC, 1));
					break;
				case 2:
					// DebugOut.debugMessage("actionPerformed", "Button 2 Pressed");
					Main.INSTANCE.sendToServer(new MessageACGUI(tileEntityAC, 2));
					break;
				case 3:
					// DebugOut.debugMessage("actionPerformed", "Button 3 Pressed");
					Main.INSTANCE.sendToServer(new MessageACGUI(tileEntityAC, 3));
					break;
				case 4:
					// DebugOut.debugMessage("actionPerformed", "Button 4 Pressed");
					Main.INSTANCE.sendToServer(new MessageACGUI(tileEntityAC, 4));
					break;
			}
		} catch (Exception e) {
			DebugOut.debugException("GUIAutoCompressor:actionPerformed", e);
		}
		System.out.println("updateScreen: acRecipeList[0]:" + tileEntityAC.acRecipeList[0]);
		System.out.println("updateScreen: acRecipeList[1]:" + tileEntityAC.acRecipeList[1]);
		System.out.println("updateScreen: acRecipeList[2]:" + tileEntityAC.acRecipeList[2]);
		System.out.println("updateScreen: acRecipeList[3]:" + tileEntityAC.acRecipeList[3]);
		System.out.println("updateScreen: acRecipeList[4]:" + tileEntityAC.acRecipeList[4]);
	}
}
