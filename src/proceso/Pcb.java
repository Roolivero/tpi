package proceso;

public class Pcb extends Proceso {
    private int estado;
    // Estasdos:
    // 0: inactivo, 1: ejecutando, 2: bloqueado, 3: listo

    public Pcb(int numeroProceso, int tiempoArribo, int cantRafagas, int duracionRafaga, int duracionBloqueo, int prioridad) {
        super(numeroProceso, tiempoArribo, cantRafagas, duracionRafaga, duracionBloqueo, prioridad);
        this.setEstado(0);
    }

    public int getEstado(){
        return this.estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
