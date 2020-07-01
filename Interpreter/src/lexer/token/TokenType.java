package lexer.token;

public enum TokenType {
        End_of_input, Op_add, Op_subtract, 
        Op_assign,  LeftParen, RightParen,
        LeftBrace, RightBrace, Semicolon, Comma, Identifier, Integer, Keyword_int,
        Keyword_return, Keyword_test, Keyword_assert, Keyword_connect, Keyword_disconnect, 
        Keyword_MostFailingTest, Keyword_MostExecutedTest
    }