package util;

import graph.Graph;

import java.awt.*;
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

        boolean doublePrecision = config.isDoublePrecision();
        boolean isCroppedToData = graph.isCroppedToData();
        boolean drawTickLabels = graph.isShowingTickLabels();
        boolean drawGrid = graph.isShowingGridLines();
        int height = graph.getHeight();
        int width = graph.getWidth();
        int margin = graph.getMarginSize();
        int halfTickLength = config.getTickLength() / 2;

        drawFeatures(
                config.getIntXTicks(),
                config.getIntYTicks(),
                height,
                width,
                margin,
                halfTickLength,
                graph.getAspectRatio(),
                config.getDeltaX(),
                config.getDeltaY(),
                graph.getXMinVal(),
                graph.getXMaxVal(),
                isCroppedToData,
                g2
        );

//        if (config.isShowXTicks()) {
//            if (doublePrecision) {
//                drawDoubleGridFeatures(
//                        height,
//                        margin,
//                        config.getDeltaX(),
//                        halfTickLength,
//                        graph.getXMinVal(),
//                        graph.getXMaxVal(),
//                        isCroppedToData,
//                        drawTickLabels,
//                        config.getDoubleXTicks(),
//                        g2
//                );
//            } else {
//                drawIntGridFeatures(
//                        height,
//                        margin,
//                        config.getDeltaX(),
//                        halfTickLength,
//                        graph.getXMinVal(),
//                        graph.getXMaxVal(),
//                        isCroppedToData,
//                        drawTickLabels,
//                        config.getIntXTicks(),
//                        g2
//                );
//            }
//        }
//        if (config.isShowYTicks()) {
//            if (doublePrecision) {
//                drawDoubleGridFeatures(
//                        margin + height - margin,
//                        height - margin,
//                        -config.getDeltaY(),
//                        halfTickLength,
//                        graph.getYMinVal(),
//                        graph.getYMaxVal(),
//                        isCroppedToData,
//                        drawTickLabels,
//                        config.getDoubleYTicks(),
//                        g2
//                );
//            } else {
//                drawIntGridFeatures(
//                        margin + height - margin,
//                        height - margin,
//                        -config.getDeltaY(),
//                        halfTickLength,
//                        graph.getYMinVal(),
//                        graph.getYMaxVal(),
//                        isCroppedToData,
//                        drawTickLabels,
//                        config.getIntYTicks(),
//                        g2
//                );
//            }
//        }
    }

    public static int[] getTickMagnitudes(int[] ticks, double delta, int tickPosNaught) {
        int[] tickMagnitudes = new int[ticks.length];
        for (int i = 0; i < ticks.length; ++i) {
            tickMagnitudes[i] = (int) (delta * i + tickPosNaught);
        }
        return tickMagnitudes;
    }

    private static void drawFeatures(
            int[] xTicks,
            int[] yTicks,
            int height,
            int width,
            int margin,
            int halfTickLength,
            double ratio,
            double deltaX,
            double deltaY,
            double oScroll,
            double fScroll,
            boolean croppedToData,
            Graphics2D g2
    ) {
        if (xTicks.length == 0 || yTicks.length == 0) {
            return;
        }
        for (int i = 0; i < xTicks.length; ++i) {
//            if (croppedToData && (xTicks[i] < oScroll || xTicks[i] >= oScroll + fScroll)) {
//                continue;
//            }

            int heightDeltaMargin = height - margin;

            int magnitude = (int) (deltaX * i) + margin;
            int breadth1 = heightDeltaMargin + halfTickLength;
            int breadth2 = heightDeltaMargin - halfTickLength;

            FontMetrics fm = g2.getFontMetrics();
            int leftMostPixelIndex;

            // draw tick x
            g2.drawLine(magnitude, breadth1, magnitude, breadth2);

            // draw tick y
            int magnitudeY = (int) (-deltaY * i) + heightDeltaMargin;

            g2.drawLine(margin - halfTickLength, magnitudeY, margin + halfTickLength, magnitudeY);
        }
    }

    private static void drawIntGridFeatures(
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
        for (int tick : ticks) {
            if (croppedToData && (tick < oScroll || tick >= oScroll + fScroll)) {
                continue;
            }
            int magnitude = (int) (delta * i++ + tickPosNaught);
            int breadth1 = borderCenter - tickPosNaught + halfTickLineLength;
            int breadth2 = borderCenter - tickPosNaught - halfTickLineLength;

            FontMetrics fm = g2.getFontMetrics();
            int leftMostPixelIndex;

            if (delta > 0) { // x-axis
                // draw tick
                g2.drawLine(magnitude, breadth1, magnitude, breadth2);
                // draw labels
                if (drawTickLabels) {
                    String label = String.valueOf(tick);
                    int labelWidth = fm.stringWidth(label);
                    int halfLabelWidth = labelWidth / 2;
                    leftMostPixelIndex = breadth1 + (fm.getAscent() / 3) + fm.getAscent();
                    g2.drawString(label, magnitude - halfLabelWidth, leftMostPixelIndex);
                }
            } else { // y-axis
                // draw tick
                g2.drawLine(breadth1, magnitude, breadth2, magnitude);
                // draw labels
                if (drawTickLabels) {
                    String label = String.valueOf(tick);
                    int labelWidth = fm.stringWidth(label);
                    int halfLabelWidth = labelWidth / 2;
                    leftMostPixelIndex = breadth2 - halfLabelWidth - labelWidth;
                    int baselinePixelIndex = magnitude + halfLabelWidth;
                    g2.drawString(label, leftMostPixelIndex, baselinePixelIndex);
                }
            }
        }
    }

    private static void drawDoubleGridFeatures(
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

            FontMetrics fm = g2.getFontMetrics();
            int fmAscent = fm.getAscent();

            if (delta > 0) { // x-axis
                g2.drawLine(magnitude, breadth1, magnitude, breadth2);
                // draw labels
                if (drawTickLabels) {
                    int breadthStringPixelo = breadth1 + (fmAscent / 3) + fmAscent;
                    String label = doubleToString(doubleTick);
                    int halfLabelWidth = fm.stringWidth(label) / 2;
                    g2.drawString(label, magnitude - halfLabelWidth, breadthStringPixelo);
                }
            } else { // y-axis
                g2.drawLine(breadth1, magnitude, breadth2, magnitude);
                //draw labels
                if (drawTickLabels) {
                    String label = doubleToString(doubleTick);
                    int halfLabelWidth = fm.stringWidth(label) / 2;
                    int centeredLabelPt = breadth2 / 2 - halfLabelWidth;
                    g2.drawString(label, centeredLabelPt, magnitude + fmAscent / 2);
                }
            }
        }
    }
}
