package com.advancedtech.block;

import com.advancedtech.AdvancedTech;
import com.advancedtech.config.ATConfig;
import com.advancedtech.gui.GuiHandler;
import com.advancedtech.item.ItemCasingUpgrade;
import com.advancedtech.research.IResearchData;
import com.advancedtech.research.ResearchCapability;
import com.advancedtech.tile.TileEntityMachine;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BlockMachine extends Block {
    private final Supplier<TileEntityMachine> factory;
    @Nullable private final String requiredResearch;

    public BlockMachine(String name, Supplier<TileEntityMachine> factory, @Nullable String requiredResearch) {
        super(Material.IRON);
        this.factory = factory;
        this.requiredResearch = requiredResearch;
        setRegistryName(AdvancedTech.MODID, name);
        setTranslationKey(AdvancedTech.MODID + "." + name);
        setCreativeTab(CreativeTabs.REDSTONE);
        setHardness(3.5f);
        setResistance(10f);
        setHarvestLevel("pickaxe", 1);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return factory.get(); }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand,
                                    EnumFacing side, float hx, float hy, float hz) {
        if (w.isRemote) return true;

        // Исследование (в творческом режиме не требуется)
        if (ATConfig.enableResearch && requiredResearch != null && !p.isCreative()) {
            IResearchData d = ResearchCapability.get(p);
            if (d == null || !d.isUnlocked(requiredResearch)) {
                p.sendMessage(new TextComponentTranslation("msg.advancedtech.locked",
                        new TextComponentTranslation("research.advancedtech." + requiredResearch)));
                return true;
            }
        }

        TileEntity te = w.getTileEntity(pos);
        ItemStack held = p.getHeldItem(hand);

        // Улучшение корпуса
        if (!held.isEmpty() && held.getItem() instanceof ItemCasingUpgrade && te instanceof TileEntityMachine) {
            ItemCasingUpgrade up = (ItemCasingUpgrade) held.getItem();
            TileEntityMachine m = (TileEntityMachine) te;
            if (m.getCasing() + 1 == up.getLevel()) {
                m.upgradeCasing();
                if (!p.isCreative()) held.shrink(1);
                p.sendStatusMessage(new TextComponentString("Корпус улучшен до уровня " + m.getCasing()), true);
            } else if (m.getCasing() >= up.getLevel()) {
                p.sendStatusMessage(new TextComponentString("Корпус уже на этом уровне или выше"), true);
            } else {
                p.sendStatusMessage(new TextComponentString("Сначала нужен улучшитель уровня " + (m.getCasing() + 1)), true);
            }
            return true;
        }

        p.openGui(AdvancedTech.instance, GuiHandler.MACHINE, w, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state) {
        TileEntity te = w.getTileEntity(pos);
        if (te instanceof TileEntityMachine) {
            for (int i = 0; i < ((TileEntityMachine) te).getInventory().getSlots(); i++) {
                ItemStack s = ((TileEntityMachine) te).getInventory().getStackInSlot(i);
                if (!s.isEmpty()) InventoryHelper.spawnItemStack(w, pos.getX(), pos.getY(), pos.getZ(), s);
            }
        }
        super.breakBlock(w, pos, state);
    }
}
