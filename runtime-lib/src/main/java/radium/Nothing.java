package radium;

/**
 * Represents an impossible value, used for functions that cannot terminate like functions
 * which throw exception, are an infinite loop.
 * In the case of Block expression from IF Expressions, cases where no return value can be assigned given the usage
 * of IF as an expression.
 */
public final class Nothing {
    private Nothing() {
    }
}
