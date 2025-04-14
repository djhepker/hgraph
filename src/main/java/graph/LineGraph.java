package graph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import util.GraphTools;
import util.TickMarkConfig;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Logic for creating a JPanel LineGraph
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public final class LineGraph extends JPanel {
    @Getter(AccessLevel.NONE)
    private final Deque<Double> dataPoints;

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
        this.dataPoints = new ArrayDeque<>();
        this.tickMarkConfig = new TickMarkConfig();
        this.lineThickness = 2.0f;
        this.marginSize = 24;
        this.lineColor = Color.GREEN;
        this.backgroundColor = new Color(0, 0, 0);
        this.minValue = Double.MAX_VALUE;
        this.maxValue = Double.MIN_VALUE;
        this.borderColor = Color.LIGHT_GRAY;
    }

    /**
     * Parameterized constructor for when you already have a dataset.
     * @param data Iterable object, made to accept numerous different data containers to fill dataPoints with
     */
    public LineGraph(Iterable<Double> data) {
        this();
        for (double value : data) {
            insertDataPoint(value);
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
     * Adds data to the end of queue
     *
     * @param data Value to be added to dataPoints
     */
    public LineGraph insertDataPoint(double data) {
        dataPoints.add(data);
        if (data > maxValue) {
            maxValue = data;
        }
        if (data < minValue) {
            minValue = data;
        }
        return this;
    }

    /**
     * Getter for the size of dataPoints
     * @return Size of queued data
     */
    public int getDataSize() {
        return dataPoints.size();
    }

    /**
     * Automatically called by repaint()
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dataPoints.isEmpty()) {
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
        double xStep = (double) graphWidth / (dataPoints.size() - 1);
        int i = 0;
        int prevX = -1, prevY = -1;
        for (double value : dataPoints) {
            int x = (int) (marginSize + i * xStep);
            int y = (int) (marginSize + graphHeight - ((value - minValue) / rangeY) * graphHeight);
            if (i > 0) {
                g2.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
            i++;
        }
    }
}