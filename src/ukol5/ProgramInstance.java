package ukol5;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgramInstance {
    
    int variablesCount;
    int clausesCount;
    HashMap<Integer,Variable> variables = new HashMap<>();
    ArrayList<Clause> clauses = new ArrayList<>();
    
    public ProgramInstance(int variables,int clauses){
        this.variablesCount = variables;
        for(int i = 1 ; i <= variablesCount ; i++){
            this.variables.put(i, new Variable(i, 0));
        }
    }
    
    public boolean isValid(Result result){
        boolean valid = true;
        for(Clause clause : clauses){
            if(!clause.isValid(result)){
                valid = false;
                break;
            }
        }
        return valid;
    }
    
    public int countOfInvalidClauses(Result result){
        int invalidClauses = 0;
        for(Clause clause : clauses){
            if(!clause.isValid(result)){
                invalidClauses++;                
            }
        }
        return invalidClauses;
    }
    
    public int summaryWeight(Result result){
        int summaryWeight = 0;
        for(Variable variable : variables.values()){
            if(result.evaluation[variable.id]){
                summaryWeight += variable.weight;
            }
        }
        return summaryWeight;
    }
    
    public static class Clause {
        
        ArrayList<Pair<Boolean,Variable>> variables = new ArrayList<>();
        
        public void addVariable(Boolean sign, Variable variable){
            variables.add(new Pair<>(sign, variable));
        }                
        
        public boolean isValid(Result result){
            boolean valid = false;
            for(Pair pair : variables){
                if(((Boolean)pair.left && result.evaluation[((Variable)pair.right).id]) ||
                  (!(Boolean)pair.left && !result.evaluation[((Variable)pair.right).id])){
                    valid = true;
                    break;
                }
            }
            return valid;
        }
        
        private class Pair<L,R> {

            private final L left;
            private final R right;

            public Pair(L left, R right) {
              this.left = left;
              this.right = right;
            }

            public L getLeft() { return left; }
            public R getRight() { return right; }

            @Override
            public int hashCode() { return left.hashCode() ^ right.hashCode(); }

            @Override
            public boolean equals(Object o) {
              if (o == null) return false;
              if (!(o instanceof Pair)) return false;
              Pair pairo = (Pair) o;
              return this.left.equals(pairo.getLeft()) &&
                     this.right.equals(pairo.getRight());
            }
        }
    }
    
    public static class Variable {
        
        int id;
        int weight;
        
        public Variable(int id, int weight){
            this.id = id;
            this.weight = weight;
        }        
    }
    
    @Override
    public String toString(){
        return "variables: " + variablesCount + ", clauses: " + clausesCount;
    } 
}