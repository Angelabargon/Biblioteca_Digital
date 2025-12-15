package com.example.biblioteca_digital;

/**
 * Imports necesarios.
 */
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX.
 */
public class Application extends javafx.application.Application {

    /**
     * Metodo que se inicia automáticamente al iniciar la aplicación JavaFX.
     *
     * @param stage ventana principal proporcionada por JavaFX
     */
    @Override
    public void start(Stage stage) throws IOException {

        // Se carga la vista inicial desde el archivo FXML.
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/biblioteca_digital/vistas/Vista-Pagina-Inicio.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Centramos la ventana en la pantalla.
        stage.centerOnScreen();

        // Aplicamos el título de la ventana.
        stage.setTitle("BIBLIOTECA DIGITAL");
        stage.setScene(scene);

        // Mostramos la ventana.
        stage.show();
    }
}
