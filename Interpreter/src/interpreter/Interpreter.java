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
		String exprResult = "";
		for (String currentLine : lines) {
			//Lexer currentLineLexer = new Lexer(currentLine);
			//List<Token> tokenList = currentLineLexer.getTokens();
			if(currentLine.startsWith("int ") && currentLine.endsWith(";") && 
			   !currentLine.contains("{")) {
				    String[] lineFragments = currentLine.split(";");
				    for(String currentFragment : lineFragments) {
				    	variablesDefinition(currentFragment, true);
				    }
			}
			else if(!currentLine.startsWith("int ") && !currentLine.startsWith("test ") && 
					currentLine.endsWith(";") && !currentLine.contains("{")) {
				String[] lineFragments = currentLine.split(";");
			    for(String currentFragment : lineFragments) {
			    	variablesDefinition(currentFragment, false);
			    }
			}
			else if((currentLine.startsWith("test ") && currentLine.endsWith(";")) || 
					(!currentLine.endsWith(";") && !currentLine.endsWith("{") && !currentLine.endsWith("}"))) {
				if(currentLine.startsWith("test ")) {
					
				}
				else if(!currentLine.contains("(") && !currentLine.contains("connect") &&
						!currentLine.contains("disconnect") && !currentLine.contains("most-failing-test") &&
						!currentLine.contains("most-executed-test")) {
					exprResult = calculateVariableExpressionResult(currentLine);
					break;
				}
				
			}
			
		}	
			
		return exprResult;
	}
	
	
	public String interpret() {
		return ProgramBody();
	}	
	

	public void variablesDefinition(String lineText, boolean defined) {
			
		if(lineText.contains(",")) {
			String[] variableFragments = lineText.split(",");
			for(String currentVariableFragment : variableFragments) {
				int assignIndex = currentVariableFragment.indexOf("=");
				if(assignIndex > 0) {
					parseAssignedVariable(currentVariableFragment,assignIndex, defined);				
				}
				else {
					parseUnassignedVariable(currentVariableFragment, defined);
				}
			}				
		}
		else {
			int assignIndex = lineText.indexOf("=");
			if(assignIndex > 0) {
				parseAssignedVariable(lineText,assignIndex, defined);				
			}
			else {
				parseUnassignedVariable(lineText, defined);
			}            
		}	
	}
	
	public void parseAssignedVariable(String assignExpression, int assignIndex, boolean defined) {
		String variableName = "";
		String variableValue = "";	
		if(assignExpression.startsWith("int ")) {
			int intIndex = assignExpression.indexOf("int ") + 4;
			variableName = assignExpression.substring(intIndex,assignIndex).trim();
			variableValue = assignExpression.substring(assignIndex + 1).replace(" ","");
			if(defined) {
				currentData.put(variableName, variableValue);
			}
			else {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, variableValue);
				}
				else {
					currentData.put(variableName, "not defined");
				}					
			}
			
		}
		else {
			variableName = assignExpression.substring(0, assignIndex - 1).trim();
			variableValue = assignExpression.substring(assignIndex + 1).trim();
			if(defined) {
				currentData.put(variableName, variableValue);
			}
			else {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, variableValue);
				}
				else {
					currentData.put(variableName, "not defined");
				}					
			}
		}
	}
	
	public void parseUnassignedVariable(String assignExpression, boolean defined) {
		String variableName = "";
		String variableValue = "";	
		if(assignExpression.startsWith("int ")) {
			int intIndex = assignExpression.indexOf("int ") + 4;
			variableName = assignExpression.substring(intIndex).trim();			
			if(defined) {
				currentData.put(variableName, variableValue);
			}
			else {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, variableValue);
				}
				else {
					currentData.put(variableName, "not defined");
				}					
			}
		}
		else {
			variableName = assignExpression.trim();			
			if(defined) {
				currentData.put(variableName, variableValue);
			}
			else {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, variableValue);
				}
				else {
					currentData.put(variableName, "not defined");
				}					
			}
		}
	}
	
	public void methodDefinition(String textBlock) {
		
	}
	
	public void testDefinition(String textBlock) {
		
	}
	
	public String calculateVariableExpressionResult(String evaluationText) {
		return	evalVariable(currentData,evaluationText.replace(" ", ""));
	}
	
	public String evalVariable(DataStorage dataTable, String key) {
		if(dataTable.get(key) == null || dataTable.get(key).isEmpty()) {
			return key + " is not initialized "; 
		}
		else if(dataTable.get(key) == "not defined") {
			return key + " not defined ";
		}
		else if(tryParseInt(dataTable.get(key))) {
			return dataTable.get(key);
		}
		else {
			Lexer evalLexer = new Lexer(key);
			Token t = evalLexer.getToken();
			int identifierLength = t.getValue().length();
			if(t.getType() == TokenType.Identifier) {
				Token op = evalLexer.getToken();
				switch(op.getType()) {
					case Op_add:
					 return	Integer.toString(Integer.parseInt(evalVariable(dataTable,t.getValue())) + 
						Integer.parseInt(evalVariable(dataTable, key.substring(identifierLength + 1))));
					case Op_subtract:
						 return	Integer.toString(Integer.parseInt(evalVariable(dataTable,t.getValue())) - 
							Integer.parseInt(evalVariable(dataTable, key.substring(identifierLength + 1))));
					default:
						return "";
				
				}				
			}
			else {
				return "";
			}
		}
	}
	
	
	public void connectIPBlock() {
		
	}
	
	boolean tryParseInt(String value) {  
	     try {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}
	
}
