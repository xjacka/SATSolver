package ukol5;

import java.io.File;
import java.util.Random;

public class Ukol5 {

    public static void main(String[] args) {
        
        // načítá/generuje instance, které lze ověřit hrubou silou
        for(int i = 1; i <= 50; i++){
            ProgramInstance instance;
            Result result1, result2;
            do {
                instance = Parser.parseInput(new File("./data/uf20-0" + i + ".out"));
//                instance = Generator.generateInstance(91,20,3,100);
                result2 = SATSolver.solveByBruteForceMethod(instance);
            } while (result2 == null);
            result1 = SATSolver.solveByGeneticAlgorithm(instance);   

            System.out.println((double)(result2.weight - result1.weight)/(double)result2.weight);
        }
     
        // načítá instance, které není možné ověřit hrubou silou
//        for(int i = 1; i <= 50; i++){
//            ProgramInstance instance;
//            Result result1;
//            instance = Parser.parseInput(new File("./data/100_500/random_ksat (" + i + ").dimacs"));
//            
//            Random random = new Random();
//            for(int j = 1 ; j <= instance.variablesCount ; j++ ){
//                ProgramInstance.Variable variable = instance.variables.get(j);
//                variable.weight = random.nextInt(1000);
//            }
//            result1 = SATSolver.solveByGeneticAlgorithm(instance);   
//            
//            Reporter.print(result1);              
//        }
    }
}
