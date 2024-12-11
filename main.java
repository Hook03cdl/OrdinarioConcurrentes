import java.util.*;

class Vuelo {
    String id;
    String origen;
    String destino;
    int capacidadTotal;
    int asientosDisponibles;

    public Vuelo(String id, String origen, String destino, int capacidadTotal) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.capacidadTotal = capacidadTotal;
        this.asientosDisponibles = capacidadTotal;
    }
}

class Pasajero {
    String id;
    String nombre;
    int asientosSolicitados;

    public Pasajero(String id, String nombre, int asientosSolicitados) {
        this.id = id;
        this.nombre = nombre;
        this.asientosSolicitados = asientosSolicitados;
    }
}

class SistemaReservas {
    List<Vuelo> vuelos = new ArrayList<>();
    List<Pasajero> pasajeros = new ArrayList<>();
    Map<String, Map<String, Integer>> matrizAsignacion = new HashMap<>();

    public void agregarVuelo(Vuelo vuelo) {
        vuelos.add(vuelo);
        matrizAsignacion.put(vuelo.id, new HashMap<>());
    }

    public void agregarPasajero(Pasajero pasajero) {
        pasajeros.add(pasajero);
    }

    public boolean solicitarAsientos(String pasajeroId, String vueloId, int asientosSolicitados) {
        Vuelo vuelo = buscarVuelo(vueloId);
        if (vuelo != null && vuelo.asientosDisponibles >= asientosSolicitados) {
            vuelo.asientosDisponibles -= asientosSolicitados;
            matrizAsignacion.get(vueloId).put(pasajeroId, asientosSolicitados);
            return true;
        } else {
            System.out.println("No hay suficientes asientos disponibles. Pasajero en lista de espera.");
            return false;
        }
    }

    public void liberarAsientos(String pasajeroId, String vueloId) {
        Vuelo vuelo = buscarVuelo(vueloId);
        if (vuelo != null && matrizAsignacion.get(vueloId).containsKey(pasajeroId)) {
            int asientosLiberados = matrizAsignacion.get(vueloId).remove(pasajeroId);
            vuelo.asientosDisponibles += asientosLiberados;
        }
    }

    public boolean estadoSeguro() {
        Map<String, Integer> work = new HashMap<>();
        Map<String, Boolean> finish = new HashMap<>();

        for (Vuelo vuelo : vuelos) {
            work.put(vuelo.id, vuelo.asientosDisponibles);
            finish.put(vuelo.id, false);
        }

        boolean found;
        do {
            found = false;
            for (Vuelo vuelo : vuelos) {
                if (!finish.get(vuelo.id)) {
                    boolean puedeAsignar = true;
                    for (Pasajero pasajero : pasajeros) {
                        int asientosSolicitados = matrizAsignacion.get(vuelo.id).getOrDefault(pasajero.id, 0);
                        if (asientosSolicitados > work.get(vuelo.id)) {
                            puedeAsignar = false;
                            break;
                        }
                    }
                    if (puedeAsignar) {
                        for (Pasajero pasajero : pasajeros) {
                            int asientosSolicitados = matrizAsignacion.get(vuelo.id).getOrDefault(pasajero.id, 0);
                            work.put(vuelo.id, work.get(vuelo.id) + asientosSolicitados);
                        }
                        finish.put(vuelo.id, true);
                        found = true;
                    }
                }
            }
        } while (found);

        for (boolean f : finish.values()) {
            if (!f) {
                return false;
            }
        }
        return true;
    }

    private Vuelo buscarVuelo(String vueloId) {
        for (Vuelo vuelo : vuelos) {
            if (vuelo.id.equals(vueloId)) {
                return vuelo;
            }
        }
        return null;
    }
}

public class Main {
    public static void main(String[] args) {
        SistemaReservas sistema = new SistemaReservas();

        Vuelo vuelo1 = new Vuelo("V001", "Ciudad A", "Ciudad B", 100);
        Vuelo vuelo2 = new Vuelo("V002", "Ciudad C", "Ciudad D", 150);

        sistema.agregarVuelo(vuelo1);
        sistema.agregarVuelo(vuelo2);

        Pasajero pasajero1 = new Pasajero("P001", "Juan Perez", 2);
        Pasajero pasajero2 = new Pasajero("P002", "Maria Lopez", 3);

        sistema.agregarPasajero(pasajero1);
        sistema.agregarPasajero(pasajero2);

        sistema.solicitarAsientos("P001", "V001", 2);
        sistema.solicitarAsientos("P002", "V002", 3);

        sistema.liberarAsientos("P001", "V001");

        System.out