import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * ESCENARIO 02 - PATRÓN BUILDER
 * Registro de activos fijos con datos obligatorios y opcionales.
 *
 * Roles del patrón:
 *   Product  -> ActivoFijo         (el objeto final, ya configurado)
 *   Builder  -> ActivoFijoBuilder  (el "borrador" que acumula datos paso a paso)
 *   Client   -> ModuloActivos      (el módulo que registra los activos)
 */

// =====================================================================
// PRODUCT: el activo fijo final
// =====================================================================
class ActivoFijo {
    // Obligatorios
    private final String codigo;
    private final String nombre;
    private final double precio;

    // Opcionales (pueden quedar sin valor si no aplican)
    private final String marca;
    private final String modelo;
    private final String numeroSerie;
    private final String ubicacion;
    private final String custodio;
    private final LocalDate fechaAdquisicion;
    private final Integer garantiaMeses;
    private final String proveedor;
    private final Map<String, String> caracteristicas;
    private final String observaciones;

    // Constructor que recibe el builder. No es público: solo el builder lo usa.
    // (En Java, para hacerlo totalmente privado el builder tendría que ser
    //  una clase anidada dentro de ActivoFijo.)
    ActivoFijo(ActivoFijoBuilder b) {
        this.codigo = b.codigo;
        this.nombre = b.nombre;
        this.precio = b.precio;
        this.marca = b.marca;
        this.modelo = b.modelo;
        this.numeroSerie = b.numeroSerie;
        this.ubicacion = b.ubicacion;
        this.custodio = b.custodio;
        this.fechaAdquisicion = b.fechaAdquisicion;
        this.garantiaMeses = b.garantiaMeses;
        this.proveedor = b.proveedor;
        this.caracteristicas = Collections.unmodifiableMap(new LinkedHashMap<>(b.caracteristicas));
        this.observaciones = b.observaciones;
    }

    // Getters: el activo solo se puede leer, no modificar (es inmutable).
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }

    // Muestra solo los datos que el activo realmente tiene.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] %s - $%.2f%n", codigo, nombre, precio));
        agregar(sb, "Marca", marca);
        agregar(sb, "Modelo", modelo);
        agregar(sb, "N° serie", numeroSerie);
        agregar(sb, "Ubicación", ubicacion);
        agregar(sb, "Custodio", custodio);
        agregar(sb, "Fecha adquisición", fechaAdquisicion);
        agregar(sb, "Garantía (meses)", garantiaMeses);
        agregar(sb, "Proveedor", proveedor);
        caracteristicas.forEach((k, v) -> agregar(sb, k, v));
        agregar(sb, "Observaciones", observaciones);
        return sb.toString();
    }

    private void agregar(StringBuilder sb, String etiqueta, Object valor) {
        if (valor != null) {
            sb.append("   ").append(etiqueta).append(": ").append(valor).append("\n");
        }
    }
}

// =====================================================================
// BUILDER: construye el activo paso a paso
// =====================================================================
class ActivoFijoBuilder {
    // Guarda temporalmente los datos mientras se van agregando
    String codigo;
    String nombre;
    double precio;
    String marca;
    String modelo;
    String numeroSerie;
    String ubicacion;
    String custodio;
    LocalDate fechaAdquisicion;
    Integer garantiaMeses;
    String proveedor;
    Map<String, String> caracteristicas = new LinkedHashMap<>();
    String observaciones;

    // CONSTRUCTOR: recibe los 3 datos OBLIGATORIOS.
    // Así es imposible empezar a construir un activo sin ellos.
    public ActivoFijoBuilder(String codigo, String nombre, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

    // MÉTODOS OPCIONALES: cada uno agrega un dato y devuelve "this"
    // (el mismo builder), lo que permite encadenarlos: .marca().modelo()...
    // El nombre del método deja explícito qué dato se está incorporando.

    public ActivoFijoBuilder marca(String marca) {
        this.marca = marca;
        return this;
    }

    public ActivoFijoBuilder modelo(String modelo) {
        this.modelo = modelo;
        return this;
    }

    public ActivoFijoBuilder numeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
        return this;
    }

    public ActivoFijoBuilder ubicacion(String ubicacion) {
        this.ubicacion = ubicacion;
        return this;
    }

    public ActivoFijoBuilder custodio(String custodio) {
        this.custodio = custodio;
        return this;
    }

    public ActivoFijoBuilder fechaAdquisicion(LocalDate fecha) {
        this.fechaAdquisicion = fecha;
        return this;
    }

    public ActivoFijoBuilder garantiaMeses(int meses) {
        this.garantiaMeses = meses;
        return this;
    }

    public ActivoFijoBuilder proveedor(String proveedor) {
        this.proveedor = proveedor;
        return this;
    }

    // Características técnicas propias de cada tipo de activo
    // (procesador en un computador, material o dimensiones en una mesa...).
    public ActivoFijoBuilder caracteristica(String nombre, String valor) {
        this.caracteristicas.put(nombre, valor);
        return this;
    }

    public ActivoFijoBuilder observaciones(String observaciones) {
        this.observaciones = observaciones;
        return this;
    }

    // BUILD: el paso final. Valida que todo esté correcto y crea el ActivoFijo.
    // Si algo está mal, no se crea el activo.
    public ActivoFijo build() {
        if (codigo == null || codigo.isBlank())
            throw new IllegalStateException("El código institucional es obligatorio");
        if (nombre == null || nombre.isBlank())
            throw new IllegalStateException("El nombre o descripción es obligatorio");
        if (precio <= 0)
            throw new IllegalStateException("El precio de adquisición debe ser mayor a 0");
        if (garantiaMeses != null && fechaAdquisicion == null)
            throw new IllegalStateException("Si hay garantía, debe registrarse la fecha de adquisición");

        return new ActivoFijo(this);
    }
}

// =====================================================================
// CLIENT: el módulo que registra activos
// =====================================================================
class ModuloActivos {
    public void registrar(ActivoFijo activo) {
        System.out.println("Activo registrado:");
        System.out.println(activo);
    }
}

// =====================================================================
// PRUEBA
// =====================================================================
public class Escenario02_Builder {
    public static void main(String[] args) {
        ModuloActivos modulo = new ModuloActivos();

        // Computador: muchos datos opcionales
        ActivoFijo computador = new ActivoFijoBuilder("AF001", "Computador", 1200)
                .marca("Dell")
                .modelo("Latitude")
                .numeroSerie("SN-88231")
                .caracteristica("Procesador", "Intel i7")
                .fechaAdquisicion(LocalDate.of(2026, 3, 15))
                .garantiaMeses(36)
                .build();
        modulo.registrar(computador);

        // Mesa: solo los datos que le corresponden, sin null ni valores vacíos
        ActivoFijo mesa = new ActivoFijoBuilder("AF002", "Mesa", 120)
                .caracteristica("Material", "Madera")
                .caracteristica("Dimensiones", "120x60x75 cm")
                .ubicacion("Cuenca - Bodega 2")
                .custodio("Juan Pérez")
                .build();
        modulo.registrar(mesa);

        // Validación del build(): garantía sin fecha de adquisición
        try {
            new ActivoFijoBuilder("AF003", "Impresora", 300)
                    .marca("HP")
                    .garantiaMeses(12)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("No se registró AF003: " + e.getMessage());
        }
    }
}
