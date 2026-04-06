package com.pvpclient.hud;

import com.pvpclient.config.ConfigManager;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.gui.DrawContext;

/**
 * Base class for all draggable, resizable HUD elements.
 * Each element stores its own position and scale and persists them via ConfigManager.
 */
public abstract class HudElement {

    protected final String id;
    protected int x;
    protected int y;
    protected float scale;
    protected boolean visible;

    /** Dragging state — transient, not persisted. */
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    protected final ConfigManager config = ConfigManager.getInstance();

    protected HudElement(String id, int defaultX, int defaultY) {
        this.id = id;
        this.scale = 1.0f;
        this.visible = true;
        loadPosition();
        // Use saved position; fall back to default on first run
        if (x == 0 && y == 0) {
            this.x = defaultX;
            this.y = defaultY;
        }
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    /**
     * Called every frame to render this HUD element.
     *
     * @param ctx   The draw context.
     * @param delta Partial tick delta.
     */
    public abstract void render(DrawContext ctx, float delta);

    /**
     * Returns the pixel width of this element (used for snapping and click detection).
     */
    public abstract int getWidth();

    /**
     * Returns the pixel height of this element.
     */
    public abstract int getHeight();

    // -----------------------------------------------------------------------
    // Drag handling (called from HudManager during HUD editor mode)
    // -----------------------------------------------------------------------

    public void onMousePress(double mouseX, double mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            dragging = true;
            dragOffsetX = (int) (mouseX - x);
            dragOffsetY = (int) (mouseY - y);
        }
    }

    public void onMouseRelease() {
        if (dragging) {
            dragging = false;
            snapToEdges(HudManager.getInstance().getScreenWidth(),
                        HudManager.getInstance().getScreenHeight());
            savePosition();
        }
    }

    public void onMouseDrag(double mouseX, double mouseY) {
        if (!dragging) return;
        x = (int) (mouseX - dragOffsetX);
        y = (int) (mouseY - dragOffsetY);
    }

    public boolean isDragging() {
        return dragging;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + getWidth()
            && mouseY >= y && mouseY <= y + getHeight();
    }

    // -----------------------------------------------------------------------
    // Screen-edge snapping
    // -----------------------------------------------------------------------

    private static final int SNAP_THRESHOLD = 8;

    private void snapToEdges(int screenW, int screenH) {
        if (x < SNAP_THRESHOLD) x = 0;
        if (y < SNAP_THRESHOLD) y = 0;
        if (x + getWidth() > screenW - SNAP_THRESHOLD) x = screenW - getWidth();
        if (y + getHeight() > screenH - SNAP_THRESHOLD) y = screenH - getHeight();
    }

    // -----------------------------------------------------------------------
    // Config persistence
    // -----------------------------------------------------------------------

    public void savePosition() {
        config.setInt(id, "x", x);
        config.setInt(id, "y", y);
        config.setFloat(id, "scale", scale);
        config.setBoolean(id, "visible", visible);
        config.save();
    }

    public void loadPosition() {
        x = config.getInt(id, "x", 0);
        y = config.getInt(id, "y", 0);
        scale = config.getFloat(id, "scale", 1.0f);
        visible = config.getBoolean(id, "visible", true);
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = Math.max(0.5f, Math.min(3.0f, scale)); }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
