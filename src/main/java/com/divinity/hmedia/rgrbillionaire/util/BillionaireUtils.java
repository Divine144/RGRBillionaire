package com.divinity.hmedia.rgrbillionaire.util;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class BillionaireUtils {

    public static void createHelix(DustParticleOptions type, LivingEntity player, ServerLevel pLevel, float radius) {
        Vec3 loc = player.position();
        for (double y = 0; y <= 1; y += 0.001) {
            double x = radius * Math.cos(9 * y);
            double z = radius * Math.sin(9 * y);
            pLevel.sendParticles(type, loc.x() + x, loc.y() + 1.1, loc.z() + z, 0, 0, 0, 0, 0);
        }
    }

    public static boolean hasEnoughMoney(Player player, int amount) {
        var holder = BillionaireHolderAttacher.getHolderUnwrap(player);
        if (holder != null) {
            return holder.getMoney() >= amount;
        }
        return false;
    }

    public static void takeMoney(Player player, int amount) {
        var holder = BillionaireHolderAttacher.getHolderUnwrap(player);
        if (holder != null) {
            holder.addMoney(-amount);
        }
    }

    // Snipped from HundredDaysStory
    public static Direction findHorizontalDirection(BlockPos pos, Vec3 vector) {
        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 direction = vector.subtract(center);
        boolean eastWest = (Math.abs(direction.x()) > Math.abs(direction.z()));
        if (eastWest) {
            if (direction.x >= 0) {
                return Direction.EAST;
            } else {
                return Direction.WEST;
            }
        } else {
            if (direction.z >= 0) {
                return Direction.SOUTH;
            } else {
                return Direction.NORTH;
            }
        }
    }

}
