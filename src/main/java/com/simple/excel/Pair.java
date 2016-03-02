package com.simple.excel;

/**
 * Pair tuple.
 */
public class Pair<T, U> {
    private final T first;
    private final U second;
    private transient final int hash;

    public Pair(final T f, final U s) {
        this.first = f;
        this.second = s;
        hash = (first == null ? 0 : first.hashCode() * 31)
                + (second == null ? 0 : second.hashCode());
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || !(getClass().isInstance(otherObject))) {
            return false;
        }
        Pair<T, U> other = getClass().cast(otherObject);
        return first == null ? other.first == null : first.equals(other.first)
                && second == null ? other.second == null : second.equals(other.second);
    }

}
