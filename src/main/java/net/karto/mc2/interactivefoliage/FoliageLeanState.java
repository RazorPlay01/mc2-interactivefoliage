package net.karto.mc2.interactivefoliage;

import net.minecraft.core.BlockPos;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Estado de física compartido entre el hilo de render y el chunk compiler.
 * Contiene SOLO los bloques actualmente influenciados por una entidad.
 * Bloques sin entrada = sin deformación = geometría vanilla pura.
 */
public class FoliageLeanState {

    public record LeanVector(float dirX, float dirZ, float intensity) {}

    /** Solo bloques activamente influenciados este frame. */
    public static final Map<BlockPos, LeanVector> ACTIVE =
            new ConcurrentHashMap<>();

    public static void clear() {
        ACTIVE.clear();
    }
}