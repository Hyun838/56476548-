package com.advancedtech.client;

import com.advancedtech.AdvancedTech;
import com.advancedtech.entity.EntityMechanicalGolem;
import com.advancedtech.entity.EntityMutant;
import com.advancedtech.entity.EntityRobotGuard;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = AdvancedTech.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void registerRenderers(ModelRegistryEvent e) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRobotGuard.class,
                m -> new RenderAT<EntityRobotGuard>(m, "robot_guard", 1.0F));
        RenderingRegistry.registerEntityRenderingHandler(EntityMutant.class,
                m -> new RenderAT<EntityMutant>(m, "mutant", 1.0F));
        RenderingRegistry.registerEntityRenderingHandler(EntityMechanicalGolem.class,
                m -> new RenderAT<EntityMechanicalGolem>(m, "mechanical_golem", 1.9F));
    }
}
