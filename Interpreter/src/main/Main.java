package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import interpreter.Interpreter;
import interpreter.TestStatistic;
import lexer.Lexer;

public class Main {

static boolean advanceReadEndSymbols(String line){
    	boolean advanceRead = false;
    	if(line.startsWith("test ") && line.endsWith(";")) {
    		return false;
    	}
    		
    	if(line.endsWith(";") || line.endsWith("{") || line.endsWith("}") || line.isEmpty()) {
    		advanceRead = true;
    	}
    	return advanceRead;
    }
    
   
    public static void main(String[] args) {
    	Scanner userInput = new Scanner(System.in);
    	
    	try {
    		System.out.print(">");
    		String currentLineText;
    		DataStorage mainData = DataStorage.getInstance();
    		List<TestStatistic> testStatsList = new ArrayList<TestStatistic>();
    		
    		while (userInput.hasNext()) {
                	StringBuilder source = new StringBuilder();
                	do {
                		currentLineText = userInput.nextLine();
                		source.append(currentLineText.trim() + "\n");
                		System.out.print(">");
                	}
                	while(advanceReadEndSymbols(currentLineText));
                	
                    Lexer lexer = new Lexer(source.toString());
                    String[] allLines = lexer.getLines();
                    Interpreter interpreter = new Interpreter(allLines, mainData,testStatsList);
            		String interpretResult = interpreter.interpret();
            		System.out.println(interpretResult);
            		System.out.print(">");
                }               
            } 
       catch(Exception e) {
                Lexer.error("Exception: " + e.getMessage());
            }
       finally {
    	   if(userInput != null) {
    		   userInput.close();   
    	   }    	   
       }
       
    }
}