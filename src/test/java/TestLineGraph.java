import graph.LineGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.TickMarkConfig;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
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
        graph = new LineGraph(initialData);
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
        TickMarkConfig config = new TickMarkConfig();
        config.setXTickValues(new int[]{1, 2, 3});
        config.setYTickValues(new int[]{10, 20, 30});

        assertArrayEquals(new int[]{1, 2, 3}, config.getIntXTicks(), "X ticks should match input int array");
        assertArrayEquals(new int[]{10, 20, 30}, config.getIntYTicks(), "Y ticks should match input int array");

        config.setDoublePrecision(true);

        assertTrue(config.isDoublePrecision(), "Expected double precision to be true");
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, config.getDoubleXTicks(), 0.001);
        assertArrayEquals(new double[]{10.0, 20.0, 30.0}, config.getDoubleYTicks(), 0.001);
    }

    @Disabled("Disabled for CI/CD GitHub Actions because it opens GUI window")
    @Test
    void testCreatingFrameIntTicks() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TickMarkConfig config = new TickMarkConfig()
                .setXTickValues(new int[]{0, 1, 2, 3, 4, 5, 6})
                .setYTickValues(new int[]{0, 1, 2, 3, 4, 5, 6});
        graph.setTickConfig(config);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame: Int Ticks");
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
    void testCropGraphToDataVisual() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        TickMarkConfig config = new TickMarkConfig()
                .setDoublePrecision(false)
                .setXTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
                .setYTickValues(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        graph.setTickConfig(config).cropGraphToData(true);
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
}
