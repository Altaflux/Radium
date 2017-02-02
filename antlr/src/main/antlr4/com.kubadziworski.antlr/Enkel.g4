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


typeName
	:	ID
	|	packageOrTypeName '.' ID
	;
packageOrTypeName
	:	ID
	|	packageOrTypeName '.' ID
	;

importDeclaration
	:	singleTypeImportDeclaration
	|	typeImportOnDemandDeclaration
	;
singleTypeImportDeclaration
	:	'import' typeName ';'
	;

typeImportOnDemandDeclaration
	:	'import' packageOrTypeName '.' '*' ';'
	;

//RULES
compilationUnit : packageDeclaration? importDeclaration* classDeclaration* EOF ;
//
packageDeclaration
	:   'package' ID ('.' ID)* ';'
	;

classDeclaration : className  classBody  ;
className : qualifiedName ;
classBody : '{' (field | function)* '}' ;
field : fieldModifier* name ':' type (EQUALS expression)? getter? setter?;

getter: 'get()' functionContent ;
setter: 'set' '(' ID ')' block ;

function : 'fn' functionDeclaration functionContent? ;
functionDeclaration : methodModifiers* functionName '('? parametersList? ')'? (':'(type))? ;
parametersList:  parameter (',' parameter)*
          |  parameter (',' parameterWithDefaultValue)*
          |  parameterWithDefaultValue (',' parameterWithDefaultValue)* ;
functionName : ID ;
parameter : type ID ;
parameterWithDefaultValue : type ID '=' defaultValue=expression ;

functionContent : (block |  ('=' blockStatement)) ;

block : '{' blockStatement* '}' ;

blockStatement :  {(_input.LA(1) != OpenBrace) }? statement eos ;

statement : block
           | variableDeclaration
           | assignment
           | printStatement
           | forStatement
           | returnStatement
           | throwStatement
           | expression ;

returnable : block | expression ;

variableDeclaration : (VARIABLE | IMMUTABLE) name (':' type)? EQUALS expression;
assignment :  (preExp=expression '.')?  name  EQUALS postExpr=expression;
printStatement : PRINT expression ;
returnStatement : 'return' expression #ReturnWithValue
                | 'return' #ReturnVoid ;
forStatement : 'for' ('(')? forConditions (')')? statement ;
forConditions : iterator=variableReference  'from' startExpr=expression range='to' endExpr=expression ;

catchBlock :  'catch'  '(' name ':' type  ')' block ;
finallyBlock : 'finally' block ;
throwStatement : 'throw' expression ;

name : ID ;

argumentList : argument? (',' a=argument)* #UnnamedArgumentsList
             | namedArgument? (',' namedArgument)* #NamedArgumentsList ;
argument : expression ;
namedArgument : name '->' expression ;



expression
           : primary #PrimaryExpression
           | SUPER '.' functionName '(' argumentList ')' #FunctionCall
           | owner=expression '.' functionName '(' argumentList ')' #FunctionCall
           | functionName '(' argumentList ')' #FunctionCall
           | superCall='super' '('argumentList ')' #Supercall
           | newCall='new' typeName '('argumentList ')' #ConstructorCall
           | variableReference #VarReference
           | owner=expression '.' variableReference  #VarReference
           | expression '!!' #NotNullCastExpression
           | expr=expression operation='--'  #SuffixExpression
           | expr=expression operation='++'  #SuffixExpression
           | 'try' block ((catchBlock+ | finallyBlock)  finallyBlock?) #TryExpression
           |  'if'  ('(')? expression (')')? trueStatement=returnable ('else' falseStatement=returnable)? #IfExpression
           | operation='--' (expression) #PrefixExpression
           | operation='++' (expression) #PrefixExpression
           | operation='-' expression #SignExpression
           | operation='+' expression #SignExpression
           | operation='!' expression #UnaryExpression
           | expression opType='*' expression  #BinaryExpression
           | expression opType='/' expression #BinaryExpression
           | expression opType='+' expression #BinaryExpression
           | expression opType='-' expression #BinaryExpression
           | expression opType='%' expression #BinaryExpression
           | expression cmp='>' expression #ConditionalExpression
           | expression cmp='<' expression #ConditionalExpression
           | expression cmp='==' expression #ConditionalExpression
           | expression cmp='!=' expression #ConditionalExpression
           | expression cmp='>=' expression #ConditionalExpression
           | expression cmp='<=' expression #ConditionalExpression
           ;
primary
    : '('expression')' #ParenthesisExpression
    | THIS #ThisReference
    | value #ValueExpr
    ;

variableReference : ID ;


value : IntegerLiteral
      |	FloatingPointLiteral
      | CharacterLiteral
      | BOOL
      | NULL
      | STRING ;
qualifiedName : ID ('.' ID)*;

methodModifiers
    : 'static'
    | 'public'
    | 'private' ;

fieldModifier : ('public' | 'protected' | 'private') ;

type : simpleName=typeComposition nullable='?'? ;

typeComposition : referenceType
	;
referenceType
	:	typeName
	|	arrayType
	;

arrayType
	:	typeName dims
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
LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;
IntegerLiteral
	:	DecimalIntegerLiteral
	|	HexIntegerLiteral
	|	OctalIntegerLiteral
	|	BinaryIntegerLiteral
	;

CharacterLiteral
	:	'\'' SingleCharacter '\''
	;

fragment
SingleCharacter
	:	~['\\]
	;
fragment
DecimalIntegerLiteral
	:	DecimalNumeral IntegerTypeSuffix?
	;
fragment
HexIntegerLiteral
	:	HexNumeral IntegerTypeSuffix?
	;
fragment
OctalIntegerLiteral
	:	OctalNumeral IntegerTypeSuffix?
	;
fragment
BinaryIntegerLiteral
	:	BinaryNumeral IntegerTypeSuffix?
	;
fragment
BinaryNumeral
	:	'0' [bB] BinaryDigits
	;
fragment
BinaryDigits
	:	BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)?
	;
fragment
BinaryDigit
	:	[01]
	;
fragment
BinaryDigitsAndUnderscores
	:	BinaryDigitOrUnderscore+
	;
fragment
BinaryDigitOrUnderscore
	:	BinaryDigit
	|	'_'
	;
fragment
DecimalNumeral
	:	'0'
	|	NonZeroDigit (Digits? | Underscores Digits)
	;
fragment
IntegerTypeSuffix
	:	[lL]
	;
fragment
DigitsAndUnderscores
	:	DigitOrUnderscore+
	;
fragment
DigitOrUnderscore
	:	Digit
	|	'_'
	;
fragment
Digits
	:	Digit (DigitsAndUnderscores? Digit)?
	;
fragment
OctalNumeral
	:	'0' Underscores? OctalDigits
	;
fragment
Digit
	:	'0'
	|	NonZeroDigit
	;

fragment
NonZeroDigit
	:	[1-9]
	;

fragment
HexNumeral
	:	'0' [xX] HexDigits
	;
fragment
HexDigits
	:	HexDigit (HexDigitsAndUnderscores? HexDigit)?
	;
fragment
HexDigitsAndUnderscores
	:	HexDigitOrUnderscore+
	;

fragment
HexDigitOrUnderscore
	:	HexDigit
	|	'_'
	;
fragment
HexDigit
	:	[0-9a-fA-F]
	;
fragment
Underscores
	:	'_'+
	;
fragment
OctalDigits
	:	OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?
	;

fragment
OctalDigit
	:	[0-7]
	;
fragment
OctalDigitsAndUnderscores
	:	OctalDigitOrUnderscore+
	;
fragment
OctalDigitOrUnderscore
	:	OctalDigit
	|	'_'
	;
FloatingPointLiteral
	:	DecimalFloatingPointLiteral
	|	HexadecimalFloatingPointLiteral
	;
fragment
DecimalFloatingPointLiteral
	:	Digits '.' Digits  ExponentPart? FloatTypeSuffix?
	|	'.' Digits ExponentPart? FloatTypeSuffix?
	|	Digits ExponentPart FloatTypeSuffix?
	|	Digits FloatTypeSuffix
	;
fragment
FloatTypeSuffix
	:	[fFdD]
	;
fragment
HexadecimalFloatingPointLiteral
	:	HexSignificand BinaryExponent FloatTypeSuffix?
	;
fragment
HexSignificand
	:	HexNumeral '.'?
	|	'0' [xX] HexDigits? '.' HexDigits
	;

fragment
BinaryExponent
	:	BinaryExponentIndicator SignedInteger
	;
fragment
BinaryExponentIndicator
	:	[pP]
	;
fragment
ExponentPart
	:	ExponentIndicator SignedInteger
	;

fragment
ExponentIndicator
	:	[eE]
	;
fragment
SignedInteger
	:	Sign? Digits
	;

fragment
Sign
	:	[+-]
	;

//TOKENS
STATIC : 'static' ;
THIS : 'this' ;
SUPER: 'super' ;
VARIABLE : 'var' ;
IMMUTABLE : 'val' ;
PRINT : 'print' ;
EQUALS : '=' ;
BOOL : 'true' | 'false' ;
NULL : 'null' ;
STRING : '"'~('\r' | '\n' | '"')*'"' ;
ID : [a-zA-Z0-9]+ ;
