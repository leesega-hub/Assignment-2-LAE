package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        //store vector data and its orientation
        this.vector = vector;
        this.orientation = orientation;

    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        if (index < 0 || index > vector.length) {
            throw new IndexOutOfBoundsException();
        }
        //Using readLock because non of the class values have changed
        readLock();
        try {
            return vector[index];
        }
        finally {
            readUnlock();
        }
    }

    public int length() {
        // TODO: return vector length
        //Using readLock because non of the class values have changed
        readLock();
        try {
            return vector.length;
        }
        finally {
            readUnlock();
        }
    }

    public VectorOrientation getOrientation() {
        //TODO: return Orientation
        //Using readLock because non of the class values have changed
        readLock();
        try {
            return orientation;
        }
        finally {
            readUnlock();
        }
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        //Using a writeLock because the function is changing the value of orientation
        writeLock();
        try {
            if (orientation.equals(VectorOrientation.COLUMN_MAJOR)) {
                orientation = VectorOrientation.ROW_MAJOR;
            } else {
                orientation = VectorOrientation.COLUMN_MAJOR;
            }
        }
        finally {
            writeUnlock();
        }
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        //Using a writeLock because the function is changing the values in the vector array
        if (other == null) {
            throw new IllegalArgumentException("input vector is null");
        }
        if (other.vector.length != vector.length || orientation != other.orientation) {
            throw new IllegalArgumentException("Vectors are not suitable for adding");
        }
        writeLock();
        other.readLock();
        try {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = vector[i] + other.vector[i];
            }
        }
        finally {
            //Last in first out strategy
            other.readUnlock();
            writeUnlock();
        }
    }

    public void negate() {
        // TODO: negate vector
        //Using a writeLock because the function is changing the values in the vector array
        writeLock();
        try {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = -vector[i];
            }
        } finally {
            writeUnlock();
        }
    }


    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        //Using readLock because non of the class values have changed
        if (other == null) {
            throw new IllegalArgumentException("input vector is null");
        }
        if (other.vector.length != vector.length || orientation == other.orientation) {
            throw new IllegalArgumentException("Vectors are not suitable for dot product");
        }
        readLock();
        other.readLock();
        try {
            double product = 0;
            for (int i = 0; i < vector.length; i++) {
                product += (vector[i] * other.vector[i]);
            }
            return product;
        }
        finally {
            //Lifo strategy
            other.readUnlock();
            readUnlock();
        }
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix

        if (matrix == null) {
            throw new IllegalArgumentException("matrix input is null");
        }
        if (orientation != VectorOrientation.ROW_MAJOR ||
                matrix.get(0).orientation != VectorOrientation.COLUMN_MAJOR ||
                matrix.get(0).length() != vector.length) {
            throw new IllegalArgumentException("The conditions for row-vector × matrix are not satisfied");
        }
        //Using a writeLock because now the function is changing the values in the vector array
        writeLock();
        try {
            double newVector[] = new double[matrix.length()];
            for (int i = 0; i < matrix.length(); i++) {
                newVector[i] = this.dot(matrix.get(i));
            }
            vector = newVector;
        }
        finally {
            writeUnlock();
        }
    }
}
