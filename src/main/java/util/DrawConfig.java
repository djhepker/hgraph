package util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;

/**
 * Configuration object for controlling the appearance and behavior of axis tick marks in a LineGraph.
 * Supports both X and Y axis customization, including visibility, color, length, font, and label count.
 */
@AllArgsConstructor
public final class DrawConfig {

    @Getter private Color backgroundColor;
    @Getter private Color edgeColor;
    @Getter private Color tickColor;
    @Getter private Color gridColor;
    @Getter private Color borderColor;
    @Getter private Color tickLabelColor;

    @Getter private boolean showingGrid;
    @Getter private boolean showingGraphTickMarks;
    @Getter private boolean showingTickLabels;
    @Getter private boolean showingMarginBorder;
    @Getter private boolean doublePrecision;

    @Getter private int tickLength;
    @Getter private int marginSize; // TODO build margin x and margin y

    @Getter @Setter private double xPixelsDelta;
    @Getter @Setter private double yPixelsDelta;

    @Getter private float edgeThickness;

    private double[] xTicksDouble; // TODO replace tick[] with String[]
    private double[] yTicksDouble;
    private int[] xTicksInt;
    private int[] yTicksInt;

    /**
     * Default configuration with all settings enabled and standard styling.
     */
    public DrawConfig() {
        showingTickLabels = true;
        showingMarginBorder = true;
        showingGraphTickMarks = true;
        showingGrid = false;
        doublePrecision = false;

        tickLength = 10;
        marginSize = 32;

        xPixelsDelta = 0.0;
        yPixelsDelta = 0.0;

        edgeThickness = 2.0f;

        tickColor = Color.GREEN;
        tickLabelColor = tickColor;
        borderColor = Color.WHITE;
        backgroundColor = Color.BLACK;
        edgeColor = Color.GREEN;

        gridColor = new Color(255, 255, 255, 64);

        xTicksInt = new int[0];
        yTicksInt = new int[0];
        xTicksDouble = null;
        yTicksDouble = null;
    }

    /**
     * Sets whether the margin border around the graph area is shown.
     *
     * @param showingMarginBorder True if showing border, false otherwise.
     * @return this instance for method chaining.
     */
    public DrawConfig setShowingMarginBorder(boolean showingMarginBorder) {
        this.showingMarginBorder = showingMarginBorder;
        return this;
    }

    /**
     * Sets whether tick marks should be shown on the graph.
     *
     * @param showingGraphTickMarks true to show tick marks, false to hide them.
     * @return this instance for method chaining.
     */
    public DrawConfig showTicks(boolean showingGraphTickMarks) {
        this.showingGraphTickMarks = showingGraphTickMarks;
        return this;
    }

    /**
     * Mutates boolean determining if grid parameters are drawn. True if shown, false if not shown.
     *
     * @param showGrid Value showingGrid will be mutated to,
     * @return this instance for method chaining.
     */
    public DrawConfig setShowGrid(boolean showGrid) {
        this.showingGrid = showGrid;
        return this;
    }

    /**
     * Mutates boolean determining if margin parameters are drawn. True if shown, false if not shown.
     *
     * @param showMarginBorder Value showingGrid will be mutated to,
     * @return this instance for method chaining.
     */
    public DrawConfig setShowMarginBorder(boolean showMarginBorder) {
        this.showingMarginBorder = showMarginBorder;
        return this;
    }

    /**
     * Mutates whether tick labels will be displayed.
     *
     * @param showTickLabels True if tick labels are to be shown. False otherwise.
     * @return this instance for method chaining.
     */
    public DrawConfig setShowTickLabels(boolean showTickLabels) {
        this.showingTickLabels = showTickLabels;
        return this;
    }

    /**
     * Sets the margin size around the graph area.
     *
     * @param marginSize margin size in pixels.
     * @return this instance for method chaining.
     */
    public DrawConfig setMarginSize(int marginSize) {
        this.marginSize = marginSize;
        return this;
    }

    /**
     * Sets the thickness of edges connecting vertices.
     *
     * @param edgeThickness Pixel width of drawn edges.
     * @return this instance for method chaining.
     */
    public DrawConfig setEdgeThickness(float edgeThickness) {
        this.edgeThickness = edgeThickness;
        return this;
    }

    /**
     * Sets the background color for the graph area.
     *
     * @param backgroundColor background fill color.
     * @return this instance for method chaining.
     */
    public DrawConfig setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * Sets the connecting line, if any, color for the graph area.
     *
     * @param argEdgeColor The color of the connecting lines, if any, on this Graph.
     * @return this instance for method chaining.
     */
    public DrawConfig setEdgeColor(Color argEdgeColor) {
        this.edgeColor = argEdgeColor;
        return this;
    }

    /**
     * Sets whether tick labels should be shown on the graph.
     *
     * @param showingTickLabels true to show tick labels, false otherwise
     * @return this instance for method chaining
     */
    public DrawConfig setShowingTickLabels(boolean showingTickLabels) {
        this.showingTickLabels = showingTickLabels;
        return this;
    }

    /**
     * Mutator allowing user to allow or disallow double precision. When enabled, ticks are formatted
     * as 1.00. When disabled, 1. Default false.<br>
     * @param doublePrecision Value to set doublePrecision to.
     * @return This config instance for chaining
     */
    public DrawConfig setDoublePrecision(boolean doublePrecision) {
        if (doublePrecision != this.doublePrecision) {
            this.doublePrecision = doublePrecision;
            if (doublePrecision) {
                this.xTicksDouble = GraphTools.arrayIntToArrayDouble(xTicksInt);
                this.xTicksInt = null;
                this.yTicksDouble = GraphTools.arrayIntToArrayDouble(yTicksInt);
                this.yTicksInt = null;
            } else {
                this.xTicksInt = GraphTools.arrayDoubleToArrayInt(xTicksDouble);
                this.xTicksDouble = null;
                this.yTicksInt = GraphTools.arrayDoubleToArrayInt(yTicksDouble);
                this.yTicksDouble = null;
            }
        }
        return this;
    }

    /**
     * Sets whether gridlines are shown on graph.
     *
     * @param showGrid True if lines are shown, false otherwise
     * @return this instance for method chaining
     */
    public DrawConfig setShowGridLines(boolean showGrid) {
        this.showingGrid = showGrid;
        return this;
    }

    /**
     * Getter returns the size for the appropriate yTick container.
     *
     * @return [0,length) of the container.
     */
    public int getXTicksSize() {
        if (doublePrecision) {
            return xTicksDouble.length;
        } else {
            return xTicksInt.length;
        }
    }

    /**
     * Getter returns the size for the appropriate xTick container.
     *
     * @return [0,length) of the container.
     */
    public int getYTicksSize() {
        if (doublePrecision) {
            return yTicksDouble.length;
        } else {
            return yTicksInt.length;
        }
    }

    /**
     * Sets the length of each tick mark.
     * @param tickLength Length in pixels
     * @return This config instance for chaining
     */
    public DrawConfig tickLength(int tickLength) {
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
    public DrawConfig setTickColor(Color tickColor) {
        this.tickColor = tickColor;
        return this;
    }

    /**
     * Sets the color used to draw gridlines.
     * <p>
     * Enables fluent configuration by returning the same {@code TickMarkConfig} instance.
     * </p>
     *
     * @param gridColorArg This will be the set color of gridlines when shown on the graph.
     * @return this config instance for method chaining
     */
    public DrawConfig setGridColor(Color gridColorArg) {
        this.gridColor = gridColorArg;
        return this;
    }

    /**
     * Sets the color of the graph's border.
     * <p>
     * Enables fluent configuration by returning the same {@code TickMarkConfig} instance.
     * </p>
     *
     * @param borderColorArg This will be the set color of the border surrounding the graph's data.
     * @return this config instance for method chaining
     */
    public DrawConfig setBorderColor(Color borderColorArg) {
        this.borderColor = borderColorArg;
        return this;
    }

    /**
     * Sets the ticks which are displayed on the X-axis. If graph is in double precision, will modify the double[] to
     * an int[] for setting.
     *
     * @param xTicks array of integer X-axis values
     * @return this config instance
     */
    public DrawConfig setXTickValues(double[] xTicks) {
        if (doublePrecision) {
            this.xTicksDouble = xTicks;
        } else {
            this.xTicksInt = GraphTools.arrayDoubleToArrayInt(xTicks);
        }
        return this;
    }

    /**
     * Sets the ticks which are displayed on the Y-axis. If graph is in double precision, will modify the double[] to
     * an int[] for setting.
     *
     * @param yTicks array of integer Y-axis values
     * @return this config instance
     */
    public DrawConfig setYTickValues(double[] yTicks) {
        if (doublePrecision) {
            this.yTicksDouble = yTicks;
        } else {
            this.yTicksInt = GraphTools.arrayDoubleToArrayInt(yTicks);
        }
        return this;
    }

    /**
     * Sets the ticks which are displayed on the X-axis. If graph is in double precision, will modify the int[] to
     * a double[] for setting.
     *
     * @param xTicks array of integer X-axis values
     * @return this config instance
     */
    public DrawConfig setXTickValues(int[] xTicks) {
        if (doublePrecision) {
            this.xTicksDouble = GraphTools.arrayIntToArrayDouble(xTicks);
        } else {
            this.xTicksInt = xTicks;
        }
        return this;
    }

    /**
     * Sets the ticks which are displayed on the Y-axis. If graph is in double precision, will modify the int[] to
     * a double[] for setting.
     *
     * @param yTicks array of integer Y-axis values
     * @return this config instance
     */
    public DrawConfig setYTickValues(int[] yTicks) {
        if (doublePrecision) {
            this.yTicksDouble = GraphTools.arrayIntToArrayDouble(yTicks);
        } else {
            this.yTicksInt = yTicks;
        }
        return this;
    }

    /**
     * Retrieves the length of the x container
     *
     * @return int representing [].length
     */
    public int getXArraySize() {
        return doublePrecision ? xTicksDouble.length : xTicksInt.length;
    }

    /**
     * Retrieves the length of the y container
     *
     * @return int representing [].length
     */
    public int getYArraySize() {
        return doublePrecision ? yTicksDouble.length : yTicksInt.length;
    }

    /**
     * Gets the active X-axis tick values as a {@code double[]} array.
     * <p>
     * If {@code doublePrecision} is enabled, returns {@code xTicksDouble}. Otherwise, returns null.
     * </p>
     *
     * @return the X-axis tick values as {@code double[]}
     */
    public double[] getDoubleXTicks() {
        if (doublePrecision) {
            return xTicksDouble != null ? xTicksDouble : new double[0];
        } else {
            return null;
        }
    }

    /**
     * Gets the active Y-axis tick values as a {@code double[]} array.
     * <p>
     * If {@code doublePrecision} is enabled, returns {@code yTicksDouble}. Otherwise, returns null.
     * </p>
     *
     * @return the Y-axis tick values as {@code double[]}
     */
    public double[] getDoubleYTicks() {
        if (doublePrecision) {
            return yTicksDouble != null ? yTicksDouble : new double[0];
        } else {
            return null;
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
            return GraphTools.arrayDoubleToArrayInt(xTicksDouble);
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
            return GraphTools.arrayDoubleToArrayInt(yTicksDouble);
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
    public DrawConfig tickColor(Color tickColor) {
        this.tickColor = tickColor;
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
