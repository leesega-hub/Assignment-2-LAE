package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // DONE: initialize empty matrix
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // DONE: construct matrix as row-major SharedVectors
        loadRowMajor(matrix);
    }

    public void loadRowMajor(double[][] matrix) {
        // DONE: replace internal data with new row-major matrix

        if (matrix == null || matrix.length == 0 || matrix[0].length ==0) {
            throw new IllegalArgumentException("Matrix input can't be null or empty");
        }

        //Using a write lock for all the vectors to insure a correct updating
        SharedVector[] oldVectors = vectors;
        SharedVector[] newVectors = new SharedVector[matrix.length];
        acquireAllVectorWriteLocks(oldVectors);
        try {
            int numRows = matrix.length;
            
            for (int i = 0; i < numRows; i++) {
                double[] vector = matrix[i];
                SharedVector sharedVector = new SharedVector(vector, VectorOrientation.ROW_MAJOR);
                newVectors[i] = sharedVector;
            }
           
        }
        finally {
            // first relese the old vectors lock before swithching
            releaseAllVectorWriteLocks(oldVectors);
             vectors = newVectors;
        }
    }



    public void loadColumnMajor(double[][] matrix) {
        // DONE: replace internal data with new column-major matrix

        if (matrix == null || matrix.length == 0 || matrix[0].length ==0) {
            throw new IllegalArgumentException("Matrix input can't be null or empty");
        }

        //Using a write lock for all the vectors to insure a correct updating
        SharedVector[] oldVectors = vectors;
        SharedVector[] newVectors = new SharedVector[matrix[0].length];
        acquireAllVectorWriteLocks(oldVectors);
        try {
            int numCols = matrix[0].length;
            int numRows = matrix.length;
            
            for (int i = 0; i < numCols; i++) {
                double[] vector = new double[numRows];
                for (int j = 0; j < numRows; j++){
                    vector[j] = matrix[j][i];
                }
                SharedVector sharedVector = new SharedVector(vector, VectorOrientation.COLUMN_MAJOR);
                newVectors[i] = sharedVector;
            }
            
        }
        finally {
            // first relese the old vectors lock before swithching 
            releaseAllVectorWriteLocks(oldVectors);
            vectors = newVectors;
        }
    }


    public double[][] readRowMajor() {
        // DONE: return matrix contents as a row-major double[][]
        //Saving the current state of the array for safety
        SharedVector[] currentVectors = this.vectors;

        if (currentVectors.length == 0) {
            return new double[0][0];
        }

        acquireAllVectorReadLocks(currentVectors);
        try {

            if (isRowMajor()) {
                int numRows = currentVectors.length;
                int numCols = currentVectors[0].length();
                double[][] matrix = new double[numRows][numCols];
                for (int i = 0; i < numRows; i++) {
                    matrix[i] = currentVectors[i].getVector();
                }
                return matrix;
            } else {
                int numCols = currentVectors.length;
                int numRows = currentVectors[0].length();
                double[][] matrix = new double[numRows][numCols];
                for (int i = 0; i < numRows; i++) {
                    double[] vector = new double[numCols];
                    for (int j = 0; j < numCols; j++) {
                        vector[j] = currentVectors[j].get(i);
                    }
                    matrix[i] = vector;
                }
                return matrix;
            }
        }
        finally {
            releaseAllVectorReadLocks(currentVectors);
        }

    }

    public SharedVector get(int index) {
        // DONE: return vector at index
        acquireAllVectorReadLocks(vectors);
        try {
            if (index < 0 || index >= vectors.length) {
                throw new IndexOutOfBoundsException();
            }
            return vectors[index];
        }
        finally {
            releaseAllVectorReadLocks(vectors);
        }
    }

    public int length() {
        // DONE: return number of stored vectors
        //The array is initialized as empty so no null pointer danger. so no need to lock the vectors.
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // DONE: return orientation
        //Creating a local reference of vectors to prevent index out of bounds error in case of change in the original array
        SharedVector[] currentVectors = vectors;
        if (vectors == null || currentVectors.length == 0) {
            throw new IllegalArgumentException("The matrix is empty, no orientation defined.");
        }
        return currentVectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // DONE: acquire read lock for each vector
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // DONE: release read locks
        //Using Lifo strategy - releasing in the opposite order than writeLockAllVectors
        for (int i = vectors.length - 1; i >= 0; i--) {
            vectors[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // DONE: acquire write lock for each vector
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // DONE: release write locks
            //Using Lifo strategy - releasing in the opposite order than writeLockAllVectors
            for (int i = vectors.length - 1; i >= 0; i--) {
                vectors[i].writeUnlock();
            }
    }

    //Assistant function for the class. return true if the matrix is ROW_MAJOR. Return false otherwise.
    public boolean isRowMajor() {
        //No need to lock the vectors because they are already locked when the function is called.
        if (vectors.length == 0) {
            return false;
        }
        if (vectors[0].getOrientation() == VectorOrientation.ROW_MAJOR) {
            return true;
        }
        return false;
    }
}
