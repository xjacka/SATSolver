package ukol5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    
    public static ProgramInstance parseInput(File file){
        BufferedReader reader = null;
        ProgramInstance programInstance = null;
        boolean readWeight = false;
        int weightNumber = 1;
        
        try {
            String text;
            reader = new BufferedReader(new FileReader(file));
            while((text = reader.readLine()) != null){
                
                String [] tokens = (text.trim().replaceAll("( )+", " ")).split(" ");
                
                if(tokens[0].equals("c")) {
                    continue;
                }                
                
                if(tokens[0].equals("%")) {
                    readWeight = true;
                    continue;
                }  
                
                if(tokens[0].equals("#")) {                    
                    continue;
                }  
                
                if(tokens[0].equals("p")) {
                    programInstance = new ProgramInstance(  Integer.parseInt(tokens[2]),
                                                            Integer.parseInt(tokens[3]));
                    continue;
                }
                         
                if(!readWeight){ 
                    ProgramInstance.Clause clause = new ProgramInstance.Clause();
                    programInstance.clauses.add(clause);
                    for (String token : tokens) {
                        int var = Integer.parseInt(token);
                        ProgramInstance.Variable variable = programInstance.variables.get(Math.abs(var));
                        if(var != 0){
                            clause.addVariable(var > 0, variable);
                        }
                    }
                }
                else{
                    ProgramInstance.Variable variable = programInstance.variables.get(weightNumber);
                    variable.weight = Integer.parseInt(tokens[0]);
                    weightNumber++;
                }                
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Ukol5.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Ukol5.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                }
        }
        return programInstance;
    }
}
