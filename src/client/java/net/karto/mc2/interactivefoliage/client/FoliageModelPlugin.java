package net.karto.mc2.interactivefoliage.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.karto.mc2.interactivefoliage.FoliageRegistry;

public class FoliageModelPlugin implements ModelLoadingPlugin {

    @Override
    public void initialize(Context ctx) {
        ctx.modifyBlockModelAfterBake().register((model, context) -> {
            if (!FoliageRegistry.isInteractiveFoliage(context.state())) return model;
            return new FoliageDeformedModel(model);
        });
    }
}