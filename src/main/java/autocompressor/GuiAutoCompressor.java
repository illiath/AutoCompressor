package autocompressor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class GuiAutoCompressor extends GuiContainer {
	private ResourceLocation			GUITexture		= new ResourceLocation(Main.MODID, "textures/gui/gui.png");

	private GuiLabel					energyLabel;
	private TileEntityAutoCompressor	tileEntityAC;

	private GuiButton[]					recipeButtons	= new GuiButton[5];
	private boolean[]					recipeList;

	protected int						x;
	protected int						y;
	protected int						z;

	public GuiAutoCompressor(InventoryPlayer inventoryPlayer, TileEntityAutoCompressor tileEntity) {
		super(new ContainerAutoCompressor(inventoryPlayer, tileEntity));
		tileEntityAC = tileEntity;
		x = tileEntityAC.xCoord;
		y = tileEntityAC.yCoord;
		z = tileEntityAC.zCoord;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// Name of the block in the top left corner
		fontRendererObj.drawString(LanguageRegistry.instance().getStringLocalization("tile.blockAutoCompressor.name"), 8, 6,
				4210752);
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

		for (int i = 0; i < 5; i++) {
			try {
				buttonList.add(this.recipeButtons[i] = new GuiButton(i, baseX + (buttonGap * i), baseY, 21, 20, "-"));
			} catch (Exception errorE) {
				System.out.println("i = " + i);
				DebugOut.debugException("initGUI", errorE);
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		boolean[] updateRecipeList = new boolean[5];
		TileEntity updateEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(x, y, z);

		if (updateEntity instanceof TileEntityAutoCompressor) {
			updateRecipeList = ((TileEntityAutoCompressor) updateEntity).getRecipeList();
		}

		// Update with button information
		for (int i = 0; i < 5; i++) {
			if (updateRecipeList[i]) {
				recipeButtons[i].displayString = "*";
			} else {
				recipeButtons[i].displayString = "-";
			}
			buttonList.set(i, recipeButtons[i]);
		}
	}

	protected void actionPerformed(GuiButton guibutton) {
		TileEntity actionEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(x, y, z);

		if (actionEntity instanceof TileEntityAutoCompressor) {

			try {
				switch (guibutton.id) {
					case 0:
						Main.INSTANCE.sendToServer(new MessageACGUI((TileEntityAutoCompressor) actionEntity, 0));
						break;
					case 1:
						Main.INSTANCE.sendToServer(new MessageACGUI((TileEntityAutoCompressor) actionEntity, 1));
						break;
					case 2:
						Main.INSTANCE.sendToServer(new MessageACGUI((TileEntityAutoCompressor) actionEntity, 2));
						break;
					case 3:
						Main.INSTANCE.sendToServer(new MessageACGUI((TileEntityAutoCompressor) actionEntity, 3));
						break;
					case 4:
						Main.INSTANCE.sendToServer(new MessageACGUI((TileEntityAutoCompressor) actionEntity, 4));
						break;
				}
			} catch (Exception e) {
				DebugOut.debugException("GUIAutoCompressor:actionPerformed", e);
			}
		}

		// DEBUG
		tileEntityAC.debugRecipeList();
	}
}
