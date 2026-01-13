import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Aplicación de Solicitud de Beca con Restricciones de Formato.
 */
public class BecaFormularioSwing extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SECONDARY_COLOR = new Color(75, 85, 99);
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsPanel = new JPanel(cardLayout);
    private final Map<String, FormField> formFields = new HashMap<>();
    private final Map<String, JLabel> summaryLabels = new HashMap<>();
    private int currentStep = 1;
    private final int totalSteps = 4;

    private JButton btnAtras, btnSiguiente, btnConfirmar;
    private JLabel progressLabel;
    private JPanel progressIndicatorPanel;

    public BecaFormularioSwing() {
        setupFrame();
        initUI();
    }

    private void setupFrame() {
        setTitle("Portal de Solicitud de Becas v2.1 - Formatos Validados");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));
    }

    private void initUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);

        cardsPanel.setBackground(BACKGROUND_COLOR);
        cardsPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        cardsPanel.add(createStep1(), "Paso1");
        cardsPanel.add(createStep2(), "Paso2");
        cardsPanel.add(createStep3(), "Paso3");
        cardsPanel.add(createStep4(), "Paso4");

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        add(createButtonPanel(), BorderLayout.SOUTH);
        updateNavigationUI();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 25, 15, 25));

        JLabel titleLabel = new JLabel("Solicitud de Beca Académica");
        titleLabel.setFont(TITLE_FONT.deriveFont(24f));
        titleLabel.setForeground(SECONDARY_COLOR);

        progressLabel = new JLabel("Paso 1 de 4");
        progressLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        progressLabel.setForeground(PRIMARY_COLOR);

        progressIndicatorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        progressIndicatorPanel.setBackground(Color.WHITE);
        for (int i = 1; i <= totalSteps; i++) {
            progressIndicatorPanel.add(createCircleIndicator(i));
        }

        JPanel topInfo = new JPanel(new BorderLayout());
        topInfo.setOpaque(false);
        topInfo.add(titleLabel, BorderLayout.WEST);
        topInfo.add(progressLabel, BorderLayout.EAST);

        header.add(topInfo, BorderLayout.NORTH);
        header.add(progressIndicatorPanel, BorderLayout.CENTER);
        header.add(new JSeparator(), BorderLayout.SOUTH);

        return header;
    }

    private JComponent createCircleIndicator(int num) {
        JLabel label = new JLabel(String.valueOf(num), SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(30, 30));
        label.setOpaque(true);
        label.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        label.setBackground(new Color(229, 231, 235));
        label.setForeground(SECONDARY_COLOR);
        return label;
    }

    private JPanel createStep1() {
        JPanel p = createStepBasePanel("1. Información Personal");
        addFormField(p, "Nombre", "nombre", "", "text");
        addFormField(p, "Apellidos", "apellidos", "", "text");
        addFormField(p, "DNI / NIE (Formato: 12345678X)", "dni", "", "text");
        addFormField(p, "Correo Electrónico", "email", "", "text");
        addFormField(p, "Teléfono de Contacto (Solo números)", "telefono", "", "numeric");
        addFormField(p, "Fecha de Nacimiento (AAAA-MM-DD)", "fechaNac", "2000-01-01", "text");
        return p;
    }

    private JPanel createStep2() {
        JPanel p = createStepBasePanel("2. Trayectoria Académica");
        addFormField(p, "Centro de Estudios", "centro", "", "text");
        addFormField(p, "Grado / Titulación", "grado", "", "text");
        addFormField(p, "Curso Actual", "curso", "", "text");
        return p;
    }

    private JPanel createStep3() {
        JPanel p = createStepBasePanel("3. Situación Socioeconómica");
        addFormField(p, "Nombre Tutor/a Legal", "tutor", "", "text");
        addFormField(p, "Renta Anual Familiar (€)", "renta", "0.00", "decimal");
        addFormField(p, "Miembros de la Unidad Familiar", "miembros", "1", "numeric");
        return p;
    }

    private JPanel createStep4() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "4. Resumen de Solicitud",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, TITLE_FONT, PRIMARY_COLOR));

        JPanel grid = new JPanel(new GridLayout(0, 1, 5, 5));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] summaryFields = {"Nombre Completo", "DNI", "Titulación", "Renta Familiar"};
        String[] keys = {"resNombre", "resDni", "resGrado", "resRenta"};

        for (int i = 0; i < summaryFields.length; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            JLabel lbl = new JLabel(summaryFields[i] + ":");
            lbl.setFont(MAIN_FONT.deriveFont(Font.BOLD));
            JLabel val = new JLabel("---");
            val.setForeground(PRIMARY_COLOR.darker());
            summaryLabels.put(keys[i], val);
            row.add(lbl, BorderLayout.WEST);
            row.add(val, BorderLayout.EAST);
            grid.add(row);
        }

        p.add(grid, BorderLayout.NORTH);

        JTextArea notice = new JTextArea("Revise sus datos. Solo se aceptarán formularios con formatos válidos (Correo, DNI y campos numéricos).");
        notice.setWrapStyleWord(true);
        notice.setLineWrap(true);
        notice.setEditable(false);
        notice.setBackground(new Color(239, 246, 255));
        notice.setBorder(new EmptyBorder(10,10,10,10));
        p.add(notice, BorderLayout.CENTER);

        return p;
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        btnAtras = createStyledButton("Anterior", SECONDARY_COLOR);
        btnSiguiente = createStyledButton("Siguiente", PRIMARY_COLOR);
        btnConfirmar = createStyledButton("Enviar Solicitud", SUCCESS_COLOR);

        btnAtras.addActionListener(e -> navigate(-1));
        btnSiguiente.addActionListener(e -> navigate(1));
        btnConfirmar.addActionListener(this::handleFinalize);

        p.add(btnAtras);
        p.add(btnSiguiente);
        p.add(btnConfirmar);
        return p;
    }

    private JPanel createStepBasePanel(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, TITLE_FONT, SECONDARY_COLOR));
        return p;
    }

    private void addFormField(JPanel container, String label, String key, String defaultValue, String type) {
        FormField field = new FormField(label, defaultValue, type);
        formFields.put(key, field);
        container.add(field);
        container.add(Box.createVerticalStrut(15));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        return btn;
    }

    private void navigate(int delta) {
        if (delta > 0 && !validateCurrentStep()) return;
        currentStep += delta;
        updateNavigationUI();
        cardLayout.show(cardsPanel, "Paso" + currentStep);
    }

    /**
     * Valida los campos y sus formatos antes de avanzar.
     */
    private boolean validateCurrentStep() {
        boolean valid = true;

        // Patrones de Regex
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Pattern dniPattern = Pattern.compile("^[0-9]{8}[A-Z]$"); // DNI Español básico
        Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

        String[] fieldsToValidate = switch (currentStep) {
            case 1 -> new String[]{"nombre", "apellidos", "dni", "email", "telefono", "fechaNac"};
            case 2 -> new String[]{"centro", "grado"};
            case 3 -> new String[]{"renta", "miembros"};
            default -> new String[]{};
        };

        for (String key : fieldsToValidate) {
            FormField f = formFields.get(key);
            String text = f.getText();
            boolean fieldValid = true;

            // 1. Validar Vacío
            if (text.isEmpty()) {
                fieldValid = false;
            }
            // 2. Validar Formatos específicos
            else {
                if (key.equals("email") && !emailPattern.matcher(text).matches()) fieldValid = false;
                if (key.equals("dni") && !dniPattern.matcher(text).matches()) fieldValid = false;
                if (key.equals("fechaNac") && !datePattern.matcher(text).matches()) fieldValid = false;
            }

            f.markInvalid(!fieldValid);
            if (!fieldValid) valid = false;
        }

        if (!valid) {
            JOptionPane.showMessageDialog(this, "Por favor, revise los campos marcados en rojo. Asegúrese de que el formato sea correcto (ej: Correo o DNI).", "Error de Validación", JOptionPane.WARNING_MESSAGE);
        }

        return valid;
    }

    private void updateNavigationUI() {
        btnAtras.setVisible(currentStep > 1);
        btnSiguiente.setVisible(currentStep < totalSteps);
        btnConfirmar.setVisible(currentStep == totalSteps);
        progressLabel.setText("Paso " + currentStep + " de " + totalSteps);

        Component[] circles = progressIndicatorPanel.getComponents();
        for (int i = 0; i < circles.length; i++) {
            JLabel c = (JLabel) circles[i];
            int stepNum = i + 1;
            if (stepNum < currentStep) c.setBackground(SUCCESS_COLOR);
            else if (stepNum == currentStep) c.setBackground(PRIMARY_COLOR);
            else c.setBackground(new Color(229, 231, 235));
            c.setForeground(stepNum <= currentStep ? Color.WHITE : SECONDARY_COLOR);
        }

        if (currentStep == totalSteps) updateSummary();
    }

    private void updateSummary() {
        summaryLabels.get("resNombre").setText(formFields.get("nombre").getText() + " " + formFields.get("apellidos").getText());
        summaryLabels.get("resDni").setText(formFields.get("dni").getText());
        summaryLabels.get("resGrado").setText(formFields.get("grado").getText());
        summaryLabels.get("resRenta").setText(formFields.get("renta").getText() + " €");
    }

    private void handleFinalize(ActionEvent e) {
        try {
            BecaData data = collectData();
            saveToFile(data.toXML(), data.dni);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BecaData collectData() throws Exception {
        BecaData d = new BecaData();
        try {
            d.nombre = formFields.get("nombre").getText();
            d.apellidos = formFields.get("apellidos").getText();
            d.dni = formFields.get("dni").getText();
            d.email = formFields.get("email").getText();
            d.fechaNac = LocalDate.parse(formFields.get("fechaNac").getText());
            d.renta = Double.parseDouble(formFields.get("renta").getText());
            d.miembros = Integer.parseInt(formFields.get("miembros").getText());
            d.centro = formFields.get("centro").getText();
            d.grado = formFields.get("grado").getText();
        } catch (DateTimeParseException ex) {
            throw new Exception("Formato de fecha inválido (AAAA-MM-DD)");
        } catch (NumberFormatException ex) {
            throw new Exception("La renta y miembros deben ser valores numéricos válidos.");
        }
        return d;
    }

    private void saveToFile(String content, String id) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("beca_" + id + ".xml"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.write(content);
                JOptionPane.showMessageDialog(this, "¡Solicitud guardada con éxito!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al escribir archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- CLASES INTERNAS ---

    private static class FormField extends JPanel {
        private final JTextField textField;
        private final JLabel label;

        public FormField(String labelText, String defaultValue, String type) {
            setLayout(new BorderLayout(5, 5));
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            label = new JLabel(labelText);
            label.setFont(MAIN_FONT.deriveFont(Font.BOLD));
            label.setForeground(SECONDARY_COLOR);

            textField = new JTextField(defaultValue);
            textField.setFont(MAIN_FONT);
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(209, 213, 219)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            // Aplicar filtros según el tipo
            if (type.equals("numeric")) {
                ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericFilter(false));
            } else if (type.equals("decimal")) {
                ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericFilter(true));
            }

            add(label, BorderLayout.NORTH);
            add(textField, BorderLayout.CENTER);
        }

        public String getText() { return textField.getText().trim(); }

        public void markInvalid(boolean invalid) {
            Color color = invalid ? ERROR_COLOR : new Color(209, 213, 219);
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, invalid ? 2 : 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }
    }

    /**
     * Filtro para restringir la entrada a solo números (y opcionalmente un punto decimal).
     */
    private static class NumericFilter extends DocumentFilter {
        private final boolean allowDecimal;

        public NumericFilter(boolean allowDecimal) {
            this.allowDecimal = allowDecimal;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (isValid(string, fb.getDocument().getText(0, fb.getDocument().getLength()), offset)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (isValid(text, fb.getDocument().getText(0, fb.getDocument().getLength()), offset)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValid(String text, String currentContent, int offset) {
            if (text == null || text.isEmpty()) return true;

            // Regex para solo números (y opcionalmente un solo punto decimal)
            String regex = allowDecimal ? "^[0-9.]+$" : "^[0-9]+$";
            if (!text.matches(regex)) return false;

            // Si es decimal, evitar múltiples puntos
            if (allowDecimal && text.contains(".") && currentContent.contains(".")) {
                return false;
            }
            return true;
        }
    }

    private static class BecaData {
        String nombre, apellidos, dni, email, centro, grado;
        LocalDate fechaNac;
        double renta;
        int miembros;

        public String toXML() {
            return String.format(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<Solicitud>\n" +
                            "  <Personal>\n" +
                            "    <Nombre>%s %s</Nombre>\n" +
                            "    <DNI>%s</DNI>\n" +
                            "    <Email>%s</Email>\n" +
                            "    <Nacimiento>%s</Nacimiento>\n" +
                            "  </Personal>\n" +
                            "  <Academico>\n" +
                            "    <Centro>%s</Centro>\n" +
                            "    <Grado>%s</Grado>\n" +
                            "  </Academico>\n" +
                            "  <Economico>\n" +
                            "    <Renta>%.2f</Renta>\n" +
                            "    <Miembros>%d</Miembros>\n" +
                            "  </Economico>\n" +
                            "</Solicitud>",
                    nombre, apellidos, dni, email, fechaNac, centro, grado, renta, miembros
            );
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) { }
        SwingUtilities.invokeLater(() -> new BecaFormularioSwing().setVisible(true));
    }
}