package memory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestSharedMetrix {

    @Test
    public void TestConstructorEmpty(){
        SharedMatrix sm = new SharedMatrix();
        assertEquals(0, sm.length());
    }

    @Test
    public void TestConstructorWithData(){
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        };
        SharedMatrix sm = new SharedMatrix(data);
        assertEquals(3, sm.length());
    }

    @Test
    public void TestLoadRowMajorAndGetVectors(){
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        };
        SharedMatrix sm = new SharedMatrix();
        sm.loadRowMajor(data);
        SharedVector[] compSharedVectors = new SharedVector[sm.length()];
        for(int i=0; i<sm.length();i++){
            compSharedVectors[i] = sm.get(i);
        }
        assertEquals(3, compSharedVectors.length);
        for (int i = 0; i < data.length; i++) {
            SharedVector sv = compSharedVectors[i];
            for (int j = 0; j < data[i].length; j++) {
                assertEquals(data[i][j], sv.get(j), 0.0001);
            }
        }
    }

    @Test

    public void TestLoadColumnMajorAndGetVectors(){
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        };
        SharedMatrix sm = new SharedMatrix();
        sm.loadColumnMajor(data);
        SharedVector[] vectors = new SharedVector[sm.length()];
        for(int i=0; i<sm.length();i++){
            vectors[i] = sm.get(i);
        }
        assertEquals(3, vectors.length);
        for (int i = 0; i < data[0].length; i++) {
            SharedVector sv = vectors[i];
            for (int j = 0; j < data.length; j++) {
                assertEquals(data[j][i], sv.get(j), 0.0001);
            }
        }

    }

    @Test
    public void TestRead(){
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        };
        SharedMatrix RowMx = new SharedMatrix();
        RowMx.loadRowMajor(data);
        SharedMatrix ColMx = new SharedMatrix();
        ColMx.loadColumnMajor(data);
        for (int i=0;i<data.length;i++){
            for(int j=0;j<data[0].length;j++){
                assertEquals(data[i][j], RowMx.get(i).get(j), 0.0001);
                assertEquals(data[i][j], ColMx.get(j).get(i), 0.0001);
            }
        }
    }


    @Test 
    public void TestGetandLength(){
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0}
        };
        SharedMatrix RowMx = new SharedMatrix();
        RowMx.loadRowMajor(data);
        SharedMatrix ColMx = new SharedMatrix();
        ColMx.loadColumnMajor(data);
        assertEquals(2, RowMx.length());
        assertEquals(3, ColMx.length());
        assertThrows(IndexOutOfBoundsException.class, () -> RowMx.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> ColMx.get(4));
    }

    @Test 
    public void TestOrientation(){
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0}
        };
        SharedMatrix RowMx = new SharedMatrix();
        RowMx.loadRowMajor(data);
        SharedMatrix ColMx = new SharedMatrix();
        ColMx.loadColumnMajor(data);
        assertEquals(VectorOrientation.ROW_MAJOR, RowMx.getOrientation());
        assertEquals(VectorOrientation.COLUMN_MAJOR, ColMx.getOrientation());
        assertEquals(true,RowMx.isRowMajor());
        assertEquals(false,ColMx.isRowMajor());
    }

}
