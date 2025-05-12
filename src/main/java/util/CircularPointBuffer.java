package util;

import lombok.Getter;

import java.util.*;

/**
 * A fixed-size circular buffer that stores paired (x, y) coordinate values.
 * When full, the buffer overwrites the oldest elements in FIFO order.
 * Useful for graphing or time-series data where old data can be discarded as new data arrives.
 */
public final class CircularPointBuffer implements Iterable<Pair>, Collection<Pair> {
    private double[] x; // TODO: Make these into byte[] buffer for memory efficiency
    private double[] y;
    private int head; // first element index of container
    private int cursor; // Physical, low level pointer
    private int iterCount; // High-level counter, user visible
    private int size;
    @Getter private int capacity;

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

    /**
     * Moves cursor forward one step in buffer. If pre-cursor is at last valid index in buffer, post-cursor
     * will be pointing to head. This can be done infinitely without mutation.
     *
     * @return Instance of this class for method chaining.
     */
    public CircularPointBuffer advanceCursorWrapped() {
        cursor = ++cursor % capacity;
        iterCount = (capacity + cursor - head) % capacity;
        return this;
    }

    /**
     * Sets cursor to the beginning (top) of buffer.
     *
     * @return Instance of this class for method chaining.
     */
    public CircularPointBuffer resetCursor() {
        cursor = head;
        iterCount = 0;
        return this;
    }

    /**
     * Boolean checks if relative cursor position in buffer is less than the current number
     * of elements in the buffer.
     *
     * @return True when next index is valid index. False otherwise.
     */
    public boolean hasNext() {
        return iterCount < size;
    }

    /**
     * Simple getter function for retrieving, without mutating, the x cursor is pointing to.
     *
     * @return x value cursor is pointing to.
     */
    public double cursorGetX() {
        return x[cursor];
    }

    /**
     * Simple getter function for retrieving, without mutating, the y cursor is pointing to.
     *
     * @return y value cursor is pointing to.
     */
    public double cursorGetY() {
        return y[cursor];
    }

    /**
     * Retrieves Point at a given index. This process mutates cursor. Value remains stored.
     *
     * @param index The index to retrieve.
     * @return Pair representing the 2D data point at index in buffer.
     */
    public Pair get(int index) {
        setCursor(index);
        return new Pair(x[cursor], y[cursor]);
    }

    /**
     * Sets the logical cursor to the given index in the buffer.
     * The index must be in the range [0, size). Cursor is mutated.
     *
     * @param index logical position relative to head (0 = head, 1 = head+1, etc.).
     * @throws IndexOutOfBoundsException if index is outside valid range.
     */
    public void setCursor(int index) {
        if (index < 0 || index >= size) {
            final String iOOBE = "Index " + index + " out of bounds for size " + size;
            throw new IndexOutOfBoundsException(iOOBE);
        }
        cursor = (head + index) % capacity;
        iterCount = index;
    }

    /**
     * Removes the top (head) element from the buffer and returns it as a Pair.
     *
     * @return Pair containing [x, y] at the head, or null if buffer is empty.
     */
    public Pair pop() {
        if (size == 0) {
            return null;
        }
        Pair point = new Pair(x[head], y[head]);
        head = (head + 1) % capacity;
        if (cursor == head) {
            cursor = head;
        }
        --size;
        return point;
    }

    /**
     * Peeks ahead in the buffer by a given offset from the current cursor.
     * Does not modify cursor or iterCount.
     *
     * @param offset number of steps to peek ahead
     * @return Pair representing the point at (cursor + offset) % capacity
     * @throws IndexOutOfBoundsException if offset is negative or offset >= size
     */
    public Pair peek(int offset) {
        if (offset < 0 || offset >= size) {
            final String iOOBE = "Offset " + offset + " out of bounds for size " + size;
            throw new IndexOutOfBoundsException(iOOBE);
        }
        int peekIndex = (cursor + offset) % capacity;
        return new Pair(x[peekIndex], y[peekIndex]);
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
     * Checks buffer for Pair.
     *
     * @param o element whose presence in this collection is to be tested. Should be Pair, otherwise
     *          returns false.
     * @return True if point coordinates match existing buffer element.
     */
    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Pair p)) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            int idx = (head + i) % capacity;
            if (Double.compare(x[idx], p.first) == 0 && Double.compare(y[idx], p.second) == 0) {
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
     * Sorts the buffer contents in-place by X values.
     */
    public void sortByX() {
        sort(Comparator.comparingDouble(a -> a.first));
    }

    /**
     * Sorts the buffer contents in-place by Y values.
     */
    public void sortByY() {
        sort(Comparator.comparingDouble(a -> a.second));
    }

    /**
     * Internal sort function that linearizes the buffer, sorts, and writes back.
     */
    private void sort(Comparator<Pair> comparator) {
        if (size <= 1) return;

        // Step 1: Convert circular buffer to linear list
        List<Pair> linear = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            int idx = (head + i) % capacity;
            linear.add(new Pair(x[idx], y[idx]));
        }
        linear.sort(comparator);

        for (int i = 0; i < size; ++i) {
            x[i] = linear.get(i).first;
            y[i] = linear.get(i).second;
        }
        head = 0;
        cursor = 0;
    }

    /**
     * Returns a copy of all X values currently stored in the buffer.
     *
     * @return a new double array containing the current X values.
     */
    public double[] getDataXCopy() {
        return getDataXCopy(0);
    }

    /**
     * Returns a copy of all X values currently stored in the buffer, with an optional offset applied to each value.
     *
     * @param valueOffset an integer value to add to each X coordinate in the copy.
     * @return a new double array containing the offset-adjusted X values.
     */
    public double[] getDataXCopy(double valueOffset) {
        double[] copy = new double[size];
        for (int i = 0; i < size; i++) {
            int idx = (head + i) % capacity;
            copy[i] = x[idx] + valueOffset;
        }
        return copy;
    }

    /**
     * Returns a copy of all Y values currently stored in the buffer.
     *
     * @return a new double array containing the current Y values.
     */
    public double[] getDataYCopy() {
        return getDataYCopy(0);
    }

    /**
     * Returns a copy of all Y values currently stored in the buffer, with an optional offset applied to each value.
     *
     * @param valueOffset an integer value to add to each Y coordinate in the copy.
     * @return a new double array containing the offset-adjusted Y values.
     */
    public double[] getDataYCopy(double valueOffset) {
        double[] copy = new double[size];
        for (int i = 0; i < size; i++) {
            int idx = (head + i) % capacity;
            copy[i] = y[idx] + valueOffset;
        }
        return copy;
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
     * @return Pair[] of all valid elements.
     */
    @Override
    public Pair[] toArray() {
        Pair[] pArr = new Pair[size];
        int idx = 0;
        for (Pair p : this) {
            pArr[idx++] = new Pair(p.first, p.second);
        }
        return pArr;
    }

    /**
     * Conversion for casting elements to the proper Pair type.
     *
     * @param a the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @return [] of validated Pair.
     * @param <T> [] to validate.
     */
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }
        int idx = 0;
        for (Pair p : this) {
            a[idx++] = (T) new Pair(p.first, p.second);
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
     * @return True if store is successful. False otherwise
     */
    @Override
    public boolean add(Pair point) {
        if (point == null) {
            return false;
        }
        int index = (head + size) % capacity;
        x[index] = point.first;
        y[index] = point.second;
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
    public boolean addAll(Collection<? extends Pair> c) {
        for (Pair p : c) {
            if (!add(p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Searches buffer for given value after verification. Object o must be Pair.
     *
     * @param o element to be removed from this collection, if present.
     * @return True if found. False otherwise.
     */
    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Pair target)) {
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
    public Iterator<Pair> iterator() {
        return new Iterator<>() {
            private int iteratorIndex = 0;
            private int iteratorCursor = head;
            private final Pair reusable = new Pair();

            @Override
            public boolean hasNext() {
                return iteratorIndex < size;
            }

            @Override
            public Pair next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                reusable.setPair(x[iteratorCursor], y[iteratorCursor]);
                iteratorCursor = (iteratorCursor + 1) % capacity;
                ++iteratorIndex;
                return reusable;
            }
        };
    }
}
