# PvP Client Mod — Build & Usage Instructions

## Overview

A high-performance Fabric mod for Minecraft 1.21.4 with advanced PvP HUD,
performance optimizations, and a clean click GUI.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 21 or higher |
| Git | Any recent version |

> **Mobile users (Zalith / PojavLauncher):** Compile on a desktop PC or use
> a cloud build service, then transfer the `.jar` to your device.

---

## Step 1 — Import into IntelliJ IDEA (recommended)

1. Open IntelliJ IDEA.
2. Click **File → Open** and select the `pvp-client-mod/` folder.
3. IntelliJ will detect the Gradle project automatically and begin importing.
4. Wait for Gradle sync and dependency download to finish (~3–5 minutes on first run).
5. Run **Tasks → fabric → genSources** in the Gradle tool window to decompile
   Minecraft sources for code navigation.

### VS Code (alternative)
1. Install the **Extension Pack for Java** and **Gradle for Java** extensions.
2. Open the `pvp-client-mod/` folder.
3. VS Code will automatically detect the Gradle project.

---

## Step 2 — Build the jar

Open a terminal inside the `pvp-client-mod/` directory and run:

```bash
./gradlew build
```

On Windows:
```cmd
gradlew.bat build
```

The compiled mod jar is output to:
```
build/libs/pvp-client-1.0.0.jar
```

> The `-sources.jar` and `-javadoc.jar` files in the same folder are optional.
> Only `pvp-client-1.0.0.jar` needs to be installed.

---

## Step 3 — Install the mod

### Desktop (Windows / macOS / Linux)

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.4.
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) — required dependency.
3. Copy `pvp-client-1.0.0.jar` into your `.minecraft/mods/` folder.
4. Launch Minecraft using the Fabric profile.

### Mobile — Zalith Launcher

1. Open Zalith Launcher.
2. Create a profile for Minecraft 1.21.4 with Fabric Loader.
3. Go to **Mods** → tap the **+** button.
4. Select `pvp-client-1.0.0.jar` from your device storage.
5. Also add `fabric-api-*.jar`.
6. Launch the profile.

### Mobile — PojavLauncher

1. Install Fabric Loader via PojavLauncher's **Install** → **Fabric** flow.
2. Navigate to `/storage/emulated/0/PojavLauncher/.minecraft/mods/`
   (or use the in-app file manager).
3. Copy both `pvp-client-1.0.0.jar` and `fabric-api-*.jar` into the `mods/` folder.
4. Launch with the Fabric profile.

---

## Keybinds

| Key | Action |
|-----|--------|
| **Right Shift** | Open / Close Click GUI |
| **H** | Toggle HUD Editor (drag elements to reposition) |
| Module-specific | Configurable per module via Click GUI |

---

## Config Files

All settings are saved to:
```
.minecraft/config/pvpclient/default.json
```

You can create multiple profiles via the Config section of the GUI and switch
between them with one click.

---

## Performance Tips for Mobile / Low-End

1. Enable **Low-End Mode** in the Performance tab — this applies maximum
   optimizations automatically.
2. Enable **Entity Culling** — skips off-screen entity rendering.
3. Enable **Particle Limiter** — halves particle draw calls.
4. Enable **FPS Saver** — automatically reduces render distance when FPS drops.
5. Set render distance to **4–6 chunks** for smoothest gameplay on mobile.

---

## Module Reference

### HUD Tab
| Module | Description |
|--------|-------------|
| FPS Counter | Current frames per second (color-coded) |
| Ping Display | Server ping in ms |
| Coordinates | XYZ block position |
| Direction | Cardinal/intercardinal facing direction |
| CPS Counter | Left and right clicks per second |
| Keystrokes | WASD + Space + Shift + LMB/RMB display |
| Armor HUD | Each armor piece with durability % + bar |
| Health HUD | Health bar + numeric value |
| Hunger HUD | Food level bar |
| Shield Status | Shield raised/cooldown indicator |
| Combo Counter | Consecutive hit counter (fades after 3s) |
| Reach Display | Current attack reach (visual info only) |
| Target HUD | Crosshair target: name, health, distance, armor |

### PvP Tab
| Module | Description |
|--------|-------------|
| Toggle Sprint | Auto-sprints when moving forward |
| Toggle Sneak | Press sneak to toggle instead of holding |
| Attack Indicator | Visual cooldown bar for 1.9+ combat |
| Hit Feedback | Extra sound on successful hit |
| Crosshair Editor | Custom crosshair: size, gap, color, dynamic |
| Damage Indicators | Floating damage numbers above entities |

### Performance Tab
| Module | Description |
|--------|-------------|
| Entity Culling | Skips rendering off-camera entities |
| Particle Limiter | Reduces particle system tick rate |
| Animation Reducer | Disables entity shadows and animations |
| FPS Saver | Auto-lowers settings when FPS drops below 30 |
| Low-End Mode | Full optimization preset for mobile/low-spec |

---

## Architecture Overview

```
com.pvpclient/
├── client/
│   └── ClientMain.java        — Mod entry point, tick loop, keybinds
├── modules/
│   ├── Module.java            — Abstract base class
│   ├── ModuleManager.java     — Registry + keybind dispatcher
│   ├── hud/                   — HUD toggle modules
│   ├── pvp/                   — PvP feature modules
│   └── performance/           — Performance optimization modules
├── hud/
│   ├── HudElement.java        — Draggable/resizable base class
│   ├── HudManager.java        — Render + editor mode orchestration
│   └── elements/              — 13 concrete HUD element implementations
├── render/
│   └── RendererUtils.java     — Shared drawing utilities
├── utils/
│   ├── MathUtils.java         — Lerp, easing, color math
│   └── PlayerUtils.java       — Player state accessors
├── config/
│   └── ConfigManager.java     — JSON config with multi-profile support
├── gui/
│   ├── ClickGUI.java          — Full-featured click GUI screen
│   └── components/
│       └── CategoryPanel.java — Module list panel component
├── events/
│   ├── EventBus.java          — Generic type-safe event dispatcher
│   ├── TickEvent.java
│   ├── RenderEvent.java
│   ├── AttackEvent.java
│   └── InputEvent.java
└── mixin/                     — Minimal Mixin injections
    ├── MixinGameRenderer.java              — HUD render hook
    ├── MixinInGameHud.java                 — Crosshair suppression
    ├── MixinClientPlayerInteractionManager — Attack event dispatch
    ├── MixinWorldRenderer.java             — Entity culling
    ├── MixinParticleManager.java           — Particle rate limit
    └── MixinMouseHandler.java              — Mouse input events
```

---

## Troubleshooting

**Build fails with "Could not resolve fabric-api"**
→ Make sure you have an active internet connection. Gradle downloads dependencies on first build.

**"Incompatible Fabric Loader version"**
→ Update Fabric Loader to version ≥ 0.15.0.

**Crash on startup**
→ Check `logs/latest.log` in your `.minecraft` folder. Usually a missing
   Fabric API or wrong Minecraft version.

**Blank HUD / GUI not opening**
→ Ensure Fabric API is installed alongside the mod.

**Mobile: game crashes immediately**
→ PojavLauncher/Zalith may need at least 1.5 GB RAM allocated. Increase the
   memory limit in launcher settings.
