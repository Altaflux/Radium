//header
grammar Enkel;

//RULES
compilationUnit : classDeclaration EOF ;
classDeclaration : className '{' classBody '}' ;
className : ID ;
classBody :  function* ;
function : functionDeclaration '{' (blockStatement)* '}' ;
functionDeclaration locals [ int paramIndex ] : (type)? functionName '('(functionArgument[ $paramIndex++ ])*')' ;
functionName : ID ;
functionArgument [ int index ] : type ID functionParamdefaultValue? ;
functionParamdefaultValue : '=' expression ;
type : primitiveType
     | classType ;

primitiveType :  'boolean' ('[' ']')*
                |   'string' ('[' ']')*
                |   'char' ('[' ']')*
                |   'byte' ('[' ']')*
                |   'short' ('[' ']')*
                |   'int' ('[' ']')*
                |   'long' ('[' ']')*
                |   'float' ('[' ']')*
                |   'double' ('[' ']')*
                | 'void' ('[' ']')* ;
classType : QUALIFIED_NAME ('[' ']')* ;

blockStatement locals [ int lastVarIndex ]: variableDeclaration[ $lastVarIndex++ ]
               | printStatement
               | functionCall ;
variableDeclaration [ int index ] : VARIABLE identifier EQUALS expression;
printStatement : PRINT expression ;
functionCall : functionName '('expressionList ')';
identifier : ID ;
expressionList : expression (',' expression)* ;
expression : identifier
           | value
           | functionCall ;
value : NUMBER
      | STRING ;
//TOKENS
VARIABLE : 'var' ;
PRINT : 'print' ;
EQUALS : '=' ;
NUMBER : [0-9]+ ;
STRING : '"'.*'"' ;
ID : [a-zA-Z0-9]+ ;
QUALIFIED_NAME : ID ('.' ID)+;
WS: [ \t\n\r]+ -> skip ;