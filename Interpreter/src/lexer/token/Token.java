package lexer.token;

/**
 * class for working with tokens
 * @author st
 *
 */
public class Token {
        private TokenType tokentype;
        private String value;        
        
        public Token(TokenType token, String value) {
            this.tokentype = token; this.value = value; 
        }
        public TokenType getType() {
        	return this.tokentype;
        }
        public String getValue() {
    		return this.value;
    	}
        
        @Override
        public String toString() {
            String result = String.format("%5s", this.tokentype);
            if(this.tokentype == TokenType.Integer || this.tokentype == TokenType.Identifier) {            
                    result += String.format(" %s", value);                                 
            }
            return result;
        }
    }