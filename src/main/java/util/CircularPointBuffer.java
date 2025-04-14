package util;

import lombok.Getter;

import java.util.Iterator;

import util.CircularPointBuffer.PointView;

/**
 * A fixed-size circular buffer that stores paired (x, y) coordinate values.
 * When full, the buffer overwrites the oldest elements in FIFO order.
 * Useful for graphing or time-series data where old data can be discarded as new data arrives.
 */
public final class CircularPointBuffer implements Iterable<PointView> {

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

    /** Logical iteration counter for cursor-based traversals */
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
     * Resets all values in buffer, maintains capacity.
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
     * Checks whether the buffer is currently empty.
     *
     * @return true if the buffer has no elements, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Increments cursor to the next legal position in buffer.
     *
     * @return this buffer instance for method chaining
     */
    public CircularPointBuffer advanceCursorWrapped() {
        cursor = (cursor + 1) % capacity;
        ++iterCount;
        return this;
    }

    /**
     * Moves cursor to the head of the container.
     *
     * @return this buffer instance for method chaining
     */
    public CircularPointBuffer resetCursor() {
        cursor = head;
        iterCount = 0;
        return this;
    }

    /**
     * Flag for when cursor has reached the end of buffer.
     *
     * @return true if there is a valid value stored in the next index; false otherwise
     */
    public boolean hasNext() {
        return iterCount < size;
    }

    /**
     * Gets the x-value at the current cursor position.
     *
     * @return the x-coordinate at cursor
     */
    public double cursorGetX() {
        return x[cursor];
    }

    /**
     * Gets the y-value at the current cursor position.
     *
     * @return the y-coordinate at cursor
     */
    public double cursorGetY() {
        return y[cursor];
    }

    /**
     * Removes the first (oldest) element from the container.
     *
     * @return a double array containing the removed (x, y) pair
     */
    public double[] pop() {
        if (size == 0) return null;

        double[] top = {x[head], y[head]};
        head = (head + 1) % capacity;
        if (cursor == head) cursor = head;
        --size;
        return top;
    }

    /**
     * Returns an iterator that yields PointView objects in insertion order.
     * Note: the returned PointView is reused during iteration for performance.
     *
     * @return an iterator over (x, y) point views
     */
    @Override
    public Iterator<PointView> iterator() {
        return new Iterator<>() {
            private int index = 0;
            private int iteratorCursor = head;
            private final PointView point = new PointView();

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public PointView next() {
                point.set(x[iteratorCursor], y[iteratorCursor]);
                iteratorCursor = (iteratorCursor + 1) % capacity;
                ++index;
                return point;
            }
        };
    }

    /**
     * Reusable view object for exposing (x, y) points during iteration.
     */
    public static final class PointView {
        public double x, y;

        private PointView() {

        }

        public void set(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
