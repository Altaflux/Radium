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

value
    :  integerLiteral
    |  floatingPointLiteral
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

floatingPointLiteral
	:	DecimalFloatingPointLiteral
	|	HexadecimalFloatingPointLiteral
	;
integerLiteral
    :   DecimalIntegerLiteral
    |	HexIntegerLiteral
    |	OctalIntegerLiteral
    |	BinaryIntegerLiteral
    ;
stringLiteral
    : SINGLE_QUOTE (SINLE_QUOTE_ESCAPED_CHAR | SINLE_QUOTE_EXPRESSION_START expression CLOSE_BLOCK | SINGLE_QUOTE_REF | ~SINLE_QUOTE_CLOSE)* SINLE_QUOTE_CLOSE
    | TRIPLE_QUOTE (MULTILINE_QUOTE_EXPRESSION_START expression CLOSE_BLOCK | MULTILINE_QUOTE_REF | ~MULTILINE_QUOTE_CLOSE)* MULTILINE_QUOTE_CLOSE
    ;
