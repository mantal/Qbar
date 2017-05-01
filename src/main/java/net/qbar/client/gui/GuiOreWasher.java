package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.common.tile.machine.TileOreWasher;

public class GuiOreWasher extends GuiMachineBase<TileOreWasher>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/orewasher.png");

    public GuiOreWasher(final EntityPlayer player, final TileOreWasher orewasher)
    {
        super(player, orewasher);

        this.addSteamTank(orewasher.getSteamTank(), 151,7,18,73);
        this.addFluidTank(orewasher.getInputTanks()[0], 7, 7, 18, 73);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        this.mc.renderEngine.bindTexture(GuiOreWasher.BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        final int j = this.getMachine().getProgressScaled(24);
        if (j > 0)
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, j + 1, 16);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
}