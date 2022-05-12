package com.github.mutcianm.lafswitch;

public class LafNotFoundException extends RuntimeException {
    public LafNotFoundException(String name, LAF_KIND kind) {
        super("Can't find " + kind + " LAF - " + name);
        this.kind = kind;
    }
    LAF_KIND kind;
}
