package com.advancedtech.entity;

import com.advancedtech.registry.ModItems;
import com.advancedtech.research.ResearchEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;

/** Босс: Механический Голем. Стреляет ракетами, роняет квантовое ядро. */
public class EntityMechanicalGolem extends EntityMob implements IRangedAttackMob {
    private final BossInfoServer bossInfo;

    public EntityMechanicalGolem(World w) {
        super(w);
        setSize(1.4F, 3.2F);
        experienceValue = 100;
        isImmuneToFire = true;
        bossInfo = new BossInfoServer(new TextComponentString("Механический Голем"),
                BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
        enablePersistence();
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackRanged(this, 0.9D, 40, 24.0F));
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.6D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        tasks.addTask(6, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        double dx = target.posX - posX;
        double dy = target.getEntityBoundingBox().minY + target.height / 2.0F - (posY + height * 0.75D);
        double dz = target.posZ - posZ;
        EntityLargeFireball rocket = new EntityLargeFireball(world, this, dx, dy, dz);
        rocket.explosionPower = 1;
        rocket.setPosition(posX, posY + height * 0.75D, posZ);
        world.spawnEntity(rocket);
        playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.5F, 0.6F);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) { }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        bossInfo.removePlayer(player);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        bossInfo.setPercent(getHealth() / getMaxHealth());
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote) {
            Entity src = cause.getTrueSource();
            if (src instanceof EntityPlayerMP) ResearchEvents.award((EntityPlayerMP) src, 100, "босс");
        }
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        entityDropItem(new ItemStack(ModItems.QUANTUM_CORE, 1), 0.0F);
        entityDropItem(new ItemStack(ModItems.CIRCUIT, 4 + rand.nextInt(5)), 0.0F);
        entityDropItem(new ItemStack(ModItems.get("plate_iron"), 6 + rand.nextInt(6)), 0.0F);
    }

    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.ENTITY_IRONGOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_IRONGOLEM_DEATH; }
}
