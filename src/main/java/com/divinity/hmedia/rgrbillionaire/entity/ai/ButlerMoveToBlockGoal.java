package com.divinity.hmedia.rgrbillionaire.entity.ai;

import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public abstract class ButlerMoveToBlockGoal extends Goal {

    private static final int GIVE_UP_TICKS = 1200;
    private static final int STAY_TICKS = 1200;
    private static final int INTERVAL_TICKS = 200;
    protected final AIRoboButlerEntity mob;
    public final double speedModifier;

    protected int nextStartTick;
    protected int tryTicks;
    protected int maxStayTicks;

    protected BlockPos blockPos = BlockPos.ZERO;
    protected boolean reachedTarget;
    protected final int searchRange;
    protected final int verticalSearchRange;
    protected int verticalSearchStart;

    public ButlerMoveToBlockGoal(AIRoboButlerEntity pMob, double pSpeedModifier, int pSearchRange) {
        this(pMob, pSpeedModifier, pSearchRange, 1);
    }

    public ButlerMoveToBlockGoal(AIRoboButlerEntity pMob, double pSpeedModifier, int pSearchRange, int pVerticalSearchRange) {
        this.mob = pMob;
        this.speedModifier = pSpeedModifier;
        this.searchRange = pSearchRange;
        this.verticalSearchStart = 0;
        this.verticalSearchRange = pVerticalSearchRange;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            return this.findNearestBlock();
        }
    }

    protected int nextStartTick(PathfinderMob pCreature) {
        return reducedTickDelay(200 + pCreature.getRandom().nextInt(200));
    }


    @Override
    public boolean canContinueToUse() {
        return this.tryTicks >= -this.maxStayTicks && this.tryTicks <= (2 * 20) && this.isValidTarget(this.mob.level(), this.blockPos);
    }


    @Override
    public void start() {
        this.moveMobToBlock();
        this.tryTicks = 0;
        this.maxStayTicks = 40;
        this.reachedTarget = false;
    }

    protected void moveMobToBlock() {
        BlockPos targetPos = this.blockPos.above();

        for (Direction dir : Direction.values()) {
            BlockPos offset = this.blockPos.relative(dir);
            if (!this.mob.level().getBlockState(offset).isSolid()) {
                targetPos = offset;
                break;
            }
        }

        PathNavigation navigation = this.mob.getNavigation();
        Path path = navigation.createPath(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, 5);
        if (path != null && Math.abs(this.blockPos.getY() - path.getTarget().getY()) <= 4)
            this.mob.getNavigation().moveTo(path, this.speedModifier);
    }

    public double acceptedDistance() {
        return 2.0D;
    }

    protected BlockPos getMoveToTarget() {
        return this.blockPos.above();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!reachedIt()) {
            this.reachedTarget = false;
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                this.moveMobToBlock();
            }
        } else {
            this.reachedTarget = true;
            --this.tryTicks;
        }

    }

    protected boolean reachedIt() {
        return this.blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance());
    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 40 == 0;
    }


    protected boolean findNearestBlock() {
        int i = this.searchRange;
        int j = this.verticalSearchRange;
        BlockPos blockpos = this.mob.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k = this.verticalSearchStart; k <= j; k = k > 0 ? -k : 1 - k) {
            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, i1, k - 1, j1);
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

    protected abstract boolean isValidTarget(LevelReader pLevel, BlockPos pPos);
}
