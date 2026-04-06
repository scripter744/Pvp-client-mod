package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class CoordinatesModule extends Module {

    public CoordinatesModule() {
        super("Coordinates", "Displays player XYZ position.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("coordinates"))
            .forEach(e -> e.setVisible(visible));
    }
}
