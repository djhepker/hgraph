package util;

public final class Pair {
    public double first, second;

    public Pair() {
        this(0.0, 0.0);
    }

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public Pair(double first, double second) {
        this.first = first;
        this.second = second;
    }

    public Pair setPair(int x, int y) {
        this.first = x;
        this.second = y;
        return this;
    }

    public Pair setPair(double x, double y) {
        this.first = x;
        this.second = y;
        return this;
    }
}
