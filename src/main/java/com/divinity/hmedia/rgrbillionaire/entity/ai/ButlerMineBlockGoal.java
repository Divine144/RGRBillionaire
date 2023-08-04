package com.divinity.hmedia.rgrbillionaire.entity.ai;

import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

// TODO: MAKE THIS WORK
public class ButlerMineBlockGoal extends MoveToBlockGoal {

    private static final int STUCK_TIMER = 20;
    public AIRoboButlerEntity imposter;
    protected float destroyProgress;
    private final Predicate<BlockState> blockTest;
    private int stuckTimer;
    private int horizontalRange;
    private int verticalRange;

    public ButlerMineBlockGoal(AIRoboButlerEntity imposter, double speedModifier, int horizontalRange, int verticalRange, Predicate<BlockState> blockTest) {
        this(imposter, speedModifier, horizontalRange, verticalRange, blockTest, 1f);
    }

    public ButlerMineBlockGoal(AIRoboButlerEntity imposter, double speedModifier, int horizontalRange, int verticalRange, Predicate<BlockState> blockTest, float probability) {
        super(imposter, speedModifier, horizontalRange, verticalRange);
        this.imposter = imposter;
        this.blockTest = blockTest;
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalRange;
    }

    @Override
    public boolean canContinueToUse() {
        return isValidTarget(mob.level(), blockPos) && !this.imposter.isSitting();
    }

    @Override
    public double acceptedDistance() {
        return 2D;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return blockTest.test(level.getBlockState(pos)) && pos.getY() >= imposter.blockPosition().getY() && !level.getBlockState(pos).isAir();
    }

    @Override
    protected void moveMobToBlock() {
        BlockPos target = getMoveToTarget();
        if (mob.level().getBlockState(target).getBlock() instanceof DropExperienceBlock) {
            if (mob.distanceToSqr(Vec3.atCenterOf(target)) > 10 * 10) {
                mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, speedModifier / 2);
            }
        }
        else mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, speedModifier / 2);
        mob.getLookControl().setLookAt(Vec3.atCenterOf(target));
        stuckTimer = 0;
    }

    @Override
    protected BlockPos getMoveToTarget() {
        Direction direction = BillionaireUtils.findHorizontalDirection(blockPos, mob.position());
        return blockPos.relative(direction);
    }

    @Override
    public void tick() {
        super.tick();
        System.out.println(blockPos);
        if (isReachedTarget() && isValidTarget(mob.level(), blockPos)) {
            imposter.setMining(true);

            BlockHitResult result = mob.level().clip(new ClipContext(mob.getEyePosition(), Vec3.atCenterOf(blockPos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
            BlockPos hit = result.getBlockPos();

            // If the butler is within 10 blocks of the block, he will attempt mine it
            if (imposter.level().getBlockState(blockPos).getBlock() instanceof DropExperienceBlock) {
                if (imposter.distanceToSqr(Vec3.atCenterOf(blockPos)) <= 10 * 10) {
                    hit = blockPos;
                }
            }

            BlockState state = mob.level().getBlockState(hit);
            float destroyTime = state.getDestroySpeed(mob.level(), blockPos);
            float speed = imposter.getTool().getDestroySpeed(state);
            speed /= state.requiresCorrectToolForDrops() && !imposter.getTool().isCorrectToolForDrops(state) ? 100 : 30;
            destroyProgress += speed;

            if (destroyProgress > destroyTime) {
                if (state.isSolid()) {
                    //mob.level.playSound(null, blockPos, state.getSoundType().getBreakSound(), SoundSource.PLAYERS, 1, 1);
                    imposter.setMining(false);
                    if (blockTest.test(state)) {
                        this.findNearestBlock();
                    }
                    var container = imposter.getInventory();
                    if (container != null) {
                        if (container.addItem(this.getStackForBlockState(mob.level().getBlockState(hit))) != ItemStack.EMPTY) {
                            // At this point, we know the butler's inventory is full, so drop blocks
                            mob.level().destroyBlock(hit, true);
                        }
                        else {
                            container.addItem(this.getStackForBlockState(mob.level().getBlockState(hit)));
                            mob.level().destroyBlock(hit, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean isReachedTarget() {
        boolean retVal = super.isReachedTarget();
        if (!retVal) {
            if (++stuckTimer >= STUCK_TIMER) {
                return true;
            }
        }
        return retVal;
    }

    @Override
    public void stop() {
        super.stop();
        imposter.setMining(false);
    }

    @Override
    public boolean canUse() {
        return findNearestBlock() && !this.imposter.isSitting();
    }

    @Override
    public void start() {
        this.destroyProgress = 0.0f;
        stuckTimer = 0;
        super.start();
    }

    private ItemStack getStackForBlockState(BlockState state) {
        return state.getBlock().asItem().getDefaultInstance();
    }
}