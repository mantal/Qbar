package net.ros.common.tile;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.fluid.LimitedTank;
import net.ros.common.machine.Machines;
import net.ros.common.machine.component.SteamComponent;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamTank;

public class TileOffshorePump extends TileBase implements ITickable
{
    private SteamComponent steamComponent;
    private int            transferCapacity;
    private IFluidHandler  top;
    private boolean        water = false;

    private final LimitedTank tank;

    private final SteamTank steamTank;

    private EnumFacing facing;

    public TileOffshorePump(final int transferCapacity)
    {
        this.transferCapacity = transferCapacity;

        this.tank = new LimitedTank(0, 0);
        this.tank.setCanDrain(false);
        this.tank.setCanFill(false);

        this.steamComponent = Machines.OFFSHORE_PUMP.get(SteamComponent.class);
        this.steamTank = new SteamTank(steamComponent.getSteamCapacity(), steamComponent.getMaxPressureCapacity());

        this.facing = EnumFacing.NORTH;
    }

    public TileOffshorePump()
    {
        this(0);
    }

    @Override
    public void update()
    {
        if (this.world.getTotalWorldTime() % 40 == 0)
        {
            this.water = this.world.getBlockState(this.getPos().down()).getBlock().equals(Blocks.WATER);
            this.scanFluidHandler();
        }

        if (this.water && this.steamTank.drainSteam(steamComponent.getSteamConsumption(), true) ==
                steamComponent.getSteamConsumption())
            this.transferFluid();
    }

    private void transferFluid()
    {
        if (this.top != null)
            this.top.fill(new FluidStack(FluidRegistry.WATER, this.transferCapacity), true);
    }

    private void scanFluidHandler()
    {
        final BlockPos posNeighbor = this.getPos().up();
        final TileEntity tile = this.world.getTileEntity(posNeighbor);

        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN))
            this.top = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
        else if (this.top != null)
            this.top = null;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        tag.setInteger("transferCapacity", this.transferCapacity);

        final NBTTagCompound subTag = new NBTTagCompound();
        this.steamTank.writeToNBT(subTag);
        tag.setTag("steamTank", subTag);

        tag.setInteger("facing", this.facing.ordinal());

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.transferCapacity = tag.getInteger("transferCapacity");

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));

        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
        super.readFromNBT(tag);
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        list.addText("Orientation: " + this.getFacing());
        list.addText("Water : " + this.water);
        list.addText("Top : " + (this.top != null));
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (facing == EnumFacing.UP && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;
        if (capability == SteamCapabilities.STEAM_HANDLER
                && facing == this.getFacing().rotateAround(Axis.Y).getOpposite())
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (facing == EnumFacing.UP && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this.tank;
        if (capability == SteamCapabilities.STEAM_HANDLER
                && facing == this.getFacing().rotateAround(Axis.Y).getOpposite())
            return (T) this.steamTank;
        return super.getCapability(capability, facing);
    }

    public EnumFacing getFacing()
    {
        return this.facing;
    }

    public void setFacing(final EnumFacing facing)
    {
        this.facing = facing;
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        if (this.isClient())
            this.askServerSync();
    }
}
