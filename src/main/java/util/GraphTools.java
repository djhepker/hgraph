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
    public static void drawTicks(
            Graphics2D g2,
            TickMarkConfig config,
            int margin,
            int width,
            int height
    ) {
        int tickLineLength = config.getTickLength();
        int halfTickLineLength = tickLineLength / 2;
        int graphWidth = width - 2 * margin;
        int graphHeight = height - 2 * margin;

        g2.setColor(config.getTickColor());
        g2.setFont(config.getTickFont());

        if (config.isShowYTicks()) {
            if (config.isDoublePrecision()) {
                drawDoubleYTicks(
                        g2, config.getDoubleYTicks(), height, margin, graphHeight, tickLineLength, halfTickLineLength
                );
            } else {
                drawIntYTicks(
                        g2, config.getIntYTicks(), height, margin, graphHeight, tickLineLength, halfTickLineLength
                );
            }
        }
        if (config.isShowXTicks()) {
            if (config.isDoublePrecision()) {
                drawDoubleXTicks(
                        g2, config.getDoubleXTicks(), margin, graphWidth, tickLineLength, height
                );
            } else {
                drawIntXTicks(
                        g2, config.getIntXTicks(), margin, graphWidth, tickLineLength, height
                );
            }
        }
    }

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

    private static void drawDoubleYTicks(
            Graphics2D g2,
            double[] doubleTicks,
            int height,
            int margin,
            int graphHeight,
            int tickLineLength,
            int halfTickLineLength
    ) {
        if (doubleTicks.length == 0) {
            return;
        }

        double minY = Arrays.stream(doubleTicks).min().orElse(0.0);
        double maxY = Arrays.stream(doubleTicks).max().orElse(1.0);
        double rangeY = maxY - minY;
        if (rangeY == 0) {
            rangeY = 1;
        }
        for (double doubleTick : doubleTicks) {
            double norm = (doubleTick - minY) / rangeY;
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

    private static void drawIntYTicks(
            Graphics2D g2,
            int[] intTicks,
            int height,
            int margin,
            int graphHeight,
            int tickLineLength,
            int halfTickLineLength
    ) {
        if (intTicks.length == 0) {
            return;
        }

        int minY = Arrays.stream(intTicks).min().orElse(0);
        int maxY = Arrays.stream(intTicks).max().orElse(1);
        int rangeY = maxY - minY;
        if (rangeY == 0) {
            rangeY = 1;
        }
        for (int intTick : intTicks) {
            double norm = (intTick - minY) / (double) rangeY;
            int y = (int) (height - margin - norm * graphHeight);
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

    private static void drawIntXTicks(
            Graphics2D g2,
            int[] xTicks,
            int margin,
            int graphWidth,
            int tickLineLength,
            int height
    ) {
        if (xTicks.length == 0) {
            return;
        }

        int minX = Arrays.stream(xTicks).min().orElse(0);
        int maxX = Arrays.stream(xTicks).max().orElse(1);
        int rangeX = maxX - minX;
        if (rangeX == 0) {
            rangeX = 1;
        }

        for (int xTick : xTicks) {
            double normX = (xTick - minX) / (double) rangeX;
            int x = (int) (margin + normX * graphWidth);
            int y1 = height - margin;
            int y2 = y1 + tickLineLength;

            g2.drawLine(x, y1, x, y2);

            String label = String.valueOf(xTick);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            g2.drawString(label, x - labelWidth / 2, y2 + fm.getAscent());
        }
    }

    private static void drawDoubleXTicks(
            Graphics2D g2,
            double[] xTicks,
            int margin,
            int graphWidth,
            int tickLineLength,
            int height
    ) {
        if (xTicks.length == 0) {
            return;
        }

        double minX = Arrays.stream(xTicks).min().orElse(0.0);
        double maxX = Arrays.stream(xTicks).max().orElse(1.0);
        double rangeX = maxX - minX;
        if (rangeX == 0) {
            rangeX = 1;
        }

        for (double xTick : xTicks) {
            double normX = (xTick - minX) / rangeX;
            int x = (int) (margin + normX * graphWidth);
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
