package com.kubadziworski.domain.types;


public enum Variance {
    INVARIANT("", true, true, 0),
    IN_VARIANCE("in", true, false, -1),
    OUT_VARIANCE("out", false, true, +1);

    private final String label;
    private final boolean allowsInPosition;
    private final boolean allowsOutPosition;
    private final int superpositionFactor;

    Variance(String label, boolean allowsInPosition, boolean allowsOutPosition, int superpositionFactor) {
        this.label = label;
        this.allowsInPosition = allowsInPosition;
        this.allowsOutPosition = allowsOutPosition;
        this.superpositionFactor = superpositionFactor;
    }

    public boolean allowsPosition(Variance position) {
        switch (position) {
            case IN_VARIANCE:
                return allowsInPosition;
            case OUT_VARIANCE:
                return allowsOutPosition;
            case INVARIANT:
                return allowsInPosition && allowsOutPosition;
            default:
                throw new IllegalStateException("Unknown variance type");
        }
    }


    public Variance superpose(Variance other) {
        int r = this.superpositionFactor * other.superpositionFactor;
        switch (r) {
            case 0:
                return INVARIANT;
            case -1:
                return IN_VARIANCE;
            case +1:
                return OUT_VARIANCE;
            default:
                throw new IllegalStateException("Illegal factor: " + r);
        }
    }

    public Variance opposite() {
        switch (this) {
            case INVARIANT:
                return INVARIANT;
            case IN_VARIANCE:
                return OUT_VARIANCE;
            case OUT_VARIANCE:
                return INVARIANT;
            default:
                throw new IllegalStateException("Unknown variance type");
        }
    }

    public String getLabel() {
        return label;
    }

    public boolean isAllowsInPosition() {
        return allowsInPosition;
    }

    public boolean isAllowsOutPosition() {
        return allowsOutPosition;
    }
}
