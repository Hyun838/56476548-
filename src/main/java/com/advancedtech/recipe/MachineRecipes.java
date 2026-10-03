package com.advancedtech.recipe;

import com.advancedtech.registry.ModBlocks;
import com.advancedtech.registry.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class MachineRecipes {
    public static final List<MachineRecipe> CRUSHER = new ArrayList<>();
    public static final List<MachineRecipe> COMPRESSOR = new ArrayList<>();
    public static final List<MachineRecipe> ALLOY = new ArrayList<>();

    private static ItemStack s(String name, int n) { return new ItemStack(ModItems.get(name), n); }

    private static String cap(String m) { return Character.toUpperCase(m.charAt(0)) + m.substring(1); }

    public static void init() {
        CRUSHER.clear(); COMPRESSOR.clear(); ALLOY.clear();

        // Дробилка: руда -> 2 пыли, слиток -> пыль
        for (String m : new String[]{"copper", "tin", "silver", "iron", "gold"}) {
            CRUSHER.add(new MachineRecipe(s("dust_" + m, 2), 160, 20, "ore" + cap(m), 1));
            CRUSHER.add(new MachineRecipe(s("dust_" + m, 1), 100, 16, "ingot" + cap(m), 1));
        }
        CRUSHER.add(new MachineRecipe(s("dust_bronze", 1), 100, 16, "ingotBronze", 1));

        // Пресс: слиток -> пластина
        for (String m : new String[]{"copper", "tin", "silver", "iron", "gold", "bronze"}) {
            COMPRESSOR.add(new MachineRecipe(s("plate_" + m, 1), 120, 20, "ingot" + cap(m), 1));
        }

        // Сплавная печь: 3 меди + 1 олова = 4 бронзы
        ALLOY.add(new MachineRecipe(s("ingot_bronze", 4), 200, 24, "ingotCopper", 3, "ingotTin", 1));
        ALLOY.add(new MachineRecipe(s("ingot_bronze", 4), 200, 24, "dustCopper", 3, "dustTin", 1));
        ALLOY.add(new MachineRecipe(s("dust_bronze", 4), 160, 20, "dustCopper", 3, "dustTin", 1));

        // Обычная печь: руды и пыль -> слитки
        GameRegistry.addSmelting(ModBlocks.ORE_COPPER, s("ingot_copper", 1), 0.7f);
        GameRegistry.addSmelting(ModBlocks.ORE_TIN, s("ingot_tin", 1), 0.7f);
        GameRegistry.addSmelting(ModBlocks.ORE_SILVER, s("ingot_silver", 1), 0.9f);
        GameRegistry.addSmelting(ModItems.get("dust_copper"), s("ingot_copper", 1), 0.3f);
        GameRegistry.addSmelting(ModItems.get("dust_tin"), s("ingot_tin", 1), 0.3f);
        GameRegistry.addSmelting(ModItems.get("dust_silver"), s("ingot_silver", 1), 0.3f);
        GameRegistry.addSmelting(ModItems.get("dust_bronze"), s("ingot_bronze", 1), 0.3f);
        GameRegistry.addSmelting(ModItems.get("dust_iron"), new ItemStack(Items.IRON_INGOT), 0.3f);
        GameRegistry.addSmelting(ModItems.get("dust_gold"), new ItemStack(Items.GOLD_INGOT), 0.3f);
    }
}
