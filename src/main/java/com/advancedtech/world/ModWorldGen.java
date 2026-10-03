package com.advancedtech.world;

import com.advancedtech.AdvancedTech;
import com.advancedtech.config.ATConfig;
import com.advancedtech.registry.ModBlocks;
import com.advancedtech.registry.ModItems;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class ModWorldGen implements IWorldGenerator {

    @Override
    public void generate(Random r, int cx, int cz, World w, IChunkGenerator gen, IChunkProvider prov) {
        if (w.provider.getDimension() != 0) return;
        if (ATConfig.enableOreGen) {
            ore(w, r, cx, cz, ModBlocks.ORE_COPPER.getDefaultState(), 9, 14, 20, 72);
            ore(w, r, cx, cz, ModBlocks.ORE_TIN.getDefaultState(), 8, 10, 20, 64);
            ore(w, r, cx, cz, ModBlocks.ORE_SILVER.getDefaultState(), 6, 4, 5, 40);
        }
        if (ATConfig.enableLabs && r.nextInt(Math.max(1, ATConfig.labRarity)) == 0) {
            lab(w, r, cx, cz);
        }
    }

    private void ore(World w, Random r, int cx, int cz, IBlockState state, int size, int tries, int minY, int maxY) {
        WorldGenMinable g = new WorldGenMinable(state, size);
        for (int i = 0; i < tries; i++) {
            BlockPos p = new BlockPos(cx * 16 + 8 + r.nextInt(16), minY + r.nextInt(maxY - minY), cz * 16 + 8 + r.nextInt(16));
            g.generate(w, r, p);
        }
    }

    private IBlockState wall(Random r) {
        int k = r.nextInt(4);
        BlockStoneBrick.EnumType t = k == 0 ? BlockStoneBrick.EnumType.CRACKED
                : k == 1 ? BlockStoneBrick.EnumType.MOSSY : BlockStoneBrick.EnumType.DEFAULT;
        return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, t);
    }

    /** Заброшенная лаборатория 7x7: сундук с лутом и спавнер Роботов-охранников. */
    private void lab(World w, Random r, int cx, int cz) {
        int x0 = cx * 16 + 4 + 8, z0 = cz * 16 + 4 + 8;
        int y0 = w.getHeight(new BlockPos(x0, 0, z0)).getY() - 1;
        if (y0 < 50 || y0 > 110) return;
        if (w.getBlockState(new BlockPos(x0 + 3, y0, z0 + 3)).getMaterial().isLiquid()) return;

        IBlockState air = Blocks.AIR.getDefaultState();
        for (int dx = 0; dx < 7; dx++) {
            for (int dz = 0; dz < 7; dz++) {
                boolean edge = dx == 0 || dx == 6 || dz == 0 || dz == 6;
                for (int dy = -1; dy < 5; dy++) {
                    IBlockState s;
                    if (dy == -1) s = Blocks.STONEBRICK.getDefaultState();
                    else if (dy == 4) s = r.nextInt(5) == 0 ? air : wall(r);
                    else if (edge) {
                        boolean door = dz == 0 && dx == 3 && dy < 2;
                        if (door) s = air;
                        else if (dy == 2 && r.nextInt(2) == 0) s = Blocks.IRON_BARS.getDefaultState();
                        else s = wall(r);
                    } else s = air;
                    w.setBlockState(new BlockPos(x0 + dx, y0 + dy, z0 + dz), s, 2);
                }
            }
        }
        w.setBlockState(new BlockPos(x0 + 1, y0, z0 + 1), Blocks.IRON_BLOCK.getDefaultState(), 2);

        BlockPos spawner = new BlockPos(x0 + 3, y0, z0 + 3);
        w.setBlockState(spawner, Blocks.MOB_SPAWNER.getDefaultState(), 2);
        TileEntity st = w.getTileEntity(spawner);
        if (st instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner) st).getSpawnerBaseLogic().setEntityId(new ResourceLocation(AdvancedTech.MODID, "robot_guard"));
        }

        BlockPos chest = new BlockPos(x0 + 5, y0, z0 + 5);
        w.setBlockState(chest, Blocks.CHEST.getDefaultState(), 2);
        TileEntity ct = w.getTileEntity(chest);
        if (ct instanceof TileEntityChest) {
            TileEntityChest c = (TileEntityChest) ct;
            c.setInventorySlotContents(0, new ItemStack(ModItems.CIRCUIT, 3 + r.nextInt(5)));
            c.setInventorySlotContents(1, new ItemStack(ModItems.get("ingot_copper"), 2 + r.nextInt(6)));
            c.setInventorySlotContents(3, new ItemStack(ModItems.get("plate_iron"), 4 + r.nextInt(8)));
            if (r.nextInt(3) == 0) c.setInventorySlotContents(5, new ItemStack(ModItems.ENGINEER_BOOK));
            if (r.nextInt(4) == 0) c.setInventorySlotContents(7, new ItemStack(ModItems.CASINGS[0]));
        }
    }
}
