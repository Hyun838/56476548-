package com.advancedtech.tile;

import com.advancedtech.energy.EnergyTier;
import net.minecraft.item.ItemStack;

/** Бесконечный источник энергии (LV, 32 FE/т) - для тестов. */
public class TileCreativeSource extends TileEntityMachine {
    public TileCreativeSource() { super(0, EnergyTier.LV); }

    @Override
    public void update() {
        if (world.isRemote) return;
        energy.generate(energy.getTier().maxTransfer);
        pushEnergy();
    }

    @Override protected void tickHeat(boolean working) { }
    @Override public boolean acceptsEnergy() { return false; }
    @Override public boolean canProcess() { return false; }
    @Override public void finishProcess() { }
    @Override public int getBaseProcessTime() { return 1; }
    @Override public int getEnergyPerTick() { return 0; }
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) { return false; }
    @Override public int[][] getSlotLayout() { return new int[0][]; }
}
