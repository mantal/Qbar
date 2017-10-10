package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.common.QBarConstants;
import net.qbar.common.tile.IFilteredMachine;

@ReceivedOn(Side.SERVER)
public class FilteredMachinePacket extends Message
{
    private EnumFacing facing;
    private boolean    whitelist;

    @MarshalledAs("blockpos")
    private BlockPos   pos;
    @MarshalledAs("i32")
    private int        dimensionID;

    public FilteredMachinePacket(final NetworkContext ctx)
    {
        super(ctx);
    }

    public FilteredMachinePacket(final TileEntity tile, final EnumFacing facing, final boolean whitelist)
    {
        this(QBarConstants.network);

        this.pos = tile.getPos();
        this.dimensionID = tile.getWorld().provider.getDimension();
        this.facing = facing;
        this.whitelist = whitelist;
    }

    @Override
    protected void handle(final EntityPlayer sender)
    {
        if (sender.getEntityWorld().provider.getDimension() == this.dimensionID
                && sender.getEntityWorld().isBlockLoaded(this.pos)
                && sender.getEntityWorld().getTileEntity(this.pos) != null
                && sender.getEntityWorld().getTileEntity(this.pos) instanceof IFilteredMachine)
        {
            ((IFilteredMachine) sender.getEntityWorld().getTileEntity(this.pos)).setWhitelist(this.facing,
                    this.whitelist);
            sender.getEntityWorld().getTileEntity(this.pos).markDirty();
        }
    }
}