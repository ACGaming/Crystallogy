package de.comeight.crystallogy.items.crafting;

import de.comeight.crystallogy.blocks.EnumCrystalColor;
import de.comeight.crystallogy.handler.ItemHandler;
import net.minecraft.item.ItemStack;

public class InfusionRecipeHammer extends InfusionRecipe {
	//-----------------------------------------------Variabeln:---------------------------------------------

	
	//-----------------------------------------------Constructor:-------------------------------------------
	public InfusionRecipeHammer() {
		super("hammer", 300);
	}
	
	//-----------------------------------------------Set-, Get-Methoden:------------------------------------
	@Override
	public InfusionRecipe getRecipe() {
		return new InfusionRecipeHammer();
	}
	
	//-----------------------------------------------Sonstige Methoden:-------------------------------------
	@Override
	public boolean match(ItemStack centerInput, ItemStack[] ingredients) {
		if(centerInput.getItem() != ItemHandler.toolRod){
			return false;
		}
		
		int head_red = 0;
		int dust_red = 0;
		
		int head_blue = 0;
		int dust_blue = 0;
		
		int head_green = 0;
		int dust_green = 0;
		
		int head_yellow = 0;
		int dust_yellow = 0;
		
		for (int i = 0; i < ingredients.length; i++) {
			if(ingredients[i] != null){
				if(ingredients[i].getItem() == ItemHandler.crystallHammerHead){
					int meta = ingredients[i].getItem().getDamage(ingredients[i]); 
					if(meta == EnumCrystalColor.RED.getMetadata()){
						head_red++;
					}
					else if(meta == EnumCrystalColor.BLUE.getMetadata()){
						head_blue++;
					}
					else if(meta == EnumCrystalColor.GREEN.getMetadata()){
						head_green++;
					}
					else if(meta == EnumCrystalColor.YELLOW.getMetadata()){
						head_yellow++;
					}
				}
				else if(ingredients[i].getItem() == ItemHandler.crystallDust_red){
					dust_red++;
				}
				else if(ingredients[i].getItem() == ItemHandler.crystallDust_blue){
					dust_blue++;
				}
				else if(ingredients[i].getItem() == ItemHandler.crystallDust_green){
					dust_green++;
				}
				else if(ingredients[i].getItem() == ItemHandler.crystallDust_yellow){
					dust_yellow++;
				}
				
			}
		}
		if(ingredients.length != 3){
			return false;
		}
		if(head_red == 1 && dust_red == 2){
			output = new ItemStack(ItemHandler.crystallHammer_red);
			return true;
		}
		else if(head_blue == 1 && dust_blue == 2){
			output = new ItemStack(ItemHandler.crystallHammer_blue);
			return true;
		}
		else if(head_green == 1 && dust_green == 2){
			output = new ItemStack(ItemHandler.crystallHammer_green);
			return true;
		}
		else if(head_yellow == 1 && dust_yellow == 2){
			output = new ItemStack(ItemHandler.crystallHammer_yellow);
			return true;
		}
		
		return false;
	}
}
