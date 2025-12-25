package memory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class TestSharedVector {
    @Test
    public void TestGetandLength(){
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
        SharedVector sv = new SharedVector(data, VectorOrientation.ROW_MAJOR);
        assertEquals(5, sv.length());  
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], sv.get(i)); 
        }
    

        assertThrows(IndexOutOfBoundsException.class, () -> sv.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> sv.get(-1));

    }

    @Test
    public void Testadd(){
        SharedVector sv1 = new SharedVector(new double[]{1.0, 2.0, 3.0}, VectorOrientation.ROW_MAJOR);
        SharedVector sv2 = new SharedVector(new double[]{4.0, 5.0, 6.0}, VectorOrientation.ROW_MAJOR);
        sv1.add(sv2);

        assertEquals(5.0, sv1.get(0), 0.0001);
        assertEquals(7.0, sv1.get(1), 0.0001);
        assertEquals(9.0, sv1.get(2), 0.0001);


        
    }

    @Test
    public void Testdot(){
        SharedVector sv1 = new SharedVector(new double[]{1.0, 2.0, 3.0}, VectorOrientation.ROW_MAJOR);
        SharedVector sv2 = new SharedVector(new double[]{4.0, 5.0, 6.0}, VectorOrientation.COLUMN_MAJOR);
        double result = sv1.dot(sv2);
        assertEquals(32.0, result);

        
        
    }

    @Test
    public void TestOrientationAndTranspose(){
        SharedVector sv = new SharedVector(new double[]{1.0, 2.0, 3.0}, VectorOrientation.ROW_MAJOR);
        assertEquals(VectorOrientation.ROW_MAJOR, sv.getOrientation());

        SharedVector copy = new SharedVector(sv.getVector(), sv.getOrientation());
        VectorOrientation oldOrientation = sv.getOrientation();
        copy.transpose();
        VectorOrientation newOrientation = copy.getOrientation();
        VectorOrientation expected = (oldOrientation == VectorOrientation.ROW_MAJOR) ?
            VectorOrientation.COLUMN_MAJOR : VectorOrientation.ROW_MAJOR;
        assertEquals(expected, newOrientation);

    }

    @Test
    public void TestNegate(){
        SharedVector sv = new SharedVector(new double[]{1.0, 2.0, 3.0}, VectorOrientation.ROW_MAJOR);
        sv.negate();
        assertEquals(-1.0, sv.get(0));
        assertEquals(-2.0, sv.get(1));
        assertEquals(-3.0, sv.get(2));
    }

    @Test
    public void testAddExceptions() {
        SharedVector v1 = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        
        //  different lengths
        SharedVector v2 = new SharedVector(new double[]{1, 2, 3}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v1.add(v2));

        //  different orientations 
        SharedVector v3 = new SharedVector(new double[]{1, 2}, VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v1.add(v3));
    }

    @Test
    public void testDotProductExceptions() {
        SharedVector row = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        SharedVector row2 = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        
        assertThrows(IllegalArgumentException.class, () -> row.dot(row2));
    }

    @Test 
    public void TestVectorMetrixMultiplication(){
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        };
        SharedMatrix RowMx = new SharedMatrix();
        RowMx.loadRowMajor(data);
        SharedVector ColVec = new SharedVector(new double[]{1.0,2.0,3.0}, VectorOrientation.COLUMN_MAJOR);
        SharedVector RowVec = new SharedVector(new double[]{1.0,2.0,3.0}, VectorOrientation.ROW_MAJOR);

        RowVec.vecMatMul(RowMx);
    }

}
