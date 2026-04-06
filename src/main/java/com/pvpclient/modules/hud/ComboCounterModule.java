package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class ComboCounterModule extends Module {

    public ComboCounterModule() {
        super("Combo Counter", "Tracks consecutive hits and shows combo count.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("combo_counter"))
            .forEach(e -> e.setVisible(visible));
    }
}
