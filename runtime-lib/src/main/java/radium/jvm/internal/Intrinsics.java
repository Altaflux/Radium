package radium.jvm.internal;

@SuppressWarnings("unused")
public class Intrinsics {

    private Intrinsics() {
    }

    public static int compare(long thisVal, long anotherVal) {
        return thisVal < anotherVal ? -1 : thisVal == anotherVal ? 0 : 1;
    }

    public static int compare(int thisVal, int anotherVal) {
        return thisVal < anotherVal ? -1 : thisVal == anotherVal ? 0 : 1;
    }

}
