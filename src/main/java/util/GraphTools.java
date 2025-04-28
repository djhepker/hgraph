package util;

import graph.Graph;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D.Double;
import java.text.DecimalFormat;

/**
 * Utility class providing common rendering functions for graphs such as margin drawing and tick mark rendering.
 */
public final class GraphTools {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

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
    public static double[] arrayIntToArrayDouble(int[] intArray) {
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
    public static int[] arrayDoubleToArrayInt(double[] doubleArr) {
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
     * Returns a String representation of a double with two decimal places.
     *
     * @param d The double being converted.
     * @return String representation of d rounded to two decimal places.
     */
    public static String doubleToString(double d) {
        return DECIMAL_FORMAT.format(d);
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
        int margin = graph.getMarginSize();
        int borderW = graph.getWidth() - margin * 2;
        int borderH = graph.getHeight() - margin * 2;

        g2.setColor(graph.getBorderColor());
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(margin, margin, borderW, borderH);
    }

    /**
     * Draws tick marks and labels along the Y- and (optionally) X-axes based on given tick configuration.
     * Tick mark spacing is derived from the value range of the provided dataset.
     *
     * @param g2          The graphics context to draw with
     * @param graph       Contains the context parameters we will be drawing
     */
    public static void drawTicks(Graphics2D g2, Graph graph) {
        TickMarkConfig config = graph.getTickConfig();
        g2.setColor(config.getTickColor());
        g2.setFont(graph.getFont());

        if (config.isDoublePrecision()) {
            drawDoubleFeatures(graph, config, g2);
        } else {
            drawIntFeatures(graph, config, g2);
        }
    }

    private static void drawIntFeatures(Graph graph, TickMarkConfig config, Graphics2D g2) {
        int[] xTicks = config.getIntXTicks();
        int[] yTicks = config.getIntYTicks();
        if (xTicks.length == 0 || yTicks.length == 0) {
            return;
        }

        boolean drawTickLabels = graph.isShowingTickLabels();
        boolean drawGrid = graph.isShowingGridLines();
        int height = graph.getHeight();
        int width = graph.getWidth();
        int margin = graph.getMarginSize();
        int halfTickLength = config.getTickLength() / 2;
        double deltaX = config.getDeltaX();
        double deltaY = config.getDeltaY();

        int maxLength = Math.max(xTicks.length, yTicks.length);
        for (int i = 0; i < maxLength; ++i) {
            int heightDeltaMargin = height - margin;
            int magnitudeX = (int) (deltaX * i) + margin;
            int breadth1 = heightDeltaMargin + halfTickLength;
            int breadth2 = heightDeltaMargin - halfTickLength;
            // draw tick x
            g2.drawLine(magnitudeX, breadth1, magnitudeX, breadth2);
            // draw tick y
            int magnitudeY = (int) (-deltaY * i) + heightDeltaMargin;
            g2.drawLine(margin - halfTickLength, magnitudeY, margin + halfTickLength, magnitudeY);

            if (drawTickLabels) {
                FontMetrics fm = g2.getFontMetrics();
                if (yTicks.length > i) {
                    String yLabel = String.valueOf(yTicks[i]);
                    int yLabelWidth = fm.stringWidth(yLabel);
                    int yLabelX = margin - halfTickLength - yLabelWidth - 4;
                    int yLabelY = magnitudeY + (fm.getAscent() / 2);
                    g2.drawString(yLabel, yLabelX, yLabelY);
                }
                if (xTicks.length > i) {
                    String xLabel = String.valueOf(xTicks[i]);
                    int xLabelWidth = fm.stringWidth(xLabel);
                    int xLabelX = magnitudeX - (xLabelWidth / 2);
                    int xLabelY = breadth1 + fm.getAscent() + 4;
                    g2.drawString(xLabel, xLabelX, xLabelY);
                }
            }
        }
    }

    private static void drawDoubleFeatures(Graph graph, TickMarkConfig config, Graphics2D g2) {
        double[] xTicks = config.getDoubleXTicks();
        double[] yTicks = config.getDoubleYTicks();
        if (xTicks.length == 0 || yTicks.length == 0) {
            return;
        }
        boolean drawTickLabels = graph.isShowingTickLabels();
        boolean drawGrid = graph.isShowingGridLines();
        int height = graph.getHeight();
        int width = graph.getWidth();
        int margin = graph.getMarginSize();
        int halfTickLength = config.getTickLength() / 2;
        double deltaX = config.getDeltaX();
        double deltaY = config.getDeltaY();

        int maxLength = Math.max(xTicks.length, yTicks.length);
        for (int i = 0; i < maxLength; ++i) {
            int heightDeltaMargin = height - margin;
            int magnitudeX = (int) (deltaX * i) + margin;
            int breadth1 = heightDeltaMargin + halfTickLength;
            int breadth2 = heightDeltaMargin - halfTickLength;

            // draw tick x
            g2.drawLine(magnitudeX, breadth1, magnitudeX, breadth2);

            // draw tick y
            int magnitudeY = (int) (-deltaY * i) + heightDeltaMargin;
            g2.drawLine(margin - halfTickLength, magnitudeY, margin + halfTickLength, magnitudeY);

            if (drawTickLabels) {
                FontMetrics fm = g2.getFontMetrics();
                if (yTicks.length > i) {
                    String yLabel = String.format("%.2f", yTicks[i]);
                    int yLabelWidth = fm.stringWidth(yLabel);
                    int yLabelX = margin - halfTickLength - yLabelWidth - 4;
                    int yLabelY = magnitudeY + (fm.getAscent() / 2);
                    g2.drawString(yLabel, yLabelX, yLabelY);
                }
                if (xTicks.length > i) {
                    String xLabel = String.format("%.2f", xTicks[i]);
                    int xLabelWidth = fm.stringWidth(xLabel);
                    int xLabelX = magnitudeX - (xLabelWidth / 2);
                    int xLabelY = breadth1 + fm.getAscent() + 4;
                    g2.drawString(xLabel, xLabelX, xLabelY);
                }
            }
        }
    }
}
