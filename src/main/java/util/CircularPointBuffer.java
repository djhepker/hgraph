package util;

import lombok.Getter;

import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A fixed-size circular buffer that stores paired (x, y) coordinate values.
 * When full, the buffer overwrites the oldest elements in FIFO order.
 * Useful for graphing or time-series data where old data can be discarded as new data arrives.
 */
public final class CircularPointBuffer implements Iterable<Double>, Collection<Double> {
    private double[] x;
    private double[] y;
    private int head;
    @Getter
    private int cursor;
    private int size;
    @Getter
    private int capacity;
    private int iterCount;

    /**
     * Parameterized constructor.
     *
     * @param capacity The capacity of buffer at initialization
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
     * Updates the capacity of the buffer and accurately updates member variables in the process.
     *
     * @param newCapacity Capacity to be applied to buffer.
     * @return This class instance for chain methods.
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

    public CircularPointBuffer advanceCursorWrapped() {
        cursor = (cursor + 1) % capacity;
        ++iterCount;
        return this;
    }

    public CircularPointBuffer resetCursor() {
        cursor = head;
        iterCount = 0;
        return this;
    }

    public boolean hasNext() {
        return iterCount < size;
    }

    public double cursorGetX() {
        return x[cursor];
    }

    public double cursorGetY() {
        return y[cursor];
    }

    public double[] pop() {
        if (size == 0) return null;

        double[] top = {x[head], y[head]};
        head = (head + 1) % capacity;
        if (cursor == head) cursor = head;
        --size;
        return top;
    }

    /**
     * Empty checker
     *
     * @return True if empty. False otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks buffer for Point2D.Double.
     *
     * @param o element whose presence in this collection is to be tested. Should be Point2D.Double, otherwise
     *          returns false.
     * @return True if point coordinates match existing buffer element.
     */
    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Double p)) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            int idx = (head + i) % capacity;
            if (java.lang.Double.compare(x[idx], p.getX()) == 0 && java.lang.Double.compare(y[idx], p.getY()) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets all index tracking int to zero. Buffer allows lazy deletion.
     */
    @Override
    public void clear() {
        head = 0;
        cursor = 0;
        size = 0;
    }

    /**
     * Getter method for the number of valid buffer entries.
     *
     * @return Number of valid entries. [0,size).
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Converts buffer to a single []
     *
     * @return Point2D.Double[] of all valid elements.
     */
    @Override
    public Object[] toArray() {
        Double[] pArr = new Double[size];
        int idx = 0;
        for (Double p : this) {
            pArr[idx++] = new Double(p.getX(), p.getY());
        }
        return pArr;
    }

    /**
     * Conversion for casting elements to the proper Point2D.Double type.
     *
     * @param a the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @return [] of validated Point2D.Double.
     * @param <T> [] to validate.
     */
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }
        int idx = 0;
        for (Double p : this) {
            a[idx++] = (T) new Double(p.getX(), p.getY());
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    /**
     * Inserts double x and double y into the buffer. Stored with minimal overhead.
     *
     * @param point element whose presence in this collection is to be ensured
     * @return True if store is successful. False if invalid input parameter.
     */
    @Override
    public boolean add(Double point) {
        if (point == null) {
            return false;
        }
        int index = (head + size) % capacity;
        x[index] = point.getX();
        y[index] = point.getY();
        if (size < capacity) {
            ++size;
        } else {
            head = (head + 1) % capacity;
        }
        return true;
    }

    /**
     * Uses add() method to insert each element into the buffer.
     *
     * @param c collection containing elements to be added to this collection
     * @return True if all elements were valid.
     */
    @Override
    public boolean addAll(Collection<? extends Double> c) {
        for (Double p : c) {
            if (!add(p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Searches buffer for given value after verification. Object o must be Point2D.Double.
     *
     * @param o element to be removed from this collection, if present.
     * @return True if found. False otherwise.
     */
    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Double target)) {
            return false;
        }
        boolean found = false;
        double[] newX = new double[capacity];
        double[] newY = new double[capacity];
        int newSize = 0;

        for (int i = 0; i < size; ++i) {
            int idx = (head + i) % capacity;
            double currentX = x[idx];
            double currentY = y[idx];
            if (!found && GraphTools.matchesPoint(currentX, currentY, target)) {
                found = true;
                continue;
            }
            newX[newSize] = currentX;
            newY[newSize] = currentY;
            newSize++;
        }
        if (found) {
            x = newX;
            y = newY;
            head = 0;
            size = newSize;
            cursor = head;
        }
        return found;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<>() {
            private int iteratorIndex = 0;
            private int iteratorCursor = head;
            private final Double reusable = new Double();

            @Override
            public boolean hasNext() {
                return iteratorIndex < size;
            }

            @Override
            public Double next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                reusable.setLocation(x[iteratorCursor], y[iteratorCursor]);
                iteratorCursor = (iteratorCursor + 1) % capacity;
                ++iteratorIndex;
                return reusable;
            }
        };
    }
}
