package graph;

import lombok.Getter;
import util.DrawConfig;
import util.GraphTools;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import static graph.Graph.GraphState.*;

/**
 * Base abstract class for graphs, extending JPanel.
 * Holds reusable graph configuration and rendering logic.
 */
public abstract class Graph extends JPanel {
    private GraphState graphState;

    @Getter
    protected DrawConfig drawConfig;

    @Getter
    protected double xMinVal;
    @Getter
    protected double xMaxVal;
    @Getter
    protected double yMinVal;
    @Getter
    protected double yMaxVal;

    protected boolean cropGraphToData;

    /**
     * Protected constructor prevents instantiation outside of this package when not explicitly extending.
     */
    protected Graph() {
        this.graphState = NEUTRAL;
        this.xMinVal = Double.POSITIVE_INFINITY;
        this.yMinVal = xMinVal;
        this.xMaxVal = -xMinVal;
        this.yMaxVal = -yMinVal;
        this.drawConfig = new DrawConfig();

        super.setBackground(drawConfig.getBackgroundColor());
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
     * Sets the TickMarkConfig used for tick rendering.
     *
     * @param config configuration for tick marks
     * @return this instance for method chaining
     */
    public Graph setDrawConfig(DrawConfig config) {
        this.drawConfig = config;
        updateTickParameters();
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
     * Sets whether double precision formatting should be used for ticks.
     *
     * @param doublePrecision true to enable double precision
     * @return this instance for method chaining
     */
    public Graph setDoublePrecision(boolean doublePrecision) {
        this.drawConfig.setDoublePrecision(doublePrecision);
        updateTickParameters();
        return this;
    }

    /**
     * Retrieves aspect ratio of the current graph inside this instance of JPanel.
     *
     * @return Height-to-width aspect ratio of the given graph.
     */
    public double getAspectRatio() {
        int usableWidth = getWidth() - 2 * marginSize;
        int usableHeight = getHeight() - 2 * marginSize;
        if (usableWidth <= 0 || usableHeight <= 0) {
            return 1.0;
        }
        return (double) usableWidth / usableHeight;
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
            visibleRangeX = drawConfig.getIntXTicks().length - 1;
            visibleRangeY = drawConfig.getIntYTicks().length - 1;
        }
        double newDeltaX = (double) graphWidth / visibleRangeX;
        double newDeltaY = (double) graphHeight / visibleRangeY;
        drawConfig.setxCoordinateObjectDelta(newDeltaX);
        drawConfig.setyCoordinateObjectDelta(newDeltaY);
        repaint();
    }

    /**
     * Verifies that Margin is a viable size given the size of possible tick labels.
     * Dynamically adjusts marginSize larger or smaller as needed.
     *
     * @param fm FontMetrics used to verify size against graph parameters
     */
    protected void verifyMarginToLabelScale(FontMetrics fm) {
        int halfTickLength = (int) (drawConfig.getTickLength() * 0.5);
        // Width check (for x-axis labels)
        int labelWidth = fm.stringWidth(GraphTools.doubleToString(Math.max(Math.abs(xMaxVal), Math.abs(yMaxVal))));
        int widthRequirement = (labelWidth * 4 + 2) / 3 + halfTickLength;
        // Height check (for y-axis labels)
        int labelHeight = fm.getAscent() + fm.getDescent();
        int heightRequirement = (labelHeight * 4 + 2) / 3 + halfTickLength;
        int requiredMargin = Math.max(widthRequirement, heightRequirement);
        if (Math.abs(requiredMargin - marginSize) > 1) { // 1 pixel threshold for safety
            this.marginSize = requiredMargin;
        }
    }

    /**
     * Helper ensures Label is drawn with correct settings.
     *
     * @param g2 Obtained from repaint().
     * @param str Label being drawn.
     * @param x Beginning x coordinate of the drawn.
     * @param y Top portion of the String being drawn.
     */
    public void drawTickLabel(Graphics2D g2, String str, int x, int y) {
        if (graphState != DRAW_LABELS) {
            g2.setColor(drawConfig.getTickLabelColor());
            graphState = DRAW_LABELS;
        }
        g2.drawString(str, x, y);
    }

    /**
     * Draws a tick line with the correct configurations
     *
     * @param g2 Obtained from repaint().
     * @param x1 Beginning x coordinate of our line.
     * @param y1 Beginning y coordinate of our line.
     * @param x2 Ending x coordinate of our line.
     * @param y2 Ending y coordinate of our line.
     */
    public void drawTickLine(Graphics2D g2, int x1, int y1, int x2, int y2) {
        if (graphState != DRAW_TICKS) {
            g2.setColor(drawConfig.getTickColor());
            graphState = DRAW_TICKS;
        }
        g2.drawLine(x1, y1, x2, y2);
    }

    /**
     * Draws a single grid line across the graph.
     *
     * @param g2 Obtained from repaint().
     * @param x1 Beginning x coordinate of our line.
     * @param y1 Beginning y coordinate of our line.
     * @param x2 Ending x coordinate of our line.
     * @param y2 Ending y coordinate of our line.
     */
    public void drawGridLine(Graphics2D g2, int x1, int y1, int x2, int y2) {
        if (graphState != DRAW_GRID) {
            g2.setColor(drawConfig.getGridColor());
            graphState = DRAW_GRID;
        }
        g2.drawLine(x1, y1, x2, y2);
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
            if (showTickLabels) {
                verifyMarginToLabelScale(g2.getFontMetrics());
            }
            GraphTools.drawTicks(g2, this);
        }
        if (showMarginBorder) {
            GraphTools.drawMargin(g2, this);
        }
        paintGraphData(g2);
        graphState = NEUTRAL;
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

    protected enum GraphState {
        NEUTRAL,
        DRAW_GRID,
        DRAW_TICKS,
        DRAW_LABELS,
    }
}
