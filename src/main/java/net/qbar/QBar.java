package net.qbar;

import com.elytradev.concrete.network.NetworkContext;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.qbar.common.CommonProxy;
import net.qbar.common.CustomCreativeTab;
import org.apache.logging.log4j.Logger;

@Mod(modid = QBar.MODID, version = QBar.VERSION, name = QBar.MODNAME)
public class QBar
{
    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    public static final String MODID   = "qbar";
    public static final String VERSION = "0.1";
    public static final String MODNAME = "QBar";

    @SidedProxy(clientSide = "net.qbar.client.ClientProxy", serverSide = "net.qbar.server.ServerProxy")
    public static CommonProxy proxy;

    @Instance(QBar.MODID)
    public static QBar instance;

    public static Logger logger;

    public static final CreativeTabs TAB_ALL = new CustomCreativeTab("QBar");

    public static NetworkContext network;

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        QBar.proxy.preInit(event);
    }

    @EventHandler
    public void init(final FMLInitializationEvent event)
    {
        QBar.proxy.init(event);
    }

    @EventHandler
    public void postInit(final FMLPostInitializationEvent event)
    {
        QBar.proxy.postInit(event);
    }

    @EventHandler
    public void serverStarted(final FMLServerStartedEvent event)
    {
        QBar.proxy.serverStarted(event);
    }

    @EventHandler
    public void serverStopping(final FMLServerStoppingEvent event)
    {
        QBar.proxy.serverStopping(event);
    }
}
