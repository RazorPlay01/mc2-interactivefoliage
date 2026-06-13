package net.karto.mc2.interactivefoliage.client;

import net.karto.mc2.interactivefoliage.FoliageConfig;
import net.karto.mc2.interactivefoliage.FoliageLeanState;
import net.karto.mc2.interactivefoliage.FoliageLeanState.LeanVector;
import net.karto.mc2.interactivefoliage.FoliageRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoliagePhysicsHandler {

    private static final float MAX_INFLUENCE_RADIUS = 1.2f;
    private static final int   BLOCK_SEARCH_MARGIN  = 2;
    private static final float CHANGE_THRESHOLD     = 0.08f;

    private static final Map<BlockPos, LeanVector> previousLean = new HashMap<>();

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;

        // Si el mod está desactivado → limpiar y salir
        if (!FoliageConfig.get().enabled) {
            if (!FoliageLeanState.ACTIVE.isEmpty()) {
                for (BlockPos pos : FoliageLeanState.ACTIVE.keySet()) {
                    BlockState st = level.getBlockState(pos);
                    mc.levelRenderer.blockChanged(level, pos, st, st, 0);
                }
                FoliageLeanState.clear();
                previousLean.clear();
            }
            return;
        }

        // Radio de visibilidad desde config
        double entitySearchRadius = FoliageConfig.get().visibilityRadius;

        // ── 1. Calcular nuevo estado ───────────────────────────────────────────
        Map<BlockPos, LeanVector> newLean = new HashMap<>();

        AABB box = new AABB(
                mc.player.getX() - entitySearchRadius,
                mc.player.getY() - entitySearchRadius,
                mc.player.getZ() - entitySearchRadius,
                mc.player.getX() + entitySearchRadius,
                mc.player.getY() + entitySearchRadius,
                mc.player.getZ() + entitySearchRadius
        );

        List<Entity> entities = new ArrayList<>(level.getEntitiesOfClass(
                Entity.class, box, e -> !e.isSpectator() && !e.isRemoved()
        ));
        if (!entities.contains(mc.player)) entities.add(mc.player);

        for (Entity entity : entities) {
            BlockPos ePos = entity.blockPosition();
            int range = (int) Math.ceil(MAX_INFLUENCE_RADIUS) + BLOCK_SEARCH_MARGIN;

            for (int dx = -range; dx <= range; dx++) {
                for (int dz = -range; dz <= range; dz++) {
                    for (int dy = -1; dy <= 2; dy++) {
                        BlockPos bp = ePos.offset(dx, dy, dz);
                        if (newLean.containsKey(bp)) continue;

                        BlockState state = level.getBlockState(bp);
                        if (!FoliageRegistry.isInteractiveFoliage(state)) continue;

                        double bcx = bp.getX() + 0.5;
                        double bcz = bp.getZ() + 0.5;

                        Entity inf = null;
                        double minDist = MAX_INFLUENCE_RADIUS;
                        for (Entity e : entities) {
                            double d = Math.sqrt(
                                    (e.getX()-bcx)*(e.getX()-bcx) +
                                            (e.getZ()-bcz)*(e.getZ()-bcz)
                            );
                            if (d < minDist) { minDist = d; inf = e; }
                        }
                        if (inf == null) continue;

                        double dirX = bcx - inf.getX();
                        double dirZ = bcz - inf.getZ();
                        double len  = Math.sqrt(dirX*dirX + dirZ*dirZ);
                        if (len < 0.001) { dirX = 1; dirZ = 0; len = 1; }
                        dirX /= len; dirZ /= len;

                        float intensity = (float)(1.0 - minDist / MAX_INFLUENCE_RADIUS)
                                * FoliageRegistry.getSwayMultiplier(state)
                                * FoliageConfig.get().intensity;

                        newLean.put(bp, new LeanVector(
                                (float) dirX, (float) dirZ, intensity
                        ));
                    }
                }
            }
        }

        // ── 2. Detectar cambios y forzar recompile ─────────────────────────────
        for (Map.Entry<BlockPos, LeanVector> entry : newLean.entrySet()) {
            BlockPos   pos  = entry.getKey();
            LeanVector next = entry.getValue();
            LeanVector prev = previousLean.get(pos);

            boolean changed = prev == null
                    || Math.abs(next.intensity() - prev.intensity()) > CHANGE_THRESHOLD
                    || Math.abs(next.dirX()      - prev.dirX())      > CHANGE_THRESHOLD
                    || Math.abs(next.dirZ()      - prev.dirZ())      > CHANGE_THRESHOLD;

            if (changed) {
                BlockState st = level.getBlockState(pos);
                mc.levelRenderer.blockChanged(level, pos, st, st, 0);
            }
        }

        // Bloques que salieron → recompilar a vanilla
        for (BlockPos pos : previousLean.keySet()) {
            if (!newLean.containsKey(pos)) {
                BlockState st = level.getBlockState(pos);
                mc.levelRenderer.blockChanged(level, pos, st, st, 0);
            }
        }

        // ── 3. Actualizar estado global ────────────────────────────────────────
        FoliageLeanState.ACTIVE.clear();
        FoliageLeanState.ACTIVE.putAll(newLean);

        previousLean.clear();
        previousLean.putAll(newLean);
    }

    public static void reset() {
        FoliageLeanState.clear();
        previousLean.clear();
    }
}