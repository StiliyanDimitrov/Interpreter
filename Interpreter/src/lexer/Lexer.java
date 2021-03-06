package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lexer.token.Token;
import lexer.token.TokenType;

/**
 * class to make lexical analysis of text
 * @author st
 *
 */
public class Lexer {
    private int position;
    private char chr;
    private String s;
 
    private static Map<String, TokenType> keywords = new HashMap<>(); 
    static {
    	keywords.put("int", TokenType.Keyword_int);
    	keywords.put("return",TokenType.Keyword_return);
        keywords.put("test", TokenType.Keyword_test);
        keywords.put("assert", TokenType.Keyword_assert);
        keywords.put("connect", TokenType.Keyword_connect);
        keywords.put("disconnect", TokenType.Keyword_disconnect);
        keywords.put("most-failing-test", TokenType.Keyword_MostFailingTest);
        keywords.put("most-executed-test", TokenType.Keyword_MostExecutedTest);
    }
    
 
    public static void error(String msg) {
          System.out.println(msg);               
    }
 
    public Lexer(String source) {
        this.position = 0;
        this.s = source;
        this.chr = this.s.charAt(0); 
    }
    
    /**
     * method to check type of token identifier, integer or keyword in the list of keyword
     * @return Token object tokentype and value
     */
    Token identifierOrInteger() {
        boolean isNumber = true;
        StringBuilder text = new StringBuilder();
 
        while (Character.isAlphabetic(this.chr) || Character.isDigit(this.chr) || this.chr == '_') {
            text.append(this.chr);
            if (!Character.isDigit(this.chr)) {
                isNumber = false;
            }
            getNextChar();
        }
 
        if (text.toString().equals("")) {
            error(String.format("identiferOrInteger unrecognized character: (%d) %c", (int)this.chr, this.chr));
        }
 
        if (Character.isDigit(text.toString().charAt(0))) {
            if (!isNumber) {
                error(String.format("invalid number: %s", text.toString()));
            }
            return new Token(TokenType.Integer, text.toString());
        }
 
        if (Lexer.keywords.containsKey(text.toString())) {
            return new Token(Lexer.keywords.get(text.toString()), "");
        }
        return new Token(TokenType.Identifier, text.toString());
    }
    
    /**
     * method to read next token
     * @return Token object
     */
    public Token getToken() {
        while (Character.isWhitespace(this.chr)) {
            getNextChar();
        }
                 
        switch (this.chr) {
            case '\u0000': return new Token(TokenType.End_of_input, "");
            case '=': getNextChar(); return new Token(TokenType.Op_assign, "");
            case '{': getNextChar(); return new Token(TokenType.LeftBrace, "");
            case '}': getNextChar(); return new Token(TokenType.RightBrace, "");
            case '(': getNextChar(); return new Token(TokenType.LeftParen, "");
            case ')': getNextChar(); return new Token(TokenType.RightParen, "");
            case '+': getNextChar(); return new Token(TokenType.Op_add, "");
            case '-': getNextChar(); return new Token(TokenType.Op_subtract, "");
            case ';': getNextChar(); return new Token(TokenType.Semicolon, "");
            case ',': getNextChar(); return new Token(TokenType.Comma, "");
 
            default: return identifierOrInteger();
        }
    }
 
    /**
     * method to return next character 
     * @return char next character
     */
    char getNextChar() {
        this.position++;
        if (this.position >= this.s.length()) {
            this.chr = '\u0000';
            return this.chr;
        }
        this.chr = this.s.charAt(this.position);
        
        return this.chr;
    }
    
    /**
     * method to print tokens
     */
    public void printTokens() {
        Token t;
        while ((t = getToken()).getType() != TokenType.End_of_input) {
            System.out.println(t);
        }
        System.out.println(t);
    }
    
    /**
     * method to get list of tokens
     * @return List<Token> object
     */
    public List<Token> getTokens() {
    	List<Token> tokenList = new ArrayList<Token>();
    	Token t;
    	while((t = getToken()).getType() != TokenType.End_of_input) {
    		tokenList.add(t);
    	}
    	return tokenList;
    }
    
    /**
     * method to get all read lines
     * @return String array of lines
     */
    public String[] getLines(){    	
    	List<String> list = new ArrayList<String>();
    	for(String tempString : s.split("\n")) {
    		if(!tempString.trim().isEmpty()) {
    			list.add(tempString);
    		}
    	}
    	String[] array = list.toArray(new String[0]);

    	return array;
    }
    
}