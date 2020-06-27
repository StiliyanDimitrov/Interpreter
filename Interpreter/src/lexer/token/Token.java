package lexer.token;

public class Token {
        public TokenType tokentype;
        public String value;        
        
        public Token(TokenType token, String value) {
            this.tokentype = token; this.value = value; 
        }
        public TokenType getType() {
        	return this.tokentype;
        }
        public String getValue() {
    		return value;
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