package autocompressor.recipes;

public interface IRecipeAC {
	/*
	 * Set the listed recipe number to activeRecipe state.
	 */
	void setAuthRecipe(int recipeNum, boolean activeRecipe);

	/*
	 * Toggle recipeNum
	 */
	void toggleAuthRecipe(int recipeNum);
	
	/*
	 * List authorized Recipes
	 */
	boolean[] listAuthRecipes();
}
