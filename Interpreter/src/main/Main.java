package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import interpreter.Interpreter;
import lexer.Lexer;
import lexer.token.Token;

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
                    Interpreter interpreter = new Interpreter(allLines, mainData);
            		interpreter.interpret();
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