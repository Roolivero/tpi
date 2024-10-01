package fcfs;

import proceso.Proceso;
import archivos.ArchivoSalida;

import java.util.*;

public class FCFS {
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

    public FCFS(List<Integer> listaDatos, List<Proceso> procesos, String rutaArchivo) {
        this.setProcesos(procesos);
        this.setTIP(listaDatos.get(0));
        this.setTCP(listaDatos.get(0));
        this.setTFP(listaDatos.get(0));

        this.colaListos = new LinkedList<>();
        this.colaBloqueados = new LinkedList<>();
        this.colaFinalizados = new LinkedList<>();
        this.tiempoActual = 0;


        System.out.println("Comienza la simulacion del planificador aplicando FCFS");
        System.out.println("Tiempo: " + this.tiempoActual);

        this.resultadoArchivo = "";
        this.archivoSalida = new ArchivoSalida(rutaArchivo);
    }

    public void ejecutar() {
        int cantProcesos = procesos.size();
        agregarResultado("Comienza la simulacion del planificador aplicando FCFS");
        agregarResultado("Tiempo: " + this.tiempoActual);
        actualizaColaListos();

        while (this.getColaFinalizados().size() < cantProcesos) {
            if (this.getColaListos().isEmpty()) {  // Si NO hay procesos, avanzo en el tiempo y actualizo las colas
                this.tiempoActual++;
                agregarResultado("Tiempo: " + this.tiempoActual);
                System.out.println("Tiempo: " + this.tiempoActual);
                actualizaColaListos();
                actualizaColaBloqueados();
            } else { // si SI hay procesos, saco el primero que este en la cola de listos
                Proceso proceso = this.colaListos.poll();
                if (proceso.getRafagasEjecutadas() == 0) { // verifico si ya ejecuto o no su TIP
                    ejecutarTIP(proceso);
                    actualizaColaListos();
                    actualizaColaBloqueados();
                }
                ejecutarRafaga(proceso); // ejecuta la rafaga del proceso
                if (proceso.getRafagasEjecutadas() == proceso.getCantRafagas()) {
                    ejecutarTFP(proceso);
                    this.colaBloqueados.remove(proceso);
                    this.colaFinalizados.add(proceso);
                    agregarResultado("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de finalizados");
                    System.out.println("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de finalizados");
                    actualizaColaListos();
                    actualizaColaBloqueados();
                } else {
                    ejecutarTCP(proceso);
                    agregarResultado("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de bloqueados");
                    System.out.println("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de bloqueados");
                    this.colaListos.remove(proceso);
                    this.colaBloqueados.add(proceso);
                    actualizaColaListos();
                    actualizaColaBloqueados();
                }
            }
        }
        System.out.println("El planificador de procesos terminó exitosamente!");
        agregarResultado("El planificador de procesos terminó exitosamente!");
        escribirResultadoEnArchivo();
    }

    private void ejecutarRafaga(Proceso proceso) {
        agregarResultado("La duración de la ráfaga del proceso P" + proceso.getNumeroProceso() + " es " + proceso.getDuracionRafaga());
        System.out.println("La duración de la ráfaga del proceso P" + proceso.getNumeroProceso() + " es " + proceso.getDuracionRafaga());
        //System.out.println("La cantidad de rafagas ejecutadas es: " + proceso.getRafagasEjecutadas());
        for (int i = 0; i < proceso.getDuracionRafaga(); i++) {
            proceso.actualizarRafaga();
            if (proceso.getSubRafagasEjecutadas() == 0) {
                agregarResultado("Se ejecuta la sub ráfaga " + proceso.getDuracionRafaga() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta la sub ráfaga " + proceso.getDuracionRafaga() + " del proceso P" + proceso.getNumeroProceso());
                agregarResultado("Se ejecuta la ráfaga número: " + proceso.getRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta la ráfaga número: " + proceso.getRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            } else {
                agregarResultado("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta la sub ráfaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            }
            this.tiempoActual++;
            System.out.println("Tiempo: " + this.tiempoActual);
            agregarResultado("Tiempo: " + this.tiempoActual);
            actualizaColaListos();
            actualizaColaBloqueados();
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
            if (proceso.getSubBloqueosEjecutados() < proceso.getDuracionBloqueo()) {
                proceso.actualizarBloqueos();
                agregarResultado("Se ejecuta el sub bloqueo número " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
                System.out.println("Se ejecuta el sub bloqueo número: " + proceso.getSubBloqueosEjecutados() + " del proceso P" + proceso.getNumeroProceso());
            } else if (proceso.getSubBloqueosEjecutados() == proceso.getDuracionBloqueo()) {
                proceso.actualizarBloqueos();
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
        System.out.println("Se ejecuta el TIP para el proceso P" + proceso.getNumeroProceso());
        agregarResultado("Se ejecuta el TIP para el proceso P" + proceso.getNumeroProceso());
        for (int i = 0; i < this.TIP; i++) {
            tiempoActual++;
            System.out.println("Tiempo: " + this.tiempoActual);
            agregarResultado("Tiempo: " + this.tiempoActual);
            actualizaColaListos();
            actualizaColaBloqueados();
        }
        System.out.println("El proceso P" + proceso.getNumeroProceso() + " está en estado de running");
        agregarResultado("El proceso P" + proceso.getNumeroProceso() + " está en estado de running");
    }

    private void ejecutarTCP(Proceso proceso) {
        System.out.println("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
        agregarResultado("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
        for (int i = 0; i < this.TCP; i++) {
            tiempoActual++;
            System.out.println("Tiempo: " + this.tiempoActual);
            agregarResultado("Tiempo: " + this.tiempoActual);
            actualizaColaListos();
            actualizaColaBloqueados();
        }
        System.out.println("El proceso P" + proceso.getNumeroProceso() + " está en estado de bloqueado");
        agregarResultado("El proceso P" + proceso.getNumeroProceso() + " está en estado de bloqueado");
    }

    private void ejecutarTFP(Proceso proceso) {
        System.out.println("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
        agregarResultado("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
        for (int i = 0; i < this.TFP; i++) {
            tiempoActual++;
            System.out.println("Tiempo: " + this.tiempoActual);
            agregarResultado("Tiempo: " + this.tiempoActual);
            actualizaColaListos();
            actualizaColaBloqueados();
        }
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
    public Queue<Proceso> getColaFinalizados() {
        return colaFinalizados;
    }

    public Queue<Proceso> getColaListos() {
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
