package interpreter;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import lexer.Lexer;
import lexer.token.Token;
import lexer.token.TokenType;
import main.DataStorage;


public class Interpreter {

	private String[] lines;
	private DataStorage currentData;
	
	public Interpreter(String[] lines, DataStorage currentData) {
		this.lines = lines;
		this.currentData = currentData;
	}
	
	public String ProgramBody() {
		for (String currentLine : lines) {
			//Lexer currentLineLexer = new Lexer(currentLine);
			//List<Token> tokenList = currentLineLexer.getTokens();
			if(currentLine.startsWith("int ") && currentLine.endsWith(";") && 
			   !currentLine.contains("{")) {
				    String[] lineFragments = currentLine.split(";");
				    for(String currentFragment : lineFragments) {
				    	variablesDefinition(currentFragment);
				    }
			}
			
		}	
			
		return "";
	}
	
	
	public String interpret() {
		return ProgramBody();
	}	
	

	public void variablesDefinition(String lineText) {
		String variableName = "";
		String variableValue = "";		
		if(lineText.contains(",")) {
			
		}
		else {
			int assignIndex = lineText.indexOf("=");
			if(assignIndex > 0) {
				if(lineText.startsWith("int ")) {
					int intIndex = lineText.indexOf("int ") + 4;
					variableName = lineText.substring(intIndex,assignIndex).trim();
					variableValue = lineText.substring(assignIndex + 1).replace(" ","");
					currentData.put(variableName, variableValue);
				}
				else {
					
				}
				
			}
			else {
				
			}
            
		}
		
	
	}
	
	public void methodDefinition(String textBlock) {
		
	}
	
	public void testDefinition(String textBlock) {
		
	}
	
	public void calculateResult() {
		
	}
	
	public void connectIPBlock() {
		
	}
	
}
