//header
grammar Enkel;
@parser::members {

    /**
     * Returns {@code true} if on the current index of the parser's
     * token stream a token of the given {@code type} exists on the
     * {@code HIDDEN} channel.
     *
     * @param type
     *         the type of the token on the {@code HIDDEN} channel
     *         to check.
     *
     * @return {@code true} iff on the current index of the parser's
     * token stream a token of the given {@code type} exists on the
     * {@code HIDDEN} channel.
     */
    private boolean here(final int type) {

        // Get the token ahead of the current index.
        int possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 1;
        Token ahead = _input.get(possibleIndexEosToken);

        // Check if the token resides on the HIDDEN channel and if it's of the
        // provided type.
        return (ahead.getChannel() == Lexer.HIDDEN) && (ahead.getType() == type);
    }

    /**
     * Returns {@code true} iff on the current index of the parser's
     * token stream a token exists on the {@code HIDDEN} channel which
     * either is a line terminator, or is a multi line comment that
     * contains a line terminator.
     *
     * @return {@code true} iff on the current index of the parser's
     * token stream a token exists on the {@code HIDDEN} channel which
     * either is a line terminator, or is a multi line comment that
     * contains a line terminator.
     */
    private boolean lineTerminatorAhead() {

        // Get the token ahead of the current index.
        int possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 1;
        Token ahead = _input.get(possibleIndexEosToken);

//        if (ahead.getChannel() != Lexer.HIDDEN) {
//            // We're only interested in tokens on the HIDDEN channel.
//            return false;
//        }

        if (ahead.getType() == LineTerminator) {
            // There is definitely a line terminator ahead.
            return true;
        }

        if (ahead.getType() == WhiteSpaces) {
            // Get the token ahead of the current whitespaces.
            possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 2;
            ahead = _input.get(possibleIndexEosToken);
        }

        // Get the token's text and type.
        String text = ahead.getText();
        int type = ahead.getType();

        // Check if the token is, or contains a line terminator.
//        return (type == MultiLineComment && (text.contains("\r") || text.contains("\n"))) ||
//                (type == LineTerminator);
            return true;
    }
}




//RULES
compilationUnit : classDeclaration EOF ;
classDeclaration : className '{' classBody '}' ;
className : qualifiedName ;
classBody :  field* function* ;
field : type name;
function : functionDeclaration block ;
functionDeclaration : (type)? functionName '('? parametersList? ')'? ;
parametersList:  parameter (',' parameter)*
          |  parameter (',' parameterWithDefaultValue)*
          |  parameterWithDefaultValue (',' parameterWithDefaultValue)* ;
functionName : ID ;
parameter : type ID ;
parameterWithDefaultValue : type ID '=' defaultValue=expression ;

classType : qualifiedName  ;

block : '{' blockStatement* '}' ;

blockStatement :  {(_input.LA(1) != OpenBrace) }? statement eos ;

statement : block
           | variableDeclaration
           | assignment
           | printStatement
           | forStatement
           | returnStatement
           | ifStatement
           | expression ;

variableDeclaration : VARIABLE name EQUALS expression;
assignment :  (preExp=expression '.')?  name  EQUALS postExpr=expression;
printStatement : PRINT expression ;
returnStatement : 'return' expression #ReturnWithValue
                | 'return' #ReturnVoid ;
ifStatement :  'if'  ('(')? expression (')')? trueStatement=statement ('else' falseStatement=statement)?;
forStatement : 'for' ('(')? forConditions (')')? statement ;
forConditions : iterator=variableReference  'from' startExpr=expression range='to' endExpr=expression ;
name : ID ;

argumentList : argument? (',' a=argument)* #UnnamedArgumentsList
             | namedArgument? (',' namedArgument)* #NamedArgumentsList ;
argument : expression ;
namedArgument : name '->' expression ;

expression : THIS #ThisReference
           | variableReference #VarReference
           | owner=expression '.' variableReference  #VarReference
           | owner=expression '.' functionName '(' argumentList ')' #FunctionCall
           | functionName '(' argumentList ')' #FunctionCall
           | superCall='super' '('argumentList ')' #Supercall
           | newCall='new' className '('argumentList ')' #ConstructorCall
           | value        #ValueExpr
           | expr=expression operation='--'  #SuffixExpression
           | expr=expression operation='++'  #SuffixExpression
           //| expr=expression {!here(LineTerminator)}? operation='--'  #SuffixExpression
           //| expr=expression {!here(LineTerminator)}? operation='++'  #SuffixExpression
           | operation='--' (expression) #PrefixExpression
           | operation='++' (expression) #PrefixExpression
           | operation='-' expression #ArithmeticExpression
           | operation='+' expression #ArithmeticExpression
           |  '('expression '*' expression')' #Multiply
           | expression '*' expression  #Multiply
           | '(' expression '/' expression ')' #Divide
           | expression '/' expression #Divide
           | '(' expression '+' expression ')' #Add
           | expression '+' expression #Add
           | '(' expression '-' expression ')' #Substract
           | expression '-' expression #Substract
           | expression cmp='>' expression #ConditionalExpression
             | expression cmp='<' expression #ConditionalExpression
             | expression cmp='==' expression #ConditionalExpression
             | expression cmp='!=' expression #ConditionalExpression
             | expression cmp='>=' expression #ConditionalExpression
             | expression cmp='<=' expression #ConditionalExpression
           ;


variableReference : ID ;


value : NUMBER
      | BOOL
      | STRING ;
qualifiedName : ID ('.' ID)*;


type
	:	primitiveType
	|	referenceType
	;
referenceType
	:	classType
	|	arrayType
	;
primitiveType
	:	 numericType
	|	 'boolean'
	;

numericType
	:	integralType
	|	floatingPointType
	;

integralType
	:	'byte'
	|	'short'
	|	'int'
	|	'long'
	|	'char'
	;
floatingPointType
	:	'float'
	|	'double'
	;

arrayType
	:	primitiveType dims
	|	classType dims
	;
dims
	:	'[' ']' ('[' ']')*
	;

eos
 : SemiColon
 | EOF
 | {lineTerminatorAhead()}?
 | {_input.LT(1).getType() == CloseBrace}?
 ;

OpenBrace                  : '{';
CloseBrace                 : '}';

WhiteSpaces
 : [\t\u000B\u000C\u0020\u00A0]+ -> channel(HIDDEN)
 ;

/// 7.3 Line Terminators
LineTerminator
 : [\r\n\u2028\u2029] -> channel(HIDDEN)
 ;
SemiColon  : ';';

MultiLineComment
 : '/*' .*? '*/' -> channel(HIDDEN)
 ;


//TOKENS
THIS : 'this' ;
VARIABLE : 'var' ;
PRINT : 'print' ;
EQUALS : '=' ;
NUMBER : '-'?[0-9.]+ ;
BOOL : 'true' | 'false' ;
STRING : '"'~('\r' | '\n' | '"')*'"' ;
ID : [a-zA-Z0-9]+ ;
//WS: [ \t\n\r]+ -> skip ;