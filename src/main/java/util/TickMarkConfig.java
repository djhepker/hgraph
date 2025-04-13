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
    private boolean doublePrecision;

    private int tickLength;

    private double[] xTicksDouble;
    private double[] yTicksDouble;
    private int[] xTicksInt;
    private int[] yTicksInt;

    private Color tickColor;
    private Font tickFont;

    /**
     * Default configuration with all settings enabled and standard styling.
     */
    public TickMarkConfig() {
        this.showYTicks = true;
        this.showXTicks = true;
        this.doublePrecision = false;
        this.tickLength = 10;
        this.tickColor = Color.BLACK;
        this.xTicksInt = new int[0];
        this.yTicksInt = new int[0];
        this.tickFont = new Font("Arial", Font.PLAIN, 12);
    }

    /**
     * Mutator allowing user to allow or disallow double precision. When enabled, ticks are formatted
     * as 1.00. When disabled, 1. Default false.<br>
     * If applicable, copies x,y int[] to double[], then deallocates x,y int[]
     * @param doublePrecision Value to set doublePrecision to.
     * @return This config instance for chaining
     */
    public TickMarkConfig setDoublePrecision(boolean doublePrecision) {
        this.doublePrecision = doublePrecision;
        if (doublePrecision) {
            if (xTicksInt != null && xTicksInt.length > 0) {
                xTicksDouble = new double[xTicksInt.length];
                for (int i = 0; i < xTicksInt.length; i++) {
                    xTicksDouble[i] = xTicksInt[i];
                }
                xTicksInt = null;
            }

            if (yTicksInt != null && yTicksInt.length > 0) {
                yTicksDouble = new double[yTicksInt.length];
                for (int i = 0; i < yTicksInt.length; i++) {
                    yTicksDouble[i] = yTicksInt[i];
                }
                yTicksInt = null;
            }
        }
        xTicksInt = null;
        yTicksInt = null;
        return this;
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
     * Specifies the exact X-values to draw tick marks at.
     * If this is set, tickCount is ignored for the X-axis.
     *
     * @param xTicks array of X-axis values
     * @return this config instance
     */
    public TickMarkConfig setXTickValues(double[] xTicks) {
        this.xTicksDouble = xTicks;
        return this;
    }

    /**
     * Specifies the exact Y-values to draw tick marks at.
     * If this is set, tickCount is ignored for the Y-axis.
     *
     * @param yTicks array of Y-axis values
     * @return this config instance
     */
    public TickMarkConfig setYTickValues(double[] yTicks) {
        this.yTicksDouble = yTicks;
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
