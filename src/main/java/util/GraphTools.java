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
        DrawConfig config = graph.getDrawConfig();
        g2.setFont(graph.getFont());

        if (config.isDoublePrecision()) {
            drawGraphFeatures(graph, config, g2);
        }
    }

    /**
     * Helper method which evaluates the graphstate and draws accordingly.
     *
     * @param graph Object containing the type of graph and its parameters
     * @param config Object containing the drawing parameters which will be applied.
     * @param g2 Graphics pen which is sent from repaint.
     */
    private static void drawGraphFeatures(Graph graph, DrawConfig config, Graphics2D g2) {
        int xTicksLength = config.getXArraySize();
        int yTicksLength = config.getYArraySize();
        if (xTicksLength == 0 || yTicksLength == 0) {
            return;
        }
        boolean isDoublePrecision = config.isDoublePrecision();
        boolean drawTickLabels = graph.isShowingTickLabels();
        boolean isShowingGridLines = graph.isShowingGridLines();
        int margin = graph.getMarginSize();
        int halfTickLength = config.getTickLength() / 2;
        int heightDeltaMargin = graph.getHeight() - margin;
        double deltaX = config.getDeltaX();
        double deltaY = config.getDeltaY();
        int xVertical1 = heightDeltaMargin + halfTickLength;
        int xVertical2 = heightDeltaMargin - halfTickLength;
        int yHorizontal1 = margin - halfTickLength;
        int yHorizontal2 = margin + halfTickLength;

        double[] xTicksDouble;
        double[] yTicksDouble;
        int[] xTicksInt;
        int[] yTicksInt;
        if (isDoublePrecision) {
            xTicksDouble = config.getDoubleXTicks();
            yTicksDouble = config.getDoubleYTicks();
            xTicksInt = null;
            yTicksInt = null;
        } else {
            xTicksDouble = null;
            yTicksDouble = null;
            xTicksInt = config.getIntXTicks();
            yTicksInt = config.getIntYTicks();
        }

        int maxLength = Math.max(xTicksLength, yTicksLength);

        for (int i = 0; i < maxLength; ++i) {
            final int magnitudeX = (int) (deltaX * i) + margin;
            final int magnitudeY = (int) (-deltaY * i) + heightDeltaMargin;

            // Draw ticks
            graph.drawTickLine(g2, magnitudeX, xVertical1, magnitudeX, xVertical2);
            graph.drawTickLine(g2, yHorizontal1, magnitudeY, yHorizontal2, magnitudeY);

            if (isShowingGridLines) {
                graph.drawGridLine(g2, magnitudeX, margin, magnitudeX, xVertical1);
                graph.drawGridLine(g2, yHorizontal2, magnitudeY, graph.getWidth() - margin, magnitudeY);
            }

            // Draw Labels
            if (drawTickLabels) {
                if (xTicksLength > i) {
                    if (isDoublePrecision) {
                        drawXTickLabel(graph, xTicksDouble[i], g2, xVertical1, magnitudeX);
                    } else {
                        drawXTickLabel(graph, xTicksInt[i], g2, xVertical1, magnitudeX);
                    }
                }
                if (yTicksLength > i) {
                    if (isDoublePrecision) {
                        drawYTickLabel(graph, yTicksDouble[i], g2, yHorizontal1, magnitudeY);
                    } else {
                        drawYTickLabel(graph, yTicksInt[i], g2, yHorizontal1, magnitudeY);
                    }
                }
            }
        }
    }

    /**
     * Draws a Y-axis tick label for an integer value.
     */
    private static void drawYTickLabel(Graph graph, int tick, Graphics2D g2, int yHorizontal1, int magnitudeY) {
        FontMetrics fm = g2.getFontMetrics();
        String yLabel = String.valueOf(tick);
        int yLabelWidth = fm.stringWidth(yLabel);
        int yLabelHorizPos = yHorizontal1 - yLabelWidth - 4;
        int yLabelVerticalPos = magnitudeY + (fm.getAscent() / 2);
        graph.drawTickLabel(g2, yLabel, yLabelHorizPos, yLabelVerticalPos);
    }

    /**
     * Draws a X-axis tick label for a double value.
     */
    private static void drawYTickLabel(Graph graph, double tick, Graphics2D g2, int yHorizontal1, int magnitudeY) {
        FontMetrics fm = g2.getFontMetrics();
        String yLabel = String.format("%.2f", tick);
        int yLabelWidth = fm.stringWidth(yLabel);
        int yLabelHorizPos = yHorizontal1 - yLabelWidth - 4;
        int yLabelVerticalPos = magnitudeY + (fm.getAscent() / 2);
        graph.drawTickLabel(g2, yLabel, yLabelHorizPos, yLabelVerticalPos);
    }

    /**
     * Draws an X-axis tick label for an integer value.
     */
    private static void drawXTickLabel(Graph graph, int tick, Graphics2D g2, int xVertical1, int magnitudeX) {
        FontMetrics fm = g2.getFontMetrics();
        String xLabel = String.valueOf(tick);
        int xLabelWidth = fm.stringWidth(xLabel);
        int xLabelHorizPos = magnitudeX - (xLabelWidth / 2);
        int xLabelVerticalPos = xVertical1 + fm.getAscent() + 4;
        graph.drawTickLabel(g2, xLabel, xLabelHorizPos, xLabelVerticalPos);
    }

    /**
     * Draws an X-axis tick label for a double value.
     */
    private static void drawXTickLabel(Graph graph, double tick, Graphics2D g2, int xVertical1, int magnitudeX) {
        FontMetrics fm = g2.getFontMetrics();
        String xLabel = String.format("%.2f", tick);
        int xLabelWidth = fm.stringWidth(xLabel);
        int xLabelHorizPos = magnitudeX - (xLabelWidth / 2);
        int xLabelVerticalPos = xVertical1 + fm.getAscent() + 4;
        graph.drawTickLabel(g2, xLabel, xLabelHorizPos, xLabelVerticalPos);

    }
}
