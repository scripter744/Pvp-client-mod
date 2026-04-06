package com.pvpclient.modules;

import com.pvpclient.modules.hud.*;
import com.pvpclient.modules.performance.*;
import com.pvpclient.modules.pvp.*;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central registry for all modules.
 * Handles registration, keybind processing, and category-based lookup.
 */
public final class ModuleManager {

    private static final ModuleManager INSTANCE = new ModuleManager();

    private final List<Module> modules = new ArrayList<>();

    private ModuleManager() {}

    public static ModuleManager getInstance() {
        return INSTANCE;
    }

    /**
     * Register all modules and load their configs.
     * Called once during mod initialization.
     */
    public void init() {
        // --- HUD Modules ---
        register(new FpsCounterModule());
        register(new PingDisplayModule());
        register(new CoordinatesModule());
        register(new DirectionModule());
        register(new CpsCounterModule());
        register(new KeystrokesModule());
        register(new ArmorHudModule());
        register(new HealthHudModule());
        register(new HungerHudModule());
        register(new ShieldStatusModule());
        register(new ComboCounterModule());
        register(new ReachDisplayModule());
        register(new TargetHudModule());

        // --- PvP Modules ---
        register(new ToggleSprintModule());
        register(new ToggleSneakModule());
        register(new AttackIndicatorModule());
        register(new HitFeedbackModule());
        register(new CrosshairEditorModule());
        register(new DamageIndicatorsModule());

        // --- Performance Modules ---
        register(new EntityCullingModule());
        register(new ParticleLimiterModule());
        register(new AnimationReducerModule());
        register(new FpsSaverModule());
        register(new LowEndModeModule());

        // Load config for all modules
        for (Module module : modules) {
            module.loadConfig();
        }
    }

    private void register(Module module) {
        modules.add(module);
    }

    // -----------------------------------------------------------------------
    // Lookups
    // -----------------------------------------------------------------------

    public List<Module> getAllModules() {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> getModulesByCategory(Module.Category category) {
        return modules.stream()
                      .filter(m -> m.getCategory() == category)
                      .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (m.getClass() == clazz) return (T) m;
        }
        return null;
    }

    public Optional<Module> getByName(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
    }

    // -----------------------------------------------------------------------
    // Keybind processing — called every tick
    // -----------------------------------------------------------------------

    /** Track previously pressed states to detect edge transitions. */
    private final Set<Integer> previouslyPressed = new HashSet<>();

    public void onKeyPress(int glfwKey) {
        if (previouslyPressed.contains(glfwKey)) return;
        previouslyPressed.add(glfwKey);
        for (Module module : modules) {
            if (module.getKeybind() == glfwKey) {
                module.toggle();
            }
        }
    }

    public void onKeyRelease(int glfwKey) {
        previouslyPressed.remove(glfwKey);
    }
}
