/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.client;

import java.io.File;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiBackupFailed extends GuiScreen
{
    private GuiScreen parent;
    private File zipName;
    public GuiBackupFailed(GuiScreen parent, File zipName)
    {
        this.parent = parent;
        this.zipName = zipName;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    protected void actionPerformed(GuiButton p_73875_1_)
    {
        if (p_73875_1_.enabled && p_73875_1_.id == 1)
        {
            FMLClientHandler.instance().showGuiScreen(parent);
        }
    }
    /**
     * Draws the screen and all the components in it.
     *  
     * @param mouseX Mouse x coordinate
     * @param mouseY Mouse y coordinate
     * @param partialTicks How far into the current tick (1/20th of a second) the game is
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int offset = Math.max(85 - 2 * 10, 10);
        this.drawCenteredString(this.fontRendererObj, String.format("There was an error saving the archive %s", zipName.getName()), this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.fontRendererObj, String.format("Please fix the problem and try again"), this.width / 2, offset, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}