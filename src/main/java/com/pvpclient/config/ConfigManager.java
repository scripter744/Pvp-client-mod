package com.pvpclient.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages saving and loading of all mod settings to/from JSON.
 * Supports multiple named config profiles that can be switched at runtime.
 */
public final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("PvPClient/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final ConfigManager INSTANCE = new ConfigManager();

    /** The config directory inside .minecraft/config/pvpclient/ */
    private final Path configDir;

    /** Currently active profile name. */
    private String activeProfile = "default";

    /** In-memory storage: module-name → key → JSON value. */
    private final Map<String, JsonObject> moduleData = new HashMap<>();

    private ConfigManager() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("pvpclient");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create config directory", e);
        }
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    // -----------------------------------------------------------------------
    // Profile management
    // -----------------------------------------------------------------------

    /** Returns the name of the currently active profile. */
    public String getActiveProfile() {
        return activeProfile;
    }

    /**
     * Switch to a named profile. Saves current data first, then loads the new profile.
     */
    public void switchProfile(String profileName) {
        save();
        this.activeProfile = profileName;
        load();
    }

    // -----------------------------------------------------------------------
    // Value accessors
    // -----------------------------------------------------------------------

    public void setBoolean(String module, String key, boolean value) {
        getOrCreate(module).addProperty(key, value);
    }

    public boolean getBoolean(String module, String key, boolean defaultValue) {
        JsonObject obj = moduleData.get(module);
        if (obj == null || !obj.has(key)) return defaultValue;
        return obj.get(key).getAsBoolean();
    }

    public void setInt(String module, String key, int value) {
        getOrCreate(module).addProperty(key, value);
    }

    public int getInt(String module, String key, int defaultValue) {
        JsonObject obj = moduleData.get(module);
        if (obj == null || !obj.has(key)) return defaultValue;
        return obj.get(key).getAsInt();
    }

    public void setFloat(String module, String key, float value) {
        getOrCreate(module).addProperty(key, value);
    }

    public float getFloat(String module, String key, float defaultValue) {
        JsonObject obj = moduleData.get(module);
        if (obj == null || !obj.has(key)) return defaultValue;
        return obj.get(key).getAsFloat();
    }

    public void setString(String module, String key, String value) {
        getOrCreate(module).addProperty(key, value);
    }

    public String getString(String module, String key, String defaultValue) {
        JsonObject obj = moduleData.get(module);
        if (obj == null || !obj.has(key)) return defaultValue;
        return obj.get(key).getAsString();
    }

    // -----------------------------------------------------------------------
    // Persistence
    // -----------------------------------------------------------------------

    /**
     * Save all current in-memory settings to the active profile's JSON file.
     */
    public void save() {
        Path profileFile = configDir.resolve(activeProfile + ".json");
        JsonObject root = new JsonObject();
        for (Map.Entry<String, JsonObject> entry : moduleData.entrySet()) {
            root.add(entry.getKey(), entry.getValue());
        }
        try (Writer writer = Files.newBufferedWriter(profileFile, StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save config profile '{}': {}", activeProfile, e.getMessage());
        }
    }

    /**
     * Load settings from the active profile's JSON file into memory.
     * Missing files are silently ignored (first-run scenario).
     */
    public void load() {
        moduleData.clear();
        Path profileFile = configDir.resolve(activeProfile + ".json");
        if (!Files.exists(profileFile)) return;
        try (Reader reader = Files.newBufferedReader(profileFile, StandardCharsets.UTF_8)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) return;
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    moduleData.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
            }
        } catch (IOException | JsonParseException e) {
            LOGGER.error("Failed to load config profile '{}': {}", activeProfile, e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private JsonObject getOrCreate(String module) {
        return moduleData.computeIfAbsent(module, k -> new JsonObject());
    }
}
