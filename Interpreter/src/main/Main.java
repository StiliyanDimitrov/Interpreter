package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import interpreter.Interpreter;
import interpreter.TestStatistic;
import lexer.Lexer;

/**
 * main class of a program to interpret commands
 * @author st
 *
 */
public class Main {

/**
 * method to check whether to read more lines
 * @param line current readed line
 * @return boolean whether to continue to read lines
 */
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
    
   /**
    * main method for interpret commands
    * @param args
    */
    public static void main(String[] args) {
    	Scanner userInput = new Scanner(System.in);
    	
    	try {
    		System.out.print(">");
    		String currentLineText;
    		DataStorage mainData = new DataStorage();//DataStorage.getInstance();
    		List<TestStatistic> testStatsList = new ArrayList<TestStatistic>();
    		
    		DataStorage otherProgramData = new DataStorage();
    		List<TestStatistic> otherProgramTestStatsList = new ArrayList<TestStatistic>();
    		boolean otherProgramBlock = false;
    		
    		while (userInput.hasNext()) {
                	StringBuilder source = new StringBuilder();
                	
                	do {
                		currentLineText = userInput.nextLine();
                		source.append(currentLineText.trim() + "\n");
                		System.out.print(">");
                	}
                	while(advanceReadEndSymbols(currentLineText));  
                	
                	Interpreter interpreter;
                    Lexer lexer = new Lexer(source.toString());
                    String[] allLines = lexer.getLines();
                    if(!otherProgramBlock) {
                    	interpreter = new Interpreter(allLines, mainData,testStatsList);
                    }
                    else {
                    	interpreter = new Interpreter(allLines, otherProgramData,otherProgramTestStatsList);
                    }
                    
            		String interpretResult = interpreter.interpret();
            		System.out.println(interpretResult);
            		System.out.print(">");
            		
            		if(currentLineText.trim().startsWith("connect")) {
                		if(otherProgramBlock == true) {
                			System.out.println("Not allowed command");
                			System.out.print(">");
                			continue;
                		}
            			otherProgramBlock = true;
                		otherProgramData = new DataStorage();
                		otherProgramTestStatsList = new ArrayList<TestStatistic>();
                	}
                	else if(currentLineText.trim().startsWith("disconnect")) {
                		if(otherProgramBlock == false) {
                			System.out.println("Not allowed command");
                			System.out.print(">");
                			continue;
                		}
                		otherProgramBlock = false;
                	}
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