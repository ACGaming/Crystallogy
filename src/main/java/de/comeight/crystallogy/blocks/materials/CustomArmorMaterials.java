package de.comeight.crystallogy.blocks.materials;

import de.comeight.crystallogy.CrystallogyBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class CustomArmorMaterials {
	//-----------------------------------------------Variabeln:---------------------------------------------
	public static ArmorMaterial CRYSTALL_RED = EnumHelper.addArmorMaterial("crystall_red", CrystallogyBase.MODID + ":red", 20, new int[]{4,6,6,4}, 20, SoundEvents.item_armor_equip_diamond);
	public static ArmorMaterial CRYSTALL_BLUE = EnumHelper.addArmorMaterial("crystall_blue", CrystallogyBase.MODID + ":blue", 7, new int[]{2,3,3,2}, 7, SoundEvents.item_armor_equip_diamond);
	public static ArmorMaterial CRYSTALL_GREEN = EnumHelper.addArmorMaterial("crystall_green", CrystallogyBase.MODID + ":green", 10, new int[]{2,4,4,2}, 10, SoundEvents.item_armor_equip_diamond);
	public static ArmorMaterial CRYSTALL_YELLOW = EnumHelper.addArmorMaterial("crystall_yellow", CrystallogyBase.MODID + ":yellow", 15, new int[]{3,5,5,3}, 15, SoundEvents.item_armor_equip_diamond);
	public static ArmorMaterial CRYSTALL_COMBINED = EnumHelper.addArmorMaterial("crystall_combined", CrystallogyBase.MODID + ":combined", 15, new int[]{2,7,7,4}, 15, SoundEvents.item_armor_equip_diamond);
	
	//-----------------------------------------------Constructor:-------------------------------------------

	
	//-----------------------------------------------Set-, Get-Methoden:------------------------------------

	
	//-----------------------------------------------Sonstige Methoden:-------------------------------------

	
}
