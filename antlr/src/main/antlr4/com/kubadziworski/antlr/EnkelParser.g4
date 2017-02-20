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


floatingPointLiteral returns [ValueHolder number]
	:	DecimalFloatingPointLiteral {
            String val = $DecimalFloatingPointLiteral.text.replace("_","");
            boolean toDouble = val.endsWith("D");
            boolean toFloat = val.endsWith("F");
            Object finalValue = null;
            if(toDouble){
                finalValue = Double.parseDouble(val.replace("D", ""));
                $number = ValueHolder.of(ValueHolder.ValueType.DOUBLE, finalValue);
            }else if(toFloat){
                finalValue = (float) Double.parseDouble(val.replace("F", ""));
                $number = ValueHolder.of(ValueHolder.ValueType.FLOAT, finalValue);
            }else {
                finalValue = Double.parseDouble(val);
                $number = ValueHolder.of(ValueHolder.ValueType.DOUBLE, finalValue);
            }
        }
	|	HexadecimalFloatingPointLiteral {
            String val = $HexadecimalFloatingPointLiteral.text.replace("_","");
            boolean toDouble = val.endsWith("D");
            boolean toFloat = val.endsWith("F");
            Object finalValue = null;
            if(toDouble){
                finalValue = Double.parseDouble(val.replace("D", ""));
                $number = ValueHolder.of(ValueHolder.ValueType.DOUBLE, finalValue);
            }else if(toFloat){
                finalValue = (float) Double.parseDouble(val.replace("F", ""));
                $number = ValueHolder.of(ValueHolder.ValueType.FLOAT, finalValue);
            }else {
                finalValue = Double.parseDouble(val);
                $number = ValueHolder.of(ValueHolder.ValueType.DOUBLE, finalValue);
            }
        }
	;
integerLiteral returns [ValueHolder number]
    :   DecimalIntegerLiteral {
            String val = $DecimalIntegerLiteral.text.replace("_","");
            boolean toLong = val.endsWith("L");
            Object finalValue = null;
            if(toLong){
                finalValue = (long) Integer.parseInt(val.replace("L", ""));
                $number = ValueHolder.of(ValueHolder.ValueType.LONG, finalValue);
            }else {
                finalValue = Integer.parseInt(val);
                $number = ValueHolder.of(ValueHolder.ValueType.INT, finalValue);
            }
        }
    |	HexIntegerLiteral {
          String val = $HexIntegerLiteral.text.replace("_","");
          boolean toLong = val.endsWith("L");
          Object finalValue = null;
          if(toLong){
              finalValue = (long) Integer.decode(val.replace("L", ""));
              $number = ValueHolder.of(ValueHolder.ValueType.LONG, finalValue);
          }else {
              finalValue = Integer.decode(val);
              $number = ValueHolder.of(ValueHolder.ValueType.INT, finalValue);
          }
      }
    |	OctalIntegerLiteral {
            String val = $OctalIntegerLiteral.text.replace("_","");
            boolean toLong = val.endsWith("L");
            Object finalValue = null;
            if(toLong){
                finalValue = (long) Integer.parseInt(val.replace("L", ""), 8);
                $number = ValueHolder.of(ValueHolder.ValueType.LONG, finalValue);
            }else {
                finalValue = Integer.parseInt(val, 8);
                $number = ValueHolder.of(ValueHolder.ValueType.INT, finalValue);
            }
        }
    |	BinaryIntegerLiteral {
            String val = $BinaryIntegerLiteral.text.replace("_", "").replaceFirst("0b","").replaceFirst("0B","");
            boolean toLong = val.endsWith("L");
            Object finalValue = null;
            if(toLong){
                finalValue = (long) Integer.parseInt(val.replace("L", ""), 2);
                $number = ValueHolder.of(ValueHolder.ValueType.LONG, finalValue);
            }else {
                finalValue = Integer.parseInt(val, 2);
                $number = ValueHolder.of(ValueHolder.ValueType.INT, finalValue);
            }
        }
    ;
stringLiteral
    : SINGLE_QUOTE (SINLE_QUOTE_ESCAPED_CHAR | SINLE_QUOTE_EXPRESSION_START expression CLOSE_BLOCK | SINGLE_QUOTE_REF | ~SINLE_QUOTE_CLOSE)* SINLE_QUOTE_CLOSE
    | TRIPLE_QUOTE (MULTILINE_QUOTE_EXPRESSION_START expression CLOSE_BLOCK | MULTILINE_QUOTE_REF | ~MULTILINE_QUOTE_CLOSE)* MULTILINE_QUOTE_CLOSE
    ;
