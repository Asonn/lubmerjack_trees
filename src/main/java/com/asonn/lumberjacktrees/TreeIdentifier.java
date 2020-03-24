package com.asonn.lumberjacktrees;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class TreeIdentifier {

    private class Node {
        private Block block;
        private BlockPos pos;

        public Node(Block block, BlockPos blockPos) {
            this.block = block;
            this.pos = blockPos;
        }

        public Block getBlock() {
            return block;
        }

        public BlockPos getPos() {
            return pos;
        }
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private World world;
    private Map<BlockPos, Node> tree;
    private List<BlockPos> trunk;

    public TreeIdentifier(World world) {
        this.world = world;
    }

    /**
     * @param stump beginning of the trunk
     * @return if it is a tree or not
     */
    public void fromStump(BlockPos stump) {
        this.clear(); // Clear old data

        findStump(stump);
        findTrunk(stump.offset(Direction.UP));

        if (this.trunk.size() == this.tree.size()) {
            // Clear as it is not a tree
            this.clear();
        }
    }

    public List<BlockPos> getTrunk() {
        return this.trunk;
    }

    private void findStump(BlockPos start) {
        Queue<BlockPos> stumpPositions = new LinkedList<BlockPos>();

        stumpPositions.add(start);

        // Find the full stump, but stay on the same Y level, this is required for stumps the like 2x2 stump of the
        // Jungle tree
        do {
            BlockPos currentPos = stumpPositions.poll();

            if (currentPos == null) {
                break;
            }

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos newPos = currentPos.add(x, 0, z);

                    if (!alreadyKnown(newPos) && isTreeBlock(newPos)) {
                        stumpPositions.add(newPos);
                    }
                }
            }

        } while (stumpPositions.size() > 0);
    }

    private void findTrunk(BlockPos start) {
        Queue<BlockPos> positions = new LinkedList<BlockPos>();

        positions.add(start);

        // Find the rest of the tree
        do {
            BlockPos currentPos = positions.poll();

            if (currentPos == null) {
                break;
            }

            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos newPos = currentPos.add(x, y, z);

                        if (!alreadyKnown(newPos) && isTreeBlock(newPos)) {
                            positions.add(newPos);
                        }
                    }
                }
            }

        } while (positions.size() > 0);
    }

    private boolean isTreeBlock(BlockPos pos) {
        BlockState bs = this.world.getBlockState(pos);

        if (!isTreeLeaf(bs) && !isTreeLog(bs)) {
            return false;
        }

        if (isTreeLog(bs)) {
            this.trunk.add(pos);
        }

        this.tree.put(pos, new Node(bs.getBlock(), pos));

        return true;
    }

    private boolean alreadyKnown(BlockPos pos) {
        return this.tree.get(pos) != null;
    }

    private boolean isTreeLog(BlockState blockState) {
        return BlockTags.LOGS.contains(blockState.getBlock());
    }

    private boolean isTreeLeaf(BlockState blockState) {
        if (!BlockTags.LEAVES.contains(blockState.getBlock())) {
            return false;
        }

        Boolean persistentProp = (Boolean) blockState.getValues().get(LeavesBlock.PERSISTENT);
        return persistentProp == Boolean.valueOf(false);
    }

    private void clear() {
        this.trunk = new Vector<>();
        // Just use a HashMap, you can rewrite it to a more efficient map if it has performance issues
        this.tree = new HashMap<>();
    }

}

