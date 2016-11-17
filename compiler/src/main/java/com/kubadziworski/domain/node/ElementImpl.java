package com.kubadziworski.domain.node;

public abstract class ElementImpl implements RdElement {
    private final NodeData element;

    public ElementImpl(NodeData element) {
        if (element != null) {
            this.element = element;
        } else {
            this.element = new DummyElement();
        }
    }

    public ElementImpl() {
        this.element = new DummyElement();
    }

    @Override
    public boolean shouldAnalyze() {
        return element.shouldAnalyze();
    }

    @Override
    public int getStartLine() {
        return element.getStartLine();
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public int getEndLine() {
        return element.getEndLine();
    }

    private static class DummyElement implements NodeData {

        @Override
        public boolean shouldAnalyze() {
            return false;
        }

        @Override
        public int getStartLine() {
            return 0;
        }

        @Override
        public String getText() {
            return "";
        }

        @Override
        public int getEndLine() {
            return 0;
        }
    }

}
