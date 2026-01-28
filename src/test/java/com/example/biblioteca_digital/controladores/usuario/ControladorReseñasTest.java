package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios para el test.
 */
import com.example.biblioteca_digital.DAO.usuario.ReseñasDAO;
import com.example.biblioteca_digital.controladores.ControladorReseñas;
import com.example.biblioteca_digital.modelos.Usuario;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de test unitarios para ControladorReseñas.
 */
@DisplayName("Test de ControladorReseñas")
class ControladorReseñasTest {

    private ControladorReseñas controlador;
    private ReseñasDAO reseñasDAOMock;

    /**
     * Inicio de JavaFX antes de todos los test.
     */
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    /**
     * Preparación de controlador y mok del DAO antes de cada test.
     */
    @BeforeEach
    void setUp() {
        controlador = new ControladorReseñas();

        // Simulación de campos JavaFX.
        controlador.ta_textoresena = new TextArea();
        controlador.cb_puntuacion = new ComboBox<>();

        // Simulación de sesión de usuario actual.
        Usuario u = new Usuario();
        u.setId(10);
        controlador.usuarioActual = u;

        // Simulación de idLibro.
        controlador.idLibro = 5;

        // Mock del DAO.
        reseñasDAOMock = mock(ReseñasDAO.class);
        controlador.reseñasDAO = reseñasDAOMock;
    }

    @DisplayName("Comprobación: No debe publicar si el texto está vacío o la puntuación es nula")
    @Test
    void testValidacionCampos() {
        controlador.ta_textoresena.setText("");
        controlador.cb_puntuacion.setValue(null);

        controlador.publicarResena();

        verify(reseñasDAOMock, never()).guardarReseña(any());
    }

    @DisplayName("Comprobación: Debe construir correctamente la reseña y enviarla al DAO")
    @Test
    void testPublicarResenaCorrecta() {
        controlador.ta_textoresena.setText("Muy buen libro");
        controlador.cb_puntuacion.setValue(5);

        when(reseñasDAOMock.guardarReseña(any())).thenReturn(true);

        controlador.publicarResena();

        verify(reseñasDAOMock, times(1)).guardarReseña(argThat(r ->
                r.getId_libro() == 5 && r.getId_usuario() == 10 &&
                        r.getContenido().equals("Muy buen libro") &&
                        r.getCalificacion() == 5
        ));
    }

    @DisplayName("Comprobación: Debe limpiar los campos tras publicar correctamente")
    @Test
    void testLimpiezaCampos() {
        controlador.ta_textoresena.setText("Excelente");
        controlador.cb_puntuacion.setValue(4);

        when(reseñasDAOMock.guardarReseña(any())).thenReturn(true);

        controlador.publicarResena();

        assertEquals("", controlador.ta_textoresena.getText());
        assertNull(controlador.cb_puntuacion.getValue());
    }
}