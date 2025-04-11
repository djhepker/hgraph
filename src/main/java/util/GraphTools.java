package util;

import java.awt.*;

public class GraphTools {

    private GraphTools() {

    }

    public static void drawMargin(
            int panelWidth, int panelHeight, Graphics2D g2, int marginSize, Color backgroundColor, Color borderColor) {
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, panelWidth, panelHeight);
        int borderX = marginSize / 2;
        int borderY = marginSize / 2;
        int borderW = panelWidth - marginSize;
        int borderH = panelHeight - marginSize;
        g2.setColor(borderColor);
        g2.drawRect(borderX, borderY, borderW, borderH);
    }
}
