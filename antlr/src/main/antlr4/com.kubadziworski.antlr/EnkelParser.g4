//header
parser grammar EnkelParser;
options { tokenVocab=EnkelLexer; }

typeName
	:	SimpleName
	|	packageOrTypeName '.' SimpleName
	;
packageOrTypeName
	:	SimpleName
	|	packageOrTypeName '.' SimpleName
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
compilationUnit : packageDeclaration? importDeclaration* classDeclaration*  ;
//
packageDeclaration
	:   'package' SimpleName ('.' SimpleName)* ';'
	;

classDeclaration : className  classBody  ;
className : qualifiedName ;
classBody : '{' (field | function)* '}' ;
field : fieldModifier* name ':' type ('=' expression)? getter? setter?;

getter: 'get' '('')' functionContent ;
setter: 'set' '(' SimpleName ')' block ;

function : 'fn' functionDeclaration functionContent? ;
functionDeclaration : methodModifiers* functionName '('? parametersList? ')'? (':'(type))? ;
parametersList:  parameter (',' parameter)*
          |  parameter (',' parameterWithDefaultValue)*
          |  parameterWithDefaultValue (',' parameterWithDefaultValue)* ;
functionName : SimpleName ;
parameter : type SimpleName ;
parameterWithDefaultValue : type SimpleName '=' defaultValue=expression ;

functionContent : (block |  ('=' blockStatement)) ;

block : '{' blockStatement '}' ;

blockStatement
  : SEMI* (statement (SEMI* statement)*)? SEMI*
  ;


statement : block
           | variableDeclaration
           | assignment
           | forStatement
           | returnStatement
           | throwStatement
           | expression ;

returnable : block | expression ;

variableDeclaration : (KEYWORD_var | KEYWORD_val ) name (':' type)? '=' expression;
assignment :  (preExp=expression '.')?  name  '=' postExpr=expression;
returnStatement : 'return' expression #ReturnWithValue
                | 'return' #ReturnVoid ;
forStatement : 'for' ('(')? forConditions (')')? statement ;
forConditions : iterator=variableReference  'from' startExpr=expression range='to' endExpr=expression ;

catchBlock :  'catch'  '(' name ':' type  ')' block ;
finallyBlock : 'finally' block ;
throwStatement : 'throw' expression ;

name : SimpleName ;

argumentList : argument? (',' a=argument)* #UnnamedArgumentsList
             | namedArgument? (',' namedArgument)* #NamedArgumentsList ;
argument : expression ;
namedArgument : name '->' expression ;


expression
           : primary #PrimaryExpression
           | ConstructorDelegationCall_super '.' functionName '(' argumentList ')' #FunctionCall
           | owner=expression '.' functionName '(' argumentList ')' #FunctionCall
           | functionName '(' argumentList ')' #FunctionCall
           | superCall=ConstructorDelegationCall_super '('argumentList ')' #Supercall
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
    | 'this' #ThisReference
    | value #ValueExpr
    ;

variableReference : SimpleName ;


value : IntegerLiteral
      |	FloatingPointLiteral
      | CharacterLiteral
      | BOOL
      | NULL
      | stringLiteral
      ;
qualifiedName : SimpleName ('.' SimpleName)*;

methodModifiers
    : 'static'
    | 'public'
    | 'private'
    | 'inline' ;

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
 : SEMI
 | EOF
 | {_input.LT(1).getType() == CLOSE_BLOCK}?
 ;
stringLiteral
    : SINGLE_QUOTE (SINLE_QUOTE_ESCAPED_CHAR | SINLE_QUOTE_EXPRESSION_START expression CLOSE_BLOCK | SINGLE_QUOTE_REF | ~SINLE_QUOTE_CLOSE)* SINLE_QUOTE_CLOSE
    | TRIPLE_QUOTE (MULTILINE_QUOTE_EXPRESSION_START expression CLOSE_BLOCK | MULTILINE_QUOTE_REF | ~MULTILINE_QUOTE_CLOSE)* MULTILINE_QUOTE_CLOSE
    ;

//OpenBrace                  : '{';
//CloseBrace                 : '}';
//
//
//
//IntegerLiteral
//	:	DecimalIntegerLiteral
//	|	HexIntegerLiteral
//	|	OctalIntegerLiteral
//	|	BinaryIntegerLiteral
//	;
//
//CharacterLiteral
//	:	'\'' SingleCharacter '\''
//	;
//
//fragment
//SingleCharacter
//	:	~['\\]
//	;
//fragment
//DecimalIntegerLiteral
//	:	DecimalNumeral IntegerTypeSuffix?
//	;
//fragment
//HexIntegerLiteral
//	:	HexNumeral IntegerTypeSuffix?
//	;
//fragment
//OctalIntegerLiteral
//	:	OctalNumeral IntegerTypeSuffix?
//	;
//fragment
//BinaryIntegerLiteral
//	:	BinaryNumeral IntegerTypeSuffix?
//	;
//fragment
//BinaryNumeral
//	:	'0' [bB] BinaryDigits
//	;
//fragment
//BinaryDigits
//	:	BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)?
//	;
//fragment
//BinaryDigit
//	:	[01]
//	;
//fragment
//BinaryDigitsAndUnderscores
//	:	BinaryDigitOrUnderscore+
//	;
//fragment
//BinaryDigitOrUnderscore
//	:	BinaryDigit
//	|	'_'
//	;
//fragment
//DecimalNumeral
//	:	'0'
//	|	NonZeroDigit (Digits? | Underscores Digits)
//	;
//fragment
//IntegerTypeSuffix
//	:	[lL]
//	;
//fragment
//DigitsAndUnderscores
//	:	DigitOrUnderscore+
//	;
//fragment
//DigitOrUnderscore
//	:	Digit
//	|	'_'
//	;
//fragment
//Digits
//	:	Digit (DigitsAndUnderscores? Digit)?
//	;
//fragment
//OctalNumeral
//	:	'0' Underscores? OctalDigits
//	;
//fragment
//Digit
//	:	'0'
//	|	NonZeroDigit
//	;
//
//fragment
//NonZeroDigit
//	:	[1-9]
//	;
//
//fragment
//HexNumeral
//	:	'0' [xX] HexDigits
//	;
//fragment
//HexDigits
//	:	HexDigit (HexDigitsAndUnderscores? HexDigit)?
//	;
//fragment
//HexDigitsAndUnderscores
//	:	HexDigitOrUnderscore+
//	;
//
//fragment
//HexDigitOrUnderscore
//	:	HexDigit
//	|	'_'
//	;
//fragment
//HexDigit
//	:	[0-9a-fA-F]
//	;
//fragment
//Underscores
//	:	'_'+
//	;
//fragment
//OctalDigits
//	:	OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?
//	;
//
//fragment
//OctalDigit
//	:	[0-7]
//	;
//fragment
//OctalDigitsAndUnderscores
//	:	OctalDigitOrUnderscore+
//	;
//fragment
//OctalDigitOrUnderscore
//	:	OctalDigit
//	|	'_'
//	;
//FloatingPointLiteral
//	:	DecimalFloatingPointLiteral
//	|	HexadecimalFloatingPointLiteral
//	;
//fragment
//DecimalFloatingPointLiteral
//	:	Digits '.' Digits  ExponentPart? FloatTypeSuffix?
//	|	'.' Digits ExponentPart? FloatTypeSuffix?
//	|	Digits ExponentPart FloatTypeSuffix?
//	|	Digits FloatTypeSuffix
//	;
//fragment
//FloatTypeSuffix
//	:	[fFdD]
//	;
//fragment
//HexadecimalFloatingPointLiteral
//	:	HexSignificand BinaryExponent FloatTypeSuffix?
//	;
//fragment
//HexSignificand
//	:	HexNumeral '.'?
//	|	'0' [xX] HexDigits? '.' HexDigits
//	;
//
//fragment
//BinaryExponent
//	:	BinaryExponentIndicator SignedInteger
//	;
//fragment
//BinaryExponentIndicator
//	:	[pP]
//	;
//fragment
//ExponentPart
//	:	ExponentIndicator SignedInteger
//	;
//
//fragment
//ExponentIndicator
//	:	[eE]
//	;
//fragment
//SignedInteger
//	:	Sign? Digits
//	;
//
//fragment
//Sign
//	:	[+-]
//	;
//
////TOKENS
//STATIC : 'static' ;
//THIS : 'this' ;
//SUPER: 'super' ;
//VARIABLE : 'var' ;
//IMMUTABLE : 'val' ;
//PRINT : 'print' ;
//EQUALS : '=' ;
//BOOL : 'true' | 'false' ;
//NULL : 'null' ;
//STRING : '"'~('\r' | '\n' | '"')*'"' ;
//SimpleName : [a-zA-Z0-9]+ ;


