package net.karto.mc2.interactivefoliage.client;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.karto.mc2.interactivefoliage.FoliageLeanState;
import net.karto.mc2.interactivefoliage.FoliageLeanState.LeanVector;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FoliageDeformedModel implements BlockStateModel {

    private static final float BASE_THRESHOLD   = 0.1f;
    private static final float MAX_DISPLACEMENT = 0.3f;

    private final BlockStateModel original;

    public FoliageDeformedModel(BlockStateModel original) {
        this.original = original;
    }

    @Override
    public void emitQuads(
            @Nullable QuadEmitter emitter,
            @Nullable BlockAndTintGetter blockView,
            @Nullable BlockPos blockPos,
            @Nullable BlockState state,
            @Nullable RandomSource random,
            @Nullable Predicate<@Nullable Direction> cullTest
    ) {
        if (emitter == null || blockPos == null || state == null
                || random == null || cullTest == null) {
            return;
        }

        LeanVector lean = FoliageLeanState.ACTIVE.get(blockPos);

        if (lean == null || lean.intensity() < 0.01f) {
            original.emitQuads(emitter, blockView, blockPos, state, random, cullTest);
            return;
        }

        float dispX = lean.dirX() * lean.intensity() * MAX_DISPLACEMENT;
        float dispZ = lean.dirZ() * lean.intensity() * MAX_DISPLACEMENT;

        emitter.pushTransform((MutableQuadView quad) -> {
            for (int i = 0; i < 4; i++) {
                float y = quad.y(i);
                if (y > BASE_THRESHOLD) {
                    float factor = (y - BASE_THRESHOLD) / (1.0f - BASE_THRESHOLD);
                    quad.pos(i,
                            quad.x(i) + dispX * factor,
                            y,
                            quad.z(i) + dispZ * factor
                    );
                }
            }
            return true;
        });

        original.emitQuads(emitter, blockView, blockPos, state, random, cullTest);
        emitter.popTransform();
    }

    @Override
    public void collectParts(
            @Nullable RandomSource random,
            @Nullable List<BlockModelPart> parts
    ) {
        original.collectParts(random, parts);
    }

    @Override
    public @Nullable TextureAtlasSprite particleIcon() {
        return original.particleIcon();
    }

    @Override
    public @Nullable Object createGeometryKey(
            @Nullable BlockAndTintGetter blockView,
            @Nullable BlockPos blockPos,
            @Nullable BlockState state,
            @Nullable RandomSource random
    ) {
        return null;
    }
}