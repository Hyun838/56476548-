package com.advancedtech.tile;

import com.advancedtech.energy.EnergyTier;
import com.advancedtech.recipe.MachineRecipe;
import net.minecraft.item.ItemStack;

import java.util.List;

/** Общая машина с рецептами: 1 вход -> выход, или 2 входа -> выход. */
public abstract class TileRecipeMachine extends TileEntityMachine {
    private final List<MachineRecipe> recipes;
    private final boolean dual;
    private MachineRecipe current;
    private MachineRecipe lastRecipe;

    protected TileRecipeMachine(List<MachineRecipe> recipes, boolean dual) {
        super(dual ? 3 : 2, EnergyTier.LV);
        this.recipes = recipes;
        this.dual = dual;
    }

    private int outSlot() { return dual ? 2 : 1; }
    private ItemStack inA() { return inventory.getStackInSlot(0); }
    private ItemStack inB() { return dual ? inventory.getStackInSlot(1) : ItemStack.EMPTY; }

    private MachineRecipe find() {
        for (MachineRecipe r : recipes) if (r.match(inA(), inB()) != null) return r;
        return null;
    }

    @Override
    public boolean canProcess() {
        MachineRecipe r = find();
        if (r != lastRecipe) { progress = 0; lastRecipe = r; }
        current = r;
        return r != null && canOutput(outSlot(), r.output);
    }

    @Override
    public void finishProcess() {
        MachineRecipe r = find();
        if (r == null || !canOutput(outSlot(), r.output)) return;
        int[] use = r.match(inA(), inB());
        output(outSlot(), r.output);
        inA().shrink(use[0]);
        if (dual) inB().shrink(use[1]);
        markDirty();
    }

    @Override public int getBaseProcessTime() { return current != null ? current.time : 200; }
    @Override public int getEnergyPerTick() { return current != null ? current.energy : 20; }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == outSlot()) return false;
        for (MachineRecipe r : recipes) if (r.accepts(stack)) return true;
        return false;
    }

    @Override
    public int[][] getSlotLayout() {
        return dual ? new int[][]{{38, 35}, {60, 35}, {116, 35}} : new int[][]{{56, 35}, {116, 35}};
    }
}
