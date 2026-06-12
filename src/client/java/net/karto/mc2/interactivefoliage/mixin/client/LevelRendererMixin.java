package net.karto.mc2.interactivefoliage.mixin.client;

import net.karto.mc2.interactivefoliage.client.FoliagePhysicsHandler;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void mc2fi$onRenderTail(CallbackInfo ci) {
        FoliagePhysicsHandler.tick();
    }
}