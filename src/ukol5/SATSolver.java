package ukol5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;
import ukol5.ProgramInstance.Variable;

public class SATSolver {
    
    public static Result solveByBruteForceMethod(final ProgramInstance instance){
        Result best = null;
        
        int pow = (int)Math.pow(2,instance.variablesCount);        
        
        for(long i = 0;i < pow;i++){
            Result helpResult = new Result(instance.variablesCount);            

            long delenec = i;
            for(int j = 1; j < instance.variablesCount + 1 && delenec > 0;j++){                
                if(delenec % 2 == 1){         
                    helpResult.evaluation[j] = true;
                }                   
                delenec /= 2;                
            }
            helpResult.weight = instance.summaryWeight(helpResult);
            
            if(best == null && instance.isValid(helpResult)){
                best = helpResult;   
                best.valid = instance.isValid(best);
            }
            
            if(best != null && instance.isValid(helpResult) && helpResult.weight > best.weight){
                best = helpResult;
                best.valid = instance.isValid(best);
            }
        }         
        return best;
    }
    
    public static Result solveByGeneticAlgorithm(final ProgramInstance instance){  
        
        int sumOfWeight = 0;
        for(Variable variable : instance.variables.values()){
            sumOfWeight += variable.weight;
        }
        
        // parametry
        int populationSize = 400; // počet prvků v populaci
        int generationsCount = 100; // počet generací
        int crossbreedingCount = populationSize / 10; // počet křížení v každé generaci
        float mutationProbability = 0.8f; // pravděpodobnost mutace každého prvku při každé generaci
        int selectionSize = 2; // velikost výběru do turnaje při selekci
        final int bonusClause = sumOfWeight / 5; // bonus za splněnou klauzuli
        final int bonusFormula = sumOfWeight * 2; // bonus za splnění celé formule
        int elitism = 0; // počet nejlepších prvků, které půjdou do nové generace
        int improvementCount = populationSize / 50; // počet běhů zlepšujícího algoritmu
        
        ArrayList<Result> population = new ArrayList<>(populationSize);
        Result best = new Result(instance.variablesCount);
        
        // počáteční populace
        for(int i = 0; i < populationSize ; i++){
            Result result = new Result(instance.variablesCount);
            for(int j = 1; j <= result.variablesCount; j++){                
                result.evaluation[j] = Math.random() < 0.5;
            }   
            result.weight = instance.summaryWeight(result);
            population.add(result);
        }        
        
        // určení nové generace
        for(int i = 0; i < generationsCount; i++){           
            
            // selekce
            ArrayList<Result> newPopulation = new ArrayList<>(populationSize);
            
            // globálně nejlepší vždy vložím
            newPopulation.add(new Result(best));
            
            // dále vložím několik nejlepších z minulé populace
            if(elitism > 0){
                Collections.sort(population, new Comparator<Result>(){
                    @Override
                    public int compare(Result r1, Result r2) {
                        return fitness(instance, r1, bonusClause, bonusFormula) - fitness(instance, r2, bonusClause, bonusFormula);
                    }
                });
                for(int j = 0; j < elitism; j++){
                    newPopulation.add(new Result(population.get(j)));
                }   
            }
            
            // nakonec vložím jeden náhodný prvek (aby populace moc rychle nezdegenerovala)
            Result result = new Result(instance.variablesCount);
            for(int j = 1; j <= result.variablesCount; j++){                
                result.evaluation[j] = Math.random() < 0.5;
            }   
            result.weight = instance.summaryWeight(result);
            newPopulation.add(result);
            
            // pro ostatní prvky používám turnajový výběr
            for(int j = elitism + 2; j < populationSize ; j++){
                TreeMap<Integer, Result> selection = new TreeMap<>();
                ArrayList<Integer> randoms = new ArrayList<>(selectionSize);
                for(int k = 0; k < selectionSize; k++){
                    int random;
                    do{
                        random = new Random().nextInt(populationSize);
                    } while(randoms.contains(random));
                    randoms.add(random);
                    Result r = population.get(random);
                    
                    // fitness funkce
                    int rank = fitness(instance, r, bonusClause, bonusFormula);
                    
                    selection.put(rank, r);
                }
                newPopulation.add(new Result(selection.get(selection.lastKey())));
            }
            population = newPopulation;
               
            // křížení
            for(int j = 0; j < crossbreedingCount ; j++){
                Result c1 = population.get(new Random().nextInt(populationSize));
                Result c2 = population.get(new Random().nextInt(populationSize));
                int point1, point2;
                                
                if(Math.random() < 0.65){
                    // jednobodove křížení
                    point1 = 0;
                    point2 = new Random().nextInt(instance.variablesCount);
                }else {
                    // dvoubodové křížení
                    point1 = new Random().nextInt(instance.variablesCount) / 4;
                    do{
                        point2 = new Random().nextInt(instance.variablesCount);
                    }while(point1 >= point2);
                }
                for(int k = point1 ; k < point2 ; k++){
                    boolean store = c1.evaluation[k];
                    c1.evaluation[k] = c2.evaluation[k];
                    c2.evaluation[k] = store;
                }
                    c1.weight = instance.summaryWeight(c1);
                    c2.weight = instance.summaryWeight(c2);
            }
            
            // improvement
            for(int j = 0; j < improvementCount ; j++){
                Result c1 = population.get(new Random().nextInt(populationSize));
                Result c2 = new Result(c1);
                for(int k = 1; k <= c1.variablesCount; k++){
                    c2.evaluation[k] = !c2.evaluation[k];
                    if(instance.countOfInvalidClauses(c2) < instance.countOfInvalidClauses(c1)){
                        c1.evaluation[k] = c2.evaluation[k];
                    }else{
                        c2.evaluation[k] = !c2.evaluation[k];
                    }
                }  
                c1.weight = instance.summaryWeight(c1);
            }
            
            // mutace
            for(Result c1 : population){
                int mutate = new Random().nextInt(100);
                if(mutate < (mutationProbability * 100)){
                    int point = new Random().nextInt(instance.variablesCount + 1);
                    c1.evaluation[point] = !c1.evaluation[point];
                    c1.weight = instance.summaryWeight(c1);
                }
            }            
        
            // vybrání nejlepšího reprezentanta z celé generace        
            for(Result res : population){
                if((instance.summaryWeight(res) > instance.summaryWeight(best)) && instance.isValid(res)){
                    best = res;  
                }                
            }  
        }
              
        best.valid = instance.isValid(best);
        return best;       
    }
    
    // fitness funkce
    private static int fitness(ProgramInstance instance, Result r, int bonusClause, int bonusFormula){
        int countOfInvalidClauses = instance.countOfInvalidClauses(r);
        int validClauses = instance.clausesCount - countOfInvalidClauses;
        int rank = instance.summaryWeight(r) + validClauses * bonusClause + (countOfInvalidClauses == 0 ? bonusFormula: 0);
        return rank;
    }
}
