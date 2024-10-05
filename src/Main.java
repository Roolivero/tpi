import fcfs.FCFS;
import iniciarProyecto.IniciarProyecto;
import prioridadExterna.PrioridadExterna;
import proceso.Proceso;
import roundRobin.RoundRobin;
import spn.SPN;
import srtn.SRTN;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        IniciarProyecto iniciarProyecto = new IniciarProyecto();
        List<Integer> listaDatos = new ArrayList<>();

        listaDatos = iniciarProyecto.pedirDatos();
        iniciarProyecto.cerrarScanner();

        List<Proceso> listaProcesos = new ArrayList<>();
        listaProcesos = iniciarProyecto.leerArchivo();

        System.out.println("Los procesos que se van a utilizar son: ");
        for(Proceso proceso : listaProcesos){
            System.out.println("\n");
            System.out.println("Proceso: P" + proceso.getNumeroProceso());
            System.out.println("Tiempo de arribo: " + proceso.getTiempoArribo());
            System.out.println("Cantidad de rafagas: " + proceso.getCantRafagas());
            System.out.println("Duracion de cada rafaga: " + proceso.getDuracionRafaga());
            System.out.println("Duracion del bloqueo: " + proceso.getDuracionBloqueo());
            System.out.println("Prioridad: " + proceso.getPrioridad());
        }

        // Formatear la fecha y hora actual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        // Crear nombres de archivo con timestamp
        String rutaArvhivoFCFS = "src\\archivos\\archivo_FCFS_" + timestamp + ".txt";
        String rutaArvhivoSPN = "src\\archivos\\archivo_SPN_" + timestamp + ".txt";
        String rutaArvhivoPE = "src\\archivos\\archivo_PE_" + timestamp + ".txt";
        String rutaArvhivoSRTN = "src\\archivos\\archivo_SRTN_" + timestamp + ".txt";
        String rutaArvhivoRR = "src\\archivos\\archivo_RR_" + timestamp + ".txt";

        int opcion = listaDatos.get(0);
        if(opcion == 1){
            FCFS fcfs = new FCFS(listaDatos,listaProcesos, rutaArvhivoFCFS);
            fcfs.ejecutar();
        } else if (opcion == 2) {
            PrioridadExterna prioridadExterna = new PrioridadExterna(listaDatos,listaProcesos, rutaArvhivoFCFS);
            prioridadExterna.ejecutar();
        } else if (opcion == 3){
            RoundRobin roundRobin = new RoundRobin(listaDatos,listaProcesos, rutaArvhivoRR);
            //roundRobin.ejecutar();
        } else if (opcion == 4) {
            SPN spn = new SPN(listaDatos,listaProcesos,rutaArvhivoSPN);
            spn.ejecutar();
        } else if (opcion == 5) {
            SRTN srtn = new SRTN (listaDatos,listaProcesos,rutaArvhivoSRTN);
            srtn.ejecutar();
        }

    }

}