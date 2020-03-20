package com.asonn.lumberjacktrees;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("lumberjacktrees")
public class LumberjackTrees {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MOD_ID = "lumberjacktrees";
    private static final Tag <Item> AXES = new ItemTags.Wrapper(new ResourceLocation(MOD_ID, "axetags"));

    public LumberjackTrees() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Cut down whole tree but keeps the stump intact
     * @param event event fired when a player breaks a block
     */
    @SubscribeEvent
    public void onBlockDestroyedByPlayer(BlockEvent.BreakEvent event) {
        ItemStack toolInstance = event.getPlayer().getHeldItemMainhand();
        Block block = event.getState().getBlock();
        BlockPos pos = event.getPos();
        World worldIn = event.getWorld().getWorld();

        if (playerHoldsAxeAndIsCuttingLogs(toolInstance, block)) {
            BlockPos newPosition = pos.offset(Direction.UP);
            removeIfSameBlockTypeIsConnected(worldIn, newPosition, block, pos);
        }
    }

    private boolean playerHoldsAxeAndIsCuttingLogs(ItemStack toolInstance, Block block) {
        return (!toolInstance.isEmpty() && AXES.contains(toolInstance.getItem()) && BlockTags.LOGS.contains(block));
    }

    private void removeIfSameBlockTypeIsConnected(World worldIn, BlockPos pos, Block block, BlockPos startPosition) {
        if (worldIn.getBlockState(pos).getBlock().equals(block)) {
            worldIn.removeBlock(pos, false);
            dropItem(worldIn, pos, block);
            goThroughNextPoints(worldIn, pos, block, startPosition);
        }
    }

    private void goThroughNextPoints(World worldIn, BlockPos pos, Block block, BlockPos startPosition) {
        if (isStump(pos, startPosition)) {
            return;
        }
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos newPosition = pos.add(x, y, z);
                    removeIfSameBlockTypeIsConnected(worldIn, newPosition, block, startPosition);
                }
            }
        }
    }

    private boolean isStump(BlockPos pos, BlockPos startPosition) {
        return pos.getY() <= startPosition.getY() && pos.getX() == startPosition.getX() && pos.getZ() == startPosition.getZ();
    }

    private void dropItem(World worldIn, BlockPos pos, Block block) {
        if(!worldIn.isRemote()) {
            NonNullList <ItemStack> itemStack = NonNullList.create();
            itemStack.add(new ItemStack(block.asItem()));
            InventoryHelper.dropItems(worldIn, pos, itemStack);
        }
    }
}
