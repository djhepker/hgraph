
import graph.LineGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class TestLineGraph {

    private LineGraph graph;

    @BeforeEach
    void setUp() {
        List<Double> initialData = Arrays.asList(1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 2.0);
        graph = new LineGraph(initialData);
    }

    @Test
    void testConstructorInitializesDataCorrectly() {
        assertEquals(3, graph.getDataSize());
    }

    @Test
    void testInsertDataPointAppends() {
        graph.insertDataPoint(4.0);
        assertEquals(4, graph.getDataSize());
    }

    @Test
    void testInsertDataPointHandlesNegativeValues() {
        graph.insertDataPoint(-10.5);
        assertEquals(4, graph.getDataSize());
    }

    @Test
    void testInsertDataPointDoesNotThrow() {
        assertDoesNotThrow(() -> graph.insertDataPoint(Double.NaN));
    }

    @Test
    void testCreatingFrame() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame 1");
            frame.setSize(800, 600);
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
