package net.karto.mc2.interactivefoliage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.karto.mc2.interactivefoliage.FoliageConfig;
import net.karto.mc2.interactivefoliage.FoliageLeanState;
import net.karto.mc2.interactivefoliage.InteractiveFoliageMod;

public class InteractiveFoliageClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Cargar configuración al inicio
        FoliageConfig.load();

        ModelLoadingPlugin.register(new FoliageModelPlugin());

        FoliageKeyBindings.register();

        ClientTickEvents.END_CLIENT_TICK.register(mc ->
                FoliageKeyBindings.tick(mc)
        );

        ClientLifecycleEvents.CLIENT_STOPPING.register(client ->
                FoliageLeanState.clear()
        );

        InteractiveFoliageMod.LOGGER.info("MC2 - Interactive Foliage client ready!");
    }
}