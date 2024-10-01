package archivos;

import java.io.FileWriter;
import java.io.IOException;

public class ArchivoSalida {
    public static void main(String[] args) {
        String rutaArchivo = "datos_generados.txt";  // Definir la ruta y nombre del archivo
        FileWriter escritor = null;  // Inicializamos el objeto FileWriter

        try {
            escritor = new FileWriter(rutaArchivo, true);  // Creamos el escritor de archivos
            // Datos que quieres escribir
            String datos = "Este es un dato generado por el programa.\n";

            // Escribir en el archivo
            escritor.write(datos);
            System.out.println("Datos escritos correctamente en el archivo.");
        } catch (IOException e) {
            System.out.println("Ocurrió un error al escribir en el archivo.");
            e.printStackTrace();
        } finally {
            // Asegurarse de cerrar el FileWriter para liberar recursos
            if (escritor != null) {
                try {
                    escritor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
