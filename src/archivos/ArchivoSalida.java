package archivos;

import java.io.FileWriter;
import java.io.IOException;

public class ArchivoSalida {
    private String rutaArchivo = "datos_generados.txt";

    public ArchivoSalida(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public void escribirDatos(String datos) {
        FileWriter escritor = null;

        try {
            escritor = new FileWriter(rutaArchivo, true);  // 'true' para agregar al archivo
            escritor.write(datos);
            System.out.println("Datos escritos correctamente en el archivo.");
        } catch (IOException e) {
            System.out.println("Ocurrió un error al escribir en el archivo.");
            e.printStackTrace();
        } finally {
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