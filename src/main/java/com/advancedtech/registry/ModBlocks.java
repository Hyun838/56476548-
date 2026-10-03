package com.advancedtech.registry;

import com.advancedtech.block.BlockCable;
import com.advancedtech.block.BlockMachine;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> ALL = new ArrayList<>();

    public static BlockMachine ELECTRIC_FURNACE, COMBUSTION_GENERATOR, SOLAR_PANEL, CRUSHER,
            COMPRESSOR, ALLOY_SMELTER, CREATIVE_SOURCE;
    public static Block ORE_COPPER, ORE_TIN, ORE_SILVER;
    /** Индекс = EnergyTier.ordinal(): LV, MV, HV, EV. */
    public static final BlockCable[] CABLES = new BlockCable[4];
}
