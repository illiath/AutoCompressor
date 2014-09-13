package autocompressor.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
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
import autocompressor.recipes.AuthRecipe;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;

public class TileEntityAutoCompressor extends TileEntity implements ISidedInventory, IEnergyHandler {
	// Variables
	protected ItemStack[]		acInv;
	protected ItemStack			acInventory;

	protected InventoryCrafting	craftMatrix;
	protected EnergyStorage		acEnergyStorage;
	protected AuthRecipe		acAuthRecipe;

	// Block Defaults
	// TODO Implement configuration file for this stuff...

	// How much energy is used per block of the recipe.
	private Integer				energyPerBlock	= 100;

	// Start functions below
	public TileEntityAutoCompressor() {
		acInv = new ItemStack[10];
		acAuthRecipe = new AuthRecipe();
		craftMatrix = new AutoCompressorCrafting();
		acEnergyStorage = new EnergyStorage(5000);
	}

	// Not even sure if this gets called.
	// This doesn't seem to get called when in creative.
	// TODO Sort out how to get this functional!
	public boolean shouldDropSlotWhenBroken(int slot) {
		// Test it then...
		DebugOut.debugMessage("shouldDropSlotWhenBroken", "Hey, we actually ran this code!");

		return true;
	}

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
			DebugOut.debugException("Auto Compressor: checkMatrix", e);
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
			boolean[] authRecipe = acAuthRecipe.listAuthRecipes();

			debugRecipeList();

			int inputItems = acInv[0].stackSize;
			int energyStored = acEnergyStorage.getEnergyStored();
			int patternItems = 0;

			// Horrible pattern kludge, that works beautifully, but still feels wrong to do :)
			// Simple 2x2 pattern
			if ((authRecipe[0]) && (checkMatrix(acInv[0], "XX XX    ") != null)) {
				if ((inputItems >= 4) && (energyStored >= (energyPerBlock * 4))) {
					acInv[1] = checkMatrix(acInv[0], "XX XX    ");
					patternItems = 4;
				}

				// Simple 3x3 pattern
			} else if ((authRecipe[1]) && (checkMatrix(acInv[0], "XXXXXXXXX") != null)) {
				if ((inputItems >= 9) && (energyStored >= (energyPerBlock * 9))) {
					acInv[1] = checkMatrix(acInv[0], "XXXXXXXXX");
					patternItems = 9;
				}

				// 3x3 with no center
			} else if ((authRecipe[2]) && (checkMatrix(acInv[0], "XXXX XXXX") != null)) {
				if ((inputItems >= 8) && (energyStored >= (energyPerBlock * 8))) {
					acInv[1] = checkMatrix(acInv[0], "XXXX XXXX");
					patternItems = 8;
				}
				// simple 2x1 pattern
			} else if ((authRecipe[3]) && (checkMatrix(acInv[0], "X  X     ") != null)) {
				if ((inputItems >= 2) && (energyStored >= (energyPerBlock * 2))) {
					acInv[1] = checkMatrix(acInv[0], "X  X     ");
					patternItems = 2;
				}
				// 3x3 with no center
			} else if ((authRecipe[4]) && (checkMatrix(acInv[0], "XXX      ") != null)) {
				if ((inputItems >= 3) && (energyStored >= (energyPerBlock * 3))) {
					acInv[1] = checkMatrix(acInv[0], "XXX      ");
					patternItems = 3;
				}
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
		int[] approvedRecipes = {};

		System.out.println("------------------------------");
		debugRecipeList();

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
		acAuthRecipe.readFromNBT(nbtData);

		super.readFromNBT(nbtData);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtData) {
		// Call the original process
		super.writeToNBT(nbtData);

		System.out.println("------------------------------");
		System.out.println("x: " + this.xCoord);
		System.out.println("x: " + this.yCoord);
		System.out.println("x: " + this.zCoord);

		// Testing of pattern system
		debugRecipeList();
		System.out.println("------------------------------");

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

		try {
			acAuthRecipe.writeToNBT(nbtData);
		} catch (Exception errorE) {
			DebugOut.debugException("writeToNBT", errorE);
		}

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

	public void toggleRecipe(int recipeNum) {
		boolean[] authRecipe = acAuthRecipe.listAuthRecipes();

		acAuthRecipe.toggleAuthRecipe(recipeNum);

		authRecipe = acAuthRecipe.listAuthRecipes();
	}

	public boolean[] getRecipeList() {
		return acAuthRecipe.listAuthRecipes();
	}

	public void debugRecipeList() {
		boolean[] authRecipe = acAuthRecipe.listAuthRecipes();
		System.out.println("recipe[0]:" + authRecipe[0]);
		System.out.println("recipe[1]:" + authRecipe[1]);
		System.out.println("recipe[2]:" + authRecipe[2]);
		System.out.println("recipe[3]:" + authRecipe[3]);
		System.out.println("recipe[4]:" + authRecipe[4]);
	}
}
