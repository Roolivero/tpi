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
    private boolean terminoRafaga;
    private boolean ejecutoTIP;

    //Variables para el archivo de salida
    private int  trp;
    private int trn;
    private int trt;
    private int tmrt;
    private int tiempoCPUutilizado;


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
        this.setTerminoRafaga(false);
        this.setEjecutoTIP(false);

        this.trp = 0;
        this.trn = 0;
        this.trt = 0;
        this.tmrt = 0;
        this.tiempoCPUutilizado = 0;

    }

    public void actualizarRafaga(){
        this.subRafagasEjecutadas++;
        if(this.subRafagasEjecutadas == this.duracionRafaga){
            this.rafagasEjecutadas ++;
        }
    }

    public void actualizarBloqueos(){
        this.subBloqueosEjecutados ++;
        if(this.subBloqueosEjecutados == this.duracionBloqueo){
            this.bloqueosEjecutados ++;
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
    public boolean getTerminoRafaga() {return this.terminoRafaga;}
    public boolean getEjecutoTIP() {return this.ejecutoTIP;}

    public int getTrp() { return trp; }
    public int getTrn() { return trn; }
    public int getTrt() { return trt; }
    public int getTmrt() { return tmrt; }
    public int getTiempoCPUtilizado () {return tiempoCPUutilizado;}

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
    public void setTerminoRafaga(boolean terminoRafaga) {this.terminoRafaga = terminoRafaga;}
    public void setEjecutoTIP(boolean ejecutoTIP) {this.ejecutoTIP = ejecutoTIP;}


    public void setTrp(int trp) { this.trp = trp; }
    public void setTrn(int trn) { this.trn = trn; }
    public void setTrt(int trt) { this.trt = trt; }
    public void setTmrt(int tmrt) { this.tmrt = tmrt; }
    public void setTiempoCPUutilizado(int tiempoCPUutilizado) { this.tiempoCPUutilizado = tiempoCPUutilizado; }
}























