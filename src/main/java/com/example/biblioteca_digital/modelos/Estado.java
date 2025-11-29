package com.example.biblioteca_digital.modelos;

public enum Estado {
    activo,
    devuelto,
    bloqueado;

    public static Estado from(String s) {
        if (s == null) return null;

        String t = s.trim();

        for (Estado e : values()) {
            if (e.name().equalsIgnoreCase(t)) {
                return e;
            }
        }

        return null;
    }
}



