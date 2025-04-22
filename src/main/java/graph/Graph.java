package graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import util.TickMarkConfig;
import util.GraphTools;

import javax.swing.JPanel;
import java.awt.*;

/**
 * Base abstract class for graphs, extending JPanel.
 * Holds reusable graph configuration and rendering logic.
 */
@AllArgsConstructor
public abstract class Graph extends JPanel {
    @Getter
    protected TickMarkConfig tickConfig;
    @Getter
    protected Color backgroundColor;
    @Getter
    protected Color borderColor;

    @Getter
    protected int marginSize;

    @Getter
    protected double xMinVal;
    @Getter
    protected double xMaxVal;
    @Getter
    protected double yMinVal;
    @Getter
    protected double yMaxVal;

    protected boolean showGrid; // TODO LOGIC FOR GRID SHOWING
    protected boolean cropGraphToData;
    protected boolean showMarginBorder;
    protected boolean showGraphTickMarks;
    protected boolean showTickLabels;

    /**
     * Default Graph constructor initializes common fields.
     */
    public Graph() {
        this.xMinVal = Double.POSITIVE_INFINITY;
        this.yMinVal = xMinVal;
        this.xMaxVal = -xMinVal;
        this.yMaxVal = -yMinVal;
        this.tickConfig = new TickMarkConfig();
        this.marginSize = 32;
        this.borderColor = Color.WHITE;
        this.showGraphTickMarks = true;
        this.showTickLabels = true;
        this.cropGraphToData = false;
        this.showGrid = false;
        this.showMarginBorder = true;
        this.backgroundColor = Color.BLACK;
        super.setBackground(backgroundColor);
        super.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    /**
     * Sets whether the graph shows axis space outside visible data.
     *
     * @param cropGraphToData True if only relevant graph space is shown.
     * @return this instance for method chaining
     */
    public Graph cropGraphToData(boolean cropGraphToData) {
        this.cropGraphToData = cropGraphToData;
        updateTickParameters();
        return this;
    }

    /**
     * Sets whether gridlines are shown on graph.
     *
     * @param showGrid True if lines are shown, false otherwise
     * @return this instance for method chaining
     */
    public Graph setShowGridlines(boolean showGrid) {
        this.showGrid = showGrid;
        return this;
    }

    /**
     * Getter for checking whether gridlines are drawn or not
     *
     * @return True if gridlines shown, false otherwise
     */
    public boolean isShowingGridlines() {
        return showGrid;
    }

    /**
     * Sets whether the margin border around the graph area is shown
     *
     * @param showMarginBorder True if showing border, false otherwise
     * @return this instance for method chaining
     */
    public Graph setShowMarginBorder(boolean showMarginBorder) {
        this.showMarginBorder = showMarginBorder;
        return this;
    }

    /**
     * Checks if margin is being shown
     *
     * @return True if margin border is shown, false otherwise
     */
    public boolean isShowingMarginBorder() {
        return showMarginBorder;
    }

    /**
     * Sets whether tick marks should be shown on the graph.
     *
     * @param showGraphTickMarks true to show tick marks, false to hide them
     * @return this instance for method chaining
     */
    public Graph setShowGraphTickMarks(boolean showGraphTickMarks) {
        this.showGraphTickMarks = showGraphTickMarks;
        return this;
    }

    /**
     * Getter for checking whether tick marks are drawn or not.
     *
     * @return true if tick marks are shown, false otherwise
     */
    public boolean isShowingTicks() {
        return showGraphTickMarks;
    }

    /**
     * Sets whether tick labels should be shown on the graph.
     *
     * @param showTickLabels true to show tick labels, false otherwise
     * @return this instance for method chaining
     */
    public Graph setShowTickLabels(boolean showTickLabels) {
        this.showTickLabels = showTickLabels;
        return this;
    }

    /**
     * Sets the font used to render tick mark labels. Loosely overloads JPanel's setFont. JPanel.setFont is called
     * before returning this instance. Solely exists for chain setting.
     *
     * @param graphFont Font to set characters of the graph to
     * @return this instance for method chaining
     */
    public Graph setGraphFont(Font graphFont) {
        super.setFont(graphFont);
        return this;
    }

    /**
     * Getter for checking whether tick labels are drawn or not.
     *
     * @return true if tick labels are shown, false otherwise
     */
    public boolean isShowingTickLabels() {
        return showTickLabels;
    }


    /**
     * Sets the TickMarkConfig used for tick rendering.
     *
     * @param config configuration for tick marks
     * @return this instance for method chaining
     */
    public Graph setTickConfig(TickMarkConfig config) {
        this.tickConfig = config;
        return this;
    }

    /**
     * Sets the margin size around the graph area.
     *
     * @param marginSize margin size in pixels
     * @return this instance for method chaining
     */
    public Graph setMarginSize(int marginSize) {
        this.marginSize = marginSize;
        return this;
    }

    /**
     * Boolean checker to see if user has activated data cropping.
     *
     * @return True if graph should only show used graph space. False otherwise.
     */
    public boolean isCroppedToData() {
        return cropGraphToData;
    }

    /**
     * Sets the background color for the graph area.
     *
     * @param backgroundColor background fill color
     * @return this instance for method chaining
     */
    public Graph setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * Sets the border color around the graph margin.
     *
     * @param borderColor border stroke color
     * @return this instance for method chaining
     */
    public Graph setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    /**
     * Sets whether double precision formatting should be used for ticks.
     *
     * @param doublePrecision true to enable double precision
     * @return this instance for method chaining
     */
    public Graph setDoublePrecision(boolean doublePrecision) {
        this.tickConfig.setDoublePrecision(doublePrecision);
        updateTickParameters();
        return this;
    }

    /**
     * Helper function which updates delta values for graph ticks.
     */
    protected void updateTickParameters() {
        int graphWidth = getWidth() - 2 * marginSize;
        int graphHeight = getHeight() - 2 * marginSize;
        double visibleRangeX;
        double visibleRangeY;
        if (cropGraphToData) {
            visibleRangeX = Math.max(1e-10, xMaxVal - xMinVal);
            visibleRangeY = Math.max(1e-10, yMaxVal - yMinVal);
        } else { // If it isn't cropped, we simply set the distance between ticks to be height / numticks
            visibleRangeX = tickConfig.getIntXTicks().length - 1;
            visibleRangeY = tickConfig.getIntYTicks().length - 1;
        }
        double newDeltaX = (double) graphWidth / visibleRangeX;
        double newDeltaY = (double) graphHeight / visibleRangeY;
        tickConfig.setDeltaX(newDeltaX);
        tickConfig.setDeltaY(newDeltaY);
        repaint();
    }

    /**
     * Verifies that Margin is a viable size given the size of possible tick labels
     */
    protected void verifyMarginUse(Graphics2D g2) {
        // width testing
        String label = String.format("%.2f", Math.max(xMaxVal, yMaxVal));
        FontMetrics fm = g2.getFontMetrics();
        int labelWidth = fm.stringWidth(label);

        // height testing
        int fmAscent = fm.getAscent();

    }

    /**
     * Core rendering method called by the Swing framework.
     * <p>
     * This method first clears the panel, then draws the background, border, and axis tick marks,
     * and finally delegates the graph-specific data rendering to the subclass implementation
     * of {@link #paintGraphData(Graphics2D)}.
     * </p>
     *
     * <p>
     * Subclasses should not override this method directly; they should instead implement
     * {@link #paintGraphData(Graphics2D)} to define how their specific graph data should be drawn.
     * </p>
     *
     * @param g The Graphics context to use for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (showGraphTickMarks) {
            GraphTools.drawTicks(g2, this);
        }
        if (showMarginBorder) {
            GraphTools.drawMargin(g2, this);
        }

        paintGraphData(g2);
    }

    /**
     * Abstract method that subclasses must implement to define how the graph-specific data
     * (such as lines, bars, scatter points, etc.) should be drawn.
     * <p>
     * This method is called automatically after the margin, background, and tick marks
     * have been rendered by {@link #paintComponent(Graphics)}.
     * </p>
     *
     * @param g2     The Graphics2D context configured with antialiasing
     */
    protected abstract void paintGraphData(Graphics2D g2);
}
