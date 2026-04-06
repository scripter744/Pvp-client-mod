package com.pvpclient.client;

import com.pvpclient.config.ConfigManager;
import com.pvpclient.events.EventBus;
import com.pvpclient.events.TickEvent;
import com.pvpclient.gui.ClickGUI;
import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side mod entry point.
 * Initializes subsystems in the correct dependency order:
 *   1. Config (all other systems read from it)
 *   2. HUD Manager (elements must exist before modules reference them)
 *   3. Module Manager (loads module states from config)
 *   4. Keybinds (registered last; reference already-initialized managers)
 */
public final class ClientMain implements ClientModInitializer {

    public static final String MOD_ID = "pvpclient";
    public static final Logger LOGGER = LoggerFactory.getLogger("PvPClient");

    /** Right Shift → open Click GUI. */
    private static final KeyBinding GUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.pvpclient.opengui",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_RIGHT_SHIFT,
        "key.categories.pvpclient"
    ));

    /** H → toggle HUD editor mode. */
    private static final KeyBinding HUD_EDITOR_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.pvpclient.hudeditor",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_H,
        "key.categories.pvpclient"
    ));

    private int tickCount = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[PvP Client] Initializing...");

        // 1. Load config
        ConfigManager.getInstance().load();

        // 2. Initialize HUD elements (creates instances; position loaded from config)
        HudManager.getInstance().init();

        // 3. Initialize and register all modules
        ModuleManager.getInstance().init();

        // 4. Register client tick callback
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        LOGGER.info("[PvP Client] Initialized successfully. {} modules loaded.",
            ModuleManager.getInstance().getAllModules().size());
    }

    private void onClientTick(MinecraftClient mc) {
        // Fire tick event for all subscribers
        EventBus.getInstance().post(new TickEvent(tickCount++));

        // Check GUI open keybind
        while (GUI_KEY.wasPressed()) {
            if (mc.currentScreen == null) {
                mc.setScreen(new ClickGUI());
            } else if (mc.currentScreen instanceof ClickGUI) {
                mc.setScreen(null);
            }
        }

        // Check HUD editor keybind
        while (HUD_EDITOR_KEY.wasPressed()) {
            HudManager.getInstance().toggleEditorMode();
            if (HudManager.getInstance().isEditorMode()) {
                LOGGER.info("[PvP Client] HUD editor mode enabled — drag elements to reposition.");
            }
        }
    }
}
