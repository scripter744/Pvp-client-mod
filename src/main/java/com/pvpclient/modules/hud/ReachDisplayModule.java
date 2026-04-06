package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class ReachDisplayModule extends Module {

    public ReachDisplayModule() {
        super("Reach Display", "Shows current attack reach distance (visual only).", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("reach_display"))
            .forEach(e -> e.setVisible(visible));
    }
}
