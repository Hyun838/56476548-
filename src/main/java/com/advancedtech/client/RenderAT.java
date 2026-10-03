package com.advancedtech.client;

import com.advancedtech.AdvancedTech;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Рендер гуманоидных мобов мода: биped-модель + своя текстура + масштаб. */
@SideOnly(Side.CLIENT)
public class RenderAT<T extends EntityLiving> extends RenderBiped<T> {
    private final ResourceLocation texture;
    private final float scale;

    public RenderAT(RenderManager m, String textureName, float scale) {
        super(m, new ModelBiped(), 0.5F * scale);
        this.texture = new ResourceLocation(AdvancedTech.MODID, "textures/entity/" + textureName + ".png");
        this.scale = scale;
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) { return texture; }

    @Override
    protected void preRenderCallback(T entity, float partialTickTime) {
        GlStateManager.scale(scale, scale, scale);
    }
}
