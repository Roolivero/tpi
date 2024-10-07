package prioridadExterna;

import archivos.ArchivoSalida;
import proceso.Proceso;

import javax.naming.ldap.PagedResultsControl;
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
    private int cantProcesos;


    public PrioridadExterna(List<Integer> listaDatos, List<Proceso> procesos, String rutaArchivo) {
        this.setProcesos(procesos);
        this.setTIP(listaDatos.get(1));
        this.setTCP(listaDatos.get(2));
        this.setTFP(listaDatos.get(3));

        this.colaListos = new ArrayList<>();
        this.colaBloqueados = new LinkedList<>();
        this.colaFinalizados = new LinkedList<>();
        this.tiempoActual = 0;

        this.resultadoArchivo = "";
        this.archivoSalida = new ArchivoSalida(rutaArchivo);

        this.cantProcesos = this.procesos.size();

    }

    private void agregarResultado(String resultado) {
        this.resultadoArchivo += resultado + "\n";
    }

    private void escribirResultadoEnArchivo() {
        archivoSalida.escribirDatos(this.resultadoArchivo);
    }

    private void mostrarColaListos() {
        String colaListos = "Cola de procesos listos: [";
        for (Proceso proceso : this.colaListos) {
            colaListos = colaListos + proceso.getNumeroProceso() + ", ";
        }
        System.out.println(colaListos + "]");
        agregarResultado(colaListos + "]");
    }

    // Nuevos métodos para manejar la lista ordenada
    private void ordenarColaListos(Proceso ultimoProcesoEjecutando) {
        colaListos.sort((p1, p2) -> {
            int prioridad = Integer.compare(p2.getPrioridad(), p1.getPrioridad());
            if (prioridad != 0) return prioridad;

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
//        System.out.println("Se actualiza la cola de listos");
//        agregarResultado("Se actualiza la cola de listos");
        for (Proceso proceso : this.procesos) {
            if (proceso.getTiempoArribo() == this.tiempoActual &&
                    !this.colaListos.contains(proceso) &&
                    !this.colaBloqueados.contains(proceso) &&
                    !this.colaFinalizados.contains(proceso)) {
                this.colaListos.add(proceso);
                System.out.println("Llega el proceso P" + proceso.getNumeroProceso());
                agregarResultado("Llega el proceso P" + proceso.getNumeroProceso());
            }
        }
    }

    private void actualizaColaBloqueados() {
        System.out.println("Se actualiza la cola de bloqueados");
        agregarResultado("Se actualiza la cola de bloqueados");
        List<Proceso> procesosADesbloquear = new ArrayList<>();
        for (Proceso proceso : this.colaBloqueados) {
            proceso.actualizarBloqueos();
            if(proceso.getSubBloqueosEjecutados() == proceso.getDuracionBloqueo()) {
                System.out.println("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                agregarResultado("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecutó el bloqueo número: " + proceso.getBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                agregarResultado("Se ejecutó el bloqueo número: " + proceso.getBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                proceso.setSubBloqueosEjecutados(0);
                // Se almacena un tiempo más para guardar al proceso en la cola de listos
                tiempoDesbloqueoProcesos.put(proceso, tiempoActual + 1);
                // Se guarda el proceso en la cola de procesos a desbloquear
                procesosADesbloquear.add(proceso);
            } else {
                System.out.println("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                agregarResultado("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
            }
        }
        for (Proceso proceso : procesosADesbloquear) { //Se sacan los procesos necesarios de la cola de bloqueados
            this.colaBloqueados.remove(proceso);
            System.out.println("El proceso P" + proceso.getNumeroProceso() + " terminó su bloqueo");
            agregarResultado("El proceso P" + proceso.getNumeroProceso() + " terminó su bloqueo");
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
            System.out.println("Se agregó el proceso P" + proceso.getNumeroProceso() + " a la cola de listos. ");
            agregarResultado("Se agregó el proceso P" + proceso.getNumeroProceso() + " a la cola de listos. ");
            proceso.setPasoBloqueadoListo(true);
        }
    }

    private void ejecutarTIP(Proceso proceso){
        System.out.println("Se ejecuta el TIP para el proceso P" + proceso.getNumeroProceso());
        agregarResultado("Se ejecuta el TIP para el proceso P" + proceso.getNumeroProceso());
        for (int i = 0; i < this.TIP; i++) {
            proceso.setEjecutoTIP(true);
            tiempoActual++;
            int tiempoSiguiente = this.tiempoActual + 1;
            System.out.println("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
            agregarResultado("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
            actualizaColaListos();
            actualizaColaBloqueados();
        }
        //System.out.println("El proceso P" + proceso.getNumeroProceso() + " está en estado de running");
    }

    private void ejecutarTCP(Proceso proceso) {
        System.out.println("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
        agregarResultado("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
        for (int i = 0; i < this.TCP; i++) {
            tiempoActual++;
            int tiempoSiguiente = this.tiempoActual + 1;
            System.out.println("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
            agregarResultado("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
            actualizaColaListos();
            actualizaColaBloqueados();
        }
    }

    private void ejecutarTFP(Proceso proceso) {
        System.out.println("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
        agregarResultado("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
        for (int i = 0; i < this.TFP; i++) {
            tiempoActual++;
            int tiempoSiguiente = this.tiempoActual + 1;
            System.out.println("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
            agregarResultado("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
            actualizaColaListos();
            actualizaColaBloqueados();
        }
        System.out.println("El proceso P" + proceso.getNumeroProceso() + " ha terminado su ejecución");
        agregarResultado("El proceso P" + proceso.getNumeroProceso() + " ha terminado su ejecución");
    }

    private void ejecutarRafaga(Proceso proceso) {
        System.out.println("Se ejecuta la rafaga del P" + proceso.getNumeroProceso());
        agregarResultado("Se ejecuta la rafaga del P" + proceso.getNumeroProceso());
        proceso.actualizarRafaga();
        if(proceso.getSubRafagasEjecutadas() == proceso.getDuracionRafaga()){ //Si ejecuto una rafaga completa
            System.out.println("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            agregarResultado("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            System.out.println("Se ejecuto la ráfaga número: " + proceso.getRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            agregarResultado("Se ejecuto la ráfaga número: " + proceso.getRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            proceso.setSubRafagasEjecutadas(0);
            proceso.setTerminoRafaga(true);
        } else { //No termino de ejecutar la rafaga
            System.out.println("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            agregarResultado("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
        }
        int tiempoCpuUtilizado = proceso.getTiempoCPUtilizado();
        tiempoCpuUtilizado ++;
        proceso.setTiempoCPUutilizado(tiempoCpuUtilizado);
        this.tiempoActual++;
        int tiempoSiguiente = this.tiempoActual + 1;
        System.out.println("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
        agregarResultado("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
        actualizaColaListos();
        actualizaColaBloqueados();
        //mostrarColaListos();
    }


    public void ejecutar() {

        System.out.println("Los datos ingresados son: ");
        System.out.println("TIP: " + this.getTIP());
        System.out.println("TCP: " + this.getTCP());
        System.out.println("TFP: " + this.getTFP());
        agregarResultado("Los datos ingresados son: ");
        agregarResultado("TIP: " + this.getTIP());
        agregarResultado("TCP: " + this.getTCP());
        agregarResultado("TFP: " + this.getTFP());

        System.out.println("\nLos procesos que se van a utilizar son: ");
        agregarResultado("\nLos procesos que se van a utilizar son: ");
        for(Proceso proceso : this.procesos){
            System.out.println("\n");
            agregarResultado("\n");
            System.out.println("Proceso: P" + proceso.getNumeroProceso());
            System.out.println("Tiempo de arribo: " + proceso.getTiempoArribo());
            System.out.println("Cantidad de rafagas: " + proceso.getCantRafagas());
            System.out.println("Duracion de cada rafaga: " + proceso.getDuracionRafaga());
            System.out.println("Duracion del bloqueo: " + proceso.getDuracionBloqueo());
            System.out.println("Prioridad: " + proceso.getPrioridad());
            agregarResultado("Proceso: P" + proceso.getNumeroProceso());
            agregarResultado("Tiempo de arribo: " + proceso.getTiempoArribo());
            agregarResultado("Cantidad de rafagas: " + proceso.getCantRafagas());
            agregarResultado("Duracion de cada rafaga: " + proceso.getDuracionRafaga());
            agregarResultado("Duracion del bloqueo: " + proceso.getDuracionBloqueo());
            agregarResultado("Prioridad: " + proceso.getPrioridad());
        }

        System.out.println("\nComienza la simulacion del planificador aplicando Prioridad Externa");
        agregarResultado("\nComienza la simulacion del planificador aplicando Prioridad Externa");
        int tiempoSiguiente = this.tiempoActual + 1;
        System.out.println("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
        agregarResultado("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
        actualizaColaListos();

        //Almacena cual fue el ultimo proceso en ejecutarse, al principio es nulo
        Proceso ultimoProcesoEjecutado = null;
       // mostrarColaListos();

        while (this.getColaFinalizados().size() < this.cantProcesos ) {
            if (this.colaListos.isEmpty()) { //SI NO hay proceso:
                System.out.println("\nla CPU esta inactiva");
                agregarResultado("\nla CPU esta inactiva");
                this.tiempoActual++; //Avanzo en el tiempo
                tiempoSiguiente = this.tiempoActual + 1;
                System.out.println("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
                agregarResultado("\nTiempo [ " + this.tiempoActual + " - " + tiempoSiguiente + " ]");
                actualizaColaListos(); //Actualizo la cola de listos
            //    mostrarColaListos();
                actualizaColaBloqueados(); // Actualizo la cola de bloqueados
                ordenarColaListos(ultimoProcesoEjecutado);

            } else { // Sí HAY procesos:
                ordenarColaListos(ultimoProcesoEjecutado);
                Proceso procesoActual = obtenerProcesoMayorPrioridad();
//                System.out.println("Se saco proceso P" + procesoActual.getNumeroProceso());
//                agregarResultado("Se saco proceso P" + procesoActual.getNumeroProceso());

                // Pregunto si hay interrupciones
                //Si se cumple que el último proceso ejecutado no es nulo, que es distinto del proceso actual y
                // que el proceso actual tiene mayor prioridad que el ultimo proceso ejecutado entonces surge una interrupcion
                // Si los dos tienen la misma prioirdad, contuinua el ultimo que se estaba ejecutando

//                if (ultimoProcesoEjecutado != null){
//                    System.out.println("Ultimo P ejecutado P" + ultimoProcesoEjecutado.getNumeroProceso());
//                    agregarResultado("Ultimo P ejecutado P" + ultimoProcesoEjecutado.getNumeroProceso());
//                }

                if((ultimoProcesoEjecutado != null )&&( procesoActual != ultimoProcesoEjecutado)
                        && (procesoActual.getPrioridad() > ultimoProcesoEjecutado.getPrioridad())){
                    System.out.println("Surgió una interrupción");
                    agregarResultado("Surgió una interrupción");

                    //Devuelvo el ultimo proceso ejecutado a la cola de listos
                    //this.colaListos.add(ultimoProcesoEjecutado);
//                    System.out.println("El ultimo proceso ejecutado, P" + ultimoProcesoEjecutado.getNumeroProceso() + " vuelve a la cola de listos");
//                    agregarResultado("El ultimo proceso ejecutado, P" + ultimoProcesoEjecutado.getNumeroProceso() + " vuelve a la cola de listos");
              //      mostrarColaListos();
                    if (!procesoActual.getEjecutoTIP()) { // verifico si ya ejecuto o no su TIP
                        ejecutarTIP(procesoActual);
                        this.colaListos.add(procesoActual);
                        procesoActual.setPasoBloqueadoListo(false);
                        // Si ya ejecuto su primera rafaga y no empezo la siguiente, (pasa de bloqueado a listo) ejecuta su TCP
                    } else if(procesoActual.getRafagasEjecutadas() != 0 && procesoActual.getSubRafagasEjecutadas() == 0){
                        ejecutarTCP(procesoActual);
                        this.colaListos.add(procesoActual);
                        procesoActual.setPasoBloqueadoListo(false);
                    } else {
                        ejecutarRafaga(procesoActual); //sino, ejecuta su rafaga
                        this.colaListos.add(procesoActual);
                        procesoActual.setPasoBloqueadoListo(false);
                    }

                    // No Hay interrupción
                } else {
                    System.out.println("No hay interrupcion");
                    if (!procesoActual.getEjecutoTIP()) { // verifico si ya ejecuto o no su TIP
                        ejecutarTIP(procesoActual);
//                        System.out.println("El proceso P " + procesoActual.getNumeroProceso() + " vuelve a la cola de listos");
//                        agregarResultado("El proceso P " + procesoActual.getNumeroProceso() + " vuelve a la cola de listos");
                        this.colaListos.add(procesoActual); //Vuelve a la cola de listos
                        procesoActual.setPasoBloqueadoListo(false);
                        ordenarColaListos(ultimoProcesoEjecutado); // Se ordena la cola

                        // Si se da el caso que:
                        // El proceso no empezo a ejecutar una de sus rafagas y que es diferente al proceso actual o que
                        // el proceso actual y el siguiente son el mismo, pero el proceso acaba de pasar de bloqueado a ready
                        // Entonces ejecuta su TCP
                    } else if((procesoActual.getRafagasEjecutadas() != 0 && procesoActual.getSubRafagasEjecutadas() == 0
                            && !ultimoProcesoEjecutado.equals(procesoActual)) || (ultimoProcesoEjecutado.equals(procesoActual)
                            && procesoActual.getPasoBloqueadoListo() && (procesoActual.getRafagasEjecutadas() != 0
                            && procesoActual.getSubRafagasEjecutadas() == 0 ))){

                        ejecutarTCP(procesoActual);
//                        System.out.println("El proceso P " + procesoActual.getNumeroProceso() + " vuelve a la cola de listos");
//                        agregarResultado("El proceso P " + procesoActual.getNumeroProceso() + " vuelve a la cola de listos");
                        this.colaListos.add(procesoActual); //Vuelve a la cola de listos
                        procesoActual.setPasoBloqueadoListo(false);
                        ordenarColaListos(ultimoProcesoEjecutado); // Se ordena la cola
                    } else if (!procesoActual.getTerminoRafaga()) { // Si no termino de ejecutar una rafaga o si todavia no empezo a hacerlo
                        //Ejecuta la rafaga del proceso actual
                        ejecutarRafaga(procesoActual);
                        procesoActual.setPasoBloqueadoListo(false);

                        //Si ejecuto todas sus rafagas
                        if(procesoActual.getRafagasEjecutadas() == procesoActual.getCantRafagas()){
//                            System.out.println("El proceso P " + procesoActual.getNumeroProceso() +" ejecuta su TFP");
//                            agregarResultado("El proceso P " + procesoActual.getNumeroProceso() +" ejecuta su TFP");
                            ejecutarTFP(procesoActual); //ejecuta su TFP
                            //TRP para los datos finales
                            int trp = this.getTiempoActual() -procesoActual.getTiempoArribo();
                            procesoActual.setTrp(trp);
                            this.colaFinalizados.add(procesoActual);
                          //  mostrarColaListos();
                            System.out.println("El proceso actual P" + procesoActual.getNumeroProceso() + "se agrega a la cola de finalizados");
                            agregarResultado("El proceso actual P" + procesoActual.getNumeroProceso() + "se agrega a la cola de finalizados");
                            procesoActual.setTerminoRafaga(false);
                            ordenarColaListos(ultimoProcesoEjecutado);

                            //Si termino de ejecutar una rafaga completa, y no es la ultima
                        } else if (procesoActual.getTerminoRafaga()) {
                            this.colaBloqueados.add(procesoActual);
                            System.out.println("El proceso actual P" + procesoActual.getNumeroProceso() + " se agrega a la cola de bloqueados");
                            agregarResultado("El proceso actual P" + procesoActual.getNumeroProceso() + " se agrega a la cola de bloqueados");
                            System.out.println("El proceso actual P" + procesoActual.getNumeroProceso() + " está en estado de bloqueado");
                            agregarResultado("El proceso actual P" + procesoActual.getNumeroProceso() + " está en estado de bloqueado");
                            procesoActual.setPasoBloqueadoListo(false);
                            procesoActual.setTerminoRafaga(false);
                            actualizaColaBloqueados();
                            actualizaColaListos();

                        } else {
//                            System.out.println("El proceso P " + procesoActual.getNumeroProceso() +" pasa a la cola de listos");
//                            agregarResultado("El proceso P " + procesoActual.getNumeroProceso() +" pasa a la cola de listos");
                            this.colaListos.add(procesoActual); //Vuelve a la cola de listos
                            ordenarColaListos(ultimoProcesoEjecutado); // Se ordena la cola
                        }
                    } else {
//                        System.out.println("El proceso P " + procesoActual.getNumeroProceso() +" pasa a la cola de listos");
//                        agregarResultado("El proceso P " + procesoActual.getNumeroProceso() +" pasa a la cola de listos");
                        this.colaListos.add(procesoActual); //Vuelve a la cola de listos
                        ordenarColaListos(ultimoProcesoEjecutado); // Se ordena la cola
                    }
                }
                ultimoProcesoEjecutado = procesoActual;
            }
        }

        System.out.println("\nEl planificador de procesos terminó exitosamente!");
        agregarResultado("\nEl planificador de procesos terminó exitosamente!");
        System.out.println("\nLas estadisticas solicitadas son las siguientes: ");
        agregarResultado("\nLas estadisticas solicitadas son las siguientes: ");
        leerDatosFinales();
        escribirResultadoEnArchivo();
    }


    private void leerDatosFinales(){
        int sumaTRPtotal = 0;
        for(Proceso proceso : this.getProcesos()){
            System.out.println("\nProceso: P" + proceso.getNumeroProceso());
            agregarResultado("\nProceso: P" + proceso.getNumeroProceso());
            System.out.println("Tiempo de retorno: " + proceso.getTrp());
            agregarResultado("Tiempo de retorno: " + proceso.getTrp());

            System.out.println("Tiempo cpu: " + proceso.getTiempoCPUtilizado());
            float trn =  (float) proceso.getTrp() / proceso.getTiempoCPUtilizado();
            System.out.println("Tiempo de retorno normalizado: " + trn);
            agregarResultado("Tiempo de retorno normalizado: " + trn);

            sumaTRPtotal = sumaTRPtotal + proceso.getTrp();
        }

        int arribo1erProceso = this.procesos.get(0).getTiempoArribo();
        int trt = this.getTiempoActual() - arribo1erProceso;
        System.out.println("\nTiempo de retorno de la tanda: " + trt);
        agregarResultado("\nTiempo de retorno de la tanda: " + trt);

        System.out.println("\ncantidad de procesos: " + this.cantProcesos);
        float tmrt = sumaTRPtotal / this.cantProcesos;
        System.out.println("Tiempo medio de retorno de la tanda: " + tmrt);
        agregarResultado("Tiempo medio de retorno de la tanda: " + tmrt);

    }

    // Getters y Setters
    public List<Proceso> getColaFinalizados() {return new ArrayList<>(colaFinalizados);}
    public List<Proceso> getColaListos() {return colaListos;}
    public List<Proceso> getColaBloqueados() {return colaBloqueados;}
    public List<Proceso> getProcesos() {return procesos;}
    public int getTIP() {return TIP;}
    public int getTCP() {return TCP;}
    public int getTFP() {return TFP;}
    public int getTiempoActual() {return tiempoActual;}

    public void setProcesos(List<Proceso> procesos) {this.procesos = procesos;}
    public void setTIP(int TIP) {this.TIP = TIP;}
    public void setTCP(int TCP) {this.TCP = TCP;}
    public void setTFP(int TFP) {this.TFP = TFP;}
}