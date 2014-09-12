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
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;

public class TileEntityAutoCompressor extends TileEntity implements ISidedInventory, IEnergyHandler {
	// Variables
	private ItemStack[]				acInv;
	private int						acItemCount;
	private ItemStack				acInventory;
	private SlotCrafting			craftSlot;
	private IRecipe					craftingRecipe;

	public InventoryCrafting		craftMatrix		= new AutoCompressorCrafting();
	private InventoryCraftResult	craftResult		= new InventoryCraftResult();

	protected int[]					acRecipeList;
	protected EnergyStorage			acEnergyStorage	= new EnergyStorage(5000);

	// How much energy is used per block of the recipe.
	private int						energyPerBlock	= 100;

	// Start functions below
	public TileEntityAutoCompressor() {
		acInv = new ItemStack[10];
		acRecipeList = new int[5];

		acRecipeList[0] = 1;
		acRecipeList[1] = 1;
		acRecipeList[2] = 1;
		acRecipeList[3] = 1;
		acRecipeList[4] = 1;
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
			/*
			System.out.println("updateScreen: acRecipeList[0]:" + this.acRecipeList[0]);
			System.out.println("updateScreen: acRecipeList[1]:" + this.acRecipeList[1]);
			System.out.println("updateScreen: acRecipeList[2]:" + this.acRecipeList[2]);
			System.out.println("updateScreen: acRecipeList[3]:" + this.acRecipeList[3]);
			System.out.println("updateScreen: acRecipeList[4]:" + this.acRecipeList[4]);
			*/

			if ((acInv[0] != null) && (acInv[1] == null)) {
				int inputItems = acInv[0].stackSize;
				int energyStored = acEnergyStorage.getEnergyStored();
				int patternItems = 0;

				// Horrible pattern kludge, that works beautifully, but still feels wrong to do :)
				// TODO: Implement Control system to specify which pattern(s) to attempt.

				if ((inputItems >= 9) && (checkMatrix(acInv[0], "XXXXXXXXX") != null) && (energyStored >= (energyPerBlock * 9))) {
					// Pattern:
					// xxx
					// xxx
					// xxx
					acInv[1] = checkMatrix(acInv[0], "XXXXXXXXX");
					patternItems = 9;
					/*
					 * } else if ((inputItems >= 2) && (checkMatrix(acInv[0], "X  X     ") != null) && (energyStored >=
					 * (energyPerBlock * 2))) { // Pattern: // x // x // acInv[1] = checkMatrix(acInv[0], "X  X     ");
					 * patternItems = 2;
					 */
				} else if ((inputItems >= 8) && (checkMatrix(acInv[0], "XXXX XXXX") != null)
						&& (energyStored >= (energyPerBlock * 8))) {
					// Pattern:
					// xxx
					// x x
					// xxx
					acInv[1] = checkMatrix(acInv[0], "XXXX XXXX");
					patternItems = 8;

				} else if ((inputItems >= 4) && (checkMatrix(acInv[0], "XX XX    ") != null)
						&& (energyStored >= (energyPerBlock * 4))) {
					// Pattern:
					// xx
					// xx
					//
					acInv[1] = checkMatrix(acInv[0], "XX XX    ");
					patternItems = 4;
				}
				// Process the patternItems
				inputItems -= patternItems;
				energyStored -= (energyPerBlock * patternItems);

				// Save the changes to permanent storage
				if (inputItems > 0) {
					acInv[0].stackSize = inputItems;
				} else {
					acInv[0] = null;
				}
				acEnergyStorage.setEnergyStored(energyStored);
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
		int[] approvedRecipes = {};

		System.out.println("------------------------------");
		System.out.println("readFromNBT: acRecipeList[0]:" + this.acRecipeList[0]);
		System.out.println("readFromNBT: acRecipeList[1]:" + this.acRecipeList[1]);
		System.out.println("readFromNBT: acRecipeList[2]:" + this.acRecipeList[2]);
		System.out.println("readFromNBT: acRecipeList[3]:" + this.acRecipeList[3]);
		System.out.println("readFromNBT: acRecipeList[4]:" + this.acRecipeList[4]);

		// Read the Inventory
		NBTTagList tagList = nbtData.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < acInv.length) {
				this.acInv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}

		// Read the Authorized Recipe List
		NBTTagList authorizedRecipeList = nbtData.getTagList("Recipe", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < 5; i++) {
			NBTTagCompound tag = (NBTTagCompound) authorizedRecipeList.getCompoundTagAt(i);
			int recipe = tag.getInteger(Integer.toString(i));
			if (Integer.toString(recipe).isEmpty()) {
				recipe = 1;
			}
			System.out.println("readFromNBT: Recipe " + i + ": " + recipe);
			this.acRecipeList[i] = recipe;
		}

		// Read the energy stored
		acEnergyStorage.readFromNBT(nbtData);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtData) {
		super.writeToNBT(nbtData);
		System.out.println("------------------------------");
		System.out.println("writeToNBT: acRecipeList[0]:" + this.acRecipeList[0]);
		System.out.println("writeToNBT: acRecipeList[1]:" + this.acRecipeList[1]);
		System.out.println("writeToNBT: acRecipeList[2]:" + this.acRecipeList[2]);
		System.out.println("writeToNBT: acRecipeList[3]:" + this.acRecipeList[3]);
		System.out.println("writeToNBT: acRecipeList[4]:" + this.acRecipeList[4]);
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

		// Write the Recipe Activations
		NBTTagList authorizedRecipeList = new NBTTagList();
		for (int i = 0; i < 5; i++) {
			int tempValue = Integer.valueOf(this.acRecipeList[i]);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(Integer.toString(i), tempValue);
			authorizedRecipeList.appendTag(tag);
			System.out.println("writeToNBT: Recipe " + i + ": " + tempValue);
		}
		nbtData.setTag("Recipe", authorizedRecipeList);

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
		if (acRecipeList[recipeNum] == 0) {
			acRecipeList[recipeNum] = 1;
		} else {
			acRecipeList[recipeNum] = 0;
		}
	}
}
