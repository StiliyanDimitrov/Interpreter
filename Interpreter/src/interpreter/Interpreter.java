package interpreter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import lexer.Lexer;
import lexer.token.Token;
import lexer.token.TokenType;
import main.DataStorage;

/**
 * class to interpret given command
 * @author st
 *
 */

public class Interpreter {

	private String[] lines;
	private DataStorage currentData;
	private List<TestStatistic> testStatsList;
	
	public Interpreter(String[] lines, DataStorage currentData, List<TestStatistic> testStatsList) {
		this.lines = lines;
		this.currentData = currentData;
		this.testStatsList = testStatsList;
	}
	
	/**
	 * method to interpret program body
	 * @return String result of interpretation
	 */
	public String ProgramBody() {
		String exprResult = "";
		boolean openedMethod = false;
		StringBuilder methodBlock = new StringBuilder();
		boolean openedTest = false;
		StringBuilder testBlock = new StringBuilder();
		
		for (String currentLine : lines) {
			if(openedMethod) {
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
				testBlock.append(currentLine + "\n");
				if(currentLine.contains("}")) {
					openedTest = false;
					testDefinition(testBlock.toString());
				}
				else if(currentLine.contains("test ")) {
					return "not allowed expression !";
				}
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
					if(currentLine.startsWith("test ") && currentLine.endsWith(";")) {
						String testName = currentLine.substring(currentLine.indexOf("test ")+5,currentLine.indexOf("("));
						exprResult = calculateTestExpressionResult(testName);
						boolean successTest = exprResult.contains("successfully");
						TestStatistic currentTestStat = testStatsList.stream().filter(x->x.getTestName().equals(testName))
						.findAny()
						.orElse(null);
						if(currentTestStat != null) {
							currentTestStat.setExecutionCount(currentTestStat.getExecutionCount() + 1);
							if(!successTest) {
								currentTestStat.setFailCount(currentTestStat.getFailCount() + 1);
							}							
						}
						else {
							if(!successTest) {
								currentTestStat = new TestStatistic(testName, 1, 1);
							}
							else {
								currentTestStat = new TestStatistic(testName, 1, 0);
							}	
							testStatsList.add(currentTestStat);
						}
						break;
					}
					else if(!currentLine.contains("(") && !currentLine.contains("most-failing-test") &&
							!currentLine.contains("most-executed-test")) {
						exprResult = calculateVariableExpressionResult(currentLine);
						break;
					}
					else if(currentLine.contains("(") && currentLine.contains(")") &&
							!currentLine.endsWith(";")) {
						String methodName = currentLine.substring(0,currentLine.indexOf("(")).trim();
						exprResult = calculateMethodExpressionResult(methodName, currentLine);
						break;
					}
					else if(currentLine.startsWith("most-failing-test") || currentLine.startsWith("most-executed-test")) {
						exprResult = queryTestResult(currentLine.trim(), testStatsList);
						break;
					}
					
				}
				else if(currentLine.startsWith("int ") && currentLine.endsWith("{") &&
						currentLine.contains("(") && currentLine.contains(")")) {
					openedMethod = true;
					methodBlock = new StringBuilder();
					methodBlock.append(currentLine + "\n");
				}
				else if(currentLine.startsWith("test ") && currentLine.endsWith("{")) {
					openedTest=true;
					testBlock = new StringBuilder();
					testBlock.append(currentLine + "\n");
				}
			}
			
		}	
			
		return exprResult;
	}
	
	
	public String interpret() {
		return ProgramBody();
	}	
	
    /**
     * method to define variables
     * @param lineText given variable from program body
     * @param defined whether variable is defined or not 
     */
	public void variablesDefinition(String lineText, boolean defined) {
			
		if(lineText.contains(",") && !lineText.contains("(") && !lineText.contains(")")) {
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
		else if(lineText.contains("(") && lineText.contains(")")) {
			int assignIndex = lineText.indexOf("=");
			if(assignIndex > 0) {
				String methodName = lineText.substring(assignIndex+1,lineText.indexOf("(")).trim();
				String methodAssignResult = calculateMethodExpressionResult(methodName, lineText.substring(assignIndex+1).trim());
				lineText = lineText.substring(0, assignIndex+1).trim() + methodAssignResult;
				parseAssignedVariable(lineText,assignIndex, defined);				
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
	
	/**
	 * method to associate variable name with expression
	 * @param assignExpression given expression
	 * @param assignIndex get index of assignment operator 
	 * @param defined check whether variable is defined or not
	 */
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
	
	/**
	 * method for putting in table unassigned variables
	 * @param assignExpression variable expression
	 * @param defined whether variable is defined or not
	 */
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
	
	/**
	 * method to define variables in method
	 * @param textBlock given method block
	 */
	public void methodVariablesDefinition(String textBlock) {		
		int openParenthesisPos = textBlock.indexOf("(");
		int closeParenthesisPos = textBlock.indexOf(")");
		if(openParenthesisPos <= 0 || closeParenthesisPos <=0) {
			return;
		}
		String methodName = textBlock.substring(0, openParenthesisPos).replace("int ", "").trim();
		
		LinkedHashMap<String,String> methodVariables = new LinkedHashMap<String,String>();
		String parametersList = textBlock.substring(openParenthesisPos + 1, closeParenthesisPos);
		if(parametersList.contains(",")) {
			String[] parameterFragments = parametersList.split(",");
			for(String currentParameterFragment : parameterFragments) {
				methodVariables.put(currentParameterFragment.replace("int ", "").trim(), "");
			}
		}
		else if(!parametersList.isEmpty()) {
			methodVariables.put(parametersList.replace("int ", "").trim(), "");
		}
		int openBracePos = textBlock.indexOf("{");
		int closeBracePos = textBlock.indexOf("}");
		if(openBracePos <= 0 || closeBracePos <=0) {
			return;
		}
		String methodBody = textBlock.substring(openBracePos+1, closeBracePos);
		String[] methodLines = methodBody.split("\n");
		for(String currentLine : methodLines) {
			if(currentLine.trim().isEmpty()) {
				continue;
			}
			for(String currentExpression : currentLine.split(";")) {
				boolean defined = false;
				if(currentExpression.trim().startsWith("int ")) {
					defined = true;
				}
				if(currentExpression.contains("return")) {
					methodVariables.put("return", currentExpression.replace("return","").trim());
					continue;
				}
				else if(currentExpression.contains(",")) {
					String[] variableFragments = currentExpression.split(",");
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
					int assignIndex = currentExpression.indexOf("=");
					if(assignIndex > 0) {
						parseMethodAssignedVariable(currentExpression,assignIndex, defined, methodVariables);				
					}
					else {
						parseMethodUnassignedVariable(currentExpression, defined, methodVariables);
					}            
				}
			}
			
		}		
		currentData.addFunction(methodName, methodVariables);
	}
	
	/**
	 * method to assign variables of given function
	 * @param assignExpression variable expression
	 * @param assignIndex index of assignment operator
	 * @param defined whether variable is defined or not
	 * @param methodMap table to store method variables
	 */
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
	
	/**
	 * method to put in table function unassigned variables
	 * @param assignExpression variable expression
	 * @param defined whether variable is defined or not
	 * @param methodMap table to store method variables
	 */
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
	
	/**
	 * method to define test
	 * @param textBlock given body of test
	 */
	public void testDefinition(String textBlock) {
		int openParenthesisPos = textBlock.indexOf("(");
		int closeParenthesisPos = textBlock.indexOf(")");
		if(openParenthesisPos <= 0 || closeParenthesisPos <=0) {
			return;
		}
		String testName = textBlock.substring(0, openParenthesisPos).replace("test ", "").trim();
		
		LinkedHashMap<String,String> testVariables = new LinkedHashMap<String,String>();
		
		int openBracePos = textBlock.indexOf("{");
		int closeBracePos = textBlock.indexOf("}");
		if(openBracePos <= 0 || closeBracePos <=0) {
			return;
		}
		String testBody = textBlock.substring(openBracePos+1, closeBracePos);
		String[] testLines = testBody.split("\n");
		for(String currentLine : testLines) {
			if(currentLine.trim().isEmpty()) {
				continue;
			}
			for(String currentExpression : currentLine.split(";")) {
				boolean defined = false;
				if(currentExpression.trim().startsWith("int ")) {
					defined = true;
				}
				if(currentExpression.contains("assert")) {
					testVariables.put("assert", currentExpression.replace("assert","").trim());
					continue;
				}
				else if(!currentExpression.contains("assert") && currentExpression.contains(",")) {
					String[] variableFragments = currentExpression.split(",");
					for(String currentVariableFragment : variableFragments) {
						int assignIndex = currentVariableFragment.indexOf("=");
						if(assignIndex > 0) {
							parseMethodAssignedVariable(currentVariableFragment,assignIndex, defined, testVariables);				
						}
						else {
							parseMethodUnassignedVariable(currentVariableFragment, defined, testVariables);
						}
					}				
				}
				else {
					int assignIndex = currentExpression.indexOf("=");
					if(assignIndex > 0) {
						parseMethodAssignedVariable(currentExpression,assignIndex, defined, testVariables);				
					}
					else {
						parseMethodUnassignedVariable(currentExpression, defined, testVariables);
					}            
				}
			}
			
		}		
		currentData.addTest(testName, testVariables);
	}
	
	/**
	 * method to parse variable expression
	 * @param evaluationText given expression
	 * @return String parsed variable expression
	 */
	public String calculateVariableExpressionResult(String evaluationText) {
		String tempResult = evalVariable(currentData,evaluationText.replace(" ", ""));
		if(tempResult.contains("not") || tempResult.isEmpty()) {
			return tempResult;
		}
		tempResult=tempResult.replace("--", "+");
		ExpressionEvaluator exprEval = new ExpressionEvaluator();
		return Integer.toString(exprEval.evaluate(tempResult));
	}
	
	/**
	 * method to evaluate function expression
	 * @param methodName given function name
	 * @param evaluationText function expression to evaluate
	 * @return String evaluated expression
	 */
	public String calculateMethodExpressionResult(String methodName, String evaluationText) {
		String factParameters = evaluationText.substring(evaluationText.indexOf("(") + 1, evaluationText.indexOf(")"));
		List<String> factParamsArray = new ArrayList<String>();
		if(!factParameters.isEmpty()) {
			if(factParameters.contains(",")) {
				for(String currentFactParam : factParameters.split(",")) {
					if(!currentFactParam.isEmpty()) {
						factParamsArray.add(currentFactParam);
					}
				}
			}
			else {
				factParamsArray.add(factParameters.trim());
			}
		}		
		LinkedHashMap<String,String> methodMap = currentData.getFunction(methodName);
		Set<String> keySet = methodMap.keySet();
		List<String> listKeys = new ArrayList<String>(keySet);
		int fpCounter = 0;
		for(String factParam : factParamsArray) {			
			String factParamValue = calculateVariableExpressionResult(factParam);			
			String key = listKeys.get(fpCounter++);
			methodMap.put(key, factParamValue); 
		}
		String tempResult = evalFunctionVariable(methodMap,methodMap.get("return").replace(" ", ""));
		if(tempResult.contains("not") || tempResult.isEmpty()) {
			return tempResult;
		}
		tempResult=tempResult.replace("--", "+");
		ExpressionEvaluator exprEval = new ExpressionEvaluator();
		return Integer.toString(exprEval.evaluate(tempResult));
	}
	
	/**
	 * method to evaluate given function in test body expression
	 * @param methodName function name to evaluate
	 * @param evaluationText expression to evaluate
	 * @param testName test name
	 * @return String evaluated function expression in test
	 */
	public String calculateMethodTestExpressionResult(String methodName, String evaluationText, String testName) {
		String factParameters = evaluationText.substring(evaluationText.indexOf("(") + 1, evaluationText.indexOf(")"));
		List<String> factParamsArray = new ArrayList<String>();
		if(!factParameters.isEmpty()) {
			if(factParameters.contains(",")) {
				for(String currentFactParam : factParameters.split(",")) {
					if(!currentFactParam.isEmpty()) {
						factParamsArray.add(currentFactParam);
					}
				}
			}
			else {
				factParamsArray.add(factParameters.trim());
			}
		}		
		LinkedHashMap<String,String> methodMap = currentData.getFunction(methodName);
		Set<String> keySet = methodMap.keySet();
		List<String> listKeys = new ArrayList<String>(keySet);
		int fpCounter = 0;
		for(String factParam : factParamsArray) {			
			String factParamValue = calculateTestVariableExpressionResult(testName,factParam);			
			String key = listKeys.get(fpCounter++);
			methodMap.put(key, factParamValue); 
		}
		String tempResult = evalFunctionVariable(methodMap,methodMap.get("return").replace(" ", ""));
		if(tempResult.contains("not") || tempResult.isEmpty()) {
			return tempResult;
		}
		tempResult=tempResult.replace("--", "+");
		ExpressionEvaluator exprEval = new ExpressionEvaluator();
		return Integer.toString(exprEval.evaluate(tempResult));
	}
	
	/**
	 * method to evaluate variable in test
	 * @param testName test name
	 * @param evaluationText expression to evaluate
	 * @return String evaluated variable expression in test
	 */
	public String calculateTestVariableExpressionResult(String testName, String evaluationText) {
		LinkedHashMap<String,String> testMethodMap = currentData.getTest(testName);
		String tempResult = evalFunctionVariable(testMethodMap,evaluationText.replace(" ", ""));
		if(tempResult.contains("not") || tempResult.isEmpty()) {
			return tempResult;
		}
		tempResult=tempResult.replace("--", "+");
		ExpressionEvaluator exprEval = new ExpressionEvaluator();
		return Integer.toString(exprEval.evaluate(tempResult));
	}
	
	/**
	 * method to evaluate result of test 
	 * @param testName test name
	 * @return String evaluated test result
	 */
	public String calculateTestExpressionResult(String testName) {
		LinkedHashMap<String,String> testMap = currentData.getTest(testName);
		
		String assertEval = testMap.get("assert").replace(" ", "");
		int openParenIndex = assertEval.indexOf("(");
		int closeParenIndex = assertEval.indexOf(")");
		int commaIndex = assertEval.indexOf(",");
		String firstPart="";
		String secondPart="";
		String evalMethodName="";
		
		if(openParenIndex > 0 && closeParenIndex > 0) {			
			if(commaIndex > openParenIndex && commaIndex < closeParenIndex) {
				commaIndex = assertEval.indexOf(",",closeParenIndex+1);
			}
		}
		String[] assertParts = new String[] {assertEval.substring(0,commaIndex).trim(),assertEval.substring(commaIndex+1).trim()};
			if(assertParts[0].trim().contains("(")) {
				evalMethodName = assertParts[0].substring(0,assertParts[0].indexOf("(")).trim();
				firstPart = calculateMethodTestExpressionResult(evalMethodName, assertParts[0].trim(),testName);
			}
			else {
				firstPart = calculateTestVariableExpressionResult(testName,assertParts[0].trim());
			}
			
			if(assertParts[1].trim().contains("(")) {
				evalMethodName = assertParts[1].substring(0,assertParts[1].indexOf("(")).trim();
				secondPart = calculateMethodTestExpressionResult(evalMethodName, assertParts[1].trim(),testName);
			}
			else {
				secondPart = calculateTestVariableExpressionResult(testName,assertParts[1].trim());
			}
			if(!firstPart.trim().isEmpty() && !secondPart.trim().isEmpty() && firstPart.equals(secondPart)) {
				return String.format("%s runs successfully", testName);
			}
			else {
				return String.format("%s fails", testName);
			}
			
	}
	
	/**
	 * method to evaluate statistic results of executed and failing tests
	 * @param command most-executed or most-failing
	 * @param testStatList list of all executed tests
	 * @return String statistic results of executed and failing tests
	 */
	public String queryTestResult(String command, List<TestStatistic> testStatList) {
		if(testStatList.size() <= 0) {
			return "no available data for tests";
		}
		if(command.equals("most-failing-test")) {
			Collections.sort(testStatList,TestStatistic::compareByFailCount);
			TestStatistic mostFailingResult = testStatList.get(testStatList.size()-1);
			return String.format("%s is run %d times and failed %d times", 
					mostFailingResult.getTestName(), mostFailingResult.getExecutionCount(), mostFailingResult.getFailCount());
		}
		else {
			Collections.sort(testStatList,TestStatistic::compareByExecutionCount);
			TestStatistic mostExecutedResult = testStatList.get(testStatList.size()-1);
			return String.format("%s is run %d times", 
					mostExecutedResult.getTestName(), mostExecutedResult.getExecutionCount());
		}
	}
	
	/**
	 * method to parse variable
	 * @param dataTable variables table
	 * @param key variable name
	 * @return String parsed variable value expression
	 */
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
					if(t.getType()==TokenType.Integer) {
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
	
	/**
	 * method to parse function variable
	 * @param dataTable variables table in function
	 * @param key variable name
	 * @return String parsed function variable value expression
	 */
	public String evalFunctionVariable(LinkedHashMap<String,String> dataTable, String key) {
		Lexer evalLexer = new Lexer(key);
		Token t = evalLexer.getToken();
		int identifierLength = t.getValue().length();
		if(t.getType() == TokenType.Identifier || t.getType() == TokenType.Integer) {
			
			Token op = evalLexer.getToken();
			switch(op.getType()) {
				case Op_add:	
					if(evalFunctionVariable(dataTable,t.getValue()).contains("not") || evalFunctionVariable(dataTable,t.getValue()).isEmpty()) {
						return	evalFunctionVariable(dataTable,t.getValue());
					}
				 return	evalFunctionVariable(dataTable,t.getValue()) + "+" + 
				 evalFunctionVariable(dataTable, key.substring(identifierLength + 1));
				case Op_subtract:
					if(evalFunctionVariable(dataTable,t.getValue()).contains("not") || evalFunctionVariable(dataTable,t.getValue()).isEmpty()) {
						return	evalFunctionVariable(dataTable,t.getValue());
					}
					return evalFunctionVariable(dataTable,t.getValue()) + "-" + 
					evalFunctionVariable(dataTable, key.substring(identifierLength + 1));
				default:
					if(t.getType()==TokenType.Integer) {
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
						return evalFunctionVariable(dataTable,dataTable.get(t.getValue()));
					}
			
			}				
		}
				
		else {
			if(t.getType() == TokenType.Op_subtract) {
				return "-" + evalFunctionVariable(dataTable,key.substring(1));
			}						
		}
		return "";
	}	
	
	/**
	 * method to check whether string can be converted to int
	 * @param value given string for conversion
	 * @return boolean whether it it possible to convert string to int
	 */
	boolean tryParseInt(String value) {  
	     try {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}
	
}
