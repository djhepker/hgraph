package util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;
import java.awt.Font;

/**
 * Configuration object for controlling the appearance and behavior of axis tick marks in a LineGraph.
 * Supports both X and Y axis customization, including visibility, color, length, font, and label count.
 */
@AllArgsConstructor
public class TickMarkConfig {
    /**
     *  Returns whether Y-axis tick marks should be drawn.
     *
     * @return true if Y-axis ticks should be shown
     */
    @Getter
    private boolean showYTicks;
    /**
     *  Returns whether X-axis tick marks should be drawn.
     *
     * @return true if X-axis ticks should be shown
     */
    @Getter
    private boolean showXTicks;
    /**
     *  Returns whether double precision is enabled for tick mark formatting.
     *
     * @return true if double precision mode is enabled; false if integer formatting is used
     */
    @Getter
    private boolean doublePrecision;

    /**
     *  Returns the tick mark length in pixels.
     *
     * @return length of tick marks
     */
    @Getter
    private int tickLength;

    private double[] xTicksDouble;
    private double[] yTicksDouble;
    private int[] xTicksInt;
    private int[] yTicksInt;

    /**
     * -- GETTER --
     *  Returns the tick color used for marks and labels.
     *
     * @return tick color
     */
    @Getter
    private Color tickColor;
    /**
     * -- GETTER --
     *  Returns the font used for tick mark labels.
     *
     * @return tick font
     */
    @Getter
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
        this.xTicksDouble = null;
        this.yTicksDouble = null;
        this.tickFont = new Font("Arial", Font.PLAIN, 12);
    }

    /**
     * Mutator allowing user to allow or disallow double precision. When enabled, ticks are formatted
     * as 1.00. When disabled, 1. Default false.<br>
     * @param doublePrecision Value to set doublePrecision to.
     * @return This config instance for chaining
     */
    public TickMarkConfig setDoublePrecision(boolean doublePrecision) {
        this.doublePrecision = doublePrecision;
        if (doublePrecision) {
            this.xTicksDouble = GraphTools.intArrayToDoubleArray(xTicksInt);
            this.xTicksInt = null;
            this.yTicksDouble = GraphTools.intArrayToDoubleArray(yTicksInt);
            this.yTicksInt = null;
        } else {
            this.xTicksInt = GraphTools.doubleArrayToIntArray(xTicksDouble);
            this.xTicksDouble = null;
            this.yTicksInt = GraphTools.doubleArrayToIntArray(yTicksDouble);
            this.yTicksDouble = null;
        }
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
        this.xTicksInt = null;
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
        this.yTicksInt = null;
        return this;
    }

    /**
     * Specifies the exact X-values to draw tick marks at using integer values.
     * <p>
     * If this is set, tickCount is ignored for the X-axis.
     * Also nullifies any previously set double-based X-tick values.
     * </p>
     *
     * @param xTicks array of integer X-axis values
     * @return this config instance
     */
    public TickMarkConfig setXTickValues(int[] xTicks) {
        this.xTicksInt = xTicks;
        this.xTicksDouble = null;
        return this;
    }

    /**
     * Specifies the exact Y-values to draw tick marks at using integer values.
     * <p>
     * If this is set, tickCount is ignored for the Y-axis.
     * Also nullifies any previously set double-based Y-tick values.
     * </p>
     *
     * @param yTicks array of integer Y-axis values
     * @return this config instance
     */
    public TickMarkConfig setYTickValues(int[] yTicks) {
        this.yTicksInt = yTicks;
        this.yTicksDouble = null;
        return this;
    }

    /**
     * Gets the active X-axis tick values based on precision mode.
     * <p>
     * If {@code doublePrecision} is enabled, returns {@code xTicksDouble}, or an empty array if null.
     * Otherwise, converts {@code xTicksInt} to {@code double[]} or returns empty array if null.
     * </p>
     *
     * @return the X-axis tick values as {@code double[]} respecting precision
     */
    public double[] getXTicks() {
        if (doublePrecision) {
            return xTicksDouble != null ? xTicksDouble : new double[0];
        } else if (xTicksInt != null) {
            return GraphTools.intArrayToDoubleArray(xTicksInt);
        } else {
            return new double[0];
        }
    }

    /**
     * Gets the active Y-axis tick values based on precision mode.
     * <p>
     * If {@code doublePrecision} is enabled, returns {@code yTicksDouble}, or an empty array if null.
     * Otherwise, converts {@code yTicksInt} to {@code double[]} or returns empty array if null.
     * </p>
     *
     * @return the Y-axis tick values as {@code double[]} respecting precision
     */
    public double[] getYTicks() {
        if (doublePrecision) {
            return yTicksDouble != null ? yTicksDouble : new double[0];
        } else if (yTicksInt != null) {
            return GraphTools.intArrayToDoubleArray(yTicksInt);
        } else {
            return new double[0];
        }
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
