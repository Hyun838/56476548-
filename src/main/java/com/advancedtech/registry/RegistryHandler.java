package com.advancedtech.registry;

import com.advancedtech.AdvancedTech;
import com.advancedtech.block.BlockCable;
import com.advancedtech.block.BlockMachine;
import com.advancedtech.energy.EnergyTier;
import com.advancedtech.item.ItemCasingUpgrade;
import com.advancedtech.item.ItemEngineerBook;
import com.advancedtech.research.ResearchRegistry;
import com.advancedtech.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber(modid = AdvancedTech.MODID)
public class RegistryHandler {

    private static final String[] INGOT_METALS = {"copper", "tin", "silver", "bronze"};
    private static final String[] ALL_METALS = {"copper", "tin", "silver", "bronze", "iron", "gold"};

    private static String cap(String m) { return Character.toUpperCase(m.charAt(0)) + m.substring(1); }

    // ---------------- блоки ----------------
    private static Block ore(String name, int harvest) {
        Block b = new Block(Material.ROCK);
        b.setRegistryName(AdvancedTech.MODID, name);
        b.setTranslationKey(AdvancedTech.MODID + "." + name);
        b.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        b.setHardness(3.0f);
        b.setResistance(5.0f);
        b.setHarvestLevel("pickaxe", harvest);
        return b;
    }

    private static void reg(net.minecraftforge.registries.IForgeRegistry<Block> r, Block b) {
        ModBlocks.ALL.add(b);
        r.register(b);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e) {
        net.minecraftforge.registries.IForgeRegistry<Block> r = e.getRegistry();

        ModBlocks.ELECTRIC_FURNACE = new BlockMachine("electric_furnace", TileElectricFurnace::new, "processing");
        ModBlocks.COMBUSTION_GENERATOR = new BlockMachine("combustion_generator", TileCombustionGenerator::new, "electric_circuits");
        ModBlocks.SOLAR_PANEL = new BlockMachine("solar_panel", TileSolarPanel::new, "solar_energy");
        ModBlocks.CRUSHER = new BlockMachine("crusher", TileCrusher::new, "processing");
        ModBlocks.COMPRESSOR = new BlockMachine("compressor", TileCompressor::new, "processing");
        ModBlocks.ALLOY_SMELTER = new BlockMachine("alloy_smelter", TileAlloySmelter::new, "automation");
        ModBlocks.CREATIVE_SOURCE = new BlockMachine("creative_energy_source", TileCreativeSource::new, null);

        reg(r, ModBlocks.ELECTRIC_FURNACE);
        reg(r, ModBlocks.COMBUSTION_GENERATOR);
        reg(r, ModBlocks.SOLAR_PANEL);
        reg(r, ModBlocks.CRUSHER);
        reg(r, ModBlocks.COMPRESSOR);
        reg(r, ModBlocks.ALLOY_SMELTER);
        reg(r, ModBlocks.CREATIVE_SOURCE);

        ModBlocks.ORE_COPPER = ore("ore_copper", 1);
        ModBlocks.ORE_TIN = ore("ore_tin", 1);
        ModBlocks.ORE_SILVER = ore("ore_silver", 2);
        reg(r, ModBlocks.ORE_COPPER);
        reg(r, ModBlocks.ORE_TIN);
        reg(r, ModBlocks.ORE_SILVER);

        for (EnergyTier t : EnergyTier.values()) {
            BlockCable c = new BlockCable("cable_" + t.name().toLowerCase(), t);
            ModBlocks.CABLES[t.ordinal()] = c;
            reg(r, c);
        }

        GameRegistry.registerTileEntity(TileElectricFurnace.class, new ResourceLocation(AdvancedTech.MODID, "electric_furnace"));
        GameRegistry.registerTileEntity(TileCombustionGenerator.class, new ResourceLocation(AdvancedTech.MODID, "combustion_generator"));
        GameRegistry.registerTileEntity(TileSolarPanel.class, new ResourceLocation(AdvancedTech.MODID, "solar_panel"));
        GameRegistry.registerTileEntity(TileCrusher.class, new ResourceLocation(AdvancedTech.MODID, "crusher"));
        GameRegistry.registerTileEntity(TileCompressor.class, new ResourceLocation(AdvancedTech.MODID, "compressor"));
        GameRegistry.registerTileEntity(TileAlloySmelter.class, new ResourceLocation(AdvancedTech.MODID, "alloy_smelter"));
        GameRegistry.registerTileEntity(TileCreativeSource.class, new ResourceLocation(AdvancedTech.MODID, "creative_energy_source"));
        GameRegistry.registerTileEntity(TileCable.class, new ResourceLocation(AdvancedTech.MODID, "cable"));

        // Блокировки крафта до изучения
        ResearchRegistry.lock("advancedtech:electric_furnace", "processing");
        ResearchRegistry.lock("advancedtech:crusher", "processing");
        ResearchRegistry.lock("advancedtech:compressor", "processing");
        ResearchRegistry.lock("advancedtech:alloy_smelter", "automation");
        ResearchRegistry.lock("advancedtech:combustion_generator", "electric_circuits");
        ResearchRegistry.lock("advancedtech:solar_panel", "solar_energy");
        ResearchRegistry.lock("advancedtech:air_filter", "pollution_control");
        ResearchRegistry.lock("advancedtech:cable_mv", "mv_tier");
        ResearchRegistry.lock("advancedtech:cable_hv", "hv_tier");
        ResearchRegistry.lock("advancedtech:cable_ev", "quantum_tech");
        ResearchRegistry.lock("advancedtech:casing_upgrade_improved", "automation");
        ResearchRegistry.lock("advancedtech:casing_upgrade_industrial", "hv_tier");
        ResearchRegistry.lock("advancedtech:casing_upgrade_quantum", "quantum_tech");
    }

    // ---------------- предметы ----------------
    private static Item basic(String name, CreativeTabs tab) {
        Item i = new Item();
        i.setRegistryName(AdvancedTech.MODID, name);
        i.setTranslationKey(AdvancedTech.MODID + "." + name);
        i.setCreativeTab(tab);
        return i;
    }

    private static Item add(net.minecraftforge.registries.IForgeRegistry<Item> r, String name, Item item, String ore) {
        r.register(item);
        ModItems.add(name, item);
        if (ore != null) OreDictionary.registerOre(ore, item);
        return item;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
        net.minecraftforge.registries.IForgeRegistry<Item> r = e.getRegistry();

        for (String m : INGOT_METALS)
            add(r, "ingot_" + m, basic("ingot_" + m, CreativeTabs.MATERIALS), "ingot" + cap(m));
        for (String m : ALL_METALS) {
            add(r, "dust_" + m, basic("dust_" + m, CreativeTabs.MATERIALS), "dust" + cap(m));
            add(r, "plate_" + m, basic("plate_" + m, CreativeTabs.MATERIALS), "plate" + cap(m));
        }

        ModItems.ENGINEER_BOOK = add(r, "engineer_book", new ItemEngineerBook(), null);
        ModItems.CIRCUIT = add(r, "circuit", basic("circuit", CreativeTabs.MATERIALS), "circuitBasic");
        ModItems.AIR_FILTER = add(r, "air_filter", basic("air_filter", CreativeTabs.MISC).setMaxStackSize(1).setMaxDamage(1200), null);
        ModItems.BIOMASS = add(r, "biomass", basic("biomass", CreativeTabs.MATERIALS), null);
        ModItems.QUANTUM_CORE = add(r, "quantum_core", basic("quantum_core", CreativeTabs.MATERIALS), null);
        ModItems.CASINGS[0] = add(r, "casing_upgrade_improved", new ItemCasingUpgrade("casing_upgrade_improved", 1), null);
        ModItems.CASINGS[1] = add(r, "casing_upgrade_industrial", new ItemCasingUpgrade("casing_upgrade_industrial", 2), null);
        ModItems.CASINGS[2] = add(r, "casing_upgrade_quantum", new ItemCasingUpgrade("casing_upgrade_quantum", 3), null);

        for (Block b : ModBlocks.ALL) {
            ItemBlock ib = new ItemBlock(b);
            ib.setRegistryName(b.getRegistryName());
            r.register(ib);
            ModItems.ALL.add(ib);
            if (b == ModBlocks.ORE_COPPER) OreDictionary.registerOre("oreCopper", ib);
            if (b == ModBlocks.ORE_TIN) OreDictionary.registerOre("oreTin", ib);
            if (b == ModBlocks.ORE_SILVER) OreDictionary.registerOre("oreSilver", ib);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        for (Item item : ModItems.ALL) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                    new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
