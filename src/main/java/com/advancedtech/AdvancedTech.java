package com.advancedtech;

import com.advancedtech.entity.ModEntities;
import com.advancedtech.command.CommandATech;
import com.advancedtech.gui.GuiHandler;
import com.advancedtech.net.Network;
import com.advancedtech.recipe.MachineRecipes;
import com.advancedtech.research.IResearchData;
import com.advancedtech.research.ResearchData;
import com.advancedtech.research.ResearchStorage;
import com.advancedtech.world.ModWorldGen;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = AdvancedTech.MODID, name = AdvancedTech.NAME, version = AdvancedTech.VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "required-after:forge@[14.23.5.2847,);after:ic2;after:crafttweaker")
public class AdvancedTech {
    public static final String MODID = "advancedtech";
    public static final String NAME = "AdvancedTech Evolution";
    public static final String VERSION = "0.2.0";

    @Mod.Instance(MODID)
    public static AdvancedTech instance;
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        CapabilityManager.INSTANCE.register(IResearchData.class, new ResearchStorage(), ResearchData::new);
        Network.init();
        ModEntities.register();
        GameRegistry.registerWorldGenerator(new ModWorldGen(), 3);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        MachineRecipes.init();
        ModEntities.registerSpawns();
        if (Loader.isModLoaded("ic2")) logger.info("IC2 найден (мост EU<->FE пока не реализован)");
        if (Loader.isModLoaded("crafttweaker")) logger.info("CraftTweaker найден (ZenScript API пока не реализован)");
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandATech());
    }
}
