package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class DirectionModule extends Module {

    public DirectionModule() {
        super("Direction", "Shows cardinal direction the player faces.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("direction"))
            .forEach(e -> e.setVisible(visible));
    }
}
