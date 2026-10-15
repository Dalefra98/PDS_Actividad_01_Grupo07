import java.time.Year;
import java.util.HashMap;
import java.util.Map;

/*
 * ESCENARIO 01 - PATRÓN FACTORY METHOD
 * Registro de vehículos y cálculo del costo de matrícula.
 *
 * Roles del patrón:
 *   Product           -> Vehiculo
 *   Concrete Product  -> Auto, Camioneta, Camion
 *   Creator           -> CreadorVehiculo
 *   Concrete Creator  -> CreadorAuto, CreadorCamioneta, CreadorCamion
 *   Client            -> Aplicacion (la "lógica principal" del enunciado)
 *
 * Las fórmulas de cálculo son ilustrativas: lo importante es que cada
 * tipo de vehículo tenga su propia lógica bajo un contrato común.
 */

// =====================================================================
// DATOS QUE PROPORCIONA EL CLIENTE (la persona que matricula su vehículo)
// =====================================================================
class DatosVehiculo {
    String placa;
    String marca;
    String modelo;
    int anioFabricacion;
    double avaluo;

    // Datos adicionales que solo usan algunos tipos
    double capacidad;       // Camioneta (kg)
    String uso;             // Camioneta ("particular" / "comercial")
    double tonelaje;        // Camion
    double capacidadCarga;  // Camion

    DatosVehiculo(String placa, String marca, String modelo, int anioFabricacion, double avaluo) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anioFabricacion = anioFabricacion;
        this.avaluo = avaluo;
    }
}

// =====================================================================
// PRODUCT: contrato común de todos los vehículos
// =====================================================================
abstract class Vehiculo {
    protected String placa;
    protected String marca;
    protected String modelo;
    protected int anioFabricacion;
    protected double avaluo;

    // Constructor: copia la información general que comparten todos los vehículos.
    Vehiculo(DatosVehiculo d) {
        this.placa = d.placa;
        this.marca = d.marca;
        this.modelo = d.modelo;
        this.anioFabricacion = d.anioFabricacion;
        this.avaluo = d.avaluo;
    }

    // Método de apoyo: años transcurridos desde la fabricación.
    protected int antiguedad() {
        return Year.now().getValue() - anioFabricacion;
    }

    // Operación del contrato común. Es abstracta: cada tipo DEBE implementarla
    // con su propia lógica. La Aplicacion solo conoce este método.
    public abstract double obtenerCostoMatricula();

    public String getPlaca() {
        return placa;
    }
}

// =====================================================================
// CONCRETE PRODUCTS: cada uno calcula la matrícula a su manera
// =====================================================================
class Auto extends Vehiculo {

    Auto(DatosVehiculo d) {
        super(d);
    }

    // Auto: considera principalmente avalúo y antigüedad.
    // 1.5% del avalúo, con 2% de descuento por año (máximo 50%).
    @Override
    public double obtenerCostoMatricula() {
        double base = avaluo * 0.015;
        double descuento = Math.min(antiguedad() * 0.02, 0.5);
        return base * (1 - descuento);
    }
}

class Camioneta extends Vehiculo {
    private double capacidad;
    private String uso;

    Camioneta(DatosVehiculo d) {
        super(d);
        this.capacidad = d.capacidad;
        this.uso = d.uso;
    }

    // Camioneta: avalúo + capacidad, con recargo del 20% si es de uso comercial.
    @Override
    public double obtenerCostoMatricula() {
        double base = avaluo * 0.018 + capacidad * 0.05;
        return "comercial".equalsIgnoreCase(uso) ? base * 1.2 : base;
    }
}

class Camion extends Vehiculo {
    private double tonelaje;
    private double capacidadCarga;

    Camion(DatosVehiculo d) {
        super(d);
        this.tonelaje = d.tonelaje;
        this.capacidadCarga = d.capacidadCarga;
    }

    // Camion: avalúo + valor fijo por cada tonelada.
    @Override
    public double obtenerCostoMatricula() {
        return avaluo * 0.01 + tonelaje * 25;
    }
}

// =====================================================================
// CREATOR: declara el factory method
// =====================================================================
abstract class CreadorVehiculo {

    // FACTORY METHOD: cada creador concreto decide qué vehículo instanciar.
    // Devuelve el tipo abstracto Vehiculo, nunca el concreto.
    public abstract Vehiculo crearVehiculo(DatosVehiculo datos);
}

// =====================================================================
// CONCRETE CREATORS: cada uno crea un único tipo de vehículo
// =====================================================================
class CreadorAuto extends CreadorVehiculo {
    @Override
    public Vehiculo crearVehiculo(DatosVehiculo datos) {
        return new Auto(datos);          // único lugar donde se hace new Auto
    }
}

class CreadorCamioneta extends CreadorVehiculo {
    @Override
    public Vehiculo crearVehiculo(DatosVehiculo datos) {
        return new Camioneta(datos);     // único lugar donde se hace new Camioneta
    }
}

class CreadorCamion extends CreadorVehiculo {
    @Override
    public Vehiculo crearVehiculo(DatosVehiculo datos) {
        return new Camion(datos);        // único lugar donde se hace new Camion
    }
}

// =====================================================================
// CLIENT: la lógica principal. Solo conoce CreadorVehiculo y Vehiculo.
// =====================================================================
class Aplicacion {

    // Recibe el creador ya seleccionado y los datos del cliente.
    // 1) delega la creación al creador
    // 2) trabaja con el vehículo como Vehiculo (sin saber si es Auto, Camioneta o Camion)
    public double registrarVehiculo(CreadorVehiculo creador, DatosVehiculo datos) {
        Vehiculo vehiculo = creador.crearVehiculo(datos);
        double costo = vehiculo.obtenerCostoMatricula();
        System.out.printf("Vehículo %s registrado. Matrícula: $%.2f%n", vehiculo.getPlaca(), costo);
        return costo;
    }
}

// =====================================================================
// PRUEBA
// =====================================================================
public class Escenario01_FactoryMethod {
    public static void main(String[] args) {

        // Selección en ejecución: asocia el tipo que indica el cliente con su creador.
        // Para agregar Motocicleta: crear Motocicleta + CreadorMotocicleta y añadir UNA línea aquí.
        Map<String, CreadorVehiculo> creadores = new HashMap<>();
        creadores.put("auto", new CreadorAuto());
        creadores.put("camioneta", new CreadorCamioneta());
        creadores.put("camion", new CreadorCamion());

        Aplicacion app = new Aplicacion();

        // Cliente 1: Auto
        DatosVehiculo auto = new DatosVehiculo("PBA-1234", "Kia", "Rio", 2020, 15000);
        app.registrarVehiculo(creadores.get("auto"), auto);

        // Cliente 2: Camioneta de uso comercial
        DatosVehiculo camioneta = new DatosVehiculo("GBC-5678", "Toyota", "Hilux", 2022, 35000);
        camioneta.capacidad = 1000;
        camioneta.uso = "comercial";
        app.registrarVehiculo(creadores.get("camioneta"), camioneta);

        // Cliente 3: Camion
        DatosVehiculo camion = new DatosVehiculo("TCA-9012", "Hino", "500", 2018, 60000);
        camion.tonelaje = 12;
        camion.capacidadCarga = 8000;
        app.registrarVehiculo(creadores.get("camion"), camion);
    }
}
