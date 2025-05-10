import graph.LineGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.DrawConfig;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class TestLineGraph {
    private LineGraph graph;
    private List<Point2D.Double> initialData;
    private int listSize;
    private DrawConfig defaultConfig;

    @BeforeEach
    void setUp() {
        initialData = Arrays.asList(
                new Point2D.Double(1.0, 2.0),
                new Point2D.Double(2.0, 5.0),
                new Point2D.Double(3.0, 2.0),
                new Point2D.Double(4.0, 1.0),
                new Point2D.Double(5.0, 6.0)
        );
        listSize = initialData.size();

        defaultConfig = new DrawConfig()
                .setBackgroundColor(Color.BLACK)
                .setMarginSize(32)
                .setShowGrid(true)
                .setShowMarginBorder(true)
                .showTicks(true)
                .setShowTickLabels(true)
                .setTickColor(Color.GREEN)
                .setGridColor(new Color(255, 255, 255, 64));
        graph = new LineGraph(defaultConfig, initialData);
    }

    @Test
    void testConstructorInitializesDataCorrectly() {
        assertEquals(listSize, graph.getDataSize(), "Initial data size should match provided list");
    }

    @Test
    void testInsertDataPointAppends() {
        graph.insertData(new Point2D.Double(5.0, 6.0));
        assertEquals(listSize + 1, graph.getDataSize(), "Data size should increase after insertion");
    }

    @Test
    void testInsertDataPointHandlesNegativeValues() {
        graph.insertData(-10.5, 2.3);
        assertEquals(listSize + 1, graph.getDataSize(), "Data size should increase after negative insertion");
    }

    @Test
    void testInsertDataPointDoesNotThrow() {
        assertDoesNotThrow(() -> graph.insertData(Double.NaN, Double.NaN), "Inserting NaN values should not throw");
    }

    @Test
    void testTickMarkConfigTicks() {
        defaultConfig.setXTickValues(new int[]{1, 2, 3});
        defaultConfig.setYTickValues(new int[]{10, 20, 30});

        assertArrayEquals(new int[]{1, 2, 3}, defaultConfig.getIntXTicks(), "X ticks should match input int array");
        assertArrayEquals(new int[]{10, 20, 30}, defaultConfig.getIntYTicks(), "Y ticks should match input int array");

        defaultConfig.setDoublePrecision(true);

        assertTrue(defaultConfig.isDoublePrecision(), "Expected double precision to be true");
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, defaultConfig.getDoubleXTicks(), 0.001);
        assertArrayEquals(new double[]{10.0, 20.0, 30.0}, defaultConfig.getDoubleYTicks(), 0.001);
    }

    @Disabled("Disabled for CI/CD GitHub Actions because it opens GUI window")
    @Test
    void testDataVisualInt() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        defaultConfig.setDoublePrecision(false)
                .setXTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
                .setYTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame: Crop Graph to Data");
            frame.setSize(1000, 800);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    latch.countDown();
                }
            });
            frame.getContentPane().add(graph);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        latch.await();
    }

    @Disabled("Disabled for CI/CD GitHub Actions because it opens GUI window")
    @Test
    void testDataVisualDouble() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        defaultConfig.setDoublePrecision(false)
                .setXTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
                .setYTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
                .setDoublePrecision(true);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame: Crop Graph to Data");
            frame.setSize(1000, 800);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    latch.countDown();
                }
            });
            frame.getContentPane().add(graph);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        latch.await();
    }

    @Disabled("Disabled for CI/CD GitHub Actions because it opens GUI window")
    @Test
    void testCropDataVisualCroppedDouble() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        defaultConfig.setDoublePrecision(false)
                .setXTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
                .setYTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
                .setDoublePrecision(true);
        graph.cropData(true);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame: Crop Graph to Data");
            frame.setSize(1000, 800);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    latch.countDown();
                }
            });
            frame.getContentPane().add(graph);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        latch.await();
    }

    @Disabled("Disabled for CI/CD GitHub Actions because it opens GUI window")
    @Test
    void testDecimalDataWithPreCroppingAndDoublePrecision() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        defaultConfig.setDoublePrecision(true)
                .showVertices(true)
                .setXTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
                .setYTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        LineGraph partialGraph = new LineGraph(defaultConfig)
                .cropData(true)
                .insertData(0.25, 1.75)
                .insertData(1.5, 2.3)
                .insertData(2.75, 0.9)
                .insertData(3.1, 3.6);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame: Crop Graph to Data");
            frame.setSize(1000, 800);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    latch.countDown();
                }
            });
            frame.getContentPane().add(partialGraph);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        latch.await();
    }
}
