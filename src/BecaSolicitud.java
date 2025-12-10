import java.util.Objects;
import java.time.LocalDate;

/**
 * Clase que representa el modelo de datos para la Solicitud de Beca.
 * Contiene la lógica para almacenar la información de los 4 pasos
 * y generar el XML final.
 */
public class BecaSolicitud {

    // --- 1. DATOS PERSONALES ---
    private String nombre;
    private String apellidos;
    private String dni;
    private String direccion;
    private String correoElectronico;
    private String telefono;
    private LocalDate fechaNacimiento;

    // --- 2. DATOS ACADÉMICOS ---
    private String centroEstudios;
    private String titulacion;
    private String anio; // Se mantiene como String para aceptar "3er Curso", "2024", etc.

    // --- 3. DATOS FINANCIEROS ---
    private String nombrePadreMadre;
    private String dniPadreMadre;
    private double rentaTotalUnidadFamiliar;
    private int numeroComponentes;

    /**
     * Constructor vacío. Se asume que los datos serán establecidos
     * a través de los métodos setters (simulando la captura del formulario).
     */
    public BecaSolicitud() {
    }

    // --- MÉTODOS GETTERS Y SETTERS ---

    // Setters (omitiendo getters por brevedad, pero serían necesarios en una aplicación real)

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setDni(String dni) { this.dni = dni; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public void setCentroEstudios(String centroEstudios) { this.centroEstudios = centroEstudios; }
    public void setTitulacion(String titulacion) { this.titulacion = titulacion; }
    public void setAnio(String anio) { this.anio = anio; }

    public void setNombrePadreMadre(String nombrePadreMadre) { this.nombrePadreMadre = nombrePadreMadre; }
    public void setDniPadreMadre(String dniPadreMadre) { this.dniPadreMadre = dniPadreMadre; }
    public void setRentaTotalUnidadFamiliar(double rentaTotalUnidadFamiliar) { this.rentaTotalUnidadFamiliar = rentaTotalUnidadFamiliar; }
    public void setNumeroComponentes(int numeroComponentes) { this.numeroComponentes = numeroComponentes; }


    /**
     * Genera la cadena de texto con formato XML a partir de los datos de la solicitud.
     * Utiliza StringBuilder para construir el XML de forma eficiente.
     * @return String que contiene la estructura XML completa.
     */
    public String generateXML() {
        StringBuilder xmlBuilder = new StringBuilder();

        // Encabezado XML
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<SolicitudBeca>\n");

        // 1. Datos Personales
        xmlBuilder.append("    <DatosPersonales>\n");
        xmlBuilder.append("        <Nombre>").append(this.nombre).append("</Nombre>\n");
        xmlBuilder.append("        <Apellidos>").append(this.apellidos).append("</Apellidos>\n");
        xmlBuilder.append("        <DNI>").append(this.dni).append("</DNI>\n");
        xmlBuilder.append("        <Direccion>").append(this.direccion).append("</Direccion>\n");
        xmlBuilder.append("        <CorreoElectronico>").append(this.correoElectronico).append("</CorreoElectronico>\n");
        xmlBuilder.append("        <Telefono>").append(this.telefono).append("</Telefono>\n");
        xmlBuilder.append("        <FechaNacimiento>").append(this.fechaNacimiento != null ? this.fechaNacimiento.toString() : "").append("</FechaNacimiento>\n");
        xmlBuilder.append("    </DatosPersonales>\n");

        // 2. Datos Académicos
        xmlBuilder.append("    <DatosAcademicos>\n");
        xmlBuilder.append("        <CentroEstudios>").append(this.centroEstudios).append("</CentroEstudios>\n");
        xmlBuilder.append("        <Titulacion>").append(this.titulacion).append("</Titulacion>\n");
        xmlBuilder.append("        <Anio>").append(this.anio).append("</Anio>\n");
        xmlBuilder.append("    </DatosAcademicos>\n");

        // 3. Datos Financieros
        xmlBuilder.append("    <DatosFinancieros>\n");
        xmlBuilder.append("        <NombrePadreMadre>").append(this.nombrePadreMadre).append("</NombrePadreMadre>\n");
        xmlBuilder.append("        <DNIPadreMadre>").append(this.dniPadreMadre).append("</DNIPadreMadre>\n");
        // Formateo de la renta como cadena de texto para el XML
        xmlBuilder.append("        <RentaTotalUnidadFamiliar>").append(String.format("%.2f", this.rentaTotalUnidadFamiliar)).append("</RentaTotalUnidadFamiliar>\n");
        xmlBuilder.append("        <NumeroComponentes>").append(this.numeroComponentes).append("</NumeroComponentes>\n");
        xmlBuilder.append("    </DatosFinancieros>\n");

        // Cierre del tag principal
        xmlBuilder.append("</SolicitudBeca>");

        return xmlBuilder.toString();
    }

    /**
     * Método principal para demostrar la funcionalidad de la clase.
     * Simula la entrada de datos y la generación del XML.
     */
    public static void main(String[] args) {
        System.out.println("--- DEMOSTRACIÓN DE GENERACIÓN DE XML EN JAVA ---\n");

        // 1. Crear una nueva solicitud
        BecaSolicitud solicitud = new BecaSolicitud();

        // 2. Establecer los datos (simulando la entrada del usuario en los 3 pasos)

        // Paso 1: Datos Personales
        solicitud.setNombre("Alba");
        solicitud.setApellidos("García Ruiz");
        solicitud.setDni("12345678A");
        solicitud.setDireccion("C/ Inventada, 45, Madrid");
        solicitud.setCorreoElectronico("alba.garcia@email.com");
        solicitud.setTelefono("600112233");
        solicitud.setFechaNacimiento(LocalDate.of(2003, 5, 20)); // Usando LocalDate para la fecha

        // Paso 2: Datos Académicos
        solicitud.setCentroEstudios("Universidad de Tecnología Avanzada");
        solicitud.setTitulacion("Grado en Ingeniería Informática");
        solicitud.setAnio("2º Curso");

        // Paso 3: Datos Financieros
        solicitud.setNombrePadreMadre("Juan García");
        solicitud.setDniPadreMadre("87654321B");
        solicitud.setRentaTotalUnidadFamiliar(35450.75);
        solicitud.setNumeroComponentes(4);

        // 3. Generar y mostrar el XML
        String xmlResult = solicitud.generateXML();

        System.out.println("El XML generado es el siguiente:\n");
        System.out.println(xmlResult);
        System.out.println("\n------------------------------------------------------");
    }
}
