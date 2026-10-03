package com.advancedtech.item;

import com.advancedtech.AdvancedTech;
import com.advancedtech.gui.GuiHandler;
import com.advancedtech.net.Network;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/** ПКМ открывает интерфейс: исследования + книга рецептов. */
public class ItemEngineerBook extends Item {
    public ItemEngineerBook() {
        setRegistryName(AdvancedTech.MODID, "engineer_book");
        setTranslationKey(AdvancedTech.MODID + ".engineer_book");
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand hand) {
        ItemStack stack = p.getHeldItem(hand);
        if (w.isRemote) {
            p.openGui(AdvancedTech.instance, GuiHandler.BOOK, w, 0, 0, 0);
        } else if (p instanceof EntityPlayerMP) {
            Network.syncResearch((EntityPlayerMP) p);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
