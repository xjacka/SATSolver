package ukol5;

import java.util.Arrays;

public class Result {
        
    int weight;
    int variablesCount;
    boolean [] evaluation;
    boolean valid = false;

    public Result(int variablesCount) {
        this.variablesCount = variablesCount;
        this.evaluation = new boolean[variablesCount + 1];
    }        
    
    public Result(Result result){
        this.weight = result.weight;
        this.variablesCount = result.variablesCount;
        this.evaluation = new boolean[variablesCount + 1];
//        for(int i=0; i < result.evaluation.length; i++){
//            this.evaluation[i] = result.evaluation[i];
//        }
        System.arraycopy(result.evaluation, 0, this.evaluation, 0, result.evaluation.length );
    }
    
    public void setEvaluation(int index, boolean evaluation){
        if(index > 0 && index < variablesCount + 1){
            this.evaluation[index] = evaluation;
        }else{
            System.out.println("array out of bound exception");
        }
    }
    
    @Override
    public String toString(){
        return this.valid + "-w: " + this.weight + " " + Arrays.toString(this.evaluation);
    }
}