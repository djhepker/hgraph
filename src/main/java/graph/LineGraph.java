package graph;

import lombok.AccessLevel;
import lombok.Getter;
import util.CircularPointBuffer;
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
public final class LineGraph extends Graph {
    @Getter(AccessLevel.NONE) private final CircularPointBuffer dataBuffer;

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

        dataBuffer = new CircularPointBuffer(100);

        edgeThickness = 2.0f;
        edgeColor = Color.GREEN;

        edgeColor = config.getEdgeColor();
        edgeThickness = config.getEdgeThickness();
    }

    /**
     * Adds a dataset to the LineGraph from an Iterable of Point2D.Double objects.
     * Each point's X and Y values are inserted into the internal CircularPointBuffer.
     *
     * @param dataIterable Iterable collection of Point2D.Double objects to be added
     * @return This LineGraph instance for method chaining
     */
    public LineGraph addAll(Collection<Point2D.Double> dataIterable) {
        for (Point2D.Double p : dataIterable) {
            this.insertData(p);
        }
        return this;
    }

    /**
     * Method inserting graph vertex into buffer.
     *
     * @param newData Point2D.Double to be inserted into the graph.
     * @return This LineGraph instance for method chaining
     */
    public LineGraph insertData(Point2D.Double newData) {
        double xData = newData.getX();
        double yData = newData.getY();
        if (yData > yMaxVal) {
            yMaxVal = yData;
        }
        if (yData < yMinVal) {
            yMinVal = yData;
        }
        if (xData > xMaxVal) {
            xMaxVal = xData;
        }
        if (xData < xMinVal) {
            xMinVal = xData;
        }
        dataBuffer.add(newData);
        return this;
    }

    /**
     * Adds data to be utilized by graph.
     *
     * @param x Data to be stored for use by Graph
     * @param y Data to be stored for use by Graph
     */
    public LineGraph insertData(double x, double y) {
        return this.insertData(new Point2D.Double(x, y));
    }

    /**
     * Getter for the size of dataPoint2Ds
     *
     * @return Size of queued data
     */
    public int getDataSize() {
        return dataBuffer.size();
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
     * Override function for graph cropping. Updates argCropToData setting
     * and refreshes tick scaling based on current data bounds.
     *
     * @param argCropToData True if only relevant graph space is shown.
     * @return Instance of class for chain setting.
     */
    @Override
    public LineGraph cropData(boolean argCropToData) {
        cropGraphToData = argCropToData;
        updateTickParameters();
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
        if (dataBuffer.isEmpty()) {
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
