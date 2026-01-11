package com.example.biblioteca_digital.modelos;

/**
 * Este enum servirá para determinar como se encuentra un libro para ser prestado.
 */
public enum Estado {

    /** El libro está activo o en uso. */
    activo,

    /** El elemento ha sido devuelto. */
    devuelto,

    /** El elemento está bloqueado o restringido. */
    bloqueado;

    /**
     * Convierte una cadena de texto en el valor del enum {@code Estado}.
     * Se ignoran mayúsculas y minúsculas.
     *
     * @param s cadena que representa un estado
     * @return el valor {@code Estado} correspondiente, o {@code null} si no coincide con ninguno
     */
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



