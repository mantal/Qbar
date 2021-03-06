package net.ros.client.render.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.model.animation.FastTESR;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.CraftingModule;
import net.ros.common.tile.machine.TileRollingMill;
import net.ros.client.render.RenderUtil;

public class RenderRollingMill extends FastTESR<TileRollingMill>
{
    @Override
    public void renderTileEntityFast(final TileRollingMill tile, final double x, final double y, final double z,
                                     final float partialTicks, final int destroyStage, final float partial, final
                                         BufferBuilder VertexBuffer)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + .42, y + 1.2, z + 1.8);

        switch (tile.getFacing())
        {
            case NORTH:
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.translate(2.6, 0, 0.16);
                break;
            case SOUTH:
                GlStateManager.rotate(-90, 0, 1, 0);
                break;
            case WEST:
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(1.22, 0, 1.38);
                break;
            default:
                GlStateManager.translate(1.38, 0, -1.22);
                break;
        }

        CraftingModule crafter = tile.getModule(CraftingModule.class);
        InventoryHandler inventory = tile.getModule(InventoryModule.class).getInventory("crafting");

        if (!inventory.getStackInSlot(0).isEmpty())
            RenderUtil.handleRenderItem(inventory.getStackInSlot(0), true);
        if (!inventory.getStackInSlot(2).isEmpty())
        {
            GlStateManager.translate(-2 * (crafter.getCurrentProgress() / crafter.getMaxProgress()), 0, 0);

            if (crafter.getCurrentProgress() / crafter.getMaxProgress() > 0.5)
                RenderUtil.handleRenderItem(tile.getCachedStack(), true);
            else
                RenderUtil.handleRenderItem(inventory.getStackInSlot(2), true);
        }
        GlStateManager.popMatrix();
    }
}
