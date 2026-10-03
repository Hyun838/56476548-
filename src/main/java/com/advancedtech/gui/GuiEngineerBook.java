package com.advancedtech.gui;

import com.advancedtech.net.Network;
import com.advancedtech.net.UnlockResearchMessage;
import com.advancedtech.pollution.PollutionManager;
import com.advancedtech.research.IResearchData;
import com.advancedtech.research.ResearchCapability;
import com.advancedtech.research.ResearchNode;
import com.advancedtech.research.ResearchRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/** Книга Инженера: вкладка исследований + вкладки рецептов (сетка крафта, описание, подсветка предметов). */
@SideOnly(Side.CLIENT)
public class GuiEngineerBook extends GuiScreen {
    private static final int W = 320, H = 210;
    private static final String[] TABS = {"research", "basic", "generators", "processing", "automation"};

    private int left, top;
    private int tab = 0;
    private int selected = 0;
    private boolean highlight = false;
    private ItemStack hover = ItemStack.EMPTY;

    private FontRenderer fr() { return mc.getRenderManager().getFontRenderer(); }

    @Override
    public void initGui() {
        left = (width - W) / 2;
        top = (height - H) / 2;
        rebuild();
    }

    @Override public boolean doesGuiPauseGame() { return false; }

    /** Загрязнение чанка игрока (работает в одиночной игре, на сервере показывает 0). */
    private float clientPollution() {
        net.minecraft.server.MinecraftServer srv = mc.getIntegratedServer();
        if (srv == null || mc.player == null) return 0;
        net.minecraft.world.World w = srv.getWorld(mc.player.dimension);
        return w == null ? 0 : PollutionManager.get(w, mc.player.getPosition());
    }

    private List<ResearchNode> nodes() { return new ArrayList<>(ResearchRegistry.all()); }

    private void rebuild() {
        buttonList.clear();
        for (int i = 0; i < TABS.length; i++) {
            GuiButton b = new GuiButton(i, left + 4 + i * 62, top + 4, 60, 18, I18n.format("guide.tab." + TABS[i]));
            b.enabled = i != tab;
            buttonList.add(b);
        }
        if (tab == 0) {
            List<ResearchNode> ns = nodes();
            for (int i = 0; i < ns.size(); i++)
                buttonList.add(new GuiButton(100 + i, left + W - 72, top + 28 + i * 17, 62, 15, I18n.format("guide.learn")));
        } else {
            List<GuideData.Entry> es = GuideData.forTab(tab);
            for (int i = 0; i < es.size(); i++) {
                ItemStack rs = es.get(i).resultStack();
                String name = rs.isEmpty() ? es.get(i).shortName() : rs.getDisplayName();
                buttonList.add(new GuiButton(200 + i, left + 6, top + 28 + i * 20, 112, 18, name));
            }
            buttonList.add(new GuiButton(300, left + W - 138, top + H - 24, 130, 18,
                    I18n.format(highlight ? "guide.highlight.on" : "guide.highlight.off")));
        }
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id < TABS.length) {
            tab = b.id; selected = 0; rebuild();
        } else if (b.id >= 100 && b.id < 200) {
            List<ResearchNode> ns = nodes();
            int i = b.id - 100;
            if (i < ns.size()) Network.CHANNEL.sendToServer(new UnlockResearchMessage(ns.get(i).id));
        } else if (b.id >= 200 && b.id < 300) {
            selected = b.id - 200;
        } else if (b.id == 300) {
            highlight = !highlight; rebuild();
        }
    }

    // ---------- отрисовка ----------
    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();
        drawRect(left, top, left + W, top + H, 0xFF555555);
        drawRect(left + 2, top + 2, left + W - 2, top + H - 2, 0xFF1E1E1E);
        hover = ItemStack.EMPTY;
        List<String> tooltip = null;

        IResearchData data = ResearchCapability.get(mc.player);
        if (tab == 0) tooltip = drawResearch(data, mx, my);
        else drawRecipe(data, mx, my);

        super.drawScreen(mx, my, pt);
        if (tooltip != null) drawHoveringText(tooltip, mx, my);
        else if (!hover.isEmpty()) renderToolTip(hover, mx, my);
    }

    private List<String> drawResearch(IResearchData data, int mx, int my) {
        List<String> tip = null;
        List<ResearchNode> ns = nodes();
        for (int i = 0; i < ns.size(); i++) {
            ResearchNode n = ns.get(i);
            int y = top + 28 + i * 17;
            boolean unlocked = data != null && data.isUnlocked(n.id);
            boolean can = data != null && data.canUnlock(n.id);
            int color = unlocked ? 0x55FF55 : can ? 0xFFFF55 : 0x888888;
            fr().drawString(I18n.format("research.advancedtech." + n.id) + " (" + n.cost + ")", left + 8, y + 4, color);
            for (GuiButton b : buttonList) if (b.id == 100 + i) { b.enabled = can; b.visible = !unlocked; }
            if (mx >= left + 6 && mx < left + W - 76 && my >= y && my < y + 16) {
                tip = new ArrayList<>();
                tip.add(I18n.format("research.advancedtech." + n.id));
                tip.add(I18n.format("research.advancedtech." + n.id + ".desc"));
                for (String p : n.parents)
                    tip.add(I18n.format("guide.requires") + ": " + I18n.format("research.advancedtech." + p));
            }
        }
        int pts = data == null ? 0 : data.getPoints();
        fr().drawString(I18n.format("guide.points") + ": " + pts, left + 8, top + H - 28, 0x55FFFF);
        int pol = Math.round(clientPollution());
        fr().drawString(I18n.format("guide.pollution") + ": " + pol, left + 8, top + H - 16, 0xFFAA00);
        return tip;
    }

    private void drawRecipe(IResearchData data, int mx, int my) {
        List<GuideData.Entry> es = GuideData.forTab(tab);
        if (es.isEmpty()) {
            fr().drawString(I18n.format("guide.empty"), left + 10, top + 34, 0xAAAAAA);
            return;
        }
        if (selected >= es.size()) selected = 0;
        GuideData.Entry e = es.get(selected);
        ItemStack result = e.resultStack();
        int x0 = left + 126;

        fr().drawString((result.isEmpty() ? e.shortName() : result.getDisplayName()) + (e.count > 1 ? " x" + e.count : ""),
                x0, top + 30, 0xFFFFFF);

        // сетка 3x3
        int gx = x0 + 4, gy = top + 48;
        List<String> missing = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int sx = gx + (i % 3) * 20, sy = gy + (i / 3) * 20;
            drawRect(sx, sy, sx + 18, sy + 18, 0xFF8B8B8B);
            drawRect(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF373737);
            String spec = e.grid[i];
            if (spec == null) continue;
            ItemStack st = pick(spec);
            boolean has = hasItem(spec);
            if (highlight) {
                int c = has ? 0xFF00CC00 : 0xFFCC0000;
                drawRect(sx - 1, sy - 1, sx + 19, sy, c); drawRect(sx - 1, sy + 18, sx + 19, sy + 19, c);
                drawRect(sx - 1, sy, sx, sy + 18, c); drawRect(sx + 18, sy, sx + 19, sy + 18, c);
                if (!has && !st.isEmpty()) missing.add(st.getDisplayName());
            }
            if (!st.isEmpty()) {
                drawStack(st, sx + 1, sy + 1);
                if (mx >= sx && mx < sx + 18 && my >= sy && my < sy + 18) hover = st;
            }
        }
        fr().drawString("=>", gx + 66, gy + 26, 0xAAAAAA);
        int rx = gx + 90, ry = gy + 20;
        drawRect(rx, ry, rx + 20, ry + 20, 0xFF8B8B8B);
        drawRect(rx + 1, ry + 1, rx + 19, ry + 19, 0xFF373737);
        if (!result.isEmpty()) {
            drawStack(result, rx + 2, ry + 2);
            if (mx >= rx && mx < rx + 20 && my >= ry && my < ry + 20) hover = result;
        }

        // описание
        int ty = top + 112;
        String desc = I18n.format("guide.advancedtech." + e.shortName() + ".desc");
        for (Object line : fr().listFormattedStringToWidth(desc, W - (x0 - left) - 10)) {
            fr().drawString((String) line, x0, ty, 0xCCCCCC);
            ty += 10;
        }
        // требуемое исследование
        String req = result.isEmpty() ? null : ResearchRegistry.getRequirement(result);
        if (req != null && (data == null || !data.isUnlocked(req))) {
            fr().drawString(I18n.format("guide.locked") + ": " + I18n.format("research.advancedtech." + req), x0, ty + 4, 0xFF5555);
            ty += 14;
        }
        if (highlight && !missing.isEmpty()) {
            fr().drawString(I18n.format("guide.missing") + ":", x0, ty + 4, 0xFF8888);
            ty += 14;
            for (String m : missing) { fr().drawString("- " + m, x0, ty, 0xFFAAAA); ty += 10; if (ty > top + H - 30) break; }
        }
    }

    private void drawStack(ItemStack s, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(s, x, y);
        RenderHelper.disableStandardItemLighting();
    }

    // ---------- предметы из спецификации ----------
    private List<ItemStack> variants(String spec) {
        List<ItemStack> out = new ArrayList<>();
        if (spec.startsWith("ore:")) {
            for (ItemStack s : OreDictionary.getOres(spec.substring(4))) {
                ItemStack c = s.copy();
                if (c.getMetadata() == OreDictionary.WILDCARD_VALUE) c.setItemDamage(0);
                out.add(c);
            }
        } else {
            Item it = ForgeRegistries.ITEMS.getValue(new ResourceLocation(spec));
            if (it != null && it != Items.AIR) out.add(new ItemStack(it));
        }
        return out;
    }

    private ItemStack pick(String spec) {
        List<ItemStack> v = variants(spec);
        if (v.isEmpty()) return ItemStack.EMPTY;
        return v.get((int) ((Minecraft.getSystemTime() / 1000L) % v.size()));
    }

    private boolean hasItem(String spec) {
        List<ItemStack> v = variants(spec);
        for (ItemStack inv : mc.player.inventory.mainInventory) {
            if (inv.isEmpty()) continue;
            for (ItemStack s : v) if (OreDictionary.itemMatches(s, inv, false)) return true;
        }
        return false;
    }
}
