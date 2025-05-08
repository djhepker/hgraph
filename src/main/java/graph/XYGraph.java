package graph;

import java.awt.geom.Point2D;
import java.util.Collection;

/**
 * Interface for graph types that use (x, y) coordinate data.
 * Centralizes data management operations common to graphs like LineGraph, BarGraph, etc.
 */
public interface XYGraph {

    /**
     * Inserts a single data point into the graph.
     *
     * @param x x-coordinate of the data point
     * @param y y-coordinate of the data point
     * @return this instance for method chaining
     */
    Graph insertData(double x, double y);

    /**
     * Inserts a single Point2D.Double into the graph.
     *
     * @param newData the (x, y) point to add
     * @return this instance for method chaining
     */
    Graph insertData(Point2D.Double newData);

    /**
     * Adds all data points from the given collection to the graph.
     *
     * @param dataIterable a collection of Point2D.Double points
     * @return this instance for method chaining
     */
    Graph addAll(Collection<Point2D.Double> dataIterable);

    /**
     * Returns the number of data points currently in the graph.
     *
     * @return the data size
     */
    int getDataSize();

    /**
     * Checks whether the graph currently contains any data.
     *
     * @return true if empty, false otherwise
     */
    boolean dataEmpty();
}
