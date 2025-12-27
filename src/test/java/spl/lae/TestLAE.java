package spl.lae;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import parser.*;
import memory.*;
import scheduling.*;
import spl.lae.*;
import java.util.List;


public class TestLAE {
    @Test
    public void TestAdd() {
        // Create two matrices for addition
        double[][] dataA = {
            {1.0, 2.0},
            {3.0, 4.0}
        };
        double[][] dataB = {
            {5.0, 6.0},
            {7.0, 8.0}
        };

        // Create computation nodes for the matrices
        ComputationNode matrixA = new ComputationNode(dataA);
        ComputationNode matrixB = new ComputationNode(dataB);

        // Create an addition node
        ComputationNode additionNode = new ComputationNode(ComputationNodeType.ADD, List.of(matrixA, matrixB));
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        ComputationNode resultNode = lae.run(additionNode);

        // Verify the result
        double[][] expectedData = {
            {6.0, 8.0},
            {10.0, 12.0}
        };
        double[][] resultData = resultNode.getMatrix();

        assertArrayEquals(expectedData, resultData);
    }

    @Test
    public void TestMulty() {
        // Create two matrices for addition
        double[][] dataA = {
            {1.0, 2.0},
            {3.0, 4.0}
        };
        double[][] dataB = {
            {5.0, 6.0},
            {7.0, 8.0}
        };

        // Create computation nodes for the matrices
        ComputationNode matrixA = new ComputationNode(dataA);
        ComputationNode matrixB = new ComputationNode(dataB);

        // Create an addition node
        ComputationNode additionNode = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(matrixA, matrixB));
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        ComputationNode resultNode = lae.run(additionNode);

        // Verify the result
        double[][] expectedData = {
            {19.0, 22.0},
            {43.0, 50.0}
        };
        double[][] resultData = resultNode.getMatrix();

        assertArrayEquals(expectedData, resultData);
    }

    @Test
    public void TestNegate() {
        // Create two matrices for addition
        double[][] dataA = {
            {1.0, 2.0},
            {3.0, 4.0}
        };

        // Create computation nodes for the matrices
        ComputationNode matrixA = new ComputationNode(dataA);

        // Create an addition node
        ComputationNode additionNode = new ComputationNode(ComputationNodeType.NEGATE, List.of(matrixA));
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        ComputationNode resultNode = lae.run(additionNode);

        // Verify the result
        double[][] expectedData = {
            {-1.0, -2.0},
            {-3.0, -4.0}
        };
        double[][] resultData = resultNode.getMatrix();

        assertArrayEquals(expectedData, resultData);
    }

    @Test
    public void TestTranspose() {
        // Create two matrices for addition
        double[][] dataA = {
            {1.0, 2.0},
            {3.0, 4.0}
        };

        // Create computation nodes for the matrices
        ComputationNode matrixA = new ComputationNode(dataA);

        // Create an addition node
        ComputationNode additionNode = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(matrixA));
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        ComputationNode resultNode = lae.run(additionNode);

        // Verify the result
        double[][] expectedData = {
            {1.0, 3.0},
            {2.0, 4.0}
        };
        double[][] resultData = resultNode.getMatrix();

        assertArrayEquals(expectedData, resultData);
    }

    @Test
    public void TestWorkerReport() {
        // Create two matrices for addition
        double[][] dataA = {
            {1.0, 2.0},
            {3.0, 4.0}
        };
        double[][] dataB = {
            {5.0, 6.0},
            {7.0, 8.0}
        };

        // Create computation nodes for the matrices
        ComputationNode matrixA = new ComputationNode(dataA);
        ComputationNode matrixB = new ComputationNode(dataB);

        // Create an addition node
        ComputationNode additionNode = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(matrixA, matrixB));
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        ComputationNode resultNode = lae.run(additionNode);

        String report = lae.getWorkerReport();
        System.out.println(report);
        assertNotNull(report);
        
    }

    @Test 
    public void TestExceptionOnNullInput() {
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        assertThrows(NullPointerException.class, () -> {
            lae.run(null);
        });
    } 
    
    @Test
    public void TestLengthExseption(){
        double[][] dataA = {
            {1.0, 2.0},
            {3.0, 4.0}
        };
        double[][] dataB = {
            {5.0, 6.0},
            {7.0, 8.0},
            {9.0, 10.0}
        };

        // Create computation nodes for the matrices
        ComputationNode matrixA = new ComputationNode(dataA);
        ComputationNode matrixB = new ComputationNode(dataB);

        // Create an addition node
        ComputationNode additionNode = new ComputationNode(ComputationNodeType.ADD, List.of(matrixA, matrixB));
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        assertThrows(IllegalArgumentException.class, () -> {
            lae.run(additionNode);
        });

        ComputationNode mulComputationNode = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(matrixA, matrixB));
        assertThrows(IllegalArgumentException.class, () -> {
            lae.run(mulComputationNode);
        });

        ComputationNode mulComputationNodeEmpthy = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(new ComputationNode(new double[][]{}), matrixB));
        assertThrows(IllegalArgumentException.class, () -> {
            lae.run(mulComputationNodeEmpthy);
        });


    }

    @Test
    public void TestExample(){
        
    }
    
}
