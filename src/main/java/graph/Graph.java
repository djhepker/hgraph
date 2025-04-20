package graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import util.TickMarkConfig;
import util.GraphTools;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Base abstract class for graphs, extending JPanel.
 * Holds reusable graph configuration and rendering logic.
 */
@AllArgsConstructor
public abstract class Graph extends JPanel {
    @Getter
    protected TickMarkConfig tickConfig;
    @Getter
    protected int marginSize;
    @Getter
    protected Color backgroundColor;
    @Getter
    protected Color borderColor;
    @Getter
    protected double scrollXo;
    @Getter
    protected double scrollYo;
    @Getter
    protected double scrollXf;
    @Getter
    protected double scrollYf;
    protected boolean cropGraphToData;

    /**
     * Default Graph constructor initializes common fields.
     */
    public Graph() {
        this.tickConfig = new TickMarkConfig();
        this.marginSize = 24;
        this.backgroundColor = Color.BLACK;
        this.borderColor = Color.WHITE;
        this.cropGraphToData = false;
    }

    /**
     * Sets whether the graph shows axis space outside visible data.
     *
     * @param cropGraphToData True if only relevant graph space is shown.
     * @return this instance for method chaining
     */
    public Graph cropGraphToData(boolean cropGraphToData) {
        this.cropGraphToData = cropGraphToData;
        return this;
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
        return this;
    }

    /**
     * Paints the graph background and tick marks.
     * Subclasses should call this before drawing specific data.
     *
     * @param g2 the Graphics2D context
     */
    protected void paintMarginAndTicks(Graphics2D g2) {
        GraphTools.drawMargin(g2, this);
        GraphTools.drawTicks(g2, this);
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

        paintMarginAndTicks(g2);

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
