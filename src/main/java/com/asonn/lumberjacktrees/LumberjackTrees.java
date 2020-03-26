package com.asonn.lumberjacktrees;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ModInfo.MOD_ID)
public class LumberjackTrees {

    private static final Tag <Item> AXES = new ItemTags.Wrapper(new ResourceLocation(ModInfo.MOD_ID, "axetags"));

    public LumberjackTrees() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Cut down whole tree but keeps the stump intact
     *
     * @param event event fired when a player breaks a block
     */
    @SubscribeEvent
    public void onBlockDestroyedByPlayer(BlockEvent.BreakEvent event) {
        ItemStack toolInstance = event.getPlayer().getHeldItemMainhand();
        Block block = event.getState().getBlock();
        BlockPos pos = event.getPos();
        World worldIn = event.getWorld().getWorld();

        if (playerHoldsAxeAndIsCuttingLogs(toolInstance, block)) {
            TreeIdentifier tree = new TreeIdentifier(worldIn);
            tree.fromStump(pos);
            for (BlockPos logPos : tree.getTrunk()) {
                Block logBlock = worldIn.getBlockState(logPos).getBlock();
                WorldInteraction.chopLog(worldIn, logPos, logBlock);
            }
        }
    }

    private boolean playerHoldsAxeAndIsCuttingLogs(ItemStack toolInstance, Block block) {
        return (!toolInstance.isEmpty() && AXES.contains(toolInstance.getItem()) && BlockTags.LOGS.contains(block));
    }
}
