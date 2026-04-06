package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class KeystrokesModule extends Module {

    public KeystrokesModule() {
        super("Keystrokes", "Shows WASD, Space, Shift, LMB, RMB state.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("keystrokes"))
            .forEach(e -> e.setVisible(visible));
    }
}
