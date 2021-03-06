package autocompressor.breaker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import autocompressor.DebugOut;
import autocompressor.shared.acInternalCrafting;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;

public class TileEntityAutoBreaker extends TileEntity implements ISidedInventory, IEnergyHandler {
	// Variables
	protected ItemStack[]		acInv;
	protected ItemStack			acInventory;

	protected InventoryCrafting	craftMatrix;
	protected EnergyStorage		acEnergyStorage;

	// This should be configured
	// How much energy is used per block of the recipe.
	private Integer				energyPerBlock	= 100;

	// Start functions below
	public TileEntityAutoBreaker() {
		acInv = new ItemStack[10];
		craftMatrix = new acInternalCrafting();
		acEnergyStorage = new EnergyStorage(5000);
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
						craftMatrix.setInventorySlotContents(gridPosition, craftingStack);
					}
				}

				// Run the recipe check
				testResult = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, getWorldObj());

				// Clear recipes and temporary variables.
				craftingStack = null;
				for (int gridPosition = 0; gridPosition < 9; gridPosition++) {
					craftMatrix.setInventorySlotContents(gridPosition, null);
				}
			}
		} catch (Exception e) {
			DebugOut.debugException("Auto Breaker: checkMatrix", e);
		}

		// Return our output
		return testResult;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (worldObj == null) {
			return;
		}

		if (worldObj.isRemote) {
			return;
		}

		if ((acInv[0] != null) && (acInv[1] == null)) {
			int inputItems = acInv[0].stackSize;
			int energyStored = acEnergyStorage.getEnergyStored();
			int patternItems = 0;

			// Horrible pattern kludge, that works beautifully, but still feels wrong to do :)
			// Simple place a single block on a grid return block
			if ((checkMatrix(acInv[0], "X        ") != null) && (inputItems >= 1) && (energyStored >= (energyPerBlock * 1))) {
				acInv[1] = checkMatrix(acInv[0], "X        ");
				patternItems = 1;
			}

			// Use up items, this will actually destroy them...
			inputItems -= patternItems;

			// Use up the energy we need for the process
			energyStored -= (energyPerBlock * patternItems);

			// Save the changes to permanent storage
			if (inputItems > 0) {
				acInv[0].stackSize = inputItems;
			} else {
				acInv[0] = null;
			}
			acEnergyStorage.setEnergyStored(energyStored);
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
		return "Auto Breaker";
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
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
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
	public void readFromNBT(NBTTagCompound nbtData) {
		super.readFromNBT(nbtData);

		// Read the Inventory
		NBTTagList tagList = nbtData.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < acInv.length) {
				this.acInv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}

		// Read the energy stored
		acEnergyStorage.readFromNBT(nbtData);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtData) {
		// Call the original process
		super.writeToNBT(nbtData);

		// Write the Inventory
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < this.acInv.length; i++) {
			ItemStack stack = this.acInv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				tagList.appendTag(tag);
			}
		}
		nbtData.setTag("Inventory", tagList);

		// Write the energy in the block
		acEnergyStorage.writeToNBT(nbtData);
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
	// We actually let all slots be accessible from all sides, this makes things easier
			public
			int[] getAccessibleSlotsFromSide(int p_94128_1_) {
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
			if ((getStackInSlot(slot) == null) || (stack.isItemEqual(getStackInSlot(slot)))) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return acEnergyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return acEnergyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return acEnergyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return acEnergyStorage.getMaxEnergyStored();
	}
}
