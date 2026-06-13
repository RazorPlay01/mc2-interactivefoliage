package net.karto.mc2.interactivefoliage;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InteractiveFoliageMod implements ModInitializer {

    public static final String MOD_ID = "mc2_interactivefoliage";
    public static final Logger LOGGER  = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        FoliageConfig.load();
        LOGGER.info("MC2 - Interactive Foliage initialized!");
    }
}