package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.tile.machine.TileBoiler;

public class BlockBoiler extends BlockMultiblockBase
{
    public BlockBoiler()
    {
        super("boiler", Material.IRON, Multiblocks.SOLID_BOILER);
    }

    @Override
    public void onBlockDestroyedByExplosion(final World w, final BlockPos pos, final Explosion exp)
    {
        super.onBlockDestroyedByExplosion(w, pos, exp);

        w.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 2, true);
    }

    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state)
    {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public TileEntity getTile(final World w, final IBlockState state)
    {
        return new TileBoiler();
    }
}
