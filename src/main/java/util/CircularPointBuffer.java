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

    private int iterCount;

    /**
     * Constructs a circular buffer with the specified capacity.
     *
     * @param capacity the maximum number of (x, y) points this buffer can hold
     */
    public CircularPointBuffer(int capacity) {
        this.head = 0;
        this.cursor = 0;
        this.size = 0;
        this.iterCount = 0;
        this.capacity = capacity;
        this.x = new double[capacity];
        this.y = new double[capacity];
    }

    /**
     * Resets all values in buffer, maintains capacity
     *
     * @return this buffer instance for method chaining
     */
    public CircularPointBuffer clear() {
        head = 0;
        cursor = 0;
        size = 0;
        return this;
    }

    /**
     * Adds a new (x, y) point to the buffer.
     * If the buffer is full, the oldest point will be overwritten.
     *
     * @param newX the x-coordinate to add
     * @param newY the y-coordinate to add
     * @return this buffer instance for method chaining
     */
    public CircularPointBuffer add(double newX, double newY) {
        int index = (head + size) % capacity;
        x[index] = newX;
        y[index] = newY;
        if (size < capacity) {
            ++size;
        } else {
            head = (head + 1) % capacity;
        }
        return this;
    }

    /**
     * Dynamically resizes the buffer while preserving current data order.
     * If the new capacity is smaller, only the most recent points are retained.
     *
     * @param newCapacity the new buffer capacity
     * @return this buffer instance for method chaining
     */
    public CircularPointBuffer setCapacity(int newCapacity) {
        if (newCapacity != capacity) {
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
        return this;
    }

    /**
     * Increments cursor to next legal position in buffer
     *
     * @return this buffer instance for method chaining
     */
    public CircularPointBuffer advanceCursorWrapped() {
        cursor = (cursor + 1) % capacity;
        ++iterCount;
        return this;
    }

    /**
     * Moves cursor to head of container
     */
    public CircularPointBuffer resetCursor() {
        cursor = head;
        iterCount = 0;
        return this;
    }

    /**
     * Flag for when cursor has reached the end of buffer
     *
     * @return True if there is a valid value stored in next index. False otherwise
     */
    public boolean hasNext() {
        return iterCount < size;
    }

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

    /**
     * Removes first element of container.
     *
     * @return (x,y) double[]{x, y}
     */
    public double[] pop() {
        if (size == 0) {
            return null;
        }
        double[] top = {x[head], y[head]};
        if (cursor == head) {
            head = (head + 1) % capacity;
            cursor = head;
        } else {
            head = (head + 1) % capacity;
        }
        --size;
        return top;
    }
}
