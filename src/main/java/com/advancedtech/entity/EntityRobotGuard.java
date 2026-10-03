package com.advancedtech.entity;

import com.advancedtech.registry.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/** Робот-охранник: стреляет лазером (мгновенный луч), дропает микросхемы. */
public class EntityRobotGuard extends EntityMob implements IRangedAttackMob {

    public EntityRobotGuard(World w) {
        super(w);
        setSize(0.6F, 1.95F);
        experienceValue = 10;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 30, 16.0F));
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(6, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(6.0D);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        double sx = posX, sy = posY + getEyeHeight(), sz = posZ;
        double tx = target.posX, ty = target.posY + target.height * 0.6D, tz = target.posZ;
        if (world instanceof WorldServer) {
            WorldServer ws = (WorldServer) world;
            int steps = 16;
            for (int i = 1; i <= steps; i++) {
                double f = (double) i / steps;
                ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC, sx + (tx - sx) * f, sy + (ty - sy) * f,
                        sz + (tz - sz) * f, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.6F);
        target.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) { }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (!wasRecentlyHit) return;
        entityDropItem(new ItemStack(ModItems.CIRCUIT, 1 + rand.nextInt(2 + lootingModifier)), 0.0F);
        if (rand.nextInt(3) == 0) entityDropItem(new ItemStack(ModItems.get("plate_iron"), 1 + rand.nextInt(2)), 0.0F);
    }

    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.ENTITY_IRONGOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_IRONGOLEM_DEATH; }
}
