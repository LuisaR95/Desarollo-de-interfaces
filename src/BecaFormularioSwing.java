import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter; // Necesario para filtrar archivos
import java.io.File; // Para la gestión de archivos
import java.io.FileWriter; // Para escribir datos en el archivo
import java.io.IOException; // Para manejar errores de I/O

/**
 * Aplicación de Solicitud de Beca con Interfaz Gráfica (Java Swing).
 * Utiliza CardLayout para gestionar los 4 pasos del formulario.
 * Estilo: Moderno, con esquema de colores azul/blanco y progreso visible.
 */
public class BecaFormularioSwing extends JFrame {

    // Contenedores principales para la navegación
    private final JPanel cardsPanel;
    private final CardLayout cardLayout;
    private int currentStep = 1;
    private final int totalSteps = 4;

    // Componentes para la recolección de datos
    private final Map<String, JTextField> fields = new HashMap<>();

    // Componentes de la interfaz para aplicar estilos y actualizaciones
    private JButton btnAtras;
    private JButton btnSiguiente;
    private JButton btnConfirmar;
    private JLabel progressLabel;
    private JPanel progressIndicatorPanel; // Panel para los indicadores de paso

    // Etiquetas para el resumen del Paso 4
    private final Map<String, JLabel> summaryLabels = new HashMap<>();

    // Definición de Colores y Fuentes
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246); // Índigo/Azul de Tailwind
    private static final Color SECONDARY_COLOR = new Color(75, 85, 99); // Gris oscuro
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Fondo muy claro
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129); // Verde para éxito
    private static final Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);

    // --- CLASE INTERNA: Modelo de Datos y Generador XML ---
    private static class BecaSolicitud {
        String nombre, apellidos, dni, direccion, correoElectronico, telefono, centroEstudios, titulacion, anio, nombrePadreMadre, dniPadreMadre;
        LocalDate fechaNacimiento;
        double rentaTotalUnidadFamiliar;
        int numeroComponentes;

        /**
         * Genera la cadena de texto con formato XML a partir de los datos.
         * @return String que contiene la estructura XML completa.
         */
        public String generateXML() {
            StringBuilder xmlBuilder = new StringBuilder();
            xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlBuilder.append("<SolicitudBeca>\n");

            xmlBuilder.append("    <DatosPersonales>\n");
            xmlBuilder.append("        <Nombre>").append(nombre).append("</Nombre>\n");
            xmlBuilder.append("        <Apellidos>").append(apellidos).append("</Apellidos>\n");
            xmlBuilder.append("        <DNI>").append(dni).append("</DNI>\n");
            xmlBuilder.append("        <Direccion>").append(direccion).append("</Direccion>\n");
            xmlBuilder.append("        <CorreoElectronico>").append(correoElectronico).append("</CorreoElectronico>\n");
            xmlBuilder.append("        <Telefono>").append(telefono).append("</Telefono>\n");
            xmlBuilder.append("        <FechaNacimiento>").append(fechaNacimiento != null ? fechaNacimiento.toString() : "").append("</FechaNacimiento>\n");
            xmlBuilder.append("    </DatosPersonales>\n");

            xmlBuilder.append("    <DatosAcademicos>\n");
            xmlBuilder.append("        <CentroEstudios>").append(centroEstudios).append("</CentroEstudios>\n");
            xmlBuilder.append("        <Titulacion>").append(titulacion).append("</Titulacion>\n");
            xmlBuilder.append("        <Anio>").append(anio).append("</Anio>\n");
            xmlBuilder.append("    </DatosAcademicos>\n");

            xmlBuilder.append("    <DatosFinancieros>\n");
            xmlBuilder.append("        <NombrePadreMadre>").append(nombrePadreMadre).append("</NombrePadreMadre>\n");
            xmlBuilder.append("        <DNIPadreMadre>").append(dniPadreMadre).append("</DNIPadreMadre>\n");
            xmlBuilder.append("        <RentaTotalUnidadFamiliar>").append(String.format("%.2f", rentaTotalUnidadFamiliar)).append("</RentaTotalUnidadFamiliar>\n");
            xmlBuilder.append("        <NumeroComponentes>").append(numeroComponentes).append("</NumeroComponentes>\n");
            xmlBuilder.append("    </DatosFinancieros>\n");

            xmlBuilder.append("</SolicitudBeca>");
            return xmlBuilder.toString();
        }
    }
    // --- FIN CLASE INTERNA ---


    public BecaFormularioSwing() {
        setTitle("Sistema de Solicitud de Beca - Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 550); // Aumentar tamaño para mejor visualización
        getContentPane().setBackground(BACKGROUND_COLOR); // Fondo del contenido
        setLayout(new BorderLayout(15, 15)); // Más espaciado entre componentes principales

        // Título y barra de progreso
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 1. Inicializar CardLayout y Panel de Tarjetas
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Mayor margen interior del contenido
        cardsPanel.setBackground(BACKGROUND_COLOR);

        // 2. Crear los pasos
        cardsPanel.add(createStep1(), "Paso1");
        cardsPanel.add(createStep2(), "Paso2");
        cardsPanel.add(createStep3(), "Paso3");
        cardsPanel.add(createStep4(), "Paso4");

        // 3. ENVOLVER EL PANEL DE TARJETAS EN UN SCROLL PANE para garantizar la visibilidad de todos los campos
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        // Quitar el borde por defecto del JScrollPane para mantener el diseño limpio
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // Asegurar que la barra de desplazamiento horizontal no aparezca
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER); // Añadir el JScrollPane al centro

        // 4. Crear Panel de Botones
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Mostrar el primer paso al inicio
        updateUI();
        setLocationRelativeTo(null); // Centrar la ventana
        setVisible(true);
    }

    /**
     * Crea el panel superior con el título y el indicador de progreso.
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBorder(new EmptyBorder(15, 15, 5, 15));
        header.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Sistema de Solicitud de Beca");
        titleLabel.setFont(TITLE_FONT.deriveFont(Font.BOLD, 22));
        titleLabel.setForeground(SECONDARY_COLOR);
        header.add(titleLabel, BorderLayout.NORTH);

        progressIndicatorPanel = new JPanel(new GridLayout(1, totalSteps));
        progressIndicatorPanel.setBackground(Color.WHITE);

        progressLabel = new JLabel("Paso 1 de 4: Datos Personales");
        progressLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD, 14));
        progressLabel.setForeground(PRIMARY_COLOR);

        // Crear los indicadores visuales de los 4 pasos
        for (int i = 1; i <= totalSteps; i++) {
            progressIndicatorPanel.add(createStepIndicator(i));
        }

        JPanel progressWrapper = new JPanel(new BorderLayout());
        progressWrapper.setBackground(Color.WHITE);
        progressWrapper.add(progressLabel, BorderLayout.NORTH);
        progressWrapper.add(progressIndicatorPanel, BorderLayout.CENTER);

        header.add(progressWrapper, BorderLayout.CENTER);

        return header;
    }

    /**
     * Crea un componente visual para un paso específico (círculo con número).
     */
    private JComponent createStepIndicator(int stepNumber) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        JLabel dot = new JLabel(String.valueOf(stepNumber));
        dot.setName("dot-" + stepNumber);
        dot.setPreferredSize(new Dimension(28, 28));
        dot.setHorizontalAlignment(SwingConstants.CENTER);
        dot.setOpaque(true);
        dot.setFont(MAIN_FONT.deriveFont(Font.BOLD, 14));

        p.add(dot);
        return p;
    }

    /**
     * Crea un panel con etiqueta y campo de texto con estilo mejorado.
     */
    private JPanel createFieldPanel(String labelText, String fieldName, String initialValue) {
        // Aumentar el espaciado vertical
        JPanel panel = new JPanel(new BorderLayout(8, 5));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel label = new JLabel("<html><b>" + labelText + ":</b></html>");
        label.setFont(MAIN_FONT);

        JTextField textField = new JTextField(initialValue);
        textField.setEditable(true); // Asegurar que sea editable
        textField.setFocusable(true); // Asegurar que pueda recibir el foco
        textField.setFont(MAIN_FONT);
        // Aumentar el padding interno del campo de texto
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10) // Más padding interno (antes 5, 8, 5, 8)
        ));
        textField.putClientProperty("JComponent.roundRect", true);

        fields.put(fieldName, textField);

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    // --- Implementación de los Pasos del Formulario ---

    private JPanel createStep1() {
        // Usamos BoxLayout para un mejor control del espaciado vertical
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        TitledBorder border = BorderFactory.createTitledBorder("Paso 1: Datos Personales");
        border.setTitleFont(TITLE_FONT);
        border.setTitleColor(PRIMARY_COLOR);
        panel.setBorder(border);

        // Función auxiliar para agregar campos con espaciado vertical (10px)
        addSpacedField(panel, createFieldPanel("Nombre", "nombre", ""));
        addSpacedField(panel, createFieldPanel("Apellidos", "apellidos", ""));
        addSpacedField(panel, createFieldPanel("DNI", "dni", ""));
        addSpacedField(panel, createFieldPanel("Dirección", "direccion", ""));
        addSpacedField(panel, createFieldPanel("Correo Electrónico", "correoElectronico", ""));
        addSpacedField(panel, createFieldPanel("Teléfono (TF)", "telefono", ""));
        addSpacedField(panel, createFieldPanel("Fecha Nacimiento (AAAA-MM-DD)", "fechaNacimiento", ""));

        return panel;
    }

    private JPanel createStep2() {
        // Usamos BoxLayout para un mejor control del espaciado vertical
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        TitledBorder border = BorderFactory.createTitledBorder("Paso 2: Datos Académicos (Campos de Ancho Uniforme)");
        border.setTitleFont(TITLE_FONT);
        border.setTitleColor(PRIMARY_COLOR);
        panel.setBorder(border);

        // **MODIFICACIÓN 1: Todos los campos al mismo ancho (300px)**
        final int fixedWidth = 300;
        addConstrainedField(panel, createFieldPanel("Centro de Estudios", "centroEstudios", ""), fixedWidth);
        addConstrainedField(panel, createFieldPanel("Titulación", "titulacion", ""), fixedWidth);
        addConstrainedField(panel, createFieldPanel("Año (Ej. 2º Curso / 2024)", "anio", ""), fixedWidth);

        return panel;
    }

    private JPanel createStep3() {
        // Usamos BoxLayout para un mejor control del espaciado vertical
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        TitledBorder border = BorderFactory.createTitledBorder("Paso 3: Datos Financieros (Campos de Ancho Reducido)");
        border.setTitleFont(TITLE_FONT);
        border.setTitleColor(PRIMARY_COLOR);
        panel.setBorder(border);

        // **MODIFICACIÓN 2: Reducir ancho de campos financieros**
        final int longFieldWidth = 300;
        final int shortFieldWidth = 150;

        addConstrainedField(panel, createFieldPanel("Nombre Padre / Madre", "nombrePadreMadre", ""), longFieldWidth);
        addConstrainedField(panel, createFieldPanel("DNI Padre / Madre", "dniPadreMadre", ""), longFieldWidth);

        // Campos numéricos más cortos
        addConstrainedField(panel, createFieldPanel("Renta Total Unidad Familiar (€)", "rentaTotal", "0.00"), shortFieldWidth);
        addConstrainedField(panel, createFieldPanel("N.º Componentes Unidad Familiar", "numComponentes", "1"), shortFieldWidth);

        return panel;
    }

    /**
     * Función auxiliar para añadir un componente con espaciado vertical que toma todo el ancho.
     */
    private void addSpacedField(JPanel container, JComponent component) {
        container.add(component);
        container.add(Box.createVerticalStrut(10)); // Espaciador de 10 píxeles
    }

    /**
     * NUEVA FUNCIÓN: Añade un componente con espaciado vertical y le aplica una restricción de ancho.
     */
    private void addConstrainedField(JPanel container, JComponent fieldComponent, int width) {
        // 1. Crear un wrapper que contendrá el campo
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND_COLOR);

        // 2. Establecer el tamaño máximo para restringir el ancho.
        // Se calcula la altura mínima necesaria para el componente + la etiqueta del campo.
        int height = fieldComponent.getPreferredSize().height;
        // Ajuste en altura para asegurar que la etiqueta superior y el campo caben cómodamente.
        Dimension maxDim = new Dimension(width, height + 35);

        wrapper.setMaximumSize(maxDim);
        wrapper.setPreferredSize(new Dimension(width, height + 35));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda en BoxLayout

        // 3. El campo debe ser agregado al centro del wrapper
        wrapper.add(fieldComponent, BorderLayout.CENTER);

        // 4. Agregar el wrapper al contenedor y el espaciador vertical
        container.add(wrapper);
        container.add(Box.createVerticalStrut(10));
    }


    private JPanel createSummaryRow(String labelText, String fieldName) {
        // Aumentar el espaciado interno (BorderLayout 10, 10)
        JPanel row = new JPanel(new BorderLayout(10, 10));
        row.setBackground(new Color(250, 250, 255)); // Fondo ligeramente diferente para filas
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(10, 10, 10, 10) // Más padding (antes 5, 5, 5, 5)
        ));

        JLabel label = new JLabel("<html><b>" + labelText + ":</b></html>");
        label.setFont(MAIN_FONT);
        label.setForeground(SECONDARY_COLOR);

        JLabel valueLabel = new JLabel("N/A");
        valueLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        valueLabel.setForeground(PRIMARY_COLOR.darker());

        summaryLabels.put(fieldName, valueLabel);

        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private JPanel createStep4() {
        JPanel panel = new JPanel(new BorderLayout(15, 15)); // Aumentar separación general
        panel.setBackground(BACKGROUND_COLOR);
        TitledBorder border = BorderFactory.createTitledBorder("Paso 4: Resumen y Confirmación");
        border.setTitleFont(TITLE_FONT);
        border.setTitleColor(PRIMARY_COLOR);
        panel.setBorder(border);

        // Aumentar la separación horizontal y vertical del GridLayout
        // Nota: GridLayout(0, 2) crea 6 filas de 2 columnas para 12 componentes.
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 30, 15));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 255), 1),
                new EmptyBorder(15, 15, 15, 15) // Más padding exterior
        ));

        // Etiquetas de sección con color
        JLabel personalTitle = new JLabel("<html><font color='#3b82f6'><u><b>DATOS PERSONALES</b></u></font></html>");
        personalTitle.setFont(TITLE_FONT.deriveFont(Font.BOLD, 15));
        summaryPanel.add(personalTitle);
        summaryPanel.add(new JLabel("")); // Spacer para columna 2

        summaryPanel.add(createSummaryRow("Nombre Completo", "nombreApellidos"));
        summaryPanel.add(createSummaryRow("DNI Solicitante", "dni"));

        JLabel academicTitle = new JLabel("<html><font color='#3b82f6'><u><b>DATOS ACADÉMICOS</b></u></font></html>");
        academicTitle.setFont(TITLE_FONT.deriveFont(Font.BOLD, 15));
        summaryPanel.add(academicTitle);
        summaryPanel.add(new JLabel("")); // Spacer para columna 2

        summaryPanel.add(createSummaryRow("Titulación", "titulacion"));
        summaryPanel.add(createSummaryRow("Año Académico", "anio"));

        JLabel financialTitle = new JLabel("<html><font color='#3b82f6'><u><b>DATOS FINANCIEROS</b></u></font></html>");
        financialTitle.setFont(TITLE_FONT.deriveFont(Font.BOLD, 15));
        summaryPanel.add(financialTitle);
        summaryPanel.add(new JLabel("")); // Spacer para columna 2

        summaryPanel.add(createSummaryRow("Renta Total U.F.", "rentaTotal"));
        summaryPanel.add(createSummaryRow("N.º Componentes U.F.", "numComponentes"));

        panel.add(summaryPanel, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea("Por favor, revise atentamente los datos. Si son correctos, presione 'Confirmar y Grabar XML'.");
        infoArea.setEditable(false);
        infoArea.setWrapStyleWord(true);
        infoArea.setLineWrap(true);
        infoArea.setBackground(new Color(240, 240, 255)); // Fondo suave
        infoArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Más padding
        infoArea.setFont(MAIN_FONT);
        infoArea.setForeground(SECONDARY_COLOR);

        // **CORRECCIÓN 3:** Envuelvo el área de información en un JScrollPane
        JScrollPane infoScrollPane = new JScrollPane(infoArea);
        infoScrollPane.setBorder(BorderFactory.createEmptyBorder());
        infoScrollPane.setPreferredSize(new Dimension(500, 100)); // Altura adecuada para la info

        panel.add(infoScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel inferior con los botones de navegación y aplica estilos.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220))); // Línea divisoria

        // Estilo común de botones (aplicado directamente, no globalmente)
        Font buttonFont = MAIN_FONT.deriveFont(Font.BOLD);

        btnAtras = new JButton("<< Atrás");
        btnSiguiente = new JButton("Siguiente >>");
        btnConfirmar = new JButton("Confirmar y Grabar XML");

        // Aplicar la fuente directamente
        btnAtras.setFont(buttonFont);
        btnSiguiente.setFont(buttonFont);
        btnConfirmar.setFont(buttonFont);

        // Estilos específicos
        styleButton(btnAtras, SECONDARY_COLOR, Color.WHITE);
        styleButton(btnSiguiente, PRIMARY_COLOR, Color.WHITE);
        styleButton(btnConfirmar, SUCCESS_COLOR, Color.WHITE);

        btnAtras.addActionListener(this::handleNavigation);
        btnSiguiente.addActionListener(this::handleNavigation);
        btnConfirmar.addActionListener(this::handleConfirm);

        panel.add(btnAtras);
        panel.add(btnSiguiente);
        panel.add(btnConfirmar);

        return panel;
    }

    /**
     * Aplica el estilo (colores) a un botón.
     */
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        // Agregar sombra simple (opcional, depende del LookAndFeel)
        button.putClientProperty("JComponent.outline", PRIMARY_COLOR);
        button.putClientProperty("JComponent.roundRect", true);
    }

    /**
     * Maneja la acción de los botones "Atrás" y "Siguiente".
     */
    private void handleNavigation(ActionEvent e) {
        int nextStep = currentStep;

        if (e.getSource() == btnSiguiente) {
            // Antes de avanzar, validar el paso actual
            if (!validateCurrentStep()) {
                // Usar un dialogo simple con el estilo por defecto de Swing
                JOptionPane.showMessageDialog(this, "Debe completar todos los campos obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            nextStep = currentStep + 1;
        } else if (e.getSource() == btnAtras) {
            nextStep = currentStep - 1;
        }

        if (nextStep >= 1 && nextStep <= totalSteps) {
            currentStep = nextStep;
            updateUI();
        }
    }

    /**
     * Valida que los campos obligatorios del paso actual no estén vacíos.
     * @return true si la validación es exitosa, false en caso contrario.
     */
    private boolean validateCurrentStep() {
        // Mapeo de campos requeridos por paso
        String[] requiredFields = switch (currentStep) {
            case 1 -> new String[]{"nombre", "apellidos", "dni", "direccion", "correoElectronico", "telefono", "fechaNacimiento"};
            case 2 -> new String[]{"centroEstudios", "titulacion", "anio"};
            case 3 -> new String[]{"nombrePadreMadre", "dniPadreMadre", "rentaTotal", "numComponentes"};
            default -> new String[]{};
        };

        boolean allValid = true;
        for (String fieldName : requiredFields) {
            if (fields.containsKey(fieldName)) {
                JTextField field = fields.get(fieldName);
                if (field.getText().trim().isEmpty()) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.RED, 2),
                            BorderFactory.createEmptyBorder(7, 10, 7, 10) // Mantener padding del campo
                    ));
                    allValid = false;
                } else {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                            BorderFactory.createEmptyBorder(7, 10, 7, 10) // Mantener padding del campo
                    ));
                }
            }
        }
        return allValid;
    }

    /**
     * Actualiza la interfaz gráfica al cambiar de paso (navegación y progreso).
     */
    private void updateUI() {
        // 1. Mostrar la tarjeta correcta
        cardLayout.show(cardsPanel, "Paso" + currentStep);

        // 2. Control de botones
        btnAtras.setEnabled(currentStep > 1);
        btnSiguiente.setVisible(currentStep < totalSteps);
        btnConfirmar.setVisible(currentStep == totalSteps);

        // 3. Actualizar indicador de progreso
        progressLabel.setText(String.format("Paso %d de %d: %s", currentStep, totalSteps, getStepTitle(currentStep)));
        updateProgressDots();

        // 4. Actualizar el resumen si estamos en el Paso 4
        if (currentStep == totalSteps) {
            populateSummary();
        }

        // 5. CORRECCIÓN DE FOCO: Asegurar que el primer campo editable tome el foco en los pasos de entrada de datos
        if (currentStep >= 1 && currentStep <= 3) {
            // Utilizamos invokeLater para asegurar que la solicitud de foco se haga después de que el contenedor sea visible
            SwingUtilities.invokeLater(() -> {
                String firstFieldName = switch (currentStep) {
                    case 1 -> "nombre";
                    case 2 -> "centroEstudios";
                    case 3 -> "nombrePadreMadre";
                    default -> null;
                };

                if (firstFieldName != null && fields.containsKey(firstFieldName)) {
                    fields.get(firstFieldName).requestFocusInWindow();
                }
            });
        }
    }

    /**
     * Obtiene el título del paso actual.
     */
    private String getStepTitle(int step) {
        return switch (step) {
            case 1 -> "Datos Personales";
            case 2 -> "Datos Académicos";
            case 3 -> "Datos Financieros";
            case 4 -> "Resumen y Confirmación";
            default -> "";
        };
    }

    /**
     * Actualiza el color de los indicadores de paso (círculos).
     */
    private void updateProgressDots() {
        for (Component comp : progressIndicatorPanel.getComponents()) {
            if (comp instanceof JPanel panel) {
                JLabel dot = (JLabel) panel.getComponent(0);
                int stepNumber = Integer.parseInt(dot.getText());

                if (stepNumber < currentStep) {
                    dot.setBackground(SUCCESS_COLOR);
                    dot.setForeground(Color.WHITE);
                } else if (stepNumber == currentStep) {
                    dot.setBackground(PRIMARY_COLOR);
                    dot.setForeground(Color.WHITE);
                } else {
                    dot.setBackground(new Color(220, 220, 220));
                    dot.setForeground(SECONDARY_COLOR);
                }
                dot.repaint();
            }
        }
    }

    /**
     * Rellena las etiquetas del Paso 4 con los datos recogidos.
     */
    private void populateSummary() {
        // Simplemente obtener los valores de los campos
        String nombre = fields.get("nombre").getText();
        String apellidos = fields.get("apellidos").getText();
        String dni = fields.get("dni").getText();
        String titulacion = fields.get("titulacion").getText();
        String anio = fields.get("anio").getText();
        String rentaTotal = fields.get("rentaTotal").getText();
        String numComponentes = fields.get("numComponentes").getText();

        // Actualizar etiquetas
        summaryLabels.get("nombreApellidos").setText(nombre + " " + apellidos);
        summaryLabels.get("dni").setText(dni);
        summaryLabels.get("titulacion").setText(titulacion);
        summaryLabels.get("anio").setText(anio);

        // Formateo de moneda
        try {
            double renta = Double.parseDouble(rentaTotal);
            summaryLabels.get("rentaTotal").setText(String.format("€%,.2f", renta));
        } catch (NumberFormatException e) {
            summaryLabels.get("rentaTotal").setText("Error de formato (Use número)");
        }

        summaryLabels.get("numComponentes").setText(numComponentes);
    }

    /**
     * Maneja la acción final: Confirma, rellena el modelo y genera el XML, y lo guarda en un archivo.
     */
    private void handleConfirm(ActionEvent e) {
        // 1. Recoger datos y rellenar el modelo
        BecaSolicitud solicitud = new BecaSolicitud();
        String xml;

        try {
            // Recolección y conversión de datos (incluyendo validación de formato)
            solicitud.nombre = fields.get("nombre").getText();
            solicitud.apellidos = fields.get("apellidos").getText();
            solicitud.dni = fields.get("dni").getText();
            solicitud.direccion = fields.get("direccion").getText();
            solicitud.correoElectronico = fields.get("correoElectronico").getText();
            solicitud.telefono = fields.get("telefono").getText();
            // Esto fallará si el formato de fecha es incorrecto (AAAA-MM-DD)
            solicitud.fechaNacimiento = LocalDate.parse(fields.get("fechaNacimiento").getText());

            solicitud.centroEstudios = fields.get("centroEstudios").getText();
            solicitud.titulacion = fields.get("titulacion").getText();
            solicitud.anio = fields.get("anio").getText();

            solicitud.nombrePadreMadre = fields.get("nombrePadreMadre").getText();
            solicitud.dniPadreMadre = fields.get("dniPadreMadre").getText();
            // Esto fallará si no es un número
            solicitud.rentaTotalUnidadFamiliar = Double.parseDouble(fields.get("rentaTotal").getText());
            // Esto fallará si no es un entero
            solicitud.numeroComponentes = Integer.parseInt(fields.get("numComponentes").getText());

            // 2. Generar el XML
            xml = solicitud.generateXML();

            // 3. Abrir JFileChooser para guardar el archivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Solicitud XML");
            fileChooser.setSelectedFile(new File("solicitud_beca_" + solicitud.dni + ".xml"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos XML (*.xml)", "xml"));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Asegurar que la extensión .xml está presente
                if (!fileToSave.getName().toLowerCase().endsWith(".xml")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".xml");
                }

                try (FileWriter fileWriter = new FileWriter(fileToSave)) {
                    fileWriter.write(xml);

                    // Notificación de éxito
                    JOptionPane.showMessageDialog(this,
                            "El archivo XML se ha guardado correctamente en:\n" + fileToSave.getAbsolutePath(),
                            "Guardado Exitoso",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioException) {
                    // Manejo de errores de escritura
                    JOptionPane.showMessageDialog(this,
                            "Error al guardar el archivo: " + ioException.getMessage(),
                            "Error de I/O",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Notificación si el usuario cancela
                JOptionPane.showMessageDialog(this,
                        "Guardado cancelado por el usuario.",
                        "Cancelado",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            // Captura errores de conversión (fecha, renta, componentes)
            JOptionPane.showMessageDialog(this,
                    "Error de formato. Asegúrese de que:\n" +
                            "1. La Fecha de Nacimiento esté en formato AAAA-MM-DD.\n" +
                            "2. La Renta Total sea un número (ej. 15000.00).\n" +
                            "3. Los Componentes sean un número entero.",
                    "Error de Procesamiento de Datos",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Aplicar LookAndFeel moderno (si está disponible)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Si Nimbus falla, usar el LookAndFeel del sistema.
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // No hacer nada si falla el sistema L&F
            }
        }

        // Ejecutar la aplicación Swing en el hilo de despacho de eventos
        SwingUtilities.invokeLater(BecaFormularioSwing::new);
    }
}