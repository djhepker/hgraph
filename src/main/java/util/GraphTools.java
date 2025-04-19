package util;

import graph.Graph;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D.Double;

/**
 * Utility class providing common rendering functions for graphs such as margin drawing and tick mark rendering.
 */
public final class GraphTools {

    // Prevent instantiation
    private GraphTools() {}

    /**
     * Converts an array of integers into an array of doubles by copying each element.
     * <p>
     * If the input array is {@code null} or empty, returns an empty {@code double[]} array.
     * </p>
     *
     * @param intArray the array of integers to convert
     * @return a new array of doubles containing the same values as the input array
     */
    public static double[] intArrayToDoubleArray(int[] intArray) {
        if (intArray == null || intArray.length == 0) {
            return new double[0];
        }
        double[] doubleArray = new double[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            doubleArray[i] = intArray[i];
        }
        return doubleArray;
    }

    /**
     * Converts an array of doubles into an array of integers by truncating each element.
     * <p>
     * Each {@code double} value is cast to an {@code int}, discarding any decimal portion.
     * If the input array is {@code null} or empty, returns an empty {@code int[]} array.
     * </p>
     *
     * @param doubleArr the array of doubles to convert
     * @return a new array of integers containing the truncated values of the input
     */
    public static int[] doubleArrayToIntArray(double[] doubleArr) {
        if (doubleArr == null || doubleArr.length == 0) {
            return new int[0];
        }

        int[] intArray = new int[doubleArr.length];
        for (int i = 0; i < doubleArr.length; i++) {
            intArray[i] = (int) doubleArr[i];
        }
        return intArray;
    }

    /**
     * Fast checker method for checking if coordinates match a Point2D.Double.
     *
     * @param x x value to be evaluated.
     * @param y y value to be evaluated.
     * @param target Point2D.Double to be compared against.
     * @return True if a match, false otherwise.
     */
    public static boolean matchesPoint(double x, double y, Double target) {
        return java.lang.Double.compare(x, target.getX()) == 0 && java.lang.Double.compare(y, target.getY()) == 0;
    }

    /**
     * Draws a margin area with a background fill and a rectangular border inside a JPanel.
     *
     * @param g2 The graphics context to draw with
     * @param graph The graph we are drawing in effect. Holds relevant parameter variables
     */
    public static void drawMargin(Graphics2D g2, Graph graph) {
        int graphWidth = graph.getWidth();
        int graphHeight = graph.getHeight();
        int margin = graph.getMarginSize();

        g2.setColor(graph.getBackgroundColor());
        g2.fillRect(0, 0, graphWidth, graphHeight);

        int borderW = graphWidth - margin * 2;
        int borderH = graphHeight - margin * 2;

        g2.setColor(graph.getBorderColor());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(margin, margin, borderW, borderH);
    }

    /**
     * Draws tick marks and labels along the Y- and (optionally) X-axes based on provided tick configuration.
     * Tick mark spacing is derived from the value range of the provided dataset.
     *
     * @param g2          The graphics context to draw with
     * @param graph       Contains the context parameters we will be drawing
     */
    public static void drawTicks(Graphics2D g2, Graph graph) {
        TickMarkConfig config = graph.getTickConfig();
        g2.setColor(config.getTickColor());
        g2.setFont(config.getTickFont());

        if (config.isShowYTicks()) {
            if (config.isDoublePrecision()) {
                drawDoubleYTicks(g2, config, graph);
            } else {
                drawIntYTicks(g2, config, graph);
            }
        }
        if (config.isShowXTicks()) {
            if (config.isDoublePrecision()) {
                drawDoubleXTicks(g2, config, graph);
            } else {
                drawIntXTicks(g2, config, graph);
            }
        }
    }

    private static void drawDoubleYTicks(Graphics2D g2, TickMarkConfig config, Graph graph) {
        double[] doubleTicks = config.getDoubleYTicks();
        if (doubleTicks.length == 0) {
            return;
        }
        int height = graph.getHeight();
        int margin = graph.getMarginSize();
        int graphHeight = height - 2 * margin;
        int tickLineLength = config.getTickLength();
        int halfTickLineLength = tickLineLength / 2;

        double scrollY = graph.getScrollY();
        double visibleValueHeight = graph.getVisibleValueHeight();

        int i = 0;
        for (double doubleTick : doubleTicks) {
            if (graph.isCroppedToData()) {
                if (doubleTick < scrollY || doubleTick > scrollY + visibleValueHeight) {
                    continue;
                }
            }
            double norm = config.getDeltaY() * i++;

            int y = (int) (height - margin - norm * graphHeight);
            int x1 = margin - halfTickLineLength;
            int x2 = x1 + tickLineLength;

            g2.drawLine(x1, y, x2, y);

            String label = String.format("%.2f", doubleTick);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getAscent();

            g2.drawString(label, x1 - labelWidth - 5, y + labelHeight / 2 - 2);
        }
    }

    private static void drawIntYTicks(Graphics2D g2, TickMarkConfig config, Graph graph) {
        int[] intTicks = config.getIntYTicks();
        if (intTicks.length == 0) {
            return;
        }
        int height = graph.getHeight();
        int margin = graph.getMarginSize();
        int tickLineLength = config.getTickLength();
        int halfTickLineLength = tickLineLength / 2;

        double scrollY = graph.getScrollY();
        double visibleValueHeight = graph.getVisibleValueHeight();

        int i = 0;
        for (int intTick : intTicks) {
            if (graph.isCroppedToData()) {
                if (intTick < scrollY || intTick > scrollY + visibleValueHeight) {
                    continue;
                }
            }
            double norm = config.getDeltaY() * i++;

            int y = (int) (height - margin - norm);

            int x1 = margin - halfTickLineLength;
            int x2 = x1 + tickLineLength;

            g2.drawLine(x1, y, x2, y);

            String label = String.valueOf(intTick);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getAscent();

            g2.drawString(label, x1 - labelWidth - 5, y + labelHeight / 2 - 2);
        }
    }

    // TODO fix x ticks to go over the line, matching y ticks.
    private static void drawIntXTicks(Graphics2D g2, TickMarkConfig config, Graph graph) {
        int[] xTicks = config.getIntXTicks();
        if (xTicks.length == 0) {
            return;
        }
        int height = graph.getHeight();
        int margin = graph.getMarginSize();
        int tickLineLength = config.getTickLength();

        double scrollX = graph.getScrollX();
        double visibleValueWidth = graph.getVisibleValueWidth();

        int i = 0;
        for (int xTick : xTicks) {
            if (graph.isCroppedToData()) {
                if (xTick < scrollX || xTick > scrollX + visibleValueWidth) {
                    continue;
                }
            }
            double norm = config.getDeltaX() * i++;

            int x = (int) (norm + margin);

            int y1 = height - margin;
            int y2 = y1 + tickLineLength;

            g2.drawLine(x, y1, x, y2);

            String label = String.valueOf(xTick);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            g2.drawString(label, x - labelWidth / 2, y2 + fm.getAscent());
        }
    }

    private static void drawDoubleXTicks(Graphics2D g2, TickMarkConfig config, Graph graph) {
        double[] xTicks = config.getDoubleXTicks();
        if (xTicks.length == 0) {
            return;
        }
        int height = graph.getHeight();
        int margin = graph.getMarginSize();
        int tickLineLength = config.getTickLength();

        double scrollX = graph.getScrollX();
        double visibleValueWidth = graph.getVisibleValueWidth();

        int i = 0;
        for (double xTick : xTicks) {
            if (graph.isCroppedToData()) {
                if (xTick < scrollX || xTick > scrollX + visibleValueWidth) {
                    continue;
                }
            }
            double norm = config.getDeltaX() * i++;

            int x = (int) (norm + margin);

            int y1 = height - margin;
            int y2 = y1 + tickLineLength;

            g2.drawLine(x, y1, x, y2);

            String label = String.format("%.2f", xTick);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            g2.drawString(label, x - labelWidth / 2, y2 + fm.getAscent());
        }
    }
}
