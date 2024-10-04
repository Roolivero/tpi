import fcfs.FCFS;
import iniciarProyecto.IniciarProyecto;
import prioridadExterna.PrioridadExterna;
import proceso.Proceso;
import roundRobin.RoundRobin;
import spn.SPN;
import srtn.SRTN;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        IniciarProyecto iniciarProyecto = new IniciarProyecto();
        List<Integer> listaDatos = new ArrayList<>();

        listaDatos = iniciarProyecto.pedirDatos();
        iniciarProyecto.cerrarScanner();

        List<Proceso> listaProcesos = new ArrayList<>();
//        listaProcesos = iniciarProyecto.leerArchivo();
//
        Proceso proceso1 = new Proceso(1,0,2,3,3,1);
        Proceso proceso2 = new Proceso(2,2,3,2,2,2);
        Proceso proceso3 = new Proceso(3,4,2,2,2,3);
        Proceso proceso4 = new Proceso(4,6,3,4,4,1);
        Proceso proceso5 = new Proceso(5,8,2,3,3,2);

        listaProcesos.add(proceso1);
        listaProcesos.add(proceso2);
        listaProcesos.add(proceso3);
        listaProcesos.add(proceso4);
        listaProcesos.add(proceso5);

        String rutaArvhivoFCFS = " C:\\Users\\oroci\\OneDrive\\Desktop\\Facu\\3ro\\2do cuatri\\sistemas_operativos\\tpi_so\\src\\archivos\\archivo_FCFS.txt";
        String rutaArvhivoSPN = "C:\\Users\\oroci\\OneDrive\\Desktop\\Facu\\3ro\\2do cuatri\\sistemas_operativos\\tpi_so\\src\\archivos\\archivo_SPN.txt";
        String rutaArvhivoPE = "C:\\Users\\oroci\\OneDrive\\Desktop\\Facu\\3ro\\2do cuatri\\sistemas_operativos\\tpi_so\\src\\archivos\\archivo_PE.txt";
        String rutaArvhivoSRTN = "C:\\Users\\oroci\\OneDrive\\Desktop\\Facu\\3ro\\2do cuatri\\sistemas_operativos\\tpi_so\\src\\archivos\\archivo_SRTN.txt";
        String rutaArvhivoRR = "C:\\Users\\oroci\\OneDrive\\Desktop\\Facu\\3ro\\2do cuatri\\sistemas_operativos\\tpi_so\\src\\archivos\\archivo_RR.txt";

        int opcion = listaDatos.get(0);
        if(opcion == 1){
            FCFS fcfs = new FCFS(listaDatos,listaProcesos, rutaArvhivoFCFS);
            fcfs.ejecutar();
        } else if (opcion == 2) {
            PrioridadExterna prioridadExterna = new PrioridadExterna(listaDatos,listaProcesos, rutaArvhivoFCFS);
            prioridadExterna.ejecutar();
        } else if (opcion == 3){
            RoundRobin roundRobin = new RoundRobin(listaDatos);
        } else if (opcion == 4) {
            SPN spn = new SPN(listaDatos,listaProcesos,rutaArvhivoSPN);
            spn.ejecutar();
        } else if (opcion == 5) {
            SRTN srtn = new SRTN (listaDatos,listaProcesos,rutaArvhivoSRTN);
            srtn.ejecutar();
        }

    }

}