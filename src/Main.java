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
        Proceso proceso1 = new Proceso(1,0,2,3,2,1);
        Proceso proceso2 = new Proceso(2,4,3,2,1,1);
        Proceso proceso3 = new Proceso(3,5,2,2,4,1);

        listaProcesos.add(proceso1);
        listaProcesos.add(proceso2);
        listaProcesos.add(proceso3);

        int opcion = listaDatos.get(0);
        if(opcion == 1){
            FCFS fcfs = new FCFS(listaDatos,listaProcesos);
            fcfs.ejecutar();
        } else if (opcion == 2) {
            PrioridadExterna prioridadExterna = new PrioridadExterna(listaDatos);
        } else if (opcion == 3){
            RoundRobin roundRobin = new RoundRobin(listaDatos);
        } else if (opcion == 4) {
            SPN spn = new SPN(listaDatos);
        } else if (opcion == 5) {
            SRTN srtn = new SRTN (listaDatos);
        }




    }

}