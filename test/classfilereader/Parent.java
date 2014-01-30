package classfilereader;

public class Parent {
    // Note that we enumerate using A, B, C and not 1, 2, 3, so we don't get confused with
    // the numbers javac assigns to anonymous classes.
    public static class StaticChild {}

    public class ChildA {
        public class GrandChildAA {
            private Object anonGreatGrandChild = new Object() {
                public String toString() { return "anonGreatGrandChild"; }
            };
        }
    }

    // Force double-digit anon class numbers.
    private Object anonChildA = new Object() { public String toString() { return "anonChildA"; } };
    private Object anonChildB = new Object() { public String toString() { return "anonChildB"; } };
    private Object anonChildC = new Object() { public String toString() { return "anonChildC"; } };
    private Object anonChildD = new Object() { public String toString() { return "anonChildD"; } };
    private Object anonChildE = new Object() { public String toString() { return "anonChildE"; } };
    private Object anonChildF = new Object() { public String toString() { return "anonChildF"; } };
    private Object anonChildG = new Object() { public String toString() { return "anonChildG"; } };
    private Object anonChildH = new Object() { public String toString() { return "anonChildH"; } };
    private Object anonChildI = new Object() { public String toString() { return "anonChildI"; } };
    private Object anonChildJ = new Object() { public String toString() { return "anonChildJ"; } };

    private class ChildB {
        class GrandChildBA {}
    }

    private void foo() {
        class LocalChildC {}
        class LocalChildD {}
    }

    private void bar() {
        class LocalChildE {}
    }
}
