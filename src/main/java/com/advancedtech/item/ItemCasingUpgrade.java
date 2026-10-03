package com.advancedtech.item;

import com.advancedtech.AdvancedTech;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/** ПКМ по машине: улучшает корпус на следующий уровень (1 - улучшенный, 2 - индустриальный, 3 - квантовый). */
public class ItemCasingUpgrade extends Item {
    private final int level;

    public ItemCasingUpgrade(String name, int level) {
        this.level = level;
        setRegistryName(AdvancedTech.MODID, name);
        setTranslationKey(AdvancedTech.MODID + "." + name);
        setCreativeTab(CreativeTabs.MISC);
    }

    public int getLevel() { return level; }
}
