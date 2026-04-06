package com.pvpclient.utils;

/**
 * Lightweight math utilities focused on performance for rendering code.
 * All methods are static to avoid object allocation on hot paths.
 */
public final class MathUtils {

    private MathUtils() {}

    /**
     * Linear interpolation between a and b by factor t clamped to [0, 1].
     */
    public static float lerp(float a, float b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return a + (b - a) * t;
    }

    /**
     * Smooth step easing — cubic Hermite S-curve.
     * Input t should be in [0, 1].
     */
    public static float smoothStep(float t) {
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    /**
     * Ease-out cubic — decelerating transition.
     */
    public static float easeOutCubic(float t) {
        t = Math.max(0f, Math.min(1f, t));
        float inv = 1f - t;
        return 1f - inv * inv * inv;
    }

    /**
     * Ease-in-out quad — symmetric acceleration/deceleration.
     */
    public static float easeInOutQuad(float t) {
        t = Math.max(0f, Math.min(1f, t));
        return t < 0.5f ? 2f * t * t : 1f - (-2f * t + 2f) * (-2f * t + 2f) / 2f;
    }

    /**
     * Clamp a value between min and max.
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Pack ARGB components into a single integer (0xAARRGGBB).
     */
    public static int packColor(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Linearly interpolate between two ARGB packed colors.
     */
    public static int lerpColor(int colorA, int colorB, float t) {
        int a = (int) lerp((colorA >> 24) & 0xFF, (colorB >> 24) & 0xFF, t);
        int r = (int) lerp((colorA >> 16) & 0xFF, (colorB >> 16) & 0xFF, t);
        int g = (int) lerp((colorA >> 8) & 0xFF, (colorB >> 8) & 0xFF, t);
        int b = (int) lerp(colorA & 0xFF, colorB & 0xFF, t);
        return packColor(a, r, g, b);
    }

    /**
     * Return the direction name (N/NE/E/SE/S/SW/W/NW) from a yaw angle.
     */
    public static String yawToDirection(float yaw) {
        yaw = ((yaw % 360f) + 360f) % 360f;
        int index = (int) ((yaw + 22.5f) / 45f) & 7;
        return new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"}[index];
    }
}
