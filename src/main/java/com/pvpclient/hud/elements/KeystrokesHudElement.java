package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;
import org.lwjgl.glfw.GLFW;

/**
 * Visual keystrokes display showing WASD, Space, Shift, LMB, and RMB.
 * Keys light up when pressed for a satisfying visual feedback effect.
 */
public final class KeystrokesHudElement extends HudElement {

    private static final int KEY_SIZE = 14;
    private static final int KEY_GAP  = 2;
    private static final int KEY_ON   = 0xCC00E5CC;
    private static final int KEY_OFF  = 0x66FFFFFF;

    public KeystrokesHudElement() {
        super("keystrokes", 4, 200);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen != null) return; // Hide while in a GUI

        GameOptions opts = mc.options;
        long handle = mc.getWindow().getHandle();

        boolean w     = isKeyDown(handle, opts.forwardKey.getDefaultKey().getCode());
        boolean a     = isKeyDown(handle, opts.leftKey.getDefaultKey().getCode());
        boolean s     = isKeyDown(handle, opts.backKey.getDefaultKey().getCode());
        boolean d     = isKeyDown(handle, opts.rightKey.getDefaultKey().getCode());
        boolean space = isKeyDown(handle, GLFW.GLFW_KEY_SPACE);
        boolean shift = isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT);
        boolean lmb   = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmb   = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        int step = KEY_SIZE + KEY_GAP;

        // Row 1: [W]
        drawKey(ctx, x + step, y, "W", w);
        // Row 2: [A][S][D]
        drawKey(ctx, x, y + step, "A", a);
        drawKey(ctx, x + step, y + step, "S", s);
        drawKey(ctx, x + step * 2, y + step, "D", d);
        // Row 3: [SPACE]
        drawWideKey(ctx, x, y + step * 2, "SPC", space);
        // Row 4: [SHF][LMB][RMB]
        drawKey(ctx, x, y + step * 3, "SH", shift);
        drawKey(ctx, x + step, y + step * 3, "L", lmb);
        drawKey(ctx, x + step * 2, y + step * 3, "R", rmb);
    }

    private void drawKey(DrawContext ctx, int kx, int ky, String label, boolean pressed) {
        int bg = pressed ? KEY_ON : KEY_OFF;
        RendererUtils.drawRect(ctx, kx, ky, KEY_SIZE, KEY_SIZE, bg);
        RendererUtils.drawCenteredString(ctx, label, kx + KEY_SIZE / 2, ky + 3,
            pressed ? 0xFF000000 : 0xFFCCCCCC);
    }

    private void drawWideKey(DrawContext ctx, int kx, int ky, String label, boolean pressed) {
        int width = KEY_SIZE * 3 + KEY_GAP * 2;
        int bg = pressed ? KEY_ON : KEY_OFF;
        RendererUtils.drawRect(ctx, kx, ky, width, KEY_SIZE, bg);
        RendererUtils.drawCenteredString(ctx, label, kx + width / 2, ky + 3,
            pressed ? 0xFF000000 : 0xFFCCCCCC);
    }

    private boolean isKeyDown(long handle, int keyCode) {
        if (keyCode < 0) return false;
        return GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS;
    }

    @Override public int getWidth()  { return (KEY_SIZE + KEY_GAP) * 3 - KEY_GAP; }
    @Override public int getHeight() { return (KEY_SIZE + KEY_GAP) * 4 - KEY_GAP; }
}
