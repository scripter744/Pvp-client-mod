package com.pvpclient.events;

/**
 * Fired when a mouse button is pressed or released.
 */
public final class InputEvent {

    public enum Button { LEFT, RIGHT, MIDDLE }

    private final Button button;
    private final boolean pressed;

    public InputEvent(Button button, boolean pressed) {
        this.button = button;
        this.pressed = pressed;
    }

    public Button getButton() {
        return button;
    }

    public boolean isPressed() {
        return pressed;
    }
}
