package graph;

import lombok.Getter;
import util.DrawConfig;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collection;

/**
 * Logic for creating a JPanel LineGraph
 */
@Getter
public final class LineGraph extends Graph implements XYGraph {

    private Color edgeColor;

    private float edgeThickness;

    /**
     * Constructs a LineGraph with a custom TickMarkConfig and initial data points.
     *
     * @param config TickMarkConfig to configure axis ticks
     * @param initialData Collection of Point2D.Double points to initialize graph data
     */
    public LineGraph(DrawConfig config, Collection<Point2D.Double> initialData) {
        this(config);
        addAll(initialData);
    }

    /**
     * Default constructor initializing default values and an empty data queue
     */
    public LineGraph() {
        this(new DrawConfig());
    }

    /**
     * Constructs a LineGraph with initial data points.
     *
     * @param initialData Collection of Point2D.Double points to initialize graph data
     */
    public LineGraph(Collection<Point2D.Double> initialData) {
        this(new DrawConfig(), initialData);
    }

    /**
     * Constructs a LineGraph with a custom DrawConfig.
     *
     * @param config Drawing configuration used when creating the user's display.
     */
    public LineGraph(DrawConfig config) {
        super(config);

        edgeThickness = 2.0f;
        edgeColor = Color.GREEN;
    }

    /**
     * Sets the X-axis tick values using an array of integers.
     * <p>
     * Passing an integer array will disable double-precision mode for the X-axis ticks.
     * </p>
     *
     * @param xAxis the array of integer values to use for X-axis tick marks
     */
    public void setXAxis(int[] xAxis) {
        drawConfig.setXTickValues(xAxis);
    }

    /**
     * Sets the X-axis tick values using an array of doubles.
     * <p>
     * Passing a double array will enable double-precision mode for the X-axis ticks.
     * </p>
     *
     * @param xAxis the array of double values to use for X-axis tick marks
     */
    public void setXAxis(double[] xAxis) {
        drawConfig.setXTickValues(xAxis);
    }

    /**
     * Sets the Y-axis tick values using an array of integers.
     * <p>
     * Passing an integer array will disable double-precision mode for the Y-axis ticks.
     * </p>
     *
     * @param yAxis the array of integer values to use for Y-axis tick marks
     */
    public void setYAxis(int[] yAxis) {
        drawConfig.setYTickValues(yAxis);
    }

    /**
     * Sets the Y-axis tick values using an array of doubles.
     * <p>
     * Passing a double array will enable double-precision mode for the Y-axis ticks.
     * </p>
     *
     * @param yAxis the array of double values to use for Y-axis tick marks
     */
    public void setYAxis(double[] yAxis) {
        drawConfig.setYTickValues(yAxis);
    }

    /**
     * Sets the thickness of chart lines
     * @param edgeThickness Thickness of chart lines in pixels
     * @return Instance of class for chain setting
     */
    public LineGraph setEdgeThickness(float edgeThickness) {
        this.edgeThickness = edgeThickness;
        return this;
    }

    /**
     * Sets the color of the graph line
     * @param edgeColor Color for the data line
     * @return Instance of class for chain setting
     */
    public LineGraph setEdgeColor(Color edgeColor) {
        this.edgeColor = edgeColor;
        return this;
    }

    /**
     * Override function for graph cropping. Updates dataCropped setting
     * and refreshes tick scaling based on current data bounds.
     *
     * @param dataCropped True if only relevant graph space is shown.
     * @return Instance of class for chain setting.
     */
    @Override
    public LineGraph cropData(boolean dataCropped) {
        cropGraphToData = dataCropped;
        updateTickParameters();
        return this;
    }

    @Override
    public LineGraph insertData(Point2D.Double point) {
        super.insertData(point);
        return this;
    }

    @Override
    public LineGraph insertData(double x, double y) {
        super.insertData(x, y);
        return this;
    }

    /**
     * Renders the graph-specific data for a LineGraph.
     * <p>
     * This method draws lines connecting each sequential pair of (x, y) points
     * from the internal CircularPointBuffer. It uses the configured line color
     * and line thickness for rendering.
     * </p>
     *
     * @param g2 Graphics2D context already set up with antialiasing
     */
    @Override
    protected void paintGraphData(Graphics2D g2) {
        if (dataEmpty()) {
            return;
        }

        g2.setStroke(new BasicStroke(edgeThickness));
        g2.setColor(edgeColor);

        boolean postStart = false;
        int prevX = 0;
        int prevY = 0;
        double xDelta = drawConfig.getXPixelsDelta();
        double yDelta = drawConfig.getYPixelsDelta();
        int marginSize = drawConfig.getMarginSize();

        for (Point2D.Double point : dataBuffer) {
            int x;
            int y;
            if (cropGraphToData) {
                x = (int) (marginSize + ((point.getX() - xMinVal) * xDelta));
                y = (int) (getHeight() - (marginSize + ((point.getY() - yMinVal) * yDelta)));
            } else {
                x = (int) (marginSize + (point.getX() * xDelta));
                y = (int) (getHeight() - (marginSize + (point.getY() * yDelta)));
            }
            if (postStart) {
                g2.drawLine(prevX, prevY, x, y);
            } else {
                postStart = true;
            }
            prevX = x;
            prevY = y;
        }
    }
}
