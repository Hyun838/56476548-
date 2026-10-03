package com.advancedtech.gui;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/** Данные Книги Рецептов (сгенерировано из таблицы рецептов, совпадает с JSON-рецептами). */
public class GuideData {
    public static class Entry {
        public final int tab;
        public final String result;
        public final int count;
        public final String[] grid;

        public Entry(int tab, String result, int count, String[] grid) {
            this.tab = tab; this.result = result; this.count = count; this.grid = grid;
        }

        public String shortName() { return result.substring(result.indexOf(':') + 1); }

        public ItemStack resultStack() {
            Item it = ForgeRegistries.ITEMS.getValue(new ResourceLocation(result));
            if (it == null || it == Items.AIR) return ItemStack.EMPTY;
            return new ItemStack(it, count);
        }
    }

    public static final List<Entry> ENTRIES = new ArrayList<>();

    public static List<Entry> forTab(int tab) {
        List<Entry> out = new ArrayList<>();
        for (Entry e : ENTRIES) if (e.tab == tab) out.add(e);
        return out;
    }

    static {
        ENTRIES.add(new Entry(1, "advancedtech:engineer_book", 1, new String[]{"minecraft:book", "minecraft:redstone", "minecraft:iron_ingot", null, null, null, null, null, null}));
        ENTRIES.add(new Entry(1, "advancedtech:circuit", 1, new String[]{"minecraft:redstone", "minecraft:redstone", "minecraft:redstone", "ore:ingotCopper", "minecraft:iron_ingot", "ore:ingotCopper", null, null, null}));
        ENTRIES.add(new Entry(1, "advancedtech:cable_lv", 6, new String[]{"minecraft:redstone", "minecraft:redstone", "minecraft:redstone", "ore:ingotCopper", "ore:ingotCopper", "ore:ingotCopper", null, null, null}));
        ENTRIES.add(new Entry(1, "advancedtech:cable_mv", 1, new String[]{"advancedtech:cable_lv", "ore:ingotTin", "minecraft:iron_ingot", null, null, null, null, null, null}));
        ENTRIES.add(new Entry(1, "advancedtech:cable_hv", 1, new String[]{"advancedtech:cable_mv", "ore:ingotSilver", "minecraft:diamond", null, null, null, null, null, null}));
        ENTRIES.add(new Entry(1, "advancedtech:cable_ev", 1, new String[]{"advancedtech:cable_hv", "advancedtech:quantum_core", null, null, null, null, null, null, null}));
        ENTRIES.add(new Entry(2, "advancedtech:combustion_generator", 1, new String[]{"minecraft:iron_ingot", "minecraft:iron_ingot", "minecraft:iron_ingot", "minecraft:iron_ingot", "minecraft:furnace", "minecraft:iron_ingot", "minecraft:iron_ingot", "advancedtech:circuit", "minecraft:iron_ingot"}));
        ENTRIES.add(new Entry(2, "advancedtech:solar_panel", 1, new String[]{"minecraft:glass", "minecraft:glass", "minecraft:glass", "advancedtech:circuit", "ore:plateSilver", "advancedtech:circuit", "minecraft:iron_ingot", "minecraft:redstone", "minecraft:iron_ingot"}));
        ENTRIES.add(new Entry(2, "advancedtech:air_filter", 1, new String[]{"minecraft:paper", "minecraft:paper", "minecraft:paper", "minecraft:iron_bars", "minecraft:coal", "minecraft:iron_bars", null, null, null}));
        ENTRIES.add(new Entry(3, "advancedtech:electric_furnace", 1, new String[]{"minecraft:iron_ingot", "minecraft:iron_ingot", "minecraft:iron_ingot", "advancedtech:circuit", "minecraft:furnace", "advancedtech:circuit", "minecraft:iron_ingot", "minecraft:redstone", "minecraft:iron_ingot"}));
        ENTRIES.add(new Entry(3, "advancedtech:crusher", 1, new String[]{"minecraft:flint", "minecraft:flint", "minecraft:flint", "minecraft:iron_ingot", "advancedtech:circuit", "minecraft:iron_ingot", "minecraft:iron_ingot", "minecraft:redstone", "minecraft:iron_ingot"}));
        ENTRIES.add(new Entry(3, "advancedtech:compressor", 1, new String[]{"minecraft:iron_ingot", "minecraft:piston", "minecraft:iron_ingot", "minecraft:iron_ingot", "advancedtech:circuit", "minecraft:iron_ingot", "minecraft:iron_ingot", "minecraft:redstone", "minecraft:iron_ingot"}));
        ENTRIES.add(new Entry(3, "advancedtech:alloy_smelter", 1, new String[]{"minecraft:iron_ingot", "minecraft:furnace", "minecraft:iron_ingot", "advancedtech:circuit", "minecraft:redstone", "advancedtech:circuit", "minecraft:iron_ingot", "minecraft:furnace", "minecraft:iron_ingot"}));
        ENTRIES.add(new Entry(4, "advancedtech:casing_upgrade_improved", 1, new String[]{"ore:plateIron", "ore:plateIron", "ore:plateIron", "ore:plateIron", "advancedtech:circuit", "ore:plateIron", "ore:plateIron", "ore:plateIron", "ore:plateIron"}));
        ENTRIES.add(new Entry(4, "advancedtech:casing_upgrade_industrial", 1, new String[]{"ore:plateBronze", "ore:plateBronze", "ore:plateBronze", "ore:plateBronze", "advancedtech:casing_upgrade_improved", "ore:plateBronze", "ore:plateBronze", "ore:plateBronze", "ore:plateBronze"}));
        ENTRIES.add(new Entry(4, "advancedtech:casing_upgrade_quantum", 1, new String[]{"ore:plateGold", "advancedtech:quantum_core", "ore:plateGold", "ore:plateGold", "advancedtech:casing_upgrade_industrial", "ore:plateGold", "ore:plateGold", "ore:plateGold", "ore:plateGold"}));
    }
}
