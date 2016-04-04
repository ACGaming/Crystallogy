package de.comeight.crystallogy.handler;

import de.comeight.crystallogy.CrystallogyBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public final class BlockRenderHandler {
	//-----------------------------------------------Variabeln:---------------------------------------------


	//-----------------------------------------------Constructor:-------------------------------------------


	//-----------------------------------------------Set-, Get-Methoden:------------------------------------


	//-----------------------------------------------Sonstige Methoden:-------------------------------------
	public static void registerBlockRenderer() {
		registerRenderer(BlockHandler.crystall_blue);
		registerRenderer(BlockHandler.crystall_green);
		registerRenderer(BlockHandler.crystall_red);
		registerRenderer(BlockHandler.crystall_yellow);
		registerRenderer(BlockHandler.crystallCrusher);
		registerRenderer(BlockHandler.infuserBlock);
		registerRenderer(BlockHandler.playerJar);
		registerRenderer(BlockHandler.crystallLight);
		System.out.println("Crystallogy: All block renderer are registerd");
    }
	
	public static void registerRenderer(Block block) {
		Item item = Item.getItemFromBlock(block);
		ModelResourceLocation iMRL = new ModelResourceLocation(CrystallogyBase.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory");
		
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, iMRL);
	}
	
}
