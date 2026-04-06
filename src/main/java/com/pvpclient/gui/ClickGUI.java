package com.pvpclient.gui;

import com.pvpclient.gui.components.CategoryPanel;
import com.pvpclient.modules.Module;
import com.pvpclient.modules.ModuleManager;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;

/**
 * Main Click GUI screen — opened with Right Shift.
 * Features a category sidebar, module list, smooth scrolling, and a search bar.
 * Rendering is done via standard DrawContext calls compatible with all Minecraft rendering modes.
 */
public final class ClickGUI extends Screen {

    // -----------------------------------------------------------------------
    // Layout constants
    // -----------------------------------------------------------------------
    private static final int SIDEBAR_WIDTH  = 90;
    private static final int PANEL_X        = SIDEBAR_WIDTH + 10;
    private static final int PANEL_Y        = 10;
    private static final int PANEL_WIDTH    = 200;
    private static final int PANEL_HEADER   = 20;
    private static final int MODULE_HEIGHT  = 20;
    private static final int MODULE_PADDING = 3;

    // -----------------------------------------------------------------------
    // Color palette
    // -----------------------------------------------------------------------
    private static final int BG_DARK        = 0xE0101010;
    private static final int SIDEBAR_BG     = 0xE01A1A2E;
    private static final int SIDEBAR_HOVER  = 0x4400E5CC;
    private static final int SIDEBAR_ACTIVE = 0x8800E5CC;
    private static final int PANEL_BG       = 0xE0161625;
    private static final int HEADER_BG      = 0xFF00E5CC;
    private static final int MODULE_ON      = 0x8800E5CC;
    private static final int MODULE_OFF     = 0x44FFFFFF;
    private static final int TEXT_MAIN      = 0xFFFFFFFF;
    private static final int TEXT_DIM       = 0xFFAAAAAA;
    private static final int TEXT_ACCENT    = 0xFF00E5CC;

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------
    private Module.Category selectedCategory = Module.Category.HUD;
    private float scrollOffset   = 0f;
    private float scrollTarget   = 0f;
    private String searchQuery   = "";

    /** Categories in display order. */
    private static final Module.Category[] CATEGORIES = Module.Category.values();

    public ClickGUI() {
        super(Text.literal("PvP Client"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int sw = width;
        int sh = height;

        // Smooth scroll easing
        scrollOffset += (scrollTarget - scrollOffset) * 0.2f;

        // --- Full-screen dark overlay ---
        ctx.fill(0, 0, sw, sh, BG_DARK);

        // --- Sidebar ---
        renderSidebar(ctx, mouseX, mouseY, sh);

        // --- Module panel ---
        renderModulePanel(ctx, mouseX, mouseY, sh);

        // --- Search bar ---
        renderSearchBar(ctx, mouseX, mouseY, sh);
    }

    // -----------------------------------------------------------------------
    // Sidebar
    // -----------------------------------------------------------------------

    private void renderSidebar(DrawContext ctx, int mouseX, int mouseY, int sh) {
        ctx.fill(0, 0, SIDEBAR_WIDTH, sh, SIDEBAR_BG);

        // Title
        RendererUtils.drawCenteredString(ctx, "PvP Client", SIDEBAR_WIDTH / 2, 10, TEXT_ACCENT);

        int sy = 30;
        for (Module.Category cat : CATEGORIES) {
            boolean hover  = mouseX >= 5 && mouseX <= SIDEBAR_WIDTH - 5
                          && mouseY >= sy && mouseY <= sy + 20;
            boolean active = cat == selectedCategory;

            int bg = active ? SIDEBAR_ACTIVE : (hover ? SIDEBAR_HOVER : 0);
            if (bg != 0) ctx.fill(5, sy, SIDEBAR_WIDTH - 5, sy + 20, bg);

            int textColor = active ? TEXT_ACCENT : (hover ? TEXT_MAIN : TEXT_DIM);
            RendererUtils.drawCenteredString(ctx, cat.name(), SIDEBAR_WIDTH / 2, sy + 6, textColor);

            // Active indicator bar
            if (active) ctx.fill(0, sy, 3, sy + 20, RendererUtils.ACCENT_COLOR);

            sy += 22;
        }
    }

    // -----------------------------------------------------------------------
    // Module panel
    // -----------------------------------------------------------------------

    private void renderModulePanel(DrawContext ctx, int mouseX, int mouseY, int sh) {
        List<Module> modules = getFilteredModules();

        int panelHeight = PANEL_HEADER + modules.size() * (MODULE_HEIGHT + MODULE_PADDING) + 8;

        // Clamp scroll
        int maxScroll = Math.max(0, panelHeight - (sh - PANEL_Y - 10));
        scrollTarget = com.pvpclient.utils.MathUtils.clamp(scrollTarget, 0, maxScroll);

        // Panel background
        ctx.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_WIDTH, sh - 10, PANEL_BG);

        // Header bar
        ctx.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_WIDTH, PANEL_Y + PANEL_HEADER, HEADER_BG);
        RendererUtils.drawString(ctx, selectedCategory.name(), PANEL_X + 6, PANEL_Y + 6, 0xFF000000);

        // Scissor (clip) to panel content area
        int contentTop = PANEL_Y + PANEL_HEADER;
        int contentBot = sh - 10;

        ctx.enableScissor(PANEL_X, contentTop, PANEL_X + PANEL_WIDTH, contentBot);

        int my = contentTop + 4 - (int) scrollOffset;
        for (Module module : modules) {
            renderModuleRow(ctx, module, PANEL_X + 4, my, PANEL_WIDTH - 8, MODULE_HEIGHT, mouseX, mouseY);
            my += MODULE_HEIGHT + MODULE_PADDING;
        }

        ctx.disableScissor();
    }

    private void renderModuleRow(DrawContext ctx, Module module,
                                  int rx, int ry, int rw, int rh,
                                  int mouseX, int mouseY) {
        boolean hover = mouseX >= rx && mouseX <= rx + rw
                     && mouseY >= ry && mouseY <= ry + rh;
        boolean on    = module.isEnabled();

        int bg = on ? MODULE_ON : (hover ? 0x22FFFFFF : 0x11FFFFFF);
        ctx.fill(rx, ry, rx + rw, ry + rh, bg);

        // Toggle indicator dot
        int dotColor = on ? RendererUtils.ACCENT_COLOR : 0x44FFFFFF;
        ctx.fill(rx + rw - 12, ry + rh / 2 - 3, rx + rw - 6, ry + rh / 2 + 3, dotColor);

        // Module name
        RendererUtils.drawString(ctx, module.getName(), rx + 5, ry + (rh - 9) / 2, on ? TEXT_MAIN : TEXT_DIM);

        // Left accent bar if enabled
        if (on) ctx.fill(rx, ry, rx + 2, ry + rh, RendererUtils.ACCENT_COLOR);
    }

    // -----------------------------------------------------------------------
    // Search bar
    // -----------------------------------------------------------------------

    private void renderSearchBar(DrawContext ctx, int mouseX, int mouseY, int sh) {
        int sbX = PANEL_X;
        int sbY = sh - 8 - 14;
        int sbW = PANEL_WIDTH;

        ctx.fill(sbX, sbY, sbX + sbW, sbY + 14, 0xFF1A1A2E);
        ctx.fill(sbX, sbY + 13, sbX + sbW, sbY + 14, RendererUtils.ACCENT_COLOR);

        String displayText = searchQuery.isEmpty() ? "Search..." : searchQuery;
        int textColor = searchQuery.isEmpty() ? TEXT_DIM : TEXT_MAIN;
        RendererUtils.drawString(ctx, displayText, sbX + 4, sbY + 3, textColor);
    }

    // -----------------------------------------------------------------------
    // Input handling
    // -----------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Sidebar category selection
        int sy = 30;
        for (Module.Category cat : CATEGORIES) {
            if (mouseX >= 5 && mouseX <= SIDEBAR_WIDTH - 5
             && mouseY >= sy && mouseY <= sy + 20) {
                selectedCategory = cat;
                scrollTarget = 0;
                return true;
            }
            sy += 22;
        }

        // Module toggle click
        List<Module> modules = getFilteredModules();
        int contentTop = PANEL_Y + PANEL_HEADER;
        int my = contentTop + 4 - (int) scrollOffset;

        for (Module module : modules) {
            if (mouseX >= PANEL_X + 4 && mouseX <= PANEL_X + PANEL_WIDTH - 4
             && mouseY >= my && mouseY <= my + MODULE_HEIGHT) {
                module.toggle();
                return true;
            }
            my += MODULE_HEIGHT + MODULE_PADDING;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_WIDTH) {
            scrollTarget -= (float) verticalAmount * 12f;
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
            searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            scrollTarget = 0;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (Character.isLetterOrDigit(c) || c == ' ') {
            searchQuery += c;
            scrollTarget = 0;
        }
        return true;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private List<Module> getFilteredModules() {
        List<Module> list = ModuleManager.getInstance().getModulesByCategory(selectedCategory);
        if (searchQuery.isEmpty()) return list;
        String q = searchQuery.toLowerCase(Locale.ROOT);
        List<Module> filtered = new ArrayList<>();
        for (Module m : list) {
            if (m.getName().toLowerCase(Locale.ROOT).contains(q)) filtered.add(m);
        }
        return filtered;
    }

    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game when the GUI is open
    }
}
