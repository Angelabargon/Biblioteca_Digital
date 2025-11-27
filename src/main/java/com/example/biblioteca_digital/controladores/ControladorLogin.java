package com.example.biblioteca_digital.controladores;

/*
Hacemos los importes necesarios.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/*
Creamos la clase tras la lógica del controlador.
 */
public class ControladorLogin {

    @FXML
    private ToggleButton tbt_usuario;

    @FXML
    private ToggleButton tbt_admin;

    @FXML
    private ToggleGroup grupoRol;

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField pf_contraseña;

    @FXML
    private Button bt_inicioUsuario;

    @FXML
    private Button bt_ayuda;

    @FXML
    private Button bt_volver;

        @FXML
        public void initialize() {
            grupoRol = new ToggleGroup();
            tbt_usuario.setToggleGroup(grupoRol);
            tbt_admin.setToggleGroup(grupoRol);
            tbt_usuario.setSelected(true);

            grupoRol.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    ToggleButton seleccionado = (ToggleButton) newToggle;
                    String rol = seleccionado.getText().toLowerCase();

                    if (rol.equals("admin")) {
                        tf_email.setPromptText("Email de Administrador");
                        pf_contraseña.setPromptText("Contraseña de Administrador");
                        bt_inicioUsuario.setText("Iniciar Sesión como Administrador");
                    } else {
                        tf_email.setPromptText("Email");
                        pf_contraseña.setPromptText("Contraseña");
                        bt_inicioUsuario.setText("Iniciar Sesión como Usuario");
                    }
                }
            });
        }

        @FXML
        public void mostrarAyuda(ActionEvent event) {
            ControladorAyuda.mostrarAyuda("/com/example/biblioteca_digital/vistas/Vista-Ayuda-Login.fxml", "Login");
        }

        @FXML
        private void iniciarSesion(ActionEvent event)
        {
            String email = tf_email.getText().trim();
            String contraseña = pf_contraseña.getText().trim();
            String rol = tbt_usuario.isSelected() ? "usuario" : "admin";

            if (email.isEmpty() || contraseña.isEmpty()) {
                System.out.println("Rellena todos los campos.");
                return;
            }

            Optional<Usuario> cuentaAutenticada = autenticar(email, contraseña, rol);
            if (cuentaAutenticada.isPresent())
            {
                Sesion.setUsuario(cuentaAutenticada.get());

                String vistaDestino = rol.equals("usuario")
                        ? "/com/example/biblioteca_digital/vistas/usuario/Vista-Menu-Usuario.fxml"
                        : "/com/example/biblioteca_digital/vistas/admin/Vista-Administrador.fxml";

                Navegacion.cambiarVista(event, vistaDestino, "Bienvenido");
            } else {
                System.out.println("Credenciales incorrectas, comprueba tu email o contraseña.");
            }
        }

    public static Optional<Usuario> autenticar(String correo, String contrasena, String rol)
    {
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ? AND rol = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);
            stmt.setString(3, rol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setPrimerApellido(rs.getString("primer_apellido"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setRol(Rol.valueOf(rs.getString("rol")));
                usuario.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
                return Optional.of(usuario);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error al autenticar: " + e.getMessage());
        }
        return Optional.empty();
    }

    @FXML
    private void volverAtras(ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Pagina-Inicio.fxml", "Página de Inicio");
    }
}


