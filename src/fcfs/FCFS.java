package fcfs;

import proceso.Proceso;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FCFS {
    //Atributos
    private List<Proceso> procesos;
    private int TIP;
    private int TCP;
    private int TFP;
    private Queue<Proceso> colaListos;
    private List<Proceso> colaBloqueados;
    private Queue<Proceso> colaFinalizados;
    private int tiempoActual;

    //Constructor
    public FCFS(List<Integer> listaDatos, List<Proceso> procesos) {
        this.setProcesos(procesos);
        this.setTIP(listaDatos.get(0));
        this.setTCP(listaDatos.get(0));
        this.setTFP(listaDatos.get(0));

        this.colaListos = new LinkedList<>();
        this.colaBloqueados = new LinkedList<>();
        this.colaFinalizados = new LinkedList<>();
        this.tiempoActual = 0;

        actualizaColaListos();
    }


    // Metodos
    public void ejecutar(){
        int cantProcesos = procesos.toArray().length;
        while (this.getColaFinalizados().size() < cantProcesos){
            System.out.println("Cantidad de procesos en la cola de finalizados: " + this.getColaFinalizados().size());
            System.out.println("Tiempo en el principio: " + this.getTiemoActual());
            if(this.getColaListos().isEmpty()){ // Si NO hay procesos, avanzo en el tiempo y actualizo las colas
                this.tiempoActual ++;
                System.out.println("Tiempo: " + this.getTiemoActual());
                actualizaColaListos();
                actualizaColaBloqueados();
            } else { // si SI hay procesos, saco el primero que este en la cola de listos
                Proceso proceso = this.colaListos.poll();
                if(proceso.getRafagasEjecutadas() == 0){ // verifico si ya ejecuto o no su TIP
                    System.out.println("Se ejecuta la linea 50");
                    ejecutarTIP(proceso);
                    System.out.println("Se ejecuta la linea 52");
                    actualizaColaListos();
                    System.out.println("Se ejecuta la linea 54");
                    actualizaColaBloqueados();
                }
                ejecutarRafaga(proceso);// ejecuta la rafaga del proceso
                if (proceso.getRafagasEjecutadas() == proceso.getCantRafagas()){
                    System.out.println("Se ejecuta la linea 56");
                    ejecutarTFP(proceso);
                    this.colaBloqueados.remove(proceso);
                    this.colaFinalizados.add(proceso);
                    System.out.println("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de finalizados");
                    actualizaColaListos();
                    actualizaColaBloqueados();
                } else {
                    System.out.println("Se ejecuta la linea 64");
                    ejecutarTCP(proceso);
                    System.out.println("El proceso P" + proceso.getNumeroProceso() + " entra en la cola de bloqueados");
                    this.colaListos.remove(proceso);
                    this.colaBloqueados.add(proceso);
                    actualizaColaListos();
                    actualizaColaBloqueados();
                }
            }
        }
        System.out.println("El planificador de procesos termino exitosamente! ");
    }

    private void ejecutarRafaga(Proceso proceso){
        System.out.println("La duracion de la rafaga del proceso p" + proceso.getNumeroProceso() + " es " + proceso.getDuracionRafaga());
        for(int i = 0; i < proceso.getDuracionRafaga(); i++){
            proceso.actualizarRafaga();
            System.out.println("I = " + i);
            if(proceso.getSubRafagasEjecutadas() != 0){
                System.out.println("Se ejecuta la sub rafaga " + proceso.getSubRafagasEjecutadas() + " del proceso P" + proceso.getNumeroProceso());
            }
            this.tiempoActual ++;
            System.out.println("Tiempo en la rafaga: " + this.getTiemoActual());
            actualizaColaListos();
            actualizaColaBloqueados();
        }
    }

    private void actualizaColaListos(){
        for(Proceso proceso : procesos){
            if(proceso.getTiempoArribo() == this.getTiemoActual() && !this.colaListos.contains(proceso) && !this.colaBloqueados.contains(proceso) && !this.colaFinalizados.contains(proceso)) {
                int numeroProceso = proceso.getNumeroProceso();
                this.getColaListos().add(proceso);
                System.out.println("En el tiempo " + this.getTiemoActual() + "llego el proceso P" + numeroProceso);
            }
        }
    }
    private void actualizaColaBloqueados(){
        int inidice = 0;
        for(Proceso proceso : this.colaBloqueados){
            if(proceso.getSubBloqueosEjecutados() < proceso.getDuracionBloqueo()){
                proceso.actualizarBloqueos();
            } else if(proceso.getSubBloqueosEjecutados() == proceso.getDuracionBloqueo()){
                proceso.actualizarBloqueos();
                Proceso procesoListo = this.colaBloqueados.remove(inidice);
                this.colaListos.add(procesoListo);
                System.out.println("Se agrego el proceso P: " + procesoListo.getNumeroProceso() + " a la cola de listos");
            }
            inidice ++;
        }
    }

    private void ejecutarTIP(Proceso proceso){
        System.out.println("Se ejecuta el TIP para el proceso P" + proceso.getNumeroProceso());
        for(int i = 0; i < this.TIP; i++){
            tiempoActual++;
            System.out.println("Tiempo en TIP: " + this.getTiemoActual());
            actualizaColaListos();
            actualizaColaBloqueados();
        }
        System.out.println("El proceso P" + proceso.getNumeroProceso() + " esta en estado de running");
    }
    private void ejecutarTCP(Proceso proceso){
        System.out.println("Se ejecuta el TCP para el proceso P" + proceso.getNumeroProceso());
        for(int i = 0; i < this.TCP; i++){
            tiempoActual++;
            System.out.println("Tiempo en TCP: " + this.getTiemoActual());
            actualizaColaListos();
            actualizaColaBloqueados();
        }
    }
    private void ejecutarTFP(Proceso proceso){
        System.out.println("Se ejecuta el TFP para el proceso P" + proceso.getNumeroProceso());
        for(int i = 0; i < this.TFP; i++){
            tiempoActual++;
            System.out.println("Tiempo en TFP: " + this.getTiemoActual());
            actualizaColaListos();
            actualizaColaBloqueados();
        }
    }


    // Getters y Setters
    public List<Proceso> getProcesos() {
        return procesos;
    }
    private int getTiemoActual() {
        return this.tiempoActual;
    }
    private Queue<Proceso> getColaListos() {
        return colaListos;
    }
    private  Queue<Proceso> getColaFinalizados() {
        return colaFinalizados;
    }


    private void setProcesos(List<Proceso> procesos) {
        this.procesos = procesos;
    }
    private void setTIP(int dato) {
        this.TIP = dato;
    }
    private void setTCP(int dato) {
        this.TCP = dato;
    }
    private void setTFP(int dato) {
        this.TFP = dato;
    }


}
