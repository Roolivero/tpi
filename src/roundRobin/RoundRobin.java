package roundRobin;

import proceso.Proceso;
import archivos.ArchivoSalida;

import java.util.*;

public class RoundRobin {
    private List<Proceso> procesos;
    private int TIP;
    private int TCP;
    private int TFP;
    private Queue<Proceso> colaListos;
    private List<Proceso> colaBloqueados;
    private Queue<Proceso> colaFinalizados;
    private int tiempoActual;
    public String resultadoArchivo;
    private ArchivoSalida archivoSalida;
    private int cantProcesos;
    private int quantum;

    public RoundRobin(List<Integer> listaDatos, List<Proceso> procesos, String rutaArchivo) {
        this.setProcesos(procesos);
        this.setTIP(listaDatos.get(1));
        this.setTCP(listaDatos.get(2));
        this.setTFP(listaDatos.get(3));
        this.setQuantum(listaDatos.get(4));

        this.colaListos = new LinkedList<>();
        this.colaBloqueados = new LinkedList<>();
        this.colaFinalizados = new LinkedList<>();
        this.tiempoActual = 0;

        this.resultadoArchivo = "";
        this.archivoSalida = new ArchivoSalida(rutaArchivo);

        System.out.println("Comienza la simulacion del planificador aplicando Round Robin");
        System.out.println("Tiempo: " + this.tiempoActual);

        this.cantProcesos = this.procesos.size();


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
        agregarResultado("Quantum: " + this.quantum);

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

        System.out.println("Comienza la simulacion del planificador aplicando Round Robin");
        System.out.println("Tiempo: " + this.tiempoActual);
        agregarResultado("\nComienza la simulacion del planificador aplicando Round Robin");
        agregarResultado("\nTiempo: " + this.tiempoActual);

        actualizaColaListos();

        while (this.getColaFinalizados().size() < cantProcesos) {
            if (this.getColaListos().isEmpty()) {  // Si NO hay procesos, avanzo en el tiempo y actualizo las colas
                this.tiempoActual++;
                agregarResultado("\nTiempo: " + this.tiempoActual);
                System.out.println("\nTiempo: " + this.tiempoActual);
                actualizaColaListos();
                actualizaColaBloqueados();

            } else { // si SI hay procesos, saco el primero que este en la cola de listos
                Proceso proceso = this.colaListos.poll();
//                System.out.println("Se saca el proceso: " + proceso.getNumeroProceso());

                if (!proceso.getEjecutoTIP()) { //Si No ejecuto su tip, lo ejecuta
                    ejecutarTIP(proceso);
                //Ejecuta su TCP solo cuando sale de bloqueado a listo o cuando es el unico proceso que se esta ejecutando
                // y retoma su ejecucion despues del quantum.
                } else if(((proceso.getRafagasEjecutadas() != 0) && (proceso.getSubRafagasEjecutadas() == 0)) ||
                        (this.colaFinalizados.size() == (this.cantProcesos - 1))){
                    ejecutarTCP(proceso);
                }

                ejecutarRafaga(proceso); // ejecuta la rafaga del proceso
                if (proceso.getRafagasEjecutadas() == proceso.getCantRafagas()) { //Si ya ejecuto todas sus rafagas:
                    ejecutarTFP(proceso);
                    int trp = this.getTiempoActual() -proceso.getTiempoArribo();
                    proceso.setTrp(trp);
                    this.colaBloqueados.remove(proceso);
                    this.colaFinalizados.add(proceso);
                    actualizaColaListos();
                    actualizaColaBloqueados();
                //Si no ejecuto todas sus rafagas:
                } else if (proceso.getSubRafagasEjecutadas() == 0 && proceso.getRafagasEjecutadas() != 0) {
                    actualizaColaListos();
                    actualizaColaBloqueados();
                    agregarResultado("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de bloqueados");
                    System.out.println("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de bloqueados");
                    this.colaListos.remove(proceso);
                    this.colaBloqueados.add(proceso);
                } else { //Si le qudan rafagas por ejecutar, pero se quedo sin quantum vuelve a la cola de listos
                    this.getColaListos().add(proceso);
                }
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

        System.out.println("cantidad de procesos: " + this.cantProcesos);
        float tmrt = sumaTRPtotal / this.cantProcesos;
        System.out.println("Tiempo medio de retorno de la tanda: " + tmrt);
        agregarResultado("Tiempo medio de retorno de la tanda: " + tmrt);

    }

    private void ejecutarRafaga(Proceso proceso) {
//        agregarResultado("La duración de la ráfaga del proceso P" + proceso.getNumeroProceso() + " es " + proceso.getDuracionRafaga());
//        System.out.println("La duración de la ráfaga del proceso P" + proceso.getNumeroProceso() + " es " + proceso.getDuracionRafaga());

        System.out.println("El quantum es: " + this.quantum);
        boolean terminoRafaga = false;
        for(int i =0; i <this.quantum ; i++){
            this.tiempoActual++;
            System.out.println("\nTiempo: " + this.tiempoActual);
            agregarResultado("\nTiempo: " + this.tiempoActual);
            int Q = i +1;
            System.out.println("* se ejecuta el Quantum: " + Q);
            proceso.actualizarRafaga();
            if (proceso.getSubRafagasEjecutadas() == proceso.getDuracionRafaga()) {
                agregarResultado("Se ejecuta la sub ráfaga " + proceso.getDuracionRafaga() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta la sub ráfaga " + proceso.getDuracionRafaga() + " del proceso P" + proceso.getNumeroProceso());
                agregarResultado("Se ejecuta la ráfaga número: " + proceso.getRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta la ráfaga número: " + proceso.getRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
                proceso.setSubRafagasEjecutadas(0);
                terminoRafaga = true;
            } else {
                agregarResultado("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            }
            int tiempoCpuUtilizado = proceso.getTiempoCPUtilizado();
            tiempoCpuUtilizado ++;
            proceso.setTiempoCPUutilizado(tiempoCpuUtilizado);

            actualizaColaListos();
            actualizaColaBloqueados();
            if(terminoRafaga){
                break;
            }
        }
    }

    private void actualizaColaListos() {
        for (Proceso proceso : procesos) {
            if (proceso.getTiempoArribo() == this.tiempoActual && !this.colaListos.contains(proceso) && !this.colaBloqueados.contains(proceso) && !this.colaFinalizados.contains(proceso)) {
                this.colaListos.add(proceso);
                agregarResultado("Llega el proceso P" + proceso.getNumeroProceso());
                System.out.println("Llega el proceso P" + proceso.getNumeroProceso());
            }
        }
    }

    private void actualizaColaBloqueados() {
        int indice = 0;
        for (Proceso proceso : this.colaBloqueados) {
            proceso.actualizarBloqueos();
            if (proceso.getSubBloqueosEjecutados() < proceso.getDuracionBloqueo()) {
                agregarResultado("Se ejecuta el sub bloqueo número " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
            } else if (proceso.getSubBloqueosEjecutados() == proceso.getDuracionBloqueo()) {
                agregarResultado("Se ejecuta el sub bloqueo número " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                proceso.setSubBloqueosEjecutados(0);
                agregarResultado("Se ejecuta el bloqueo número " + proceso.getBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta el bloqueo número: " + proceso.getBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                Proceso procesoListo = this.colaBloqueados.remove(indice);
                this.colaListos.add(procesoListo);
                agregarResultado("Se agregó el proceso P " + procesoListo.getNumeroProceso() + " a la cola de listos");
                System.out.println("Se agregó el proceso P " + procesoListo.getNumeroProceso() + " a la cola de listos");
            }
            indice++;
        }
    }

    private void ejecutarTIP(Proceso proceso) {
        for (int i = 0; i < this.TIP; i++) {
            proceso.setEjecutoTIP(true);
            this.tiempoActual++;
            System.out.println("\nTiempo: " + this.tiempoActual);
            agregarResultado("\nTiempo: " + this.tiempoActual);
            System.out.println("Se ejecuta el TIP para el proceso P" + proceso.getNumeroProceso());
            agregarResultado("Se ejecuta el TIP para el proceso P" + proceso.getNumeroProceso());
            actualizaColaListos();
            actualizaColaBloqueados();
        }
//        System.out.println("El proceso P" + proceso.getNumeroProceso() + " está en estado de running");
//        agregarResultado("El proceso P" + proceso.getNumeroProceso() + " está en estado de running");
    }

    private void ejecutarTCP(Proceso proceso) {
        for (int i = 0; i < this.TCP; i++) {
            tiempoActual++;
            System.out.println("\nTiempo: " + this.tiempoActual);
            agregarResultado("\nTiempo: " + this.tiempoActual);
            System.out.println("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
            agregarResultado("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
            actualizaColaListos();
            actualizaColaBloqueados();
        }
//        System.out.println("El proceso P" + proceso.getNumeroProceso() + " está en estado de bloqueado");
//        agregarResultado("El proceso P" + proceso.getNumeroProceso() + " está en estado de bloqueado");
    }

    private void ejecutarTFP(Proceso proceso) {
        for (int i = 0; i < this.TFP; i++) {
            tiempoActual++;
            System.out.println("\nTiempo: " + this.tiempoActual);
            agregarResultado("\nTiempo: " + this.tiempoActual);
            System.out.println("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
            agregarResultado("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
            actualizaColaListos();
            actualizaColaBloqueados();
        }
        agregarResultado("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de finalizados");
        System.out.println("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de finalizados");
        System.out.println("El proceso P" + proceso.getNumeroProceso() + " ha terminado su ejecución");
        agregarResultado("El proceso P" + proceso.getNumeroProceso() + " ha terminado su ejecución");
    }

    private void agregarResultado(String resultado) {
        this.resultadoArchivo += resultado + "\n";
    }

    private void escribirResultadoEnArchivo() {
        archivoSalida.escribirDatos(this.resultadoArchivo);
    }

    // Getters y Setters
    public int getTIP() {return TIP;}
    public int getTCP() {return TCP;}
    public int getTFP() {return TFP;}
    public int getTiempoActual() {return tiempoActual;}
    public Queue<Proceso> getColaFinalizados() {return colaFinalizados;}
    public Queue<Proceso> getColaListos() {return colaListos;}
    public List<Proceso> getColaBloqueados() {return colaBloqueados;}
    public List<Proceso> getProcesos() {return procesos;}

    public void setProcesos(List<Proceso> procesos) {this.procesos = procesos;}
    public void setTIP(int TIP) {this.TIP = TIP;}
    public void setTCP(int TCP) {this.TCP = TCP;}
    public void setTFP(int TFP) {this.TFP = TFP;}
    public void setQuantum(int quantum) {this.quantum = quantum;}
}
