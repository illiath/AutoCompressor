package autocompressor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntityAutoCompressor extends TileEntity implements
		ISidedInventory {
	private ItemStack[] acInv;

	private int acItemCount;
	private ItemStack acInventory;

	private SlotCrafting craftSlot;

	public TileEntityAutoCompressor() {
		acInv = new ItemStack[10];
	}

	public boolean shouldDropSlotWhenBroken(int slot) {

		return false;
	}

	public InventoryCrafting craftMatrix = new AutoCompressorCrafting();
	private InventoryCraftResult craftResult = new InventoryCraftResult();
	private IRecipe craftingRecipe;

	private class AutoCompressorCrafting extends InventoryCrafting {

		public AutoCompressorCrafting() {
			super(new Container() {
				@Override
				public boolean canInteractWith(EntityPlayer entityplayer) {
					return false;
				}
			}, 3, 3);
		}
	}

	// With a given input pattern, check to see if we get a valid recipe, if so,
	// return the item we would make with this recipe.
	private ItemStack checkMatrix(ItemStack stack, String pattern) {
		ItemStack testResult = null;
		try {
			if (stack != null) {
				ItemStack craftingStack = stack.copy();
				craftingStack.stackSize = 1;

				// Set crafting grid recipe
				for (int gridPosition = 0; gridPosition < 9; gridPosition++) {
					if (pattern.charAt(gridPosition) == 'X') {
						craftMatrix.setInventorySlotContents(gridPosition,
								craftingStack);
					}
				}

				// Run the recipe check
				testResult = CraftingManager.getInstance().findMatchingRecipe(
						craftMatrix, getWorldObj());

				// Clear recipes and temp variables.
				craftingStack = null;
				for (int gridPosition = 0; gridPosition < 9; gridPosition++) {
					craftMatrix.setInventorySlotContents(gridPosition, null);
				}
			}
		} catch (Exception e) {
			DebugOut.debugException("Auto Compressor: checkMatrix", e);
		}

		// Return our output
		return testResult;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) {
			return;
		}

		try {
			if ((acInv[0] != null) && (acInv[1] == null)) {
				int inputItems = acInv[0].stackSize;

				// DebugOut.debugMessage("updateEntity", "inputItems = "+
				// inputItems);

				// Horrible pattern kludge, that works beautifully, but still
				// feels wrong to do :)
				if ((inputItems >= 4)
						&& (checkMatrix(acInv[0], "XX XX    ") != null)) {
					// Pattern:
					// xx
					// xx
					//
					acInv[1] = checkMatrix(acInv[0], "XX XX    ");
					inputItems -= 4;
					// DebugOut.debugMessage("updateEntity", "(inputItems-4) ="+
					// inputItems);
				} else if ((inputItems >= 8)
						&& (checkMatrix(acInv[0], "XXXX XXXX") != null)) {
					// Pattern:
					// xxx
					// x x
					// xxx
					acInv[1] = checkMatrix(acInv[0], "XXXX XXXX");
					inputItems -= 8;
					// DebugOut.debugMessage("updateEntity",
					// "(inputItems-9) = "+ inputItems);
				} else if ((inputItems >= 9)
						&& (checkMatrix(acInv[0], "XXXXXXXXX") != null)) {
					// Pattern:
					// xxx
					// xxx
					// xxx
					acInv[1] = checkMatrix(acInv[0], "XXXXXXXXX");
					inputItems -= 9;
					// DebugOut.debugMessage("updateEntity",
					// "(inputItems-9) = "+ inputItems);
				}

				if (inputItems > 0) {
					acInv[0].stackSize = inputItems;
				} else {
					acInv[0] = null;
				}
			}
		} catch (Exception e) {
			DebugOut.debugException("Auto Compressor: updateEntity", e);
		}
	}

	@Override
	public void closeInventory() {
		// We do nothing special here
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public String getInventoryName() {
		return "Auto Compressor";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return 10;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return acInv[slot];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
						zCoord + 0.5) < 64;
	}

	@Override
	public void openInventory() {
		// We do nothing special here
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		acInv[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		NBTTagList tagList = tagCompound.getTagList("Inventory",
				Constants.NBT.TAG_COMPOUND);
		final String debugInt = "Tag Count: "
				+ Integer.toString(tagList.tagCount());
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);

			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < acInv.length) {
				acInv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < acInv.length; i++) {
			ItemStack stack = acInv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		// Only allow extracting from slot 1
		if (slot == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int count) {
		// Only allow insertion into slot 0
		if (slot == 0) {
			return true;
		}

		return false;
	}

	@Override
	// We actually let all slots be accessible from all sides, this makes things
	// easier
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		int[] validSlots = { 0, 1 };

		return validSlots;
	}

	// Just ignore this, I'll check later if this code is needed.
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		// We don't care about stuff for any slot by 1, as only slot 0 can be
		// added to automatically
		if (slot == 0) {
			// If the slot is empty, or already contains the same item, the slot
			// can be have automation work with it
			if ((getStackInSlot(slot) == null)
					|| (stack.isItemEqual(getStackInSlot(slot)))) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}
}
