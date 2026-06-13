package net.karto.mc2.interactivefoliage.client;

import net.karto.mc2.interactivefoliage.FoliageConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class FoliageConfigScreen extends Screen {

    private final Screen parent;
    private final FoliageConfig config = FoliageConfig.get();

    private static final float DEFAULT_INTENSITY = 1.0f;
    private static final float DEFAULT_RADIUS    = 6.0f;

    private Button resetIntensityBtn;
    private Button resetRadiusBtn;

    public FoliageConfigScreen(Screen parent) {
        super(Component.translatable("config.mc2_interactivefoliage.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y  = this.height / 4;

        // ── Toggle ON/OFF ──────────────────────────────────────────────────────
        this.addRenderableWidget(
                CycleButton.booleanBuilder(
                        Component.translatable("config.mc2_interactivefoliage.on"),
                        Component.translatable("config.mc2_interactivefoliage.off"),
                        config.enabled
                ).create(cx - 100, y, 200, 20,
                        Component.translatable("config.mc2_interactivefoliage.enabled"),
                        (btn, val) -> config.enabled = val
                )
        );

        y += 30;

        // ── Intensidad ─────────────────────────────────────────────────────────
        final int intensityY = y;
        this.addRenderableWidget(new AbstractSliderButton(
                cx - 100, y, 178, 20,
                Component.empty(),
                (config.intensity - 0.1f) / (2.0f - 0.1f)
        ) {
            { updateMessage(); }

            @Override
            protected void updateMessage() {
                setMessage(Component.translatable(
                        "config.mc2_interactivefoliage.intensity",
                        String.format("%.1f", config.intensity)
                ));
                if (resetIntensityBtn != null) {
                    resetIntensityBtn.visible =
                            Math.abs(config.intensity - DEFAULT_INTENSITY) > 0.01f;
                }
            }

            @Override
            protected void applyValue() {
                config.intensity = 0.1f + (float) value * (2.0f - 0.1f);
            }
        });

        resetIntensityBtn = Button.builder(
                Component.translatable("config.mc2_interactivefoliage.reset"),
                btn -> {
                    // FIX: asignar el valor primero, luego recrear la pantalla
                    config.intensity = DEFAULT_INTENSITY;
                    this.minecraft.setScreen(new FoliageConfigScreen(parent));
                }
        ).bounds(cx + 82, intensityY, 18, 20).build();
        resetIntensityBtn.visible =
                Math.abs(config.intensity - DEFAULT_INTENSITY) > 0.01f;
        this.addRenderableWidget(resetIntensityBtn);

        y += 30;

        // ── Radio de visibilidad ───────────────────────────────────────────────
        final int radiusY = y;
        this.addRenderableWidget(new AbstractSliderButton(
                cx - 100, y, 178, 20,
                Component.empty(),
                (config.visibilityRadius - 6.0f) / (32.0f - 6.0f)
        ) {
            { updateMessage(); }

            @Override
            protected void updateMessage() {
                setMessage(Component.translatable(
                        "config.mc2_interactivefoliage.radius",
                        String.format("%.0f", config.visibilityRadius)
                ));
                if (resetRadiusBtn != null) {
                    resetRadiusBtn.visible =
                            Math.abs(config.visibilityRadius - DEFAULT_RADIUS) > 0.1f;
                }
            }

            @Override
            protected void applyValue() {
                config.visibilityRadius = 6.0f + (float) value * (32.0f - 6.0f);
            }
        });

        resetRadiusBtn = Button.builder(
                Component.translatable("config.mc2_interactivefoliage.reset"),
                btn -> {
                    // FIX: asignar el valor primero, luego recrear la pantalla
                    config.visibilityRadius = DEFAULT_RADIUS;
                    this.minecraft.setScreen(new FoliageConfigScreen(parent));
                }
        ).bounds(cx + 82, radiusY, 18, 20).build();
        resetRadiusBtn.visible =
                Math.abs(config.visibilityRadius - DEFAULT_RADIUS) > 0.1f;
        this.addRenderableWidget(resetRadiusBtn);

        y += 40;

        // ── Guardar ────────────────────────────────────────────────────────────
        this.addRenderableWidget(Button.builder(
                Component.translatable("config.mc2_interactivefoliage.save"),
                btn -> {
                    FoliageConfig.save();
                    this.minecraft.setScreen(parent);
                }
        ).bounds(cx - 100, y, 200, 20).build());
    }

    @Override
    public void onClose() {
        FoliageConfig.save();
        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(
            @NotNull GuiGraphics graphics,
            int mouseX, int mouseY, float delta
    ) {
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(
                this.font, this.title,
                this.width / 2, this.height / 4 - 20,
                0xFFFFFF
        );
    }
}