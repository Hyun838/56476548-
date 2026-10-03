package com.advancedtech.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/** Рецепт машины: 1 или 2 входа (строка = ore dictionary, либо ItemStack). */
public class MachineRecipe {
    public final Object[] inputs;
    public final int[] counts;
    public final ItemStack output;
    public final int time;
    public final int energy;

    /** @param inAndCounts пары (вход, количество): "oreCopper", 1, ... */
    public MachineRecipe(ItemStack output, int time, int energy, Object... inAndCounts) {
        this.output = output;
        this.time = time;
        this.energy = energy;
        int n = inAndCounts.length / 2;
        this.inputs = new Object[n];
        this.counts = new int[n];
        for (int i = 0; i < n; i++) {
            inputs[i] = inAndCounts[i * 2];
            counts[i] = (Integer) inAndCounts[i * 2 + 1];
        }
    }

    /** Сколько брать из слота A и B; null если рецепт не подходит. */
    public int[] match(ItemStack a, ItemStack b) {
        if (inputs.length == 1) {
            return (ok(inputs[0], a) && a.getCount() >= counts[0]) ? new int[]{counts[0], 0} : null;
        }
        if (ok(inputs[0], a) && a.getCount() >= counts[0] && ok(inputs[1], b) && b.getCount() >= counts[1])
            return new int[]{counts[0], counts[1]};
        if (ok(inputs[0], b) && b.getCount() >= counts[0] && ok(inputs[1], a) && a.getCount() >= counts[1])
            return new int[]{counts[1], counts[0]};
        return null;
    }

    public boolean accepts(ItemStack s) {
        for (Object in : inputs) if (ok(in, s)) return true;
        return false;
    }

    private static boolean ok(Object in, ItemStack s) {
        if (s.isEmpty()) return false;
        if (in instanceof String) {
            int id = OreDictionary.getOreID((String) in);
            for (int x : OreDictionary.getOreIDs(s)) if (x == id) return true;
            return false;
        }
        return OreDictionary.itemMatches((ItemStack) in, s, false);
    }
}
