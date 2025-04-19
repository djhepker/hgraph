package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Arrays;
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
     * @param panelWidth       The width of the panel in pixels
     * @param panelHeight      The height of the panel in pixels
     * @param g2               The graphics context to draw with
     * @param marginSize       The size of the margin around the graph area
     * @param backgroundColor  The fill color of the panel background
     * @param borderColor      The stroke color of the graph border
     */
    public static void drawMargin(
            Graphics2D g2,
            int marginSize,
            int panelWidth,
            int panelHeight,
            Color backgroundColor,
            Color borderColor
    ) {
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        int borderW = panelWidth - marginSize * 2;
        int borderH = panelHeight - marginSize * 2;

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(marginSize, marginSize, borderW, borderH);
    }

    /**
     * Draws tick marks and labels along the Y- and (optionally) X-axes based on provided tick configuration.
     * Tick mark spacing is derived from the value range of the provided dataset.
     *
     * @param g2          The graphics context to draw with
     * @param config      Configuration options for tick visibility, count, style, and labels
     * @param margin      The size of the margin around the graph area
     * @param width       Total width of the graph panel
     * @param height      Total height of the graph panel
     */
    public static void drawTicks(Graphics2D g2, TickMarkConfig config, int margin, int width, int height) {
        g2.setColor(config.getTickColor());
        g2.setFont(config.getTickFont());

        if (config.isShowYTicks()) {
            if (config.isDoublePrecision()) {
                drawDoubleYTicks(g2, config, height, margin);
            } else {
                drawIntYTicks(g2, config, height, margin);
            }
        }
        if (config.isShowXTicks()) {
            if (config.isDoublePrecision()) {
                drawDoubleXTicks(g2, config, height, margin);
            } else {
                drawIntXTicks(g2, config, height, margin);
            }
        }
    }

    private static void drawDoubleYTicks(Graphics2D g2, TickMarkConfig config, int height, int margin) {
        double[] doubleTicks = config.getDoubleYTicks();
        if (doubleTicks.length == 0) {
            return;
        }
        int graphHeight = height - 2 * margin;

        int tickLineLength = config.getTickLength();
        int halfTickLineLength = tickLineLength / 2;

        int i = 0;
        for (double doubleTick : doubleTicks) {
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

    private static void drawIntYTicks(Graphics2D g2, TickMarkConfig config, int height, int margin) {
        int[] intTicks = config.getIntYTicks();
        if (intTicks.length == 0) {
            return;
        }
        int tickLineLength = config.getTickLength();
        int halfTickLineLength = tickLineLength / 2;

        int i = 0;
        for (int intTick : intTicks) {
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

    private static void drawIntXTicks(Graphics2D g2, TickMarkConfig config, int height, int margin) {
        int[] xTicks = config.getIntXTicks();
        if (xTicks.length == 0) {
            return;
        }
        int tickLineLength = config.getTickLength();

        int i = 0;
        for (int xTick : xTicks) {
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

    private static void drawDoubleXTicks(Graphics2D g2, TickMarkConfig config, int height, int margin) {
        double[] xTicks = config.getDoubleXTicks();
        if (xTicks.length == 0) {
            return;
        }
        int tickLineLength = config.getTickLength();

        int i = 0;
        for (double xTick : xTicks) {
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
