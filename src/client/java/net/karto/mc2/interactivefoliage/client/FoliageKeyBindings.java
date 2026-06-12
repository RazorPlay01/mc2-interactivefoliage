package net.karto.mc2.interactivefoliage.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.karto.mc2.interactivefoliage.FoliageConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FoliageKeyBindings {

    public static KeyMapping openConfig;
    public static KeyMapping toggleMod;

    public static void register() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("mc2_interactivefoliage", "general")
        );

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.mc2_interactivefoliage.open_config",
                InputConstants.UNKNOWN.getValue(),
                category
        ));

        toggleMod = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.mc2_interactivefoliage.toggle",
                InputConstants.UNKNOWN.getValue(),
                category
        ));
    }

    public static void tick(Minecraft mc) {
        while (openConfig.consumeClick()) {
            if (mc.screen == null) {
                mc.setScreen(new FoliageConfigScreen(null));
            }
        }

        while (toggleMod.consumeClick()) {
            FoliageConfig.get().enabled = !FoliageConfig.get().enabled;
            FoliageConfig.save();
            if (mc.player != null) {
                mc.player.displayClientMessage(
                        Component.translatable(
                                FoliageConfig.get().enabled
                                        ? "key.mc2_interactivefoliage.toggle.on"
                                        : "key.mc2_interactivefoliage.toggle.off"
                        ),
                        true
                );
            }
        }
    }
}