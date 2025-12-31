package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;
import scheduling.TiredExecutor;

public class Main {
    public static void main(String[] args) throws IOException {

        //Getting arguments from the JSON file
        if (args.length < 3) {
            System.out.println("No path to the input JSON file.");
            return;
        }
        int numThreads = Integer.parseInt(args[0]);
        String inputPath = args[1];
        String outputPath = args[2];

        //Initializing LAE (initialized tired executor)
        LinearAlgebraEngine LAE = new LinearAlgebraEngine(numThreads);
        //Initializing input parser
        InputParser parser = new InputParser();

        try {
            ComputationNode root = parser.parse(inputPath);
            //Performing the calculations
            ComputationNode res = LAE.run(root);
            //Writing the result to the JSON file
            OutputWriter.write(res.getMatrix(), outputPath);

        //Catching parse exception
        } catch (ParseException e) {
            OutputWriter.write(e.getMessage(), outputPath);
        }
    }
}