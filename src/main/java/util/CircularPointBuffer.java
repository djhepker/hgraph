package util;

import lombok.Getter;

/**
 * A fixed-size circular buffer that stores paired (x, y) coordinate values.
 * When full, the buffer overwrites the oldest elements in FIFO order.
 * Useful for graphing or time-series data where old data can be discarded as new data arrives.
 */
public final class CircularPointBuffer {

    /** Internal array for x-coordinate values */
    private double[] x;

    /** Internal array for y-coordinate values */
    private double[] y;

    /** Index of the first (oldest) element in the buffer */
    private int head = 0;

    /** Current number of elements in the buffer */
    @Getter
    private int size = 0;

    /** Total capacity of the circular buffer */
    private int capacity;

    /**
     * Constructs a circular buffer with the specified capacity.
     *
     * @param capacity the maximum number of (x, y) points this buffer can hold
     */
    public CircularPointBuffer(int capacity) {
        this.capacity = capacity;
        this.x = new double[capacity];
        this.y = new double[capacity];
    }

    /**
     * Adds a new (x, y) point to the buffer.
     * If the buffer is full, the oldest point will be overwritten.
     *
     * @param newX the x-coordinate to add
     * @param newY the y-coordinate to add
     */
    public void add(double newX, double newY) {
        int index = (head + size) % capacity;
        x[index] = newX;
        y[index] = newY;

        if (size < capacity) {
            size++;
        } else {
            head = (head + 1) % capacity; // overwrite oldest
        }
    }

    /**
     * Dynamically resizes the buffer while preserving current data order.
     * If the new capacity is smaller, only the most recent points are retained.
     *
     * @param newCapacity the new buffer capacity
     */
    public void setCapacity(int newCapacity) {
        double[] tmpX = new double[newCapacity];
        double[] tmpY = new double[newCapacity];

        for (int i = 0; i < Math.min(size, newCapacity); ++i) {
            int idx = (head + i) % capacity;
            tmpX[i] = x[idx];
            tmpY[i] = y[idx];
        }

        x = tmpX;
        y = tmpY;
        head = 0;
        size = Math.min(size, newCapacity);
        capacity = newCapacity;
    }

    /**
     * Retrieves the x-coordinate at the specified logical index.
     * The index is relative to the oldest element (index 0).
     *
     * @param i the logical index, ranging from 0 to size-1
     * @return the x-coordinate at index i
     * @throws ArrayIndexOutOfBoundsException if i is out of range
     */
    public double getX(int i) {
        if (i >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return x[(head + i) % capacity];
    }

    /**
     * Removes and returns the oldest (x, y) point from the buffer.
     *
     * @return a double array where [0] = x, [1] = y of the removed point
     * @throws IllegalStateException if the buffer is empty
     */
    public double[] pop() {
        if (size == 0) {
            throw new IllegalStateException("Buffer is empty");
        }
        double oldX = x[head];
        double oldY = y[head];
        head = (head + 1) % capacity;
        size--;
        return new double[]{oldX, oldY};
    }

    /**
     * Removes and returns the newest (x, y) point from the buffer.
     *
     * @return a double array where [0] = x, [1] = y of the removed point
     * @throws IllegalStateException if the buffer is empty
     */
    public double[] popTail() {
        if (size == 0) {
            throw new IllegalStateException("Buffer is empty");
        }
        int tailIndex = (head + size - 1) % capacity;
        double lastX = x[tailIndex];
        double lastY = y[tailIndex];
        size--;
        return new double[]{lastX, lastY};
    }
}
