package com.pvpclient.render;

import com.pvpclient.utils.MathUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Centralized rendering helpers for HUD elements and GUI components.
 * All drawing goes through DrawContext to stay compatible with Minecraft's render pipeline.
 */
public final class RendererUtils {

    /** Default dark background color (semi-transparent black). */
    public static final int BG_COLOR = 0xAA000000;
    /** Accent color — cyan-teal used for bars and highlights. */
    public static final int ACCENT_COLOR = 0xFF00E5CC;
    /** Standard white text color. */
    public static final int TEXT_COLOR = 0xFFFFFFFF;
    /** Dimmed gray for secondary text. */
    public static final int TEXT_SECONDARY = 0xFFAAAAAA;
    /** Red used for health / danger indicators. */
    public static final int HEALTH_COLOR = 0xFFFF4444;
    /** Yellow used for hunger indicators. */
    public static final int HUNGER_COLOR = 0xFFFFAA00;
    /** Green for full bars. */
    public static final int BAR_FULL = 0xFF44DD44;
    /** Orange for mid-level bars. */
    public static final int BAR_MID = 0xFFFFAA00;
    /** Red for low-level bars. */
    public static final int BAR_LOW = 0xFFFF4444;

    private RendererUtils() {}

    // -----------------------------------------------------------------------
    // Rectangles
    // -----------------------------------------------------------------------

    /**
     * Draw a filled rectangle at the given screen coordinates.
     */
    public static void drawRect(DrawContext ctx, int x, int y, int width, int height, int color) {
        ctx.fill(x, y, x + width, y + height, color);
    }

    /**
     * Draw a rectangle with a thin 1-pixel border and separate fill/border colors.
     */
    public static void drawRoundedPanel(DrawContext ctx, int x, int y, int width, int height,
                                        int fillColor, int borderColor) {
        // Border (drawn as background slightly larger)
        ctx.fill(x - 1, y - 1, x + width + 1, y + height + 1, borderColor);
        // Fill
        ctx.fill(x, y, x + width, y + height, fillColor);
    }

    // -----------------------------------------------------------------------
    // Progress bars
    // -----------------------------------------------------------------------

    /**
     * Draw a horizontal progress bar that smoothly transitions color based on value.
     *
     * @param ctx    Draw context.
     * @param x      Left edge.
     * @param y      Top edge.
     * @param width  Total bar width in pixels.
     * @param height Bar height in pixels.
     * @param value  Current value.
     * @param max    Maximum value.
     */
    public static void drawProgressBar(DrawContext ctx, int x, int y, int width, int height,
                                       float value, float max) {
        float fraction = MathUtils.clamp(value / max, 0f, 1f);
        int filledWidth = (int) (width * fraction);

        // Background track
        drawRect(ctx, x, y, width, height, 0x55FFFFFF);

        // Pick a color based on fill level
        int barColor;
        if (fraction > 0.5f) {
            barColor = MathUtils.lerpColor(BAR_MID, BAR_FULL, (fraction - 0.5f) * 2f);
        } else {
            barColor = MathUtils.lerpColor(BAR_LOW, BAR_MID, fraction * 2f);
        }

        if (filledWidth > 0) {
            drawRect(ctx, x, y, filledWidth, height, barColor);
        }
    }

    /**
     * Draw a colored progress bar with an explicit color.
     */
    public static void drawProgressBar(DrawContext ctx, int x, int y, int width, int height,
                                       float value, float max, int color) {
        float fraction = MathUtils.clamp(value / max, 0f, 1f);
        int filledWidth = (int) (width * fraction);
        drawRect(ctx, x, y, width, height, 0x55FFFFFF);
        if (filledWidth > 0) {
            drawRect(ctx, x, y, filledWidth, height, color);
        }
    }

    // -----------------------------------------------------------------------
    // Text
    // -----------------------------------------------------------------------

    /**
     * Draw a string with a drop-shadow for legibility on any background.
     */
    public static void drawString(DrawContext ctx, String text, int x, int y, int color) {
        ctx.drawTextWithShadow(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            Text.literal(text),
            x, y, color
        );
    }

    /**
     * Draw centered text at the given x center.
     */
    public static void drawCenteredString(DrawContext ctx, String text, int centerX, int y, int color) {
        int textWidth = net.minecraft.client.MinecraftClient.getInstance().textRenderer.getWidth(text);
        drawString(ctx, text, centerX - textWidth / 2, y, color);
    }

    // -----------------------------------------------------------------------
    // Crosshair
    // -----------------------------------------------------------------------

    /**
     * Draw a custom crosshair at the center of the screen.
     *
     * @param ctx       Draw context.
     * @param cx        Screen center X.
     * @param cy        Screen center Y.
     * @param size      Half-length of each arm in pixels.
     * @param thickness Arm thickness in pixels.
     * @param gap       Gap between center and arm start.
     * @param color     ARGB color.
     */
    public static void drawCrosshair(DrawContext ctx, int cx, int cy, int size, int thickness,
                                     int gap, int color) {
        int half = thickness / 2;
        // Horizontal bar
        drawRect(ctx, cx + gap, cy - half, size, thickness, color);
        drawRect(ctx, cx - gap - size, cy - half, size, thickness, color);
        // Vertical bar
        drawRect(ctx, cx - half, cy + gap, thickness, size, color);
        drawRect(ctx, cx - half, cy - gap - size, thickness, size, color);
    }
}
