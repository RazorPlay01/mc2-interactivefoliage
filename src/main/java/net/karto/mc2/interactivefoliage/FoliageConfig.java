package net.karto.mc2.interactivefoliage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FoliageConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // PATH lazy — se resuelve solo cuando se necesita, no al cargar la clase
    private static Path configPath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve("mc2_interactivefoliage.json");
    }

    public boolean enabled          = true;
    public float   intensity        = 1.0f;
    public float   visibilityRadius = 6.0f;

    private static FoliageConfig INSTANCE = new FoliageConfig();

    public static FoliageConfig get() {
        return INSTANCE;
    }

    public static void load() {
        Path path = configPath();
        if (!Files.exists(path)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            FoliageConfig loaded = GSON.fromJson(reader, FoliageConfig.class);
            if (loaded != null) {
                INSTANCE = loaded;
                clamp();
            }
        } catch (IOException e) {
            InteractiveFoliageMod.LOGGER.error("Failed to load config", e);
            INSTANCE = new FoliageConfig();
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(configPath())) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            InteractiveFoliageMod.LOGGER.error("Failed to save config", e);
        }
    }

    private static void clamp() {
        INSTANCE.intensity        = Math.clamp(INSTANCE.intensity, 0.1f, 2.0f);
        INSTANCE.visibilityRadius = Math.clamp(INSTANCE.visibilityRadius, 6.0f, 32.0f);
    }
}