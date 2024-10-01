package proceso;

public class Proceso {
    // Atributos
    private int numeroProceso;
    private int tiempoArribo;
    private int cantRafagas;
    private int duracionRafaga;
    private int duracionBloqueo;
    private int prioridad;
    private int rafagasEjecutadas;
    private int bloqueosEjecutados;
    private int subRafagasEjecutadas;
    private int subBloqueosEjecutados;
    //Constructor
    public Proceso(int numeroProceso, int tiempoArribo, int cantRafagas, int duracionRafaga, int duracionBloqueo, int prioridad) {
        this.setNombre(numeroProceso);
        this.setTiempoArribo(tiempoArribo);
        this.setCantRafagas(cantRafagas);
        this.setDuracionRafaga(duracionRafaga);
        this.setDuracionBloqueo(duracionBloqueo);
        this.setPrioridad(prioridad);
        this.setRafagasEjecutadas(0);
        this.setBloqueosEjecutados(0);
        this.setSubRafagasEjecutadas(0);
        this.setSubBloqueosEjecutados(0);
    }

    public void actualizarRafaga(){
        this.subRafagasEjecutadas++;
        System.out.println("Sub rafaga numero: " + this.subRafagasEjecutadas + " del proceso P" + this.numeroProceso);
        if(this.subRafagasEjecutadas == this.duracionRafaga){
            this.setSubRafagasEjecutadas(0);
            this.rafagasEjecutadas ++;
            System.out.println("Se ejecuto la rafaga numero: " + this.rafagasEjecutadas + " del proceso P" + this.numeroProceso);
        }
    }

    public void actualizarBloqueos(){
        if(this.subBloqueosEjecutados == this.duracionBloqueo){
            this.bloqueosEjecutados ++;
            System.out.println("Se ejecuto el bloqueo numero: " + this.bloqueosEjecutados + " del proceso P" + this.numeroProceso);
            this.subBloqueosEjecutados = 0;
        } else {
            this.subBloqueosEjecutados++;
            System.out.println("Sub bloqueo numero: " + this.subBloqueosEjecutados + " del proceso P" + this.numeroProceso);
        }
    }


    //Setters y getters
    public int getNumeroProceso() {return numeroProceso;}
    public int getTiempoArribo() {return tiempoArribo;}
    public int getCantRafagas() {return cantRafagas;}
    public int getDuracionRafaga() {return duracionRafaga;}
    public int getDuracionBloqueo() {return duracionBloqueo;}
    public int getPrioridad() {return prioridad;}
    public int getRafagasEjecutadas() {return rafagasEjecutadas;}
    public int getBloqueosEjecutados() {return bloqueosEjecutados;}
    public int getSubRafagasEjecutadas() {return subRafagasEjecutadas;}
    public int getSubBloqueosEjecutados() {return subBloqueosEjecutados;}


    public void setNombre(int  numeroProceso) {this.numeroProceso = numeroProceso;}
    public void setTiempoArribo(int tiempoArribo) {this.tiempoArribo = tiempoArribo;}
    public void setCantRafagas(int cantRafagas) {this.cantRafagas = cantRafagas;}
    public void setDuracionRafaga(int duracionRafaga) {this.duracionRafaga = duracionRafaga;}
    public void setDuracionBloqueo(int duracionBloqueo) {this.duracionBloqueo = duracionBloqueo;}
    public void setPrioridad(int prioridad) {this.prioridad = prioridad;}
    public void setRafagasEjecutadas(int rafagasEjecutadas) {this.rafagasEjecutadas = rafagasEjecutadas;}
    public void setBloqueosEjecutados(int bloqueosEjecutados) {this.bloqueosEjecutados = bloqueosEjecutados;}
    public void setSubRafagasEjecutadas(int subRafagasEjecutadas) {this.subRafagasEjecutadas = subRafagasEjecutadas;}
    public void setSubBloqueosEjecutados(int subBloqueosEjecutados) {this.subBloqueosEjecutados = subBloqueosEjecutados;}
}























