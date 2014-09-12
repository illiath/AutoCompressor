package autocompressor.recipes;

import net.minecraft.nbt.NBTTagCompound;

public class AuthRecipe implements IRecipeAC {
	protected byte	authList;

	public AuthRecipe() {
		this.authList = 0;
	}
	
	public AuthRecipe readFromNBT(NBTTagCompound nbt) {
		this.authList = nbt.getByte("AuthRecipes");
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (authList < 0) {
			authList = 0;
		}
		nbt.setByte("AuthRecipes", authList);
		return nbt;
	}

	@Override
	public boolean[] listAuthRecipes() {
		boolean[] recipeList = new boolean[5];

		// Set the output values, you have to love bitwise voodoo :)
		for (int i = 0; i < 5; i++) {
			if ((authList & (1 << i)) > 0) {
				recipeList[i] = true;
			} else {
				recipeList[i] = false;
			}
		}
		return recipeList;
	}

	@Override
	public void setAuthRecipe(int recipeNum, boolean activeRecipe) {
		authList ^= 1 << recipeNum;
	}

	@Override
	public void toggleAuthRecipe(int recipeNum) {
		authList ^= 1 << recipeNum;
	}
}
