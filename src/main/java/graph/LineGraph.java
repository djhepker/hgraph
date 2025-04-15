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
import java.awt.geom.Point2D.Double;

/**
 * Logic for creating a JPanel LineGraph
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public final class LineGraph extends JPanel {
    @Getter(AccessLevel.NONE)
    private final CircularPointBuffer CircularPointBuffer;

    private TickMarkConfig tickMarkConfig;

    private float lineThickness;
    private int marginSize;
    private double minValue;
    private double maxValue;

    private Color lineColor;
    private Color backgroundColor;
    private Color borderColor;

    /**
     * Default constructor initializing default values and an empty data queue
     */
    public LineGraph() {
        this.CircularPointBuffer = new CircularPointBuffer(100);
        this.tickMarkConfig = new TickMarkConfig();
        this.lineThickness = 2.0f;
        this.marginSize = 24;
        this.lineColor = Color.GREEN;
        this.backgroundColor = new Color(0, 0, 0);
        this.maxValue = java.lang.Double.MIN_VALUE;
        this.minValue = -maxValue;
        this.borderColor = Color.LIGHT_GRAY;
    }

    /**
     * Parameterized constructor for when you already have a dataset.
     * @param data Iterable object, made to accept numerous different data containers to fill dataPoint2Ds with
     */
    public LineGraph(Iterable<Double> data) {
        this();
        for (Double value : data) {
            insertData(value);
        }
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
     * Adds data to be utilized by graph.
     *
     * @param xData Data to be stored for use by Graph
     * @param yData Data to be stored for use by Graph
     */
    public LineGraph insertData(double xData, double yData) {
        Double p = new Double(xData, yData);
        CircularPointBuffer.add(p);
        if (yData > maxValue) {
            maxValue = yData;
        }
        if (yData < minValue) {
            minValue = yData;
        }
        return this;
    }

    /**
     * Adds data to be utilized by graph.
     *
     * @param data Data to be stored for use by Graph
     */
    public LineGraph insertData(Double data) {
        CircularPointBuffer.add(data);
        double y = data.getY();
        if (y > maxValue) {
            maxValue = y;
        }
        if (y < minValue) {
            minValue = y;
        }
        return this;
    }

    /**
     * Getter for the size of dataPoint2Ds
     *
     * @return Size of queued data
     */
    public int getDataSize() {
        return CircularPointBuffer.size();
    }

    /**
     * Automatically called by repaint()
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (CircularPointBuffer.isEmpty()) {
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
        double rangeY = maxValue - minValue;
        if (rangeY == 0) {
            rangeY = 1;
        }
        double dx = (double) graphWidth / (CircularPointBuffer.size() - 1);
        double dy = (double) graphHeight / (CircularPointBuffer.size() - 1);

        int i = 0;
        int prevX = -1, prevY = -1;
        for (Double value : CircularPointBuffer) {
            int x = (int) (marginSize + i * dx);
            int y = (int) (marginSize + graphHeight - ((value.getY() - minValue) / rangeY) * graphHeight);
            if (i > 0) {
                g2.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
            ++i;
        }
    }
}