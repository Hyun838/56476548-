package com.advancedtech.registry;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    /** Все предметы мода (включая ItemBlock) - для регистрации моделей. */
    public static final List<Item> ALL = new ArrayList<>();
    private static final Map<String, Item> BY_NAME = new HashMap<>();

    public static Item ENGINEER_BOOK, CIRCUIT, AIR_FILTER, BIOMASS, QUANTUM_CORE;
    public static final Item[] CASINGS = new Item[3];

    public static void add(String name, Item item) {
        BY_NAME.put(name, item);
        ALL.add(item);
    }

    public static Item get(String name) {
        Item i = BY_NAME.get(name);
        if (i == null) throw new IllegalStateException("Unknown AdvancedTech item: " + name);
        return i;
    }
}
