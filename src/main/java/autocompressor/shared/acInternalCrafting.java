package autocompressor.shared;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;

public class acInternalCrafting extends InventoryCrafting {
	public acInternalCrafting() {
		super(new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer entityplayer) {
				return false;
			}
		}, 3, 3);
	}
}
