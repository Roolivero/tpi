package srtn;

import archivos.ArchivoSalida;
import proceso.Proceso;

import java.util.*;

public class SRTN {
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


    public SRTN(List<Integer> listaDatos, List<Proceso> procesos, String rutaArchivo) {
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
    private void ordenarColaListos(Proceso ultimoProcesoEjecutando) {
        colaListos.sort((p1, p2) -> {

            // Calcular el tiempo de rafaga restante para cada proceso
            int rafagaRestante1 = p1.getDuracionRafaga() - p1.getSubRafagasEjecutadas();
            int rafagaRestante2 = p2.getDuracionRafaga() - p2.getSubRafagasEjecutadas();

            // Comparar por tiempo de rafaga restante
            int rafagaRestante = Integer.compare(rafagaRestante1, rafagaRestante2);
            if (rafagaRestante != 0) return rafagaRestante;

            if(ultimoProcesoEjecutando != null){
                // En caso de que haya un empate, continúa el último proceso ejecutado si no terminó sus ráfagas
                if (!ultimoProcesoEjecutando.getTerminoRafaga()) {
                    // Si el ultimo proceso ejecutado es p1, se debe priorizar p1
                    if (ultimoProcesoEjecutando == p1) return -1; // p1 va antes
                    // Si el ultimo proceso ejecutado es p2, se debe priorizar p2
                    if (ultimoProcesoEjecutando == p2) return 1;  // p2 va antes
                }
            }
            // Si ninguno es el último proceso ejecutado, mantener el orden (empate)
            return 0;
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
                System.out.println("Cola listos antes de agregar nuevo proceso");
                mostrarColaListos();
                this.colaListos.add(proceso);
                System.out.println("Cola listos despues de agregar nuevo procece");
                mostrarColaListos();
                System.out.println("Llega el proceso P" + proceso.getNumeroProceso());
                ejecutarTIP(proceso);
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

                // Se almacena un tiempo más para guardar al proceso en la cola de listos
                tiempoDesbloqueoProcesos.put(proceso, tiempoActual + 1);
                // Se guarda el proceso en la cola de procesos a desbloquear
                procesosADesbloquear.add(proceso);
            } else {
                System.out.println("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
            }
        }
        for (Proceso proceso : procesosADesbloquear) { //Se sacan los procesos necesarios de la cola de bloqueados
            this.colaBloqueados.remove(proceso);
            System.out.println("El proceso P" + proceso.getNumeroProceso() + " ha terminado su bloqueo y será añadido a la cola de listos en el tiempo " + tiempoDesbloqueoProcesos.get(proceso));
        }

        // Verificamos si hay algún proceso listo para agregar a la cola de listos.
        List<Proceso> procesosListosParaDesbloquear = new ArrayList<>();
        for (Map.Entry<Proceso, Integer> entry : tiempoDesbloqueoProcesos.entrySet()) {
            if (tiempoActual >= entry.getValue()) {
                procesosListosParaDesbloquear.add(entry.getKey());
            }
        }

        // Se agregan los procesos listos a la cola de listos, en el tiempo correcto
        for (Proceso proceso : procesosListosParaDesbloquear) {
            this.colaListos.add(proceso);
            tiempoDesbloqueoProcesos.remove(proceso);
            System.out.println("Se agregó el proceso P" + proceso.getNumeroProceso() + " a la cola de listos en el tiempo " + tiempoActual);
        }
    }

    private void ejecutarTIP(Proceso proceso){
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

    private void ejecutarTCP(Proceso proceso) {
        System.out.println("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
        // agregarResultado("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
        for (int i = 0; i < this.TCP; i++) {
            tiempoActual++;
            System.out.println("Tiempo: " + this.tiempoActual);
            //  agregarResultado("Tiempo: " + this.tiempoActual);
            actualizaColaListos();
            actualizaColaBloqueados();
        }
        System.out.println("El proceso P" + proceso.getNumeroProceso() + " está en estado de bloqueado");
        //   agregarResultado("El proceso P" + proceso.getNumeroProceso() + " está en estado de bloqueado");
    }

    private void ejecutarTFP(Proceso proceso) {
        System.out.println("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
        //  agregarResultado("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
        for (int i = 0; i < this.TFP; i++) {
            tiempoActual++;
            System.out.println("Tiempo: " + this.tiempoActual);
            //     agregarResultado("Tiempo: " + this.tiempoActual);
            actualizaColaListos();
            actualizaColaBloqueados();
        }
        System.out.println("El proceso P" + proceso.getNumeroProceso() + " ha terminado su ejecución");
        //  agregarResultado("El proceso P" + proceso.getNumeroProceso() + " ha terminado su ejecución");
    }

    private void ejecutarRafaga(Proceso proceso) {
        System.out.println("Se ejecuta la rafaga del P" + proceso.getNumeroProceso());
        proceso.actualizarRafaga();
        if(proceso.getSubRafagasEjecutadas() == proceso.getDuracionRafaga()){ //Si ejecuto una rafaga completa
            System.out.println("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            System.out.println("Se ejecuto la ráfaga número: " + proceso.getRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            proceso.setSubRafagasEjecutadas(0);
            proceso.setTerminoRafaga(true);

        } else { //No termino de ejecutar la rafaga
            System.out.println("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
        }

        this.tiempoActual++;
        System.out.println("\nTiempo: " + this.tiempoActual);
        actualizaColaListos();
        actualizaColaBloqueados();
        mostrarColaListos();
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
                ordenarColaListos(ultimoProcesoEjecutado);
            } else { // Sí HAY procesos:
                System.out.println("Se ordena la cola de listos: .");
                ordenarColaListos(ultimoProcesoEjecutado);
                System.out.println("Vemos la cola de listos:");
                mostrarColaListos();
                Proceso procesoActual = obtenerProcesoMayorPrioridad();
                System.out.println("Se saco proceso P" + procesoActual.getNumeroProceso());
                if(ultimoProcesoEjecutado != null){
                    System.out.println("ultimo proceso ejecutado P" + ultimoProcesoEjecutado.getNumeroProceso());
                    System.out.println("subRaf. ejecutadas P" + ultimoProcesoEjecutado.getSubRafagasEjecutadas());

                }

                // Pregunto si hay interrupciones
                //Si se cumple que el último proceso ejecutado no es nulo, que es distinto del proceso actual, que
                // no termino de ejecutar su rafaga y en caso de que no haya ejecutado ninguna rafaga, entonces surge una interrupcion.

                if(ultimoProcesoEjecutado != null && procesoActual != ultimoProcesoEjecutado
                        && ultimoProcesoEjecutado.getSubRafagasEjecutadas() != 0 &&
                        !(procesoActual.getRafagasEjecutadas() == 0 && procesoActual.getSubRafagasEjecutadas() ==0) ){
                    System.out.println("Surgió una interrupción.");

                    // ejecutarTPC
                    ejecutarTCP(ultimoProcesoEjecutado);

                    ultimoProcesoEjecutado = procesoActual;
                    this.colaListos.add(ultimoProcesoEjecutado);
                    mostrarColaListos();

                } else { // No Hay interrupción
                    if (!procesoActual.getTerminoRafaga()){ // Si no termino de ejecutar una rafaga o si todavia no empezo a hacerlo
                        //Ejecuta la rafaga del proceso actual
                        System.out.println("No hay interrupcion");
                        System.out.println("Caso 1, ejecuta su rafaga ");
                        ejecutarRafaga(procesoActual);
                    }
                    //Si ejecuto todas sus rafagas
                    if(procesoActual.getRafagasEjecutadas() == procesoActual.getCantRafagas()){
                        System.out.println("ejecuta tfp . ");
                        ejecutarTFP(procesoActual); //ejecuta su TFP
                        this.colaFinalizados.add(procesoActual);
                        mostrarColaListos();
                        System.out.println("El proceso actual P" + procesoActual.getNumeroProceso() + "se agrega a la cola de finalizados");
                        procesoActual.setTerminoRafaga(false);
                        ordenarColaListos(ultimoProcesoEjecutado);
                        //Si termino de ejecutar una rafaga completa, y no es la ultima
                    } else if (procesoActual.getTerminoRafaga()){
                        System.out.println("ejecuta tcp . ");
                        ejecutarTCP(procesoActual); //ejecuta su TCP
                        this.colaBloqueados.add(procesoActual);
                        System.out.println("El proceso actual P" + procesoActual.getNumeroProceso() + " se agrega a la cola de bloqueados");
                        procesoActual.setTerminoRafaga(false);
                        actualizaColaBloqueados();
                        actualizaColaListos();

                    } else {
                        System.out.println("vuelve a la cola de listos . ");
                        this.colaListos.add(procesoActual); //Vuelve a la cola de listos
                        ordenarColaListos(ultimoProcesoEjecutado); // Se ordena la cola
                    }

                    ultimoProcesoEjecutado = procesoActual;
                }
            }
        }
    }

    // Getters y Setters
    public List<Proceso> getColaFinalizados() {return new ArrayList<>(colaFinalizados);}
    public List<Proceso> getColaListos() {return colaListos;}
    public List<Proceso> getColaBloqueados() {return colaBloqueados;}
    public List<Proceso> getProcesos() {return procesos;}
    public int getTIP() {return TIP;}
    public int getTCP() {return TCP;}
    public int getTFP() {return TFP;}

    public void setProcesos(List<Proceso> procesos) {this.procesos = procesos;}
    public void setTIP(int TIP) {this.TIP = TIP;}
    public void setTCP(int TCP) {this.TCP = TCP;}
    public void setTFP(int TFP) {this.TFP = TFP;}
}
