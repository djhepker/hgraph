package graph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import util.CircularPointBuffer;
import util.GraphTools;
import util.TickMarkConfig;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Logic for creating a JPanel LineGraph
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public final class LineGraph extends JPanel {
    @Getter(AccessLevel.NONE)
    private final CircularPointBuffer circularPointBuffer;

    private TickMarkConfig tickMarkConfig;

    private float lineThickness;
    private int marginSize;

    private double minXValue;
    private double maxXValue;
    private double minYValue;
    private double maxYValue;

    private Color lineColor;
    private Color backgroundColor;
    private Color borderColor;

    /**
     * Default constructor initializing default values and an empty data queue
     */
    public LineGraph() {
        this.circularPointBuffer = new CircularPointBuffer(100);
        this.tickMarkConfig = new TickMarkConfig();
        this.lineThickness = 2.0f;
        this.marginSize = 24;
        this.lineColor = Color.GREEN;
        this.backgroundColor = new Color(0, 0, 0);
        this.maxYValue = Double.MIN_VALUE;
        this.minYValue = -maxYValue;
        this.maxXValue = maxYValue;
        this.minXValue = minYValue;
        this.borderColor = Color.LIGHT_GRAY;
    }

    /**
     * Parameterized constructor for when you already have a dataset.
     * @param dataX Iterable object, made to accept numerous different data containers to fill dataPoint2Ds with
     * @param dataY Iterable object, made to accept numerous different data containers to fill dataPoint2Ds with
     */
    public LineGraph(Iterable<Double> dataX, Iterable<Double> dataY) {
        this();
        this.addAll(dataX, dataY);
    }

    public LineGraph(Collection<Point2D.Double> initialData) {
        this();
        this.addAll(initialData);
    }

    /**
     * Adds a dataset to the LineGraph using separate X and Y iterables.
     * Internally pairs each (x, y) and forwards the combined points to the main addAll method.
     *
     * @param dataX Iterable object containing X-axis values
     * @param dataY Iterable object containing Y-axis values
     * @return This LineGraph instance for method chaining
     */
    public LineGraph addAll(Iterable<Double> dataX, Iterable<Double> dataY) {
        var iterX = dataX.iterator();
        var iterY = dataY.iterator();

        ArrayList<Point2D.Double> points = new ArrayList<>();
        while (iterX.hasNext() && iterY.hasNext()) {
            double x = iterX.next();
            double y = iterY.next();
            points.add(new Point2D.Double(x, y));
        }
        circularPointBuffer.addAll(points);
        return this;
    }

    /**
     * Adds a dataset to the LineGraph from an Iterable of Point2D.Double objects.
     * Each point's X and Y values are inserted into the internal CircularPointBuffer.
     *
     * @param dataIterable Iterable collection of Point2D.Double objects to be added
     * @return This LineGraph instance for method chaining
     */
    public LineGraph addAll(Collection<Point2D.Double> dataIterable) {
        circularPointBuffer.addAll(dataIterable);
        return this;
    }

    /**
     * Adds data to be utilized by graph.
     *
     * @param xData Data to be stored for use by Graph
     * @param yData Data to be stored for use by Graph
     */
    public LineGraph insertData(double xData, double yData) {
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
        return this.insertData(new Point2D.Double(xData, yData));
    }

    /**
     * Method inserting graph vertex into buffer.
     *
     * @param newData Point2D.Double to be inserted into the graph.
     * @return This LineGraph instance for method chaining
     */
    public LineGraph insertData(Point2D.Double newData) {
        circularPointBuffer.add(newData);
        return this;
    }

    /**
     * Sets TicMarkConfig for graph
     * @param config Configuration options for ticks on the graph
     * @return Instance of class for chain setting
     */
    public LineGraph setTickMarkConfig(TickMarkConfig config) {
        this.tickMarkConfig = config;
        return this;
    }

    /**
     * Sets the thickness of chart lines
     * @param lineThickness Thickness of chart lines in pixels
     * @return Instance of class for chain setting
     */
    public LineGraph setlineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
        return this;
    }

    /**
     * Sets the size of the graph margin
     * @param marginSize Size of the margin in pixels
     * @return Instance of class for chain setting
     */
    public LineGraph setMarginSize(int marginSize) {
        this.marginSize = marginSize;
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
     * Sets the background color of the graph
     * @param backgroundColor Background color of the graph
     * @return Instance of class for chain setting
     */
    public LineGraph setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * Sets the color of the graph border
     * @param borderColor Border color of the graph
     * @return Instance of class for chain setting
     */
    public LineGraph setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    /**
     * Getter for the size of dataPoint2Ds
     *
     * @return Size of queued data
     */
    public int getDataSize() {
        return circularPointBuffer.size();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (circularPointBuffer.isEmpty()) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(lineThickness));

        GraphTools.drawMargin(g2, marginSize, width, height, backgroundColor, borderColor);
        GraphTools.drawTicks(g2, tickMarkConfig, marginSize, width, height);

        g2.setColor(lineColor);

        int graphWidth = width - 2 * marginSize;
        int graphHeight = height - 2 * marginSize;

        double rangeX = maxXValue - minXValue;
        double rangeY = maxYValue - minYValue;

        if (rangeX == 0) {
            rangeX = 1;
        }
        if (rangeY == 0) {
            rangeY = 1;
        }

        int prevX = -1;
        int prevY = -1;

        for (Point2D.Double point : circularPointBuffer) {
            int x = (int) (marginSize + ((point.getX() - minXValue) / rangeX) * graphWidth);
            int y = (int) (marginSize + graphHeight - ((point.getY() - minYValue) / rangeY) * graphHeight);

            if (prevX != -1 && prevY != -1) {
                g2.drawLine(prevX, prevY, x, y);
            }

            prevX = x;
            prevY = y;
        }
    }
}