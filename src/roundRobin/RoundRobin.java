package roundRobin;

import archivos.ArchivoSalida;
import proceso.Proceso;

import java.util.*;

public class RoundRobin {
    private List<Proceso> procesos;
    private int TIP;
    private int TCP;
    private int TFP;
    private int quantum;
    private List<Proceso> colaListos;
    private List<Proceso> colaBloqueados;
    private Queue<Proceso> colaFinalizados;
    private int tiempoActual;
    public String resultadoArchivo;
    private ArchivoSalida archivoSalida;
    private Map<Proceso, Integer> tiempoDesbloqueoProcesos = new HashMap<>();


    public RoundRobin(List<Integer> listaDatos, List<Proceso> procesos, String rutaArchivo) {
        this.setProcesos(procesos);
        this.setTIP(listaDatos.get(1));
        this.setTCP(listaDatos.get(2));
        this.setTFP(listaDatos.get(3));
        this.setQuantum(listaDatos.get(4));

        this.colaListos = new ArrayList<>();
        this.colaBloqueados = new LinkedList<>();
        this.colaFinalizados = new LinkedList<>();
        this.tiempoActual = 0;

        System.out.println("Comienza la simulacion del planificador aplicando Round Robin");
        System.out.println("\nTiempo: " + this.tiempoActual);
        System.out.println("Quantum: " + this.quantum);

        this.resultadoArchivo = "";
        this.archivoSalida = new ArchivoSalida(rutaArchivo);
    }


    // Getters y Setters
    public List<Proceso> getColaFinalizados() {return new ArrayList<>(colaFinalizados);}
    public List<Proceso> getColaListos() {return colaListos;}
    public List<Proceso> getColaBloqueados() {return colaBloqueados;}
    public List<Proceso> getProcesos() {return procesos;}
    public int getTIP() {return TIP;}
    public int getTCP() {return TCP;}
    public int getTFP() {return TFP;}
    public int getQuantum() {return quantum;}

    public void setProcesos(List<Proceso> procesos) {this.procesos = procesos;}
    public void setTIP(int TIP) {this.TIP = TIP;}
    public void setTCP(int TCP) {this.TCP = TCP;}
    public void setTFP(int TFP) {this.TFP = TFP;}
    public void setQuantum(int quantum) {this.quantum = quantum;}
}
