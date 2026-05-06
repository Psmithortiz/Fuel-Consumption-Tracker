import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    //ARRAYS
    static ArrayList<String> patentes = new ArrayList<>();
    static ArrayList<Double> kilometrosRecorridos = new ArrayList<>();
    static ArrayList<Double> litrosCargados = new ArrayList<>();
    static JTextField[] campos;
    //SWING
    static JTextField txtPatente;
    static JTextField txtKilometrosRecorridos;
    static JTextField txtLitrosCargados;
    static JTextArea areaTexto;
    static JFrame frame;
    //CENTINELAS
    private static final Double DOUBLE_INVALIDO = -4.2;
    private static final Integer CANCELO = -2;
    private static final Integer INVALIDO_O_NO_ENCONTRADA = -1;
    // PARAMETRO NEGOCIO
    private static final double RENDIMIENTO_CORTE = 10.0;
    //AVISOS
    private static final String AVISO_ESPACIO_VACIO = "Todos los campos deben estar completos.";
    private static final String AVISO_PATENTE_NO_ENCONTRADA = "No se encontro ningun vehiculo con esa patente";
    private static final String AVISO_PATENTE_DUPLICADA = "Ya existe un vehiculo registrado con esa patente";
    private static final String AVISO_PATENTE_ELIMINADA = "Se elimino correctamente la patente: ";
    private static final String AVISO_EDICION_EXITOSA = "Se edito correctamente la patente: ";
    private static final String AVISO_NO_HAY_REGISTROS = "No hay registros";
    private static final String AVISO_DESEA_ELIMINAR = "Ingrese la patente del vehiculo que desea eliminar";
    private static final String AVISO_DESEA_EDITAR = "Ingrese la patente del vehiculo que desea editar";
    private static final String AVISO_CHAR_INVALIDO = "La patente solo puede contener letras y numeros";
    private static final String AVISO_RENDIMIENTO_MENOR_CORTE =
            "No hay autos con bajo rendimiento (menor a " + RENDIMIENTO_CORTE + " km/L)";
    private static final String AVISO_ARCHIVO_CORRUPTO = "El archivo de registros contiene datos no válidos." +
            "\nPor favor revíselo antes de iniciar el programa.";

    public static void main(String[] args) {
        crearInterfaz();
    }

    private static void crearInterfaz() {

        cargarConManejoDeError();

        frame = new JFrame("Sistema de Consumo de Bencina");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // PANEL SUPERIOR
        JPanel panelSuperior = new JPanel(new BorderLayout());

        // SUBPANEL IZQUIERDO
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // MARGEN INTERNO

        //PATENTE
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Patente:      "));
        txtPatente = new JTextField(15);
        fila1.add(txtPatente);
        panelIzquierdo.add(fila1);
        //KILOMETROS
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Kilometros:"));
        txtKilometrosRecorridos = new JTextField(15);
        fila2.add(txtKilometrosRecorridos);
        panelIzquierdo.add(fila2);
        //LITROS
        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila3.add(new JLabel("Litros:          "));
        txtLitrosCargados = new JTextField(15);
        fila3.add(txtLitrosCargados);
        panelIzquierdo.add(fila3);

        campos = new JTextField[]{txtPatente, txtKilometrosRecorridos, txtLitrosCargados};

        //BOTON AGREGAR
        JPanel filaBoton = new JPanel(new BorderLayout());
        JButton btnAgregar = new JButton("Agregar Registro");
        filaBoton.add(btnAgregar, BorderLayout.CENTER);
        panelIzquierdo.add(filaBoton);
        //PANEL DERECHO
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new GridLayout(4, 1, 5, 5));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //BOTONES LATERALES
        JButton btnEditar = new JButton("Editar");
        panelDerecho.add(btnEditar);
        JButton btnEstadisticas = new JButton("Estadisticas");
        panelDerecho.add(btnEstadisticas);
        JButton btnFiltrar = new JButton("Filtrar");
        panelDerecho.add(btnFiltrar);
        JButton btnEliminar = new JButton("Eliminar");
        panelDerecho.add(btnEliminar);

        panelSuperior.add(panelIzquierdo, BorderLayout.WEST);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);
        frame.add(panelSuperior, BorderLayout.NORTH);

        //AREA DE TEXTO
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTexto);
        frame.add(scroll, BorderLayout.CENTER);
        frame.setSize(550, 450);
        frame.setVisible(true);

        modificarAreaTexto(); //ACTUALIZA INSTANTANEO EL TEXTAREA

        //UNIENDO LOGICA A BOTONES
        btnAgregar.addActionListener(e -> agregarRegistro());
        btnEliminar.addActionListener(e -> eliminarRegistro());
        btnEditar.addActionListener(e -> editarRegistro());
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        btnFiltrar.addActionListener(e -> filtrarBajoRendimiento());

        // PARA QUE ENTER AGREGUE REGISTROS CON ENTER DESDE CUALQUIER CAMPO DE RELLENADO
        java.awt.event.KeyAdapter enterParaAgregar = new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    agregarRegistro();
                }
            }
        };
        for (JTextField campo : campos) campo.addKeyListener(enterParaAgregar);

    }

    //METODOS AUXILIARES

    //METODOS SOBRE PATENTES
    private static int buscarIndicePatente(String patenteNormalizada) {
        int patentesLen = patentes.size();
        for (int i = 0; i < patentesLen; i++) {
            if (patentes.get(i).equals(patenteNormalizada)) return i;
        }
        JOptionPane.showMessageDialog(frame, AVISO_PATENTE_NO_ENCONTRADA);
        return INVALIDO_O_NO_ENCONTRADA; //SI -1 RETURN
    }

    private static String normalizarYValidarPatente(String textoUtil) {
        String patenteNormalizada = textoUtil.trim().toUpperCase();
        int largoPatente = patenteNormalizada.length();
        //VERIFICAR QUE NO TENGA CHARS RAROS O QUE ROMPAN EL REGISTRO " ; "
        for (int i = 0; i < largoPatente; i++) {
            if (!Character.isLetterOrDigit(patenteNormalizada.charAt(i))) {
                JOptionPane.showMessageDialog(frame, AVISO_CHAR_INVALIDO);
                return null; // SI NULL RETURN
            }
        }
        return patenteNormalizada;
    }

    private static int obtenerIndiceVehiculo(String aviso) {
        String patenteIngresada = JOptionPane.showInputDialog(frame, aviso);
        if (patenteIngresada == null) return CANCELO; //   BREAK, CERRO APRETO CANCEL X
        String patenteNormalizada = normalizarYValidarPatente(patenteIngresada);
        if (patenteNormalizada == null) return INVALIDO_O_NO_ENCONTRADA; // CONTINUE, SE EQUIVOCO ESCRIBIO MAL ETC
        return buscarIndicePatente(patenteNormalizada);
    }

    //VALIDACIONES
    private static boolean camposVacios() {
        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, AVISO_ESPACIO_VACIO);
                return true;
            }
        }
        return false;
    }

    private static boolean hayRegistros() {
        if (patentes.isEmpty()) {
            JOptionPane.showMessageDialog(frame, AVISO_NO_HAY_REGISTROS);
            return false;
        }
        return true;
    }

    private static Double validarNumero(JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim().replace(",", ".");
        try {
            Double numero = Double.parseDouble(texto);
            if (numero <= 0) {
                JOptionPane.showMessageDialog(frame, nombreCampo + " debe ser un numero mayor a 0.");
                return DOUBLE_INVALIDO;
            }
            return numero;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, nombreCampo + " debe ser un numero valido.");
            return DOUBLE_INVALIDO;
        }
    }

    //ACTUALIZAR TEXTO EN APP
    private static void modificarAreaTexto() {
        int cantidadVehiculos = patentes.size();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cantidadVehiculos; i++) {
            sb.append("Patente: " + patentes.get(i) + " | Km recorridos: " + kilometrosRecorridos.get(i) + " | Litros cargados: " + litrosCargados.get(i) + " | Rendimiento: " + String.format("%.2f", calcularRendimiento(i)) + " km/L\n");
        }
        areaTexto.setText(sb.toString());
    }

    private static void limpiarCampos() {
        for (JTextField campo : campos) campo.setText("");
        txtPatente.requestFocusInWindow();
    }

    //SOBRE PERSISTENCIA
    private static void guardarConManejoDeError() {
        try {
            Persistencia.guardar(patentes, kilometrosRecorridos, litrosCargados);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error al guardar: " + e.getMessage());
        }
    }

    private static void cargarConManejoDeError() {
        try {
            Persistencia.cargar(patentes, kilometrosRecorridos, litrosCargados);
        } catch (FileNotFoundException e) {
            //PRIMERA EJECUCION NUNCA HAY ARCHIVO, NO AVISAMOS NADA, SE CREA SOLO

            //EN EL CASO DE QUE EL ARCHIVO SE CORROMPIERA
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, AVISO_ARCHIVO_CORRUPTO);
            System.exit(1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error al cargar: " + e.getMessage());
        }
    }


    private static Double calcularRendimiento(int index) {
        return kilometrosRecorridos.get(index) / litrosCargados.get(index);
    }

    // LOGICA DIRECTA DE BOTONES
    private static void agregarRegistro() {
        if (camposVacios()) return;
        String patenteNormalizada = (normalizarYValidarPatente(txtPatente.getText()));
        if (patenteNormalizada == null) return;

        if (patentes.contains(patenteNormalizada)) {
            JOptionPane.showMessageDialog(frame, AVISO_PATENTE_DUPLICADA);
            return;
        }
        Double kmIngresados = validarNumero(txtKilometrosRecorridos, "Kilometros");
        Double litrosIngresados = validarNumero(txtLitrosCargados, "Litros");


        if (kmIngresados.equals(DOUBLE_INVALIDO) || litrosIngresados.equals(DOUBLE_INVALIDO)) return;

        patentes.add(patenteNormalizada);
        kilometrosRecorridos.add(kmIngresados);
        litrosCargados.add(litrosIngresados);

        guardarConManejoDeError();
        modificarAreaTexto();
        limpiarCampos();
    }

    private static void editarRegistro() {
        if (!hayRegistros()) return;
        while (true) {
            int indice = obtenerIndiceVehiculo(AVISO_DESEA_EDITAR);
            if (indice == CANCELO) break;
            if (indice == INVALIDO_O_NO_ENCONTRADA) continue;
            String patenteNormalizada = patentes.get(indice);

            JPanel panel = new JPanel();
            JTextField txtKm = new JTextField(String.valueOf(kilometrosRecorridos.get(indice)), 10);
            JTextField txtLitros = new JTextField(String.valueOf(litrosCargados.get(indice)), 10);
            panel.add(new JLabel("Vehiculo: " + patenteNormalizada));
            panel.add(new JLabel("Km:"));
            panel.add(txtKm);
            panel.add(new JLabel("Litros:"));
            panel.add(txtLitros);

            int resultado = JOptionPane.showConfirmDialog(frame, panel, "Editar vehiculo", JOptionPane.OK_CANCEL_OPTION);
            if (resultado != JOptionPane.OK_OPTION) return;

            Double kmIngresados = validarNumero(txtKm, "Kilometros");
            Double litrosIngresados = validarNumero(txtLitros, "Litros");

            if (kmIngresados.equals(DOUBLE_INVALIDO) || litrosIngresados.equals(DOUBLE_INVALIDO)) return;

            kilometrosRecorridos.set(indice, kmIngresados);
            litrosCargados.set(indice, litrosIngresados);

            guardarConManejoDeError();
            modificarAreaTexto();
            JOptionPane.showMessageDialog(frame, AVISO_EDICION_EXITOSA + patenteNormalizada);
            return;
        }
    }

    private static void mostrarEstadisticas() {
        if (!hayRegistros()) return;
        //CASO ESPECIAL DE SOLO 1 AUTO REGISTRADO
        if (patentes.size() == 1) {
            JOptionPane.showMessageDialog(frame, "El vehiculo con patente " + patentes.get(0) + " tuvo un rendimiento de " + String.format("%.2f", calcularRendimiento(0)) + " km/L");
            return;
        }
        String extremos = Estadisticas.extremosCalcular(patentes, kilometrosRecorridos, litrosCargados);

        Double promedio = Estadisticas.promedioCalcular(kilometrosRecorridos, litrosCargados);

        JOptionPane.showMessageDialog(frame, "El promedio de rendimiento fue: " + String.format("%.2f", promedio) + " km/L\n" +
                extremos);
    }

    private static void filtrarBajoRendimiento() {
        if (!hayRegistros()) return;
        StringBuilder sb = new StringBuilder();
        int patentesLen = patentes.size();
        for (int i = 0; i < patentesLen; i++) {
            double rendimiento = calcularRendimiento(i);
            if (rendimiento < RENDIMIENTO_CORTE) {
                sb.append("Patente: " + patentes.get(i) + " | Km recorridos: " + kilometrosRecorridos.get(i) + " | Litros cargados: " + litrosCargados.get(i) + " | Rendimiento: " + String.format("%.2f", rendimiento) + " km/L\n");
            }
        }
        if (sb.isEmpty()) {
            JOptionPane.showMessageDialog(frame, AVISO_RENDIMIENTO_MENOR_CORTE);
            return;
        }
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    private static void eliminarRegistro() {
        if (!hayRegistros()) return;
        while (true) {
            int indice = obtenerIndiceVehiculo(AVISO_DESEA_ELIMINAR);
            if (indice == CANCELO) break;
            if (indice == INVALIDO_O_NO_ENCONTRADA) continue;
            String patenteNormalizada = patentes.get(indice);
            patentes.remove(indice);
            kilometrosRecorridos.remove(indice);
            litrosCargados.remove(indice);

            guardarConManejoDeError();
            modificarAreaTexto();
            JOptionPane.showMessageDialog(frame, AVISO_PATENTE_ELIMINADA + patenteNormalizada);
            return;
        }
    }
}