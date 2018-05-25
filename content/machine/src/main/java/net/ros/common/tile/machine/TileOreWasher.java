package net.ros.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.ROSConstants;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.*;
import net.ros.common.recipe.RecipeHandler;
import net.ros.common.steam.SteamUtil;
import net.ros.common.util.FluidUtils;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.machine.module.impl.AutomationModule;
import net.ros.common.machine.module.impl.CraftingModule;
import net.ros.common.machine.module.impl.FluidStorageModule;

public class TileOreWasher extends TileTickingModularMachine implements IContainerProvider
{
    public TileOreWasher()
    {
        super(Machines.ORE_WASHER);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new FluidStorageModule(this));
        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new InventoryModule(this));
        this.addModule(new CraftingModule(this));
        this.addModule(new AutomationModule(this));
        this.addModule(new IOModule(this));
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("crafting");
        CraftingModule crafter = this.getModule(CraftingModule.class);
        SteamModule steamEngine = this.getModule(SteamModule.class);
        FluidStorageModule fluidStorage = this.getModule(FluidStorageModule.class);

        return new ContainerBuilder("orewasher", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(inventory)
                .recipeSlot(0, RecipeHandler.ORE_WASHER_UID, 0, 47, 36,
                        slot -> crafter.isBufferEmpty() && crafter.isOutputEmpty())
                .outputSlot(2, 107, 35).outputSlot(3, 125, 35).displaySlot(1, -1000, 0)
                .syncFloatValue(crafter::getCurrentProgress, crafter::setCurrentProgress)
                .syncFloatValue(crafter::getMaxProgress, crafter::setMaxProgress)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("washer"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("washer"))::setFluid)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        IFluidHandler tank = this.getModule(FluidStorageModule.class).getFluidHandler("washer");

        if (FluidUtils.drainPlayerHand(tank, player) || FluidUtils.fillPlayerHand(tank, player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(ROSConstants.MODINSTANCE, MachineGui.OREWASHER.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }
}
