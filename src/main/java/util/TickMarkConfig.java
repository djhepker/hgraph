package util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

/**
 * Configuration object for controlling the appearance and behavior of axis tick marks in a LineGraph.
 * Supports both X and Y axis customization, including visibility, color, length, font, and label count.
 */
@Getter
@AllArgsConstructor
public class TickMarkConfig {
    private boolean showYTicks;
    private boolean showXTicks;
    private int tickLength;
    private int tickCount; // Axis is divided into this many equal parts and draw that many tick lines
    private Color tickColor;
    private Font tickFont;

    /**
     * Default configuration with all settings enabled and standard styling.
     */
    public TickMarkConfig() {
        this.showYTicks = true;
        this.showXTicks = true;
        this.tickLength = 10;
        this.tickCount = 10;
        this.tickColor = Color.BLACK;
        this.tickFont = new Font("Arial", Font.PLAIN, 12);
    }

    /**
     * Enables or disables Y-axis ticks.
     * @param show true to show Y-axis ticks, false to hide
     * @return This config instance for chaining
     */
    public TickMarkConfig showYTicks(boolean show) {
        this.showYTicks = show;
        return this;
    }

    /**
     * Enables or disables X-axis ticks.
     * @param show true to show X-axis ticks, false to hide
     * @return This config instance for chaining
     */
    public TickMarkConfig showXTicks(boolean show) {
        this.showXTicks = show;
        return this;
    }

    /**
     * Sets the length of each tick mark.
     * @param tickLength Length in pixels
     * @return This config instance for chaining
     */
    public TickMarkConfig tickLength(int tickLength) {
        this.tickLength = tickLength;
        return this;
    }

    /**
     * Sets how many tick marks to draw per axis.
     * @param tickCount Number of ticks
     * @return This config instance for chaining
     */
    public TickMarkConfig tickCount(int tickCount) {
        this.tickCount = tickCount;
        return this;
    }

    /**
     * Sets the color used to draw tick marks and their labels.
     * @param tickColor Color object
     * @return This config instance for chaining
     */
    public TickMarkConfig tickColor(Color tickColor) {
        this.tickColor = tickColor;
        return this;
    }

    /**
     * Sets the font used to render tick mark labels.
     * @param tickFont Font object
     * @return This config instance for chaining
     */
    public TickMarkConfig tickFont(Font tickFont) {
        this.tickFont = tickFont;
        return this;
    }
}
