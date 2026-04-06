package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class HungerHudModule extends Module {

    public HungerHudModule() {
        super("Hunger HUD", "Displays current food level.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("hunger_hud"))
            .forEach(e -> e.setVisible(visible));
    }
}
