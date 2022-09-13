package com.mopstream.common.data;

import java.io.Serializable;

/**
 * X-Y coordinates.
 */
public class Coordinates implements Serializable {
    private final Long x;
    private final double y;

    public Coordinates(Long x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return X-coordinate.
     */
    public Long getX() {
        return x;
    }

    /**
     * @return Y-coordinate.
     */
    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "X:" + x + " Y:" + y;
    }

    @Override
    public int hashCode() {
        return x.hashCode() + (int) y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Coordinates) {
            Coordinates coordinatesObj = (Coordinates) obj;
            return (x.equals(coordinatesObj.getX()) && y == coordinatesObj.getY());
        }
        return false;
    }
}
