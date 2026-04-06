package com.pvpclient.modules;

import com.pvpclient.config.ConfigManager;
import com.pvpclient.events.EventBus;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Base class for all PvP Client modules.
 * Each module has a name, category, enable/disable state, and optional keybind.
 * Subclasses subscribe to events in {@link #onEnable()} and unsubscribe in {@link #onDisable()}.
 */
public abstract class Module {

    /** Module display categories used in the Click GUI. */
    public enum Category {
        HUD, PVP, RENDER, PERFORMANCE, CONFIG
    }

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int keybind;

    protected final EventBus eventBus = EventBus.getInstance();
    protected final ConfigManager config = ConfigManager.getInstance();

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.keybind = GLFW.GLFW_KEY_UNKNOWN;
    }

    // -----------------------------------------------------------------------
    // Enable / disable lifecycle
    // -----------------------------------------------------------------------

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
        config.setBoolean(name, "enabled", enabled);
        config.save();
    }

    public final void toggle() {
        setEnabled(!enabled);
    }

    /** Called when the module is enabled. Subscribe to events here. */
    protected void onEnable() {}

    /** Called when the module is disabled. Unsubscribe from events here. */
    protected void onDisable() {}

    // -----------------------------------------------------------------------
    // Persistence helpers
    // -----------------------------------------------------------------------

    /** Load this module's state from config. */
    public void loadConfig() {
        this.enabled = config.getBoolean(name, "enabled", false);
        this.keybind = config.getInt(name, "keybind", GLFW.GLFW_KEY_UNKNOWN);
        if (this.enabled) onEnable();
    }

    /** Save this module's state to config. */
    public void saveConfig() {
        config.setBoolean(name, "enabled", enabled);
        config.setInt(name, "keybind", keybind);
    }

    // -----------------------------------------------------------------------
    // Keybind
    // -----------------------------------------------------------------------

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int glfwKey) {
        this.keybind = glfwKey;
        config.setInt(name, "keybind", glfwKey);
    }

    public boolean isKeybindPressed() {
        if (keybind == GLFW.GLFW_KEY_UNKNOWN) return false;
        return InputUtil.isKeyPressed(
            net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle(),
            keybind
        );
    }

    // -----------------------------------------------------------------------
    // Metadata
    // -----------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }
}
