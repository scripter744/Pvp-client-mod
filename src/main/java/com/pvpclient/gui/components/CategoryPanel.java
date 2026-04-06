package com.pvpclient.gui.components;

import com.pvpclient.modules.Module;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

/**
 * A self-contained panel widget that renders a list of modules for a given category.
 * Used by the ClickGUI for its main content area.
 */
public final class CategoryPanel {

    private final Module.Category category;
    private final List<Module> modules;

    private static final int MODULE_H   = 20;
    private static final int MODULE_GAP = 3;

    public CategoryPanel(Module.Category category, List<Module> modules) {
        this.category = category;
        this.modules  = modules;
    }

    public void render(DrawContext ctx, int x, int y, int width, float scrollOffset) {
        int my = y - (int) scrollOffset;
        for (Module module : modules) {
            renderRow(ctx, module, x, my, width);
            my += MODULE_H + MODULE_GAP;
        }
    }

    private void renderRow(DrawContext ctx, Module module, int rx, int ry, int rw) {
        boolean on = module.isEnabled();
        int bg = on ? 0x8800E5CC : 0x11FFFFFF;
        ctx.fill(rx, ry, rx + rw, ry + MODULE_H, bg);

        RendererUtils.drawString(ctx, module.getName(), rx + 5, ry + 6,
            on ? 0xFFFFFFFF : 0xFF888888);

        if (on) ctx.fill(rx, ry, rx + 2, ry + MODULE_H, RendererUtils.ACCENT_COLOR);
    }

    public int getTotalHeight() {
        return modules.size() * (MODULE_H + MODULE_GAP);
    }

    public Module.Category getCategory() {
        return category;
    }
}
