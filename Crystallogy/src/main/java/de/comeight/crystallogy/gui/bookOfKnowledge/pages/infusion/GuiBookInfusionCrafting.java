package de.comeight.crystallogy.gui.bookOfKnowledge.pages.infusion;

import de.comeight.crystallogy.gui.bookOfKnowledge.GuiBookPage;
import de.comeight.crystallogy.gui.bookOfKnowledge.PageRegistry;
import de.comeight.crystallogy.gui.bookOfKnowledge.pages.search.GuiBookSearch;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBookInfusionCrafting extends GuiBookPage {
	//-----------------------------------------------Variabeln:---------------------------------------------
	
	
	//-----------------------------------------------Constructor:-------------------------------------------
	public GuiBookInfusionCrafting() {
		super("Infusion Crafting:");
	}
	
	//-----------------------------------------------Set-, Get-Methoden:------------------------------------
	@Override
	public GuiBookPage getNextPage() {
		return PageRegistry.SEARCH_PAGE;
	}
	
	//-----------------------------------------------Sonstige Methoden:-------------------------------------

	
}