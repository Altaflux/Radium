lexer grammar EnkelLexer;

MULTILINE_COMMENT
    : '/*' .*? '*/' -> channel(HIDDEN)
    ;

SINGLELINE_COMMENT
    : '//' .*? '\n' -> channel(HIDDEN)
    ;

WHITESPACE
    : [\t\u000B\u000C\u0020\u00A0]+ -> channel(HIDDEN)
;
LineTerminator
 : [\r\n\u2028\u2029] -> channel(HIDDEN)
 ;

fragment
SingleCharacter
	:	~['\\]
	;

DecimalIntegerLiteral
	:	DecimalNumeral IntegerTypeSuffix?
	;

HexIntegerLiteral
	:	HexNumeral IntegerTypeSuffix?
	;

OctalIntegerLiteral
	:	OctalNumeral IntegerTypeSuffix?
	;

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
	:	[L]
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

DecimalFloatingPointLiteral
	:	Digits '.' Digits  ExponentPart? FloatTypeSuffix?
	|	'.' Digits ExponentPart? FloatTypeSuffix?
	|	Digits ExponentPart FloatTypeSuffix?
	|	Digits FloatTypeSuffix
	;
fragment
FloatTypeSuffix
	:	[FD]
	;

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

SEMI
    : ';';

OPEN_BLOCK
    : '{' -> pushMode(DEFAULT_MODE)
    ;

CLOSE_BLOCK
    : '}' -> popMode
    ;


fragment
LETTER
    : 'a' .. 'z'
    | 'A' .. 'Z'
    | '_'
    ;

fragment
EscapeChar
    :   'u' HexDigits
    ;

CharacterLiteral
    : '\'' (EscapeChar | .) '\'';

TRIPLE_QUOTE
    : '"""' -> pushMode(InMultiLineString)
    ;

SINGLE_QUOTE
    : '"' -> pushMode(InSingleLineString)
    ;

// keywords
PACKAGE
    : 'package';

IMPORT
    : 'import';

DOT
    : '.';

STAR : '*';

COMMA : ',';

LT : '<';
LTE : '<=';
GT : '>';
GTE : '>=';
EQ : '=';
EQ_EQ : '==';
EQ_EQ_EQ : '===';
NEQ : '!=';

COLON : ':';

BRACE_OPEN : '(';
BRACE_CLOSE : ')';

Q : '?';
DA : '!!.';

DISJ : '||';
BIT_OR: '|';
CONJ: '&&';
BIT_AND: '&';

ELVIS : '?:' ;
LONG_RANGE : '...';
RANGE : '..';

REFERENCE : '::';


//FALSE : 'false';
BOOL : 'true' | 'false'  ;

NULL : 'null';

OP_ASTERISK : '->';
OP_DIV : '/';
OP_MOD : '%';
OP_PLUS : '+';
OP_MUNUS : '-';

OP_IN : 'in';
OP_OUT : 'out';
OP_NOT_IN: '!in';
OP_IS : 'is';
OP_NOT_IS: '!is';
OP_AS: 'as';
OP_AS_SAFE: 'as?';

OP_PLUS_ASSIGN: '+=';
OP_MINUS_ASSIGN: '-=';
OP_MULT_ASSIGN: '*=';
OP_DIV_ASSIGN: '/=';
OP_MOD_ASSIGN: '%=';

OP_DECREMENT: '--';
OP_INCREMENT: '++';

OP_NULL_ASSERT: '!!';
OP_NOT: '!';

SQ_OPEN: '[';
SQ_CLOSE: ']';

KEYWORD_val : 'val';
KEYWORD_var : 'var';



GET : 'get';
SET : 'set';

InitKeyWord: 'init' ;



AccessModifier_private : 'private';
AccessModifier_protected : 'protected';
AccessModifier_public : 'public';
Modifier_final : 'final';
Modifier_open : 'open';
Modifier_inline : 'inline';
Modifier_static : 'static';
Modifier_abstract : 'abstract';
Create_New : 'new' ;

For_Loop_From : 'from' ;
For_Loop_To : 'to' ;


Jump_throw: 'throw';
Jump_continue: 'continue';
Jump_return: 'return';



ConstructorDelegationCall_this: 'this';
ConstructorDelegationCall_super: 'super';

Declaration_interface: 'interface';
Declaration_class: 'class';

Declaration_fun: 'fn';

CF_if: 'if';
CF_else: 'else';
CF_when: 'when';
CF_while: 'while';
CF_for: 'for';
CF_do: 'do';
CF_try: 'try';
CF_catch: 'catch';
CF_FINALLY: 'finally';

BAX: '$';

SimpleName
       :     LETTER (LETTER | Digit)*
    |    '`' ~('`')+? '`'
    ;


mode InSingleLineString;

SINLE_QUOTE_WHITESPACE
    : [\t\r\f ]+
;


SINGLE_TEXT
    : ~('\\' | '"'| [\n] | '$')+;

SINLE_QUOTE_CLOSE
    : '"' -> popMode;

SINLE_QUOTE_ESCAPED_CHAR
    : '\\' (EscapeChar | .);

SINLE_QUOTE_EXPRESSION_START
    : '${' -> pushMode(DEFAULT_MODE);

SINGLE_QUOTE_REF
    : '$' LETTER (LETTER | Digit)*
    ;

mode InMultiLineString;

MULTILINE_QUOTE_TEXT
    : ~('"' | '$')
    ;

MULTILINE_QUOTE_CLOSE
    : '"""' -> popMode;

MULTILINE_QUOTES
    : '""' | '"';

MULTILINE_QUOTE_EXPRESSION_START
    : '${' -> pushMode(DEFAULT_MODE);

MULTILINE_QUOTE_REF
    : '$' LETTER (LETTER | Digit)*
    ;
