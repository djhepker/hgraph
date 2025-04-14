
import graph.LineGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TickMarkConfig;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestLineGraph {

    private LineGraph graph;
    private List<Double> initialData = Arrays.asList(1.0, 10.0, 2.0, 3.0);
    private int listSize = initialData.size();

    @BeforeEach
    void setUp() {
        graph = new LineGraph(initialData);
    }

    @Test
    void testConstructorInitializesDataCorrectly() {
        assertEquals(listSize, graph.getDataSize());
    }

    @Test
    void testInsertDataPointAppends() {
        graph.insertDataPoint(4.0);
        assertEquals(listSize + 1, graph.getDataSize());
    }

    @Test
    void testInsertDataPointHandlesNegativeValues() {
        graph.insertDataPoint(-10.5);
        assertEquals(8 + 1, graph.getDataSize());
    }

    @Test
    void testInsertDataPointDoesNotThrow() {
        assertDoesNotThrow(() -> graph.insertDataPoint(Double.NaN));
    }

    @Test
    void testCreatingFrameIntTicks() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TickMarkConfig config = new TickMarkConfig()
                .setXTickValues(new int[]{0, 1, 2, 3, 4, 5, 6})
                .setYTickValues(new int[]{0, 1, 2, 3});
        graph.setTickMarkConfig(config);
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
}
