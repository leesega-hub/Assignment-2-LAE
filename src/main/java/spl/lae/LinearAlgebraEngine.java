package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // Done: create executor with given thread count
        //The rest of the fields are already initialized.
        executor = new TiredExecutor(numThreads);

    }

    public ComputationNode run(ComputationNode computationRoot) {
        // DONE: resolve computation tree step by step until final matrix is produced
        if (computationRoot == null) {
            throw new IllegalArgumentException("computationRoot is null");
        }
        while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
            loadAndCompute(computationRoot);
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        // DONE: load operand matrices
        // DONE: create compute tasks & submit tasks to executor
        ComputationNodeType nodeType = node.getNodeType();
        if (nodeType == ComputationNodeType.MATRIX) {
            //There's nothing to compute.
            return;
        }
        //Creating a binary tree from the input for simplification.
        node.associativeNesting();
        //Finding the next node to resolve and preparing a list for his task.
        ComputationNode resolvable = node.findResolvable();
        List<Runnable> tasks;
        //Loading the matrices and preparing the tasks according to the operation type:
        //We will use switch to keep a clean code with multiple scenarios
        switch (resolvable.getNodeType()) {
            case ADD:
                leftMatrix.loadRowMajor(resolvable.getChildren().get(0).getMatrix());
                rightMatrix.loadRowMajor(resolvable.getChildren().get(1).getMatrix());
                tasks = createAddTasks();
                break;
            case MULTIPLY:
                leftMatrix.loadRowMajor(resolvable.getChildren().get(0).getMatrix());
                rightMatrix.loadColumnMajor(resolvable.getChildren().get(1).getMatrix());
                tasks = createMultiplyTasks();
                break;
            case NEGATE:
                leftMatrix.loadRowMajor(resolvable.getChildren().get(0).getMatrix());
                tasks = createNegateTasks();
                break;
            case TRANSPOSE:
                leftMatrix.loadRowMajor(resolvable.getChildren().get(0).getMatrix());
                tasks = createTransposeTasks();
                break;
            default:
                throw new IllegalArgumentException("Unknown nodeType");
        }
        executor.submitAll(tasks);
        //Updating the value of the result in the left Matrix to keep in-place attitude and prevent a double call
        resolvable.resolve(leftMatrix.readRowMajor());
    }

    public List<Runnable> createAddTasks() {
        // DONE: return tasks that perform row-wise addition
        if (leftMatrix.length() != rightMatrix.length()) {
            throw new IllegalArgumentException("Addition is only performed for same size matrices.");
        }
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            tasks.add(() -> { //Lambada runnable function
                leftMatrix.get(rowIndex).add(rightMatrix.get(rowIndex));
            });
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // DONE: return tasks that perform row × matrix multiplication
        if (leftMatrix.length() == 0 || rightMatrix.length() == 0) {
            throw new IllegalArgumentException("Multiplication can't be performed with empty matrices");
        }
        if (!(leftMatrix.isRowMajor()) || rightMatrix.isRowMajor() ||
                leftMatrix.get(0).length() != rightMatrix.get(0).length()) {
            throw new IllegalArgumentException("the sizes of the matrices are not suitable for multiplication.");
        }
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            tasks.add(() -> { //Lambada runnable function
                leftMatrix.get(rowIndex).dot(rightMatrix.get(rowIndex));
            });
        }
        return tasks;

    }

    public List<Runnable> createNegateTasks() {
        // DONE: return tasks that negate rows
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            tasks.add(() -> { //Lambada runnable function
                leftMatrix.get(rowIndex).negate();
            });
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        // DONE: return tasks that transpose rows
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            tasks.add(() -> { //Lambada runnable function
                leftMatrix.get(rowIndex).transpose();
            });
        }
        return tasks;
    }

    public String getWorkerReport() {
        // DONE: return summary of worker activity
        return executor.getWorkerReport();
    }
}
