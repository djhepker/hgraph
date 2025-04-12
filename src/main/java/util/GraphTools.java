package util;

import java.awt.*;
import java.util.Deque;

/**
 * Utility class providing common rendering functions for graphs such as margin drawing and tick mark rendering.
 */
public class GraphTools {

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
            int panelWidth,
            int panelHeight,
            Graphics2D g2,
            int marginSize,
            Color backgroundColor,
            Color borderColor
    ) {
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        int borderX = marginSize / 2;
        int borderY = marginSize / 2;
        int borderW = panelWidth - marginSize;
        int borderH = panelHeight - marginSize;

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(borderX, borderY, borderW, borderH);
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
     * @param dataPoints  The dataset used to determine tick value scaling (Y-axis only for now)
     */
    public static void drawTicks(
            Graphics2D g2,
            TickMarkConfig config,
            int margin,
            int width,
            int height,
            Deque<Double> dataPoints
    ) {
        int tickLineLength = config.getTickLength();

        int graphWidth = width - 2 * margin;
        int graphHeight = height - 2 * margin;

        g2.setColor(config.getTickColor());
        g2.setFont(config.getTickFont());

        if (config.isShowYTicks()) {
            for (double yTick : config.getYTicksDouble()) {
                int y = (int)
            }
        }
    }
}
