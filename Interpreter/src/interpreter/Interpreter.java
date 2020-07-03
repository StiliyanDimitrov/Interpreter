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
		boolean openedConnect = false;
		StringBuilder connectBlock = new StringBuilder();
		boolean openedMethod = false;
		StringBuilder methodBlock = new StringBuilder();
		boolean openedTest = false;
		StringBuilder testBlock = new StringBuilder();
		for (String currentLine : lines) {
			if(openedConnect) {
				
			}
			else if(openedMethod) {
				methodBlock.append(currentLine + "\n");
				if(currentLine.contains("}")) {
					openedMethod = false;
					methodVariablesDefinition(methodBlock.toString());
				}
				else if(currentLine.contains("(") || currentLine.contains(")")) {
					return "not allowed symbol !";
				}				
			}
			else if(openedTest) {
				
			}
			else {
				if(currentLine.startsWith("int ") && currentLine.endsWith(";") && 
				   !currentLine.contains("{")) {
					    String[] lineFragments = currentLine.split(";");
					    for(String currentFragment : lineFragments) {
					    	variablesDefinition(currentFragment.trim(), true);
					    }
				}
				else if(!currentLine.startsWith("int ") && !currentLine.startsWith("test ") && 
						currentLine.endsWith(";") && !currentLine.contains("{")) {
					String[] lineFragments = currentLine.split(";");
				    for(String currentFragment : lineFragments) {
				    	variablesDefinition(currentFragment.trim(), false);
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
				else if(currentLine.startsWith("int ") && currentLine.endsWith("{") &&
						currentLine.contains("(") && currentLine.contains(")")) {
					openedMethod = true;
					methodBlock = new StringBuilder();
					methodBlock.append(currentLine + "\n");
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
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, variableValue.replace(variableName, currentData.get(variableName)));
				}
				else {
					currentData.put(variableName, variableValue);
				}
				
			}
			else {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, variableValue.replace(variableName, currentData.get(variableName)));
				}
				else {
					currentData.put(variableName, "not defined");
				}					
			}
			
		}
		else {
			variableName = assignExpression.substring(0, assignIndex).trim();
			variableValue = assignExpression.substring(assignIndex + 1).trim();
			if(defined) {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, variableValue.replace(variableName, currentData.get(variableName)));
				}
				else {
					currentData.put(variableName, variableValue);
				}
				
			}
			else {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, variableValue.replace(variableName, currentData.get(variableName)));
				}
				else {
					currentData.put(variableName, "not defined");
				}					
			}
		}
	}
	
	public void parseUnassignedVariable(String assignExpression, boolean defined) {
		String variableName = "";
			
		if(assignExpression.startsWith("int ")) {
			int intIndex = assignExpression.indexOf("int ") + 4;
			variableName = assignExpression.substring(intIndex).trim();			
			if(defined) {
				currentData.put(variableName, "");
			}
			else {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, "");
				}
				else {
					currentData.put(variableName, "not defined");
				}					
			}
		}
		else {
			variableName = assignExpression.trim();			
			if(defined) {
				currentData.put(variableName, "");
			}
			else {
				if(currentData.get(variableName) != null) {
					currentData.put(variableName, "");
				}
				else {
					currentData.put(variableName, "not defined");
				}					
			}
		}
	}
	
	public void methodVariablesDefinition(String textBlock) {		
		int openParenthesisPos = textBlock.indexOf("(");
		int closeParenthesisPos = textBlock.indexOf(")");
		if(openParenthesisPos <= 0 || closeParenthesisPos <=0) {
			return;
		}
		String methodName = textBlock.substring(0, openParenthesisPos).replace("int ", "").trim();
		
		HashMap<String,String> methodVariables = new HashMap<String,String>();
		String parametersList = textBlock.substring(openParenthesisPos + 1, closeParenthesisPos);
		if(parametersList.contains(",")) {
			String[] parameterFragments = parametersList.split(",");
			for(String currentParameterFragment : parameterFragments) {
				methodVariables.put(currentParameterFragment.replace("int ", "").trim(), "");
			}
		}
		int openBracePos = textBlock.indexOf("{");
		int closeBracePos = textBlock.indexOf("}");
		if(openBracePos <= 0 || closeBracePos <=0) {
			return;
		}
		String methodBody = textBlock.substring(openBracePos+1, closeBracePos);
		String[] methodLines = methodBody.split("\n");
		for(String currentLine : methodLines) {
			boolean defined = false;
			if(currentLine.trim().startsWith("int ")) {
				defined = true;
			}
			if(currentLine.contains("return")) {
				methodVariables.put("return", currentLine.replace("return","").trim());
			}
			if(currentLine.contains(",")) {
				String[] variableFragments = currentLine.split(",");
				for(String currentVariableFragment : variableFragments) {
					int assignIndex = currentVariableFragment.indexOf("=");
					if(assignIndex > 0) {
						parseMethodAssignedVariable(currentVariableFragment,assignIndex, defined, methodVariables);				
					}
					else {
						parseMethodUnassignedVariable(currentVariableFragment, defined, methodVariables);
					}
				}				
			}
			else {
				int assignIndex = currentLine.indexOf("=");
				if(assignIndex > 0) {
					parseMethodAssignedVariable(currentLine,assignIndex, defined, methodVariables);				
				}
				else {
					parseMethodUnassignedVariable(currentLine, defined, methodVariables);
				}            
			}
		}		
		currentData.addFunction(methodName, methodVariables);
	}
	
	public void parseMethodAssignedVariable(String assignExpression, int assignIndex, boolean defined, HashMap<String,String> methodMap) {
		String variableName = "";
		String variableValue = "";	
		if(assignExpression.startsWith("int ")) {
			int intIndex = assignExpression.indexOf("int ") + 4;
			variableName = assignExpression.substring(intIndex,assignIndex).trim();
			variableValue = assignExpression.substring(assignIndex + 1).replace(" ","");
			if(defined) {
				if(methodMap.get(variableName) != null) {
					methodMap.put(variableName, variableValue.replace(variableName, methodMap.get(variableName)));
				}
				else {
					methodMap.put(variableName, variableValue);
				}
				
			}
			else {
				if(methodMap.get(variableName) != null) {
					methodMap.put(variableName, variableValue.replace(variableName, methodMap.get(variableName)));
				}
				else {
					methodMap.put(variableName, "not defined");
				}					
			}
			
		}
		else {
			variableName = assignExpression.substring(0, assignIndex).trim();
			variableValue = assignExpression.substring(assignIndex + 1).trim();
			if(defined) {
				if(methodMap.get(variableName) != null) {
					methodMap.put(variableName, variableValue.replace(variableName, methodMap.get(variableName)));
				}
				else {
					methodMap.put(variableName, variableValue);
				}
				
			}
			else {
				if(methodMap.get(variableName) != null) {
					methodMap.put(variableName, variableValue.replace(variableName, methodMap.get(variableName)));
				}
				else {
					methodMap.put(variableName, "not defined");
				}					
			}
		}
	}
	
	public void parseMethodUnassignedVariable(String assignExpression, boolean defined, HashMap<String,String> methodMap) {
		String variableName = "";
			
		if(assignExpression.startsWith("int ")) {
			int intIndex = assignExpression.indexOf("int ") + 4;
			variableName = assignExpression.substring(intIndex).trim();			
			if(defined) {
				methodMap.put(variableName, "");
			}
			else {
				if(methodMap.get(variableName) != null) {
					methodMap.put(variableName, "");
				}
				else {
					methodMap.put(variableName, "not defined");
				}					
			}
		}
		else {
			variableName = assignExpression.trim();			
			if(defined) {
				methodMap.put(variableName, "");
			}
			else {
				if(methodMap.get(variableName) != null) {
					methodMap.put(variableName, "");
				}
				else {
					methodMap.put(variableName, "not defined");
				}					
			}
		}
	}
	
	public void testDefinition(String textBlock) {
		
	}
	
	public String calculateVariableExpressionResult(String evaluationText) {
		String tempResult = evalVariable(currentData,evaluationText.replace(" ", ""));
		if(tempResult.contains("not") || tempResult.isEmpty()) {
			return tempResult;
		}
		ExpressionEvaluator exprEval = new ExpressionEvaluator();
		return Integer.toString(exprEval.evaluate(tempResult));
	}
	
	public String evalVariable(DataStorage dataTable, String key) {
		Lexer evalLexer = new Lexer(key);
		Token t = evalLexer.getToken();
		int identifierLength = t.getValue().length();
		if(t.getType() == TokenType.Identifier || t.getType() == TokenType.Integer) {
			
			Token op = evalLexer.getToken();
			switch(op.getType()) {
				case Op_add:	
					if(evalVariable(dataTable,t.getValue()).contains("not") || evalVariable(dataTable,t.getValue()).isEmpty()) {
						return	evalVariable(dataTable,t.getValue());
					}
				 return	evalVariable(dataTable,t.getValue()) + "+" + 
					evalVariable(dataTable, key.substring(identifierLength + 1));
				case Op_subtract:
					if(evalVariable(dataTable,t.getValue()).contains("not") || evalVariable(dataTable,t.getValue()).isEmpty()) {
						return	evalVariable(dataTable,t.getValue());
					}
					return evalVariable(dataTable,t.getValue()) + "-" + 
						evalVariable(dataTable, key.substring(identifierLength + 1));
				default:
					if(t.tokentype==TokenType.Integer) {
						return t.getValue();
					}
					else if(dataTable.get(t.getValue()) == null || dataTable.get(t.getValue()).isEmpty()) {
						return t.getValue() + " is not initialized "; 
					}
					else if(dataTable.get(t.getValue()) == "not defined") {
						return t.getValue() + " not defined ";
					}
					else if(tryParseInt(dataTable.get(t.getValue()))) {
						return dataTable.get(t.getValue());
					}
					else {
						return evalVariable(dataTable,dataTable.get(t.getValue()));
					}
			
			}				
		}
				
		else {
			if(t.getType() == TokenType.Op_subtract) {
				return "-" + evalVariable(dataTable,key.substring(1));
			}						
		}
		return "";
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
