package prioridadExterna;

import archivos.ArchivoSalida;
import proceso.Proceso;

import java.awt.image.PackedColorModel;
import java.util.*;

public class PrioridadExterna {
    private List<Proceso> procesos;
    private int TIP;
    private int TCP;
    private int TFP;
    private List<Proceso> colaListos;
    private List<Proceso> colaBloqueados;
    private Queue<Proceso> colaFinalizados;
    private int tiempoActual;
    public String resultadoArchivo;
    private ArchivoSalida archivoSalida;
    private Map<Proceso, Integer> tiempoDesbloqueoProcesos = new HashMap<>();

    public PrioridadExterna(List<Integer> listaDatos, List<Proceso> procesos, String rutaArchivo) {
        this.setProcesos(procesos);
        this.setTIP(listaDatos.get(1));
        this.setTCP(listaDatos.get(2));
        this.setTFP(listaDatos.get(3));

        this.colaListos = new ArrayList<>();
        this.colaBloqueados = new LinkedList<>();
        this.colaFinalizados = new LinkedList<>();
        this.tiempoActual = 0;

        System.out.println("Comienza la simulacion del planificador aplicando Prioridad Externa");
        System.out.println("\nTiempo: " + this.tiempoActual);

        this.resultadoArchivo = "";
        this.archivoSalida = new ArchivoSalida(rutaArchivo);


    }

    private void mostrarColaListos() {
        String colaListos = "Cola de procesos listos: [";
        for (Proceso proceso : this.colaListos) {
            colaListos = colaListos + proceso.getNumeroProceso() + ", ";
        }
        System.out.println(colaListos + "]");
    }

    // Nuevos métodos para manejar la lista ordenada
    private void ordenarColaListos() {
        colaListos.sort((p1, p2) -> {
            int prioridad = Integer.compare(p2.getPrioridad(), p1.getPrioridad());
            if (prioridad != 0) return prioridad;
            int comparacionTiempoArribo = Integer.compare(p1.getTiempoArribo(), p2.getTiempoArribo());
            if (comparacionTiempoArribo != 0) return comparacionTiempoArribo;
            return Integer.compare(p1.getNumeroProceso(), p2.getNumeroProceso());
        });
    }

    private Proceso obtenerProcesoMayorPrioridad() {
        return colaListos.isEmpty() ? null : colaListos.removeFirst();
    }

    private void actualizaColaListos() {
        System.out.println("Se actualiza la cola de listos");
        for (Proceso proceso : this.procesos) {
            if (proceso.getTiempoArribo() == this.tiempoActual &&
                    !this.colaListos.contains(proceso) &&
                    !this.colaBloqueados.contains(proceso) &&
                    !this.colaFinalizados.contains(proceso)) {
                this.colaListos.add(proceso);
                //insertarProceso(proceso);
                System.out.println("Llega el proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta el TIP para el proceso P" + proceso.getNumeroProceso());
                for (int i = 0; i < this.TIP; i++) {
                    tiempoActual++;
                    System.out.println("\nTiempo: " + this.tiempoActual);
                    actualizaColaListos();
                    actualizaColaBloqueados();
                }
                proceso.setEjecutoTIP(true);
                System.out.println("El proceso P" + proceso.getNumeroProceso() + " está en estado de running");
            }
        }
    }

    private void actualizaColaBloqueados() {
        System.out.println("Se actualiza la cola de bloqueados");
        List<Proceso> procesosADesbloquear = new ArrayList<>();
        for (Proceso proceso : this.colaBloqueados) {
            proceso.actualizarBloqueos();
            if(proceso.getSubBloqueosEjecutados() == proceso.getDuracionBloqueo()) {
                System.out.println("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecutó el bloqueo número: " + proceso.getBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                proceso.setSubBloqueosEjecutados(0);

                // Set the time when the process should be unblocked
                tiempoDesbloqueoProcesos.put(proceso, tiempoActual + 1); // Add 2 time units delay
                procesosADesbloquear.add(proceso);
            } else {
                System.out.println("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
            }
        }
        for (Proceso proceso : procesosADesbloquear) {
            this.colaBloqueados.remove(proceso);
            // We don't add the process to the ready queue here anymore
            System.out.println("El proceso P" + proceso.getNumeroProceso() + " ha terminado su bloqueo y será añadido a la cola de listos en el tiempo " + tiempoDesbloqueoProcesos.get(proceso));
        }

        // Check if any processes are ready to be unblocked
        List<Proceso> procesosListosParaDesbloquear = new ArrayList<>();
        for (Map.Entry<Proceso, Integer> entry : tiempoDesbloqueoProcesos.entrySet()) {
            if (tiempoActual >= entry.getValue()) {
                procesosListosParaDesbloquear.add(entry.getKey());
            }
        }

        // Add unblocked processes to the ready queue
        for (Proceso proceso : procesosListosParaDesbloquear) {
            this.colaListos.add(proceso);
            tiempoDesbloqueoProcesos.remove(proceso);
            System.out.println("Se agregó el proceso P" + proceso.getNumeroProceso() + " a la cola de listos en el tiempo " + tiempoActual);
        }
    }

    public void ejecutar() {
        int cantProcesos = procesos.size();

        //Si hay procesos nuevos, este metodo ejecuta sus TIPS
        actualizaColaListos();

        //Almacena cual fue el ultimo proceso en ejecutarse, al principio es nulo
        Proceso ultimoProcesoEjecutado = null;
        mostrarColaListos();
        while (this.getColaFinalizados().size() < cantProcesos ) {
            if (this.colaListos.isEmpty()) { //SI NO hay proceso:
                System.out.println("la CPU esta inactiva");
                this.tiempoActual++; //Avanzo en el tiempo
                System.out.println("\nTiempo: " + this.tiempoActual);
                actualizaColaListos(); //Actualizo la cola de listos
                mostrarColaListos();
                actualizaColaBloqueados(); // Actualizo la cola de bloqueados
                ordenarColaListos();
                // aca
            } else { // Sí HAY procesos:
                mostrarColaListos();
                ordenarColaListos();
                Proceso procesoActual = obtenerProcesoMayorPrioridad();
                System.out.println("Se saco proceso P" + procesoActual.getNumeroProceso());
                mostrarColaListos();
                // Pregunto si hay interrupciones
                //Si se cumple que el último proceso ejecutado no es nulo, que es distinto del proceso actual y que
                // no termino de ejecutar su rafaga, entonces surge una interrupcion.

                if(ultimoProcesoEjecutado != null && procesoActual != ultimoProcesoEjecutado
                        && ultimoProcesoEjecutado.getSubRafagasEjecutadas() != 0 &&
                       !(procesoActual.getRafagasEjecutadas() == 0 && procesoActual.getSubRafagasEjecutadas() ==0) ){
                    System.out.println("Surgio una interrupcion");
                    System.out.println("Se ejecuta el TCP para el ultimo proceso ejecutado P" + ultimoProcesoEjecutado.getNumeroProceso());
                    System.out.println("Se ejecuta el TCP para el proceso P" + ultimoProcesoEjecutado.getNumeroProceso());
                    for (int i = 0; i < this.TCP; i++) {
                        tiempoActual++;
                        System.out.println("\nTiempo: " + this.tiempoActual);
                        actualizaColaListos();
                        actualizaColaBloqueados();
                    }
                    System.out.println("El proceso P" + ultimoProcesoEjecutado.getNumeroProceso() + " está en estado de bloqueado");
                    System.out.println("Se ejecuta la rafaga del proceso actual P" + procesoActual.getNumeroProceso());
                    ultimoProcesoEjecutado = procesoActual;
                    this.colaListos.add(ultimoProcesoEjecutado);
                   // insertarProceso(procesoActual);
                    mostrarColaListos();
                    //ordenarColaListos();
                    // aca

                } else { // No Hay interrupción
                    if (!procesoActual.getTerminoRafaga()){ // Si no termino de ejecutar una rafaga o si todavia no empezo a hacerlo
                        System.out.println("Se ejecuta la rafaga del P" + procesoActual.getNumeroProceso());
                        procesoActual.actualizarRafaga();
                        if(procesoActual.getSubRafagasEjecutadas() == procesoActual.getDuracionRafaga()){ //Si ejecuto una rafaga completa
                            System.out.println("Se ejecuta la sub ráfaga " + procesoActual.getSubRafagasEjecutadas() + " del proceso P" + procesoActual.getNumeroProceso());
                            System.out.println("Se ejecuto la ráfaga número: " + procesoActual.getRafagasEjecutadas() + " del proceso P" + procesoActual.getNumeroProceso());
                            procesoActual.setSubRafagasEjecutadas(0);
                            procesoActual.setTerminoRafaga(true);

                        } else { //No termino de ejecutar la rafaga
                            System.out.println("Se ejecuta la sub ráfaga " + procesoActual.getSubRafagasEjecutadas() + " del proceso P" + procesoActual.getNumeroProceso());
                        }

                        this.tiempoActual++;
                        System.out.println("\nTiempo: " + this.tiempoActual);
                        actualizaColaListos();
                        actualizaColaBloqueados();
                        mostrarColaListos();
                    }

                    if(procesoActual.getRafagasEjecutadas() == procesoActual.getCantRafagas()){ //Si ejecuto todas sus rafagas
                        System.out.println("Se ejecuta el TFP para el proceso P" + procesoActual.getNumeroProceso());
                        for (int i = 0; i < this.TFP; i++) {
                            tiempoActual++;
                            System.out.println("\nTiempo: " + this.tiempoActual);
                            actualizaColaListos();
                            actualizaColaBloqueados();
                        }
                        System.out.println("El proceso P" + procesoActual.getNumeroProceso() + " ha terminado su ejecución");
                        this.colaFinalizados.add(procesoActual);
                        mostrarColaListos();
                        System.out.println("El proceso actual P" + procesoActual.getNumeroProceso() + "se agrega a la cola de finalizados");
                        procesoActual.setTerminoRafaga(false);
                        ordenarColaListos();
                        // aca
                    } else if (procesoActual.getTerminoRafaga()){ //Si termino de ejecutar una rafaga completa
                        System.out.println("Se ejecuta el TCP para el proceso P" + procesoActual.getNumeroProceso());
                        for (int i = 0; i < this.TCP; i++) {
                            tiempoActual++;
                            System.out.println("\nTiempo: " + this.tiempoActual);
                            actualizaColaListos();
                            actualizaColaBloqueados();
                        }
                        System.out.println("El proceso P" + procesoActual.getNumeroProceso() + " está en estado de bloqueado");
                        this.colaBloqueados.add(procesoActual);
                        System.out.println("El proceso actual P" + procesoActual.getNumeroProceso() + " se agrega a la cola de bloqueados");
                        procesoActual.setTerminoRafaga(false);
                        actualizaColaBloqueados();
                        actualizaColaListos();
                        //ordenarColaListos();
                        // aca
                    } else {
                        this.colaListos.add(procesoActual); //Vuelve a la cola de listos
                      //  insertarProceso(procesoActual); //Vuelve a la cola de listos
                        ordenarColaListos();
                        // aca
                    }

                    ultimoProcesoEjecutado = procesoActual;
                }
            }
        }
    }

    // Getters y Setters
    public List<Proceso> getColaFinalizados() {
        return new ArrayList<>(colaFinalizados);
    }

    public List<Proceso> getColaListos() {
        return colaListos;
    }

    public List<Proceso> getColaBloqueados() {
        return colaBloqueados;
    }

    public List<Proceso> getProcesos() {
        return procesos;
    }

    public void setProcesos(List<Proceso> procesos) {
        this.procesos = procesos;
    }

    public int getTIP() {
        return TIP;
    }

    public void setTIP(int TIP) {
        this.TIP = TIP;
    }

    public int getTCP() {
        return TCP;
    }

    public void setTCP(int TCP) {
        this.TCP = TCP;
    }

    public int getTFP() {
        return TFP;
    }

    public void setTFP(int TFP) {
        this.TFP = TFP;
    }
}
