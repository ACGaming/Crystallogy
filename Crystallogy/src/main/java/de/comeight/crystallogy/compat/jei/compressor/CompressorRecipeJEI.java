package de.comeight.crystallogy.compat.jei.compressor;

import java.util.ArrayList;
import java.util.List;

import de.comeight.crystallogy.items.crafting.RecipeCompressor;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class CompressorRecipeJEI extends BlankRecipeWrapper {
	//-----------------------------------------------Variabeln:---------------------------------------------
	private ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
	private ArrayList<ItemStack> outputs = new ArrayList<ItemStack>();
	private int totalCookTime;
	
	//-----------------------------------------------Constructor:-------------------------------------------
	public CompressorRecipeJEI(RecipeCompressor recipe) {
		ItemStack[] inputsIS = recipe.input;
		ItemStack[] outputsIS = recipe.getOutput(inputsIS);
		
		for(int i = 0; i < inputsIS.length; i++){
			this.inputs.add(inputsIS[i]);
		}
		for(int i = 0; i < outputsIS.length; i++){
			this.outputs.add(outputsIS[i]);
		}
		this.totalCookTime = recipe.totalCookTime;
	}
	
	//-----------------------------------------------Set-, Get-Methoden:------------------------------------
	@Override
	public List<ItemStack> getInputs() {
		return inputs;
	}
	
	@Override
	public List getOutputs() {
		return outputs;
	}
	
	//-----------------------------------------------Sonstige Methoden:-------------------------------------
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRendererObj.drawString("Total Cook Time: " + totalCookTime, 47, 15, 4210752);
	}
	
}
