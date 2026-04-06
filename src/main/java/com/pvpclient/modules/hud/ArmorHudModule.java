package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class ArmorHudModule extends Module {

    public ArmorHudModule() {
        super("Armor HUD", "Shows armor pieces with durability bars.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("armor_hud"))
            .forEach(e -> e.setVisible(visible));
    }
}
