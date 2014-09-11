package autocompressor;

import cpw.mods.fml.client.config.GuiCheckBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAutoCompressor extends Container {
	protected TileEntityAutoCompressor	tileEntity;

	public GuiCheckBox testBox1;
	public GuiButton testButton1;

	
	public ContainerAutoCompressor(InventoryPlayer inventoryPlayer, TileEntityAutoCompressor te) {
		tileEntity = te;

		// Input Slot
		addSlotToContainer(new Slot(tileEntity, 0, 49, 18));

		// Output Slot
		addSlotToContainer(new Slot(tileEntity, 1, 106, 18));

		testButton1 = new GuiButton(2,10,25,20,20,"+");
		testBox1 = new GuiCheckBox(3, 10,30, "2x2", true);
		
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int xbase = 8;
		int ybase = 49;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, xbase + j * 18, ybase + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, xbase + i * 18, ybase + 58));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		try {
			Slot slotObject = (Slot) inventorySlots.get(slot);

			// null checks and checks if the item can be stacked (maxStackSize >
			// 1)
			if (slotObject != null && slotObject.getHasStack()) {
				ItemStack stackInSlot = slotObject.getStack();
				stack = stackInSlot.copy();

				// Block->Player Inventory
				if (slot < 2) {
					if (!this.mergeItemStack(stackInSlot, 2, 35, true)) {
						return null;
					}
					// Player->Block Inventory
				} else if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
					return null;
				}

				if (stackInSlot.stackSize <= 0) {
					slotObject.putStack(null);
				} else {
					slotObject.onSlotChanged();
				}

				if (stackInSlot.stackSize == stack.stackSize) {
					return null;
				}
				slotObject.onPickupFromSlot(player, stackInSlot);
			}
		} catch (Exception e) {
			DebugOut.debugException("Auto Compressor: transferStackInSlot", e);
		}
		return stack;
	}
}
