package com.pvpclient.hud;

import com.pvpclient.hud.elements.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages all HUD elements: registration, rendering, and drag interaction.
 */
public final class HudManager {

    private static final HudManager INSTANCE = new HudManager();

    private final List<HudElement> elements = new ArrayList<>();
    private boolean editorMode = false;

    private int screenWidth;
    private int screenHeight;

    private HudManager() {}

    public static HudManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        MinecraftClient mc = MinecraftClient.getInstance();
        screenWidth = mc.getWindow().getScaledWidth();
        screenHeight = mc.getWindow().getScaledHeight();

        register(new FpsHudElement());
        register(new PingHudElement());
        register(new CoordinatesHudElement());
        register(new DirectionHudElement());
        register(new CpsHudElement());
        register(new KeystrokesHudElement());
        register(new ArmorHudElement());
        register(new HealthHudElement());
        register(new HungerHudElement());
        register(new ShieldHudElement());
        register(new ComboHudElement());
        register(new ReachHudElement());
        register(new TargetHudElement());
    }

    private void register(HudElement element) {
        elements.add(element);
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    public void render(DrawContext ctx, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        screenWidth = mc.getWindow().getScaledWidth();
        screenHeight = mc.getWindow().getScaledHeight();

        for (HudElement element : elements) {
            if (!element.isVisible()) continue;
            element.render(ctx, delta);

            // In editor mode, draw a highlight border around each element
            if (editorMode) {
                int color = element.isDragging() ? 0xFF00E5CC : 0x66FFFFFF;
                // Top border
                ctx.fill(element.getX(), element.getY(),
                         element.getX() + element.getWidth(), element.getY() + 1, color);
                // Bottom border
                ctx.fill(element.getX(), element.getY() + element.getHeight() - 1,
                         element.getX() + element.getWidth(), element.getY() + element.getHeight(), color);
                // Left border
                ctx.fill(element.getX(), element.getY(),
                         element.getX() + 1, element.getY() + element.getHeight(), color);
                // Right border
                ctx.fill(element.getX() + element.getWidth() - 1, element.getY(),
                         element.getX() + element.getWidth(), element.getY() + element.getHeight(), color);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Editor drag events
    // -----------------------------------------------------------------------

    public void onMousePress(double x, double y, int button) {
        if (!editorMode || button != 0) return;
        for (HudElement element : elements) {
            element.onMousePress(x, y);
        }
    }

    public void onMouseRelease(int button) {
        if (!editorMode || button != 0) return;
        for (HudElement element : elements) {
            element.onMouseRelease();
        }
    }

    public void onMouseDrag(double x, double y) {
        if (!editorMode) return;
        for (HudElement element : elements) {
            element.onMouseDrag(x, y);
        }
    }

    // -----------------------------------------------------------------------
    // Editor mode toggle
    // -----------------------------------------------------------------------

    public boolean isEditorMode() {
        return editorMode;
    }

    public void setEditorMode(boolean editorMode) {
        this.editorMode = editorMode;
    }

    public void toggleEditorMode() {
        this.editorMode = !this.editorMode;
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public List<HudElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
}
