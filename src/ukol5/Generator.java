package ukol5;

import java.util.ArrayList;
import java.util.Random;

class Generator {

    public static ProgramInstance generateInstance(int clauses, int variables, int literalsForClause, int maxWeight){
        ProgramInstance instance = new ProgramInstance(variables,clauses);
        Random random = new Random();
        
        for(int i = 0 ; i < clauses ; i++){
            ProgramInstance.Clause clause = new ProgramInstance.Clause();
            instance.clauses.add(clause);
            
            for (int j = 0 ; j < literalsForClause ; j++ ) {
                
                ArrayList<Integer> generatedVariables = new ArrayList<>();
                int var;
                do {
                    var = random.nextInt(variables - 1);
                } while(generatedVariables.contains(var));
                generatedVariables.add(var);
                var++; // abych se zbavil 0
                
                ProgramInstance.Variable variable = instance.variables.get(var);
                clause.addVariable(Math.random() < 0.5, variable);
            }
        }
        
        for(int i = 1 ; i <= variables ; i++ ){
            ProgramInstance.Variable variable = instance.variables.get(i);
            variable.weight = random.nextInt(maxWeight);
        }
        
        return instance;
    }
}
