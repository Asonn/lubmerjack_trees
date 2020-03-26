package com.asonn.lumberjacktrees;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldInteraction {

    private static void dropItem(World worldIn, BlockPos pos, Block block) {
        if (!worldIn.isRemote()) {
            NonNullList <ItemStack> itemStack = NonNullList.create();
            itemStack.add(new ItemStack(block.asItem()));
            InventoryHelper.dropItems(worldIn, pos, itemStack);
        }
    }

    private static void removeBlock(World worldIn, BlockPos pos) {
        worldIn.removeBlock(pos, false);
    }

    public static void chopLog(World worldIn, BlockPos pos, Block block) {
        removeBlock(worldIn, pos);
        dropItem(worldIn, pos, block);
    }
}
