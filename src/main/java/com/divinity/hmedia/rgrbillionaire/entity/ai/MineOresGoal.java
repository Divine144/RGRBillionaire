package com.divinity.hmedia.rgrbillionaire.entity.ai;

import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import com.divinity.hmedia.rgrbillionaire.entity.api.IBlockInteractor;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MineOresGoal<T extends AIRoboButlerEntity> extends ButlerMoveToBlockGoal {

    private boolean breaking = false;

    public MineOresGoal(T mob, double speedModifier, int searchRange, int verticalSearchRange) {
        super(mob, speedModifier, searchRange, verticalSearchRange);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public void start() {
        super.start();
        this.breaking = false;
        ((IBlockInteractor) mob).setInteractionTicks(0);
    }

    @Override
    protected int nextStartTick(PathfinderMob pCreature) {
        return reducedTickDelay(20);
    }

    @Override
    public double acceptedDistance() {
        return 2D;
    }

    @Override
    protected boolean reachedIt() {
        return super.reachedIt() || this.reachedTarget || (this.tryTicks >= 5 && this.mob.getNavigation().isDone());
    }

    @Override
    protected boolean findNearestBlock() {
        BlockPos blockpos = this.mob.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int l = 0; l < this.searchRange; ++l) {
            for (int xOff = 0; xOff <= l; xOff = xOff > 0 ? -xOff : 1 - xOff) {
                for (int zOff = xOff < l && xOff > -l ? l : 0; zOff <= l; zOff = zOff > 0 ? -zOff : 1 - zOff) {
                    for (int yOff = 0; yOff <= this.verticalSearchRange; yOff = yOff > 0 ? -yOff : 1 - yOff) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, xOff, yOff - 1, zOff);

                        if (this.mob.isWithinRestriction(blockpos$mutableblockpos) && this.isValidTarget(this.mob.level(), blockpos$mutableblockpos)) {
                            this.blockPos = blockpos$mutableblockpos;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.reachedTarget) {
            IBlockInteractor blockInteractor = mob;
            int ticks = blockInteractor.getInteractionTicks();
            Level level = this.mob.level();
            if (!this.breaking && ticks == 0 && isValidTarget(level, blockPos)) {
                Vec3 eyePos = this.mob.getEyePosition();
                if (this.blockPos.getY() - this.mob.getY() > 3 && level.getBlockState(new BlockPos((int)eyePos.x, (int)eyePos.y, (int)eyePos.z).above()).isAir()) {
                    this.mob.setPos(this.mob.getBlockX() + 0.5D, this.mob.getY(), this.mob.getBlockZ() + 0.5D);
                    this.mob.getJumpControl().jump();
                    if (level.getBlockState(this.mob.blockPosition()).isAir())
                        level.setBlockAndUpdate(this.mob.blockPosition(), Blocks.COBBLESTONE.defaultBlockState());
                    this.reachedTarget = false;
                    return;
                }
                this.breaking = true;
                blockInteractor.setMining(true);
                this.mob.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atBottomCenterOf(this.blockPos));
                blockInteractor.setInteractionTicks(3);
                this.tryTicks = 0;
            }
            else if (this.breaking && ticks == 0) {
                Vec3 startPos = new Vec3(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                Vec3 endPos = Vec3.atCenterOf(this.blockPos);
                double distance = 10.0D;
                boolean canReachInitial = startPos.distanceTo(Vec3.atCenterOf(blockPos)) <= distance;
                BlockPos breakPos = clip(startPos, endPos);
                boolean canReach;
                if (canReachInitial) {
                    breakPos = blockPos;
                    canReach = true;
                }
                else {
                    if (breakPos == null)
                        return;
                    canReach = startPos.distanceTo(Vec3.atCenterOf(breakPos)) <= distance;
                    if (!canReach) {
                        Vec3 footPos = this.mob.position().add(0, 0.5, 0);
                        breakPos = clip(footPos, endPos);

                        if (breakPos == null)
                            return;
                        canReach = footPos.distanceTo(Vec3.atCenterOf(breakPos)) <= distance;
                    }
                }
                if (canReach) {
                    BlockState blockState = level.getBlockState(breakPos);
                    if (blockState.getDestroySpeed(level, breakPos) != -1.0F) {
                        var container = this.mob.getInventory();
                        if (container != null && level instanceof ServerLevel serverLevel) {
                            var modifiedDrops = this.getDropsForBlockPos(serverLevel, breakPos);
                            List<ItemStack> poppedStacks = new ArrayList<>();
                            for (ItemStack stack : modifiedDrops) {
                                if (!container.canAddItem(stack)) {
                                    // At this point, we know the butler's inventory cannot take that item, so pop it
                                    if (stack != ItemStack.EMPTY) {
                                        poppedStacks.add(stack);
                                    }
                                }
                                else {
                                    // If the butler can still take the item it will, but remaining will be popped
                                    var temp = container.addItem(stack);
                                    if (temp != ItemStack.EMPTY) {
                                        poppedStacks.add(temp);
                                    }
                                }
                            }
                            if (!poppedStacks.isEmpty()) {
                                BlockPos finalBreakPos = breakPos;
                                poppedStacks.forEach(stack -> Block.popResource(serverLevel, finalBreakPos, stack));
                            }
                            level.levelEvent(2001, breakPos, Block.getId(blockState));
                            blockState.spawnAfterBreak(serverLevel, breakPos , mob.getTool(), true);
                            serverLevel.setBlock(breakPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                    if (this.blockPos.equals(breakPos)) {
                        // Try to be faster on veins
                        for (Direction dir : Direction.values()) {
                            BlockPos newPos = this.blockPos.relative(dir);
                            if (isValidTarget(level, newPos)) {
                                this.blockPos = newPos;
                                start();
                                return;
                            }
                        }
                        stopGoal();
                        this.nextStartTick = 20;
                    }
                    else {
                        this.moveMobToBlock();
                    }
                }
                else {
                    stopGoal();
                    return;
                }
                this.tryTicks = 0;
                this.breaking = false;
                blockInteractor.setMining(false);
            }
            else if (this.breaking) {
                blockInteractor.setInteractionTicks(ticks - 1);
            }
        }
    }

    @Nullable
    private BlockPos clip(Vec3 startPos, Vec3 endPos) {
        BlockHitResult clipResult = this.mob.level().clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
        if (clipResult.getType() == HitResult.Type.MISS) {
            stopGoal();
            return null;
        }
        return clipResult.getBlockPos();
    }

    private void stopGoal() {
        this.mob.goalSelector.getAvailableGoals().stream()
                .filter(wrappedGoal -> wrappedGoal.getGoal() == this)
                .filter(WrappedGoal::isRunning)
                .forEach(WrappedGoal::stop);
    }

    @Override
    public void stop() {
        super.stop();
        IBlockInteractor blockInteractor = mob;
        blockInteractor.setMining(false);
        blockInteractor.setInteractionTicks(0);
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos))
            return false;
        BlockState blockState = level.getBlockState(pos);
        return !blockState.isAir() && (blockState.getBlock() instanceof DropExperienceBlock);
    }

    private List<ItemStack> getDropsForBlockPos(ServerLevel level, BlockPos pos) {
        var list = Block.getDrops(level.getBlockState(pos), level, pos, null);
        list.forEach(i -> i.setCount(i.getCount() * 2)); // Multiplying Drops by 2 cause butler
        return list;
    }
}
