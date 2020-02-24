package com.firstgame.game;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ColorCode implements Serializable {

    DEFAULT(0),
    PLAYER_1(1),
    PLAYER_2(2),
    PLAYER_3(3),
    PLAYER_4(4),
    COMMON(5),
    SOLID(6);

    private int code;

    private static final Map<Integer,ColorCode> lookup = new HashMap<Integer,ColorCode>();

    static {
        for(ColorCode w : EnumSet.allOf(ColorCode.class))
            lookup.put(w.getCode(), w);
    }

    ColorCode(int code) {
        this.code = code;
    }

    public int getCode() { return code; }

    public static ColorCode get(int code) {
        return lookup.get(code);
    }
}
