package com.advancedtech.tile;

import com.advancedtech.energy.EnergyTier;
import net.minecraft.item.ItemStack;

/** Солнечная панель: днём под открытым небом даёт энергию, без загрязнения. Корпус увеличивает выход. */
public class TileSolarPanel extends TileEntityMachine {
    public static final int BASE_OUTPUT = 6;

    public TileSolarPanel() { super(0, EnergyTier.LV); }

    @Override
    public void update() {
        if (world.isRemote) return;
        if (world.isDaytime() && !world.isRaining() && world.canSeeSky(pos.up())) {
            energy.generate(BASE_OUTPUT * (1 + casing));
        }
        pushEnergy();
        if (world.getTotalWorldTime() % 20 == 0) markDirty();
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
