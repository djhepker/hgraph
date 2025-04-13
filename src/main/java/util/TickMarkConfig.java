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
    /**
     *  Returns the tick color used for marks and labels.
     *
     * @return tick color
     */
    @Getter
    private Color tickColor;
    /**
     *  Returns the font used for tick mark labels.
     *
     * @return tick font
     */
    @Getter
    private Font tickFont;

    private double[] xTicksDouble;
    private double[] yTicksDouble;
    private int[] xTicksInt;
    private int[] yTicksInt;



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
        if (doublePrecision != this.doublePrecision) {
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
     * Sets the color used to draw tick marks and their labels.
     * <p>
     * Enables fluent configuration by returning the same {@code TickMarkConfig} instance.
     * </p>
     *
     * @param tickColor the color to use for tick marks and labels
     * @return this config instance for method chaining
     */
    public TickMarkConfig setTickColor(Color tickColor) {
        this.tickColor = tickColor;
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
     * Gets the active X-axis tick values as a {@code double[]} array.
     * <p>
     * If {@code doublePrecision} is enabled, returns {@code xTicksDouble}, or an empty array if null.
     * Otherwise, converts {@code xTicksInt} to {@code double[]} or returns an empty array if null.
     * </p>
     *
     * @return the X-axis tick values as {@code double[]}
     */
    public double[] getDoubleXTicks() {
        if (doublePrecision) {
            return xTicksDouble != null ? xTicksDouble : new double[0];
        } else if (xTicksInt != null) {
            return GraphTools.intArrayToDoubleArray(xTicksInt);
        } else {
            return new double[0];
        }
    }

    /**
     * Gets the active Y-axis tick values as a {@code double[]} array.
     * <p>
     * If {@code doublePrecision} is enabled, returns {@code yTicksDouble}, or an empty array if null.
     * Otherwise, converts {@code yTicksInt} to {@code double[]} or returns an empty array if null.
     * </p>
     *
     * @return the Y-axis tick values as {@code double[]}
     */
    public double[] getDoubleYTicks() {
        if (doublePrecision) {
            return yTicksDouble != null ? yTicksDouble : new double[0];
        } else if (yTicksInt != null) {
            return GraphTools.intArrayToDoubleArray(yTicksInt);
        } else {
            return new double[0];
        }
    }

    /**
     * Gets the active X-axis tick values as an {@code int[]} array.
     * <p>
     * If {@code doublePrecision} is disabled, returns {@code xTicksInt}, or an empty array if null.
     * Otherwise, converts {@code xTicksDouble} to {@code int[]} or returns an empty array if null.
     * </p>
     *
     * @return the X-axis tick values as {@code int[]}
     */
    public int[] getIntXTicks() {
        if (!doublePrecision) {
            return xTicksInt != null ? xTicksInt : new int[0];
        } else if (xTicksDouble != null) {
            return GraphTools.doubleArrayToIntArray(xTicksDouble);
        } else {
            return new int[0];
        }
    }

    /**
     * Gets the active Y-axis tick values as an {@code int[]} array.
     * <p>
     * If {@code doublePrecision} is disabled, returns {@code yTicksInt}, or an empty array if null.
     * Otherwise, converts {@code yTicksDouble} to {@code int[]} or returns an empty array if null.
     * </p>
     *
     * @return the Y-axis tick values as {@code int[]}
     */
    public int[] getIntYTicks() {
        if (!doublePrecision) {
            return yTicksInt != null ? yTicksInt : new int[0];
        } else if (yTicksDouble != null) {
            return GraphTools.doubleArrayToIntArray(yTicksDouble);
        } else {
            return new int[0];
        }
    }

    /**
     * Returns a portion of the X-axis tick values as {@code double[]}, in range [start, finish).
     *
     * @param start the starting index (inclusive)
     * @param finish the ending index (exclusive)
     * @return a subarray of the X-axis ticks in double format
     */
    public double[] getDoubleXTicks(int start, int finish) {
        double[] base = getDoubleXTicks();
        return sliceDoubleArray(base, start, finish);
    }

    /**
     * Returns a portion of the Y-axis tick values as {@code double[]}, in range [start, finish).
     *
     * @param start the starting index (inclusive)
     * @param finish the ending index (exclusive)
     * @return a subarray of the Y-axis ticks in double format
     */
    public double[] getDoubleYTicks(int start, int finish) {
        double[] base = getDoubleYTicks();
        return sliceDoubleArray(base, start, finish);
    }

    /**
     * Returns a portion of the X-axis tick values as {@code int[]}, in range [start, finish).
     *
     * @param start the starting index (inclusive)
     * @param finish the ending index (exclusive)
     * @return a subarray of the X-axis ticks in int format
     */
    public int[] getIntXTicks(int start, int finish) {
        int[] base = getIntXTicks();
        return sliceIntArray(base, start, finish);
    }

    /**
     * Returns a portion of the Y-axis tick values as {@code int[]}, in range [start, finish).
     *
     * @param start the starting index (inclusive)
     * @param finish the ending index (exclusive)
     * @return a subarray of the Y-axis ticks in int format
     */
    public int[] getIntYTicks(int start, int finish) {
        int[] base = getIntYTicks();
        return sliceIntArray(base, start, finish);
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

    private double[] sliceDoubleArray(double[] array, int start, int finish) {
        if (array == null || start < 0 || finish > array.length || start >= finish) {
            return new double[0];
        }
        double[] result = new double[finish - start];
        System.arraycopy(array, start, result, 0, result.length);
        return result;
    }

    private int[] sliceIntArray(int[] array, int start, int finish) {
        if (array == null || start < 0 || finish > array.length || start >= finish) {
            return new int[0];
        }
        int[] result = new int[finish - start];
        System.arraycopy(array, start, result, 0, result.length);
        return result;
    }
}
