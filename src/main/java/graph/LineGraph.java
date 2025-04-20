package graph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import util.CircularPointBuffer;
import util.TickMarkConfig;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.util.Collection;

/**
 * Logic for creating a JPanel LineGraph
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public final class LineGraph extends Graph {
    @Getter(AccessLevel.NONE)
    private final CircularPointBuffer circularPointBuffer;

    private float lineThickness;

    private Color lineColor;

    private double minXValue;
    private double maxXValue;
    private double minYValue;
    private double maxYValue;

    /**
     * Default constructor initializing default values and an empty data queue
     */
    public LineGraph() {
        super();
        this.circularPointBuffer = new CircularPointBuffer(100);
        this.lineThickness = 2.0f;
        this.lineColor = Color.GREEN;
        this.maxXValue = Double.NEGATIVE_INFINITY;
        this.minXValue = -maxXValue;
        this.maxYValue = maxXValue;
        this.minYValue = -maxYValue;

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateTickParameters();
            }
        });
    }

    /**
     * Constructs a LineGraph with initial data points.
     *
     * @param initialData Collection of Point2D.Double points to initialize graph data
     */
    public LineGraph(Collection<Point2D.Double> initialData) {
        this();
        this.addAll(initialData);
    }

    /**
     * Constructs a LineGraph with a custom TickMarkConfig.
     *
     * @param config TickMarkConfig to configure axis ticks
     */
    public LineGraph(TickMarkConfig config) {
        this();
        this.tickConfig = config;
    }

    /**
     * Constructs a LineGraph with a custom TickMarkConfig and initial data points.
     *
     * @param config TickMarkConfig to configure axis ticks
     * @param initialData Collection of Point2D.Double points to initialize graph data
     */
    public LineGraph(TickMarkConfig config, Collection<Point2D.Double> initialData) {
        this(config);
        this.addAll(initialData);
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
        if (yData > maxYValue) {
            maxYValue = yData;
        }
        if (yData < minYValue) {
            minYValue = yData;
        }
        if (xData > maxXValue) {
            maxXValue = xData;
        }
        if (xData < minXValue) {
            minXValue = xData;
        }
        circularPointBuffer.add(newData);
        return this;
    }

    /**
     * Adds data to be utilized by graph.
     *
     * @param xData Data to be stored for use by Graph
     * @param yData Data to be stored for use by Graph
     */
    public LineGraph insertData(double xData, double yData) {
        return this.insertData(new Point2D.Double(xData, yData));
    }

    /**
     * Getter for the size of dataPoint2Ds
     *
     * @return Size of queued data
     */
    public int getDataSize() {
        return circularPointBuffer.size();
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
        tickConfig.setXTickValues(xAxis);
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
        tickConfig.setXTickValues(xAxis);
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
        tickConfig.setYTickValues(yAxis);
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
        tickConfig.setYTickValues(yAxis);
    }

    /**
     * Sets the thickness of chart lines
     * @param lineThickness Thickness of chart lines in pixels
     * @return Instance of class for chain setting
     */
    public LineGraph setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
        return this;
    }

    /**
     * Sets the color of the graph line
     * @param lineColor Color for the data line
     * @return Instance of class for chain setting
     */
    public LineGraph setLineColor(Color lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    /**
     * Override function for graph cropping. Sets the scroll parameters appropriately for cropped data. Updates
     * cropGraphToData to user input.
     *
     * @param cropGraphToData True if only relevant graph space is shown.
     * @return Instance of class for chain setting.
     */
    @Override
    public Graph cropGraphToData(boolean cropGraphToData) {
        if (cropGraphToData) {
            if (Double.isFinite(minXValue) && Double.isFinite(maxXValue)
                    && Double.isFinite(minYValue) && Double.isFinite(maxYValue)) {

                scrollXo = minXValue;
                scrollYo = minYValue;
                scrollXf = maxXValue;
                scrollYf = maxYValue;
                System.out.printf("minXValue = %.2f, maxXValue = %.2f%n", minXValue, maxXValue);
                System.out.printf("minYValue = %.2f, maxYValue = %.2f%n", minYValue, maxYValue);
            } else {
                scrollXo = 0.0;
                scrollYo = 0.0;
                scrollXf = 10.0;
                scrollYf = 10.0;
            }
        }
        return super.cropGraphToData(cropGraphToData);
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
        if (circularPointBuffer.isEmpty()) {
            return;
        }

        g2.setStroke(new BasicStroke(lineThickness));
        g2.setColor(lineColor);

        boolean postStart = false;
        int prevX = 0;
        int prevY = 0;

        for (Point2D.Double point : circularPointBuffer) {
            int x = (int) (marginSize + (point.getX() * tickConfig.getDeltaX()));
            int y = (int) (getHeight() - (marginSize + (point.getY() * tickConfig.getDeltaY())));

            if (postStart) {
                g2.drawLine(prevX, prevY, x, y);
            } else {
                postStart = true;
            }
            prevX = x;
            prevY = y;
        }
    }

    /**
     * Helper function which updates delta values for graph ticks.
     */
    private void updateTickParameters() {
        int graphWidth = getWidth() - 2 * marginSize;
        int graphHeight = getHeight() - 2 * marginSize;

        double visibleRangeX = Math.max(1e-10, scrollXf);
        double visibleRangeY = Math.max(1e-10, scrollYf);

        tickConfig.setDeltaX((double) graphWidth / visibleRangeX);
        tickConfig.setDeltaY((double) graphHeight / visibleRangeY);
    }
}
