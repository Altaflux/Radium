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

classDeclaration : classAccessModifiers? className  primaryConstructor? abstractClassAndInterfaces? classBody  ;
className : qualifiedName ;
classBody : '{' (field | function | initBlock)* '}' ;
field : fieldModifier isFinal=(KEYWORD_var | KEYWORD_val) name ':' type ('=' expression)? getter? setter?;

getter: 'get' '('')' functionContent ;
setter: 'set' '(' SimpleName ')' block ;

primaryConstructor: '(' constructorParametersList? ')' ;

abstractClassAndInterfaces : ':' (abstractClassInit (',' typeName)*  | typeName (',' typeName)*) ;
abstractClassInit :  typeName '(' argumentList ')' ;


initBlock : 'init' block ;

constructorParametersList:  constructorParam (',' constructorParam)*
          |  constructorParam (',' constructorParameterWithDefaultValue)*
          |  constructorParameterWithDefaultValue (',' constructorParameterWithDefaultValue)* ;

constructorParam : (accessModifiers? asField=(KEYWORD_var | KEYWORD_val))? parameter ;
constructorParameterWithDefaultValue : (accessModifiers? asField=(KEYWORD_var | KEYWORD_val))?  parameterWithDefaultValue ;

function :  functionDeclaration functionContent? ;
functionDeclaration : methodModifier 'fn' functionName '('? parametersList? ')'? (':'(type))? ;
parametersList:  parameter (',' parameter)*
          |  parameter (',' parameterWithDefaultValue)*
          |  parameterWithDefaultValue (',' parameterWithDefaultValue)* ;
functionName : SimpleName ;
parameter :  SimpleName ':' type ;
parameterWithDefaultValue :  SimpleName ':' type '=' defaultValue=expression ;

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
           | expression cmp='||' expression #BooleanExpression
           | expression cmp='&&' expression #BooleanExpression
           | expression opType='|' expression #BinaryExpression
           | expression opType='&' expression #BinaryExpression
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

fieldModifiers
    : 'static'
    | 'open'
    | 'final' ;
methodModifiers
    : 'static'
    | 'inline'
    | 'open'
    | 'final' ;

accessModifiers: 'public' | 'private' | 'protected' ;
classAccessModifiers :  'public' | 'private' ;

methodModifier : (accessModifiers?  methodModifiers*) | (methodModifiers*  accessModifiers?) | (methodModifiers*  accessModifiers? methodModifiers*) ;
fieldModifier : (accessModifiers?  fieldModifiers*) | (fieldModifiers*  accessModifiers?) | (fieldModifiers*  accessModifiers? fieldModifiers*) ;

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


floatingPointLiteral returns [ValueHolder number]
	:	DecimalFloatingPointLiteral {
            $number = LiteralConverter.floatingPointLiteralFromString($DecimalFloatingPointLiteral.text);
        }
	|	HexadecimalFloatingPointLiteral {
            $number = LiteralConverter.floatingPointLiteralFromString($HexadecimalFloatingPointLiteral.text);
        }
	;
integerLiteral returns [ValueHolder number]
    :   DecimalIntegerLiteral {
            $number = LiteralConverter.decimalIntegerLiteral($DecimalIntegerLiteral.text);
        }
    |	HexIntegerLiteral {
            $number = LiteralConverter.hexIntegerLiteral($HexIntegerLiteral.text);
        }
    |	OctalIntegerLiteral {
            $number = LiteralConverter.octalIntegerLiteral($OctalIntegerLiteral.text);
        }
    |	BinaryIntegerLiteral {
            $number = LiteralConverter.binaryIntegerLiteral($BinaryIntegerLiteral.text);
        }
    ;
stringLiteral
    : SINGLE_QUOTE (SINLE_QUOTE_ESCAPED_CHAR | SINLE_QUOTE_EXPRESSION_START expression CLOSE_BLOCK | SINGLE_QUOTE_REF | ~SINLE_QUOTE_CLOSE)* SINLE_QUOTE_CLOSE
    | TRIPLE_QUOTE (MULTILINE_QUOTE_EXPRESSION_START expression CLOSE_BLOCK | MULTILINE_QUOTE_REF | ~MULTILINE_QUOTE_CLOSE)* MULTILINE_QUOTE_CLOSE
    ;
