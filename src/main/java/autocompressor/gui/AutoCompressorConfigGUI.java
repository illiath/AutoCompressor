package autocompressor.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import autocompressor.Main;
import autocompressor.config;
import cpw.mods.fml.client.config.GuiConfig;

public class AutoCompressorConfigGUI extends GuiConfig {
	public AutoCompressorConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(config.configFile.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Main.MODID,
				false, false, GuiConfig.getAbridgedConfigPath(config.configFile.toString()));
	}
}
