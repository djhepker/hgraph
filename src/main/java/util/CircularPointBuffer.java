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
    private int head;

    @Getter
    private int cursor;

    /** Current number of elements in the buffer */
    @Getter
    private int size;

    /** Total capacity of the circular buffer */
    @Getter
    private int capacity;

    /**
     * Constructs a circular buffer with the specified capacity.
     *
     * @param capacity the maximum number of (x, y) points this buffer can hold
     */
    public CircularPointBuffer(int capacity) {
        this.head = 0;
        this.cursor = 0;
        this.size = 0;
        this.capacity = capacity;
        this.x = new double[capacity];
        this.y = new double[capacity];
    }

    /**
     * Resets all values in buffer, maintains capacity
     */
    public void clear() {
        head = 0;
        cursor = 0;
        size = 0;
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
        cursor = 0;
        size = Math.min(size, newCapacity);
        capacity = newCapacity;
    }

    /**
     * Increments cursor to next legal position in buffer
     */
    public void advanceCursorWrapped() {
        cursor = (cursor + 1) % capacity;
    }

    /**
     * Moves cursor to head of container
     */
    public void resetCursor() {
        cursor = head;
    }

    /**
     * Returns the index after the last readable cursor position.
     * Used for safe iteration with wrapping logic.
     */
    public int getEndCursor() {
        return (head + size) % capacity;
    }

    // TODO Build removal logic. Handle random access for user to click points of graph. May just need to
    //  eliminate wrap logic

    /**
     * Getter for x at coordinate cursor position
     *
     * @return double x at cursor
     */
    public double cursorGetX() {
        return x[cursor];
    }

    /**
     * Getter for y at coordinate cursor
     *
     * @return double y at cursor position
     */
    public double cursorGetY() {
        return y[cursor];
    }
}
