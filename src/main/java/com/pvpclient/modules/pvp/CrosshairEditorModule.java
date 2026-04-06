package com.pvpclient.modules.pvp;

import com.pvpclient.events.AttackEvent;
import com.pvpclient.events.EventBus;
import com.pvpclient.events.RenderEvent;
import com.pvpclient.modules.Module;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;

/**
 * Custom crosshair editor — replaces the vanilla crosshair with a fully customizable one.
 * Supports size, gap, color, and a dynamic mode that changes color on hit.
 */
public final class CrosshairEditorModule extends Module {

    /** Crosshair configuration — persisted to config. */
    private int size     = 5;
    private int gap      = 2;
    private int thickness = 1;
    private int normalColor = 0xFFFFFFFF;
    private int hitColor    = 0xFF00E5CC;
    private boolean dynamic = true;

    /** Timed hit flash state. */
    private long lastHitTime = 0;
    private static final long HIT_FLASH_MS = 200L;

    private final java.util.function.Consumer<RenderEvent>  renderListener;
    private final java.util.function.Consumer<AttackEvent>  attackListener;

    public CrosshairEditorModule() {
        super("Crosshair Editor", "Custom crosshair with color, size, and dynamic hit response.", Category.PVP);
        renderListener = this::onRender;
        attackListener = this::onAttack;
    }

    @Override
    protected void onEnable() {
        loadCustomConfig();
        eventBus.subscribe(RenderEvent.class, renderListener);
        eventBus.subscribe(AttackEvent.class, attackListener);
    }

    @Override
    protected void onDisable() {
        eventBus.unsubscribe(RenderEvent.class, renderListener);
        eventBus.unsubscribe(AttackEvent.class, attackListener);
    }

    private void onAttack(AttackEvent event) {
        if (dynamic && event.getTarget() instanceof LivingEntity) {
            lastHitTime = System.currentTimeMillis();
        }
    }

    private void onRender(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.currentScreen != null) return;

        DrawContext ctx = event.getContext();
        int cx = event.getScaledWidth() / 2;
        int cy = event.getScaledHeight() / 2;

        boolean isHitFlash = dynamic && (System.currentTimeMillis() - lastHitTime < HIT_FLASH_MS);
        int color = isHitFlash ? hitColor : normalColor;

        RendererUtils.drawCrosshair(ctx, cx, cy, size, thickness, gap, color);
    }

    private void loadCustomConfig() {
        size      = config.getInt(getName(), "size", 5);
        gap       = config.getInt(getName(), "gap", 2);
        thickness = config.getInt(getName(), "thickness", 1);
        normalColor = config.getInt(getName(), "normalColor", 0xFFFFFFFF);
        hitColor    = config.getInt(getName(), "hitColor",    0xFF00E5CC);
        dynamic     = config.getBoolean(getName(), "dynamic", true);
    }

    // Getters / setters for use by the GUI
    public int getSize()     { return size;      }
    public int getGap()      { return gap;       }
    public int getThickness(){ return thickness; }
    public int getNormalColor()  { return normalColor;  }
    public int getHitColor()     { return hitColor;     }
    public boolean isDynamic()   { return dynamic;      }

    public void setSize(int v)       { size = v;       config.setInt(getName(), "size", v);       config.save(); }
    public void setGap(int v)        { gap = v;        config.setInt(getName(), "gap", v);        config.save(); }
    public void setThickness(int v)  { thickness = v;  config.setInt(getName(), "thickness", v);  config.save(); }
    public void setNormalColor(int v){ normalColor = v; config.setInt(getName(), "normalColor", v); config.save(); }
    public void setHitColor(int v)   { hitColor = v;   config.setInt(getName(), "hitColor", v);   config.save(); }
    public void setDynamic(boolean v){ dynamic = v;    config.setBoolean(getName(), "dynamic", v); config.save(); }
}
