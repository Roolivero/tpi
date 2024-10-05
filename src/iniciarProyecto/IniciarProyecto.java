package iniciarProyecto;

import proceso.Proceso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class IniciarProyecto {
    String rutaArchivo =  "C:\\Users\\oroci\\OneDrive\\Desktop\\Facu\\3ro\\2do cuatri\\sistemas_operativos\\tpi_so\\archivo.txt";
    Scanner scanner = new Scanner(System.in);

    public List<Proceso> leerArchivo() {
        List<Proceso> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;  // Bandera para saltar la primera línea
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    // Ignorar la primera línea (cabecera)
                    primeraLinea = false;
                    continue;
                }

                // Supongamos que los datos están separados por comas
                String[] datos = linea.split(",");

                // Parseamos los datos y creamos el objeto Proceso
                int numeroProceso = Integer.parseInt(datos[0]);
                int tiempoArribo = Integer.parseInt(datos[1]);
                int cantRafagas = Integer.parseInt(datos[2]);
                int duracionRafaga = Integer.parseInt(datos[3]);
                int duracionBloqueo = Integer.parseInt(datos[4]);
                int prioridad = Integer.parseInt(datos[5]);

                // Creamos el Proceso y lo añadimos a la lista
                Proceso proceso = new Proceso(numeroProceso, tiempoArribo, cantRafagas, duracionRafaga, duracionBloqueo, prioridad);
                lista.add(proceso);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Se produjo un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<Integer> pedirDatos() {
        List<Integer> listaDatos = new ArrayList<>();
        List<Integer> listaIntermedia = new ArrayList<>();

        int politica = solicitarPolitica();
        listaDatos.add(politica);
        listaIntermedia = solicitarDatos(politica);
        for (Integer dato : listaIntermedia){
            listaDatos.add(dato);
        }

        System.out.println("Cantidad de datos en la lista de datos: " + listaDatos.size());
        return listaDatos;
    }

    public int solicitarPolitica(){
        // Pedir al usuario que ingrese la politica que quiere usar
        System.out.println("Para seleccionar la politica deseada ingrese una opción del 1-5:");
        System.out.println("1 - FCFS");
        System.out.println("2 - Prioridad Externa");
        System.out.println("3 - Round-Robin");
        System.out.println("4 - SPN");
        System.out.println("5 - SRTN");
        System.out.println();

        int opcion = scanner.nextInt();
        leerOpcion(opcion);

        return opcion;
    }

    private List<Integer> solicitarDatos(int politica){
        List<Integer> datosSolicitados = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese los siguientes datos: ");
        System.out.println("TIP: ");
        int tip = scanner.nextInt();
        datosSolicitados.add(tip);
        System.out.println("TCP: ");
        int tcp = scanner.nextInt();
        datosSolicitados.add(tcp);
        System.out.println("TFP: ");
        int tfp = scanner.nextInt();
        datosSolicitados.add(tfp);
        if (politica == 3){
            System.out.println("Quantum: ");
            int quantum = scanner.nextInt();
            datosSolicitados.add(quantum);
        }

        return datosSolicitados;
    }

    private void leerOpcion(int opcion){
        switch (opcion){
            case 1:
                System.out.println("Usted seleccionó la politica FCFS.");
                break;
            case 2:
                System.out.println("Usted seleccionó la politica Prioridad Externa.");
                break;
            case 3:
                System.out.println("Usted seleccionó la politica Round-Robin.");
                break;
            case 4:
                System.out.println("Usted seleccionó la politica SPN.");
                break;
            case 5:
                System.out.println("Usted seleccionó la politica SRTN.");
                break;
            default:
                System.out.println("Opción no válida. Por favor, ingrese un número entre 1 y 5.");
                break;
        }
    }

    // Cierra el Scanner al final del programa
    public void cerrarScanner() {
        scanner.close();
    }
}

