package graph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Logic for creating a JPanel LineGraph
 */
public final class LineGraph extends JPanel {
    private Deque<Double> dataPoints;

    /**
     * Parameterized constructor for when you already have a dataset.
     * @param data Iterable object, made to accept numerous different data containers to fill dataPoints with
     */
    public LineGraph(Iterable<Double> data) {
        this.dataPoints = new ArrayDeque<>();
        for (double i : data) {
            this.dataPoints.add(i);
        }
    }

    /**
     * Adds data to the end of queue
     * @param data Value to be added to dataPoints
     */
    public void insertDataPoint(double data) {
        dataPoints.add(data);
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
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int graphWidth = width - 2 * padding;
        int graphHeight = height - 2 * padding;
        double minY = dataPoints.stream().min(Double::compare).orElse(0.0);
        double maxY = dataPoints.stream().max(Double::compare).orElse(1.0);
        double rangeY = maxY - minY;
        if (rangeY == 0) {
            rangeY = 1;
        }
        int size = dataPoints.size();
        double xStep = (double) graphWidth / (size - 1);
        int i = 0;
        int prevX = -1, prevY = -1;
        for (double value : dataPoints) {
            int x = (int) (padding + i * xStep);
            int y = (int) (padding + graphHeight - ((value - minY) / rangeY) * graphHeight);
            if (i > 0) {
                g2.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
            i++;
        }
    }
}