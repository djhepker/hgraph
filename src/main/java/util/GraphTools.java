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
        int margin = graph.getMarginSize();
        int borderW = graph.getWidth() - margin * 2;
        int borderH = graph.getHeight() - margin * 2;

        g2.setColor(graph.getBorderColor());
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(margin, margin, borderW, borderH);
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

        boolean doublePrecision = config.isDoublePrecision();
        boolean isCroppedToData = graph.isCroppedToData();
        boolean drawTickLabels = graph.isShowingTickLabels();
        int height = graph.getHeight();
        int margin = graph.getMarginSize();
        int halfTickLength = config.getTickLength() / 2;

        if (config.isShowXTicks()) {
            if (doublePrecision) {
                drawDoubleTicks(
                        height,
                        margin,
                        config.getDeltaX(),
                        halfTickLength,
                        graph.getXMinVal(),
                        graph.getXMaxVal(),
                        isCroppedToData,
                        drawTickLabels,
                        config.getDoubleXTicks(),
                        g2
                );
            } else {
                drawIntTicks(
                        height,
                        margin,
                        config.getDeltaX(),
                        halfTickLength,
                        graph.getXMinVal(),
                        graph.getXMaxVal(),
                        isCroppedToData,
                        drawTickLabels,
                        config.getIntXTicks(),
                        g2
                );
            }
        }
        if (config.isShowYTicks()) {
            if (doublePrecision) {
                drawDoubleTicks(
                        margin + height - margin,
                        height - margin,
                        -config.getDeltaY(),
                        halfTickLength,
                        graph.getYMinVal(),
                        graph.getYMaxVal(),
                        isCroppedToData,
                        drawTickLabels,
                        config.getDoubleYTicks(),
                        g2
                );
            } else {
                drawIntTicks(
                        margin + height - margin,
                        height - margin,
                        -config.getDeltaY(),
                        halfTickLength,
                        graph.getYMinVal(),
                        graph.getYMaxVal(),
                        isCroppedToData,
                        drawTickLabels,
                        config.getIntYTicks(),
                        g2
                );
            }
        }
    }

    private static void drawIntTicks(
            int borderCenter,
            int tickPosNaught,
            double delta,
            int halfTickLineLength,
            double oScroll,
            double fScroll,
            boolean croppedToData,
            boolean drawTickLabels,
            int[] ticks,
            Graphics2D g2
    ) {
        if (ticks.length == 0) {
            return;
        }
        int i = 0;
        for (int tick : ticks) { // TODO figure out why there is extra iteration in cropped mode
            if (croppedToData && (tick < oScroll || tick >= oScroll + fScroll)) {
                continue;
            }
            int magnitude = (int) (delta * i++ + tickPosNaught);
            int breadth1 = borderCenter - tickPosNaught + halfTickLineLength;
            int breadth2 = borderCenter - tickPosNaught - halfTickLineLength;
            String label = String.valueOf(tick);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int halfLabelWidth = labelWidth / 2;
            int leftMostPixelIndex;
            if (delta > 0) { // x-axis
                leftMostPixelIndex = breadth1 + (fm.getAscent() / 3) + fm.getAscent();
                g2.drawLine(magnitude, breadth1, magnitude, breadth2);
                if (drawTickLabels) {
                    g2.drawString(label, magnitude - halfLabelWidth, leftMostPixelIndex);
                }
            } else { // y-axis
                leftMostPixelIndex = breadth2 - halfLabelWidth - labelWidth;
                g2.drawLine(breadth1, magnitude, breadth2, magnitude);
                if (drawTickLabels) {
                    int baselinePixelIndex = magnitude + halfLabelWidth;
                    g2.drawString(label, leftMostPixelIndex, baselinePixelIndex);
                }
            }
        }
    }

    private static void drawDoubleTicks(
            int borderCenter,
            int tickPosNaught,
            double delta,
            int halfTickLineLength,
            double oScroll,
            double fScroll,
            boolean croppedToData,
            boolean drawTickLabels,
            double[] ticks,
            Graphics2D g2
    ) {
        if (ticks.length == 0) {
            return;
        }
        int i = 0;
        for (double doubleTick : ticks) {
            if (croppedToData && (doubleTick < oScroll || doubleTick >= oScroll + fScroll)) {
                continue;
            }
            int magnitude = (int) (delta * i++ + tickPosNaught);
            int breadth1 = borderCenter - tickPosNaught + halfTickLineLength;
            int breadth2 = borderCenter - tickPosNaught - halfTickLineLength;
            String label = doubleToString(doubleTick);
            FontMetrics fm = g2.getFontMetrics();
            int fmAscent = fm.getAscent();
            int labelWidth = fm.stringWidth(label);
            int halfLabelWidth = labelWidth / 2;
            if (delta > 0) { // x-axis
                int breadthStringPixelo = breadth1 + (fmAscent / 3) + fmAscent;
                g2.drawLine(magnitude, breadth1, magnitude, breadth2);
                if (drawTickLabels) {
                    g2.drawString(label, magnitude - halfLabelWidth, breadthStringPixelo);
                }
            } else { // y-axis
                int centeredLabelPt = breadth2 / 2 - halfLabelWidth;
                g2.drawLine(breadth1, magnitude, breadth2, magnitude);
                if (drawTickLabels) {
                    g2.drawString(label, centeredLabelPt, magnitude + fmAscent / 2);
                }
            }
        }
    }
}
