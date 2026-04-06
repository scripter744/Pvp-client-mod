package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class ShieldStatusModule extends Module {

    public ShieldStatusModule() {
        super("Shield Status", "Shows if shield is raised and its cooldown.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("shield_status"))
            .forEach(e -> e.setVisible(visible));
    }
}
