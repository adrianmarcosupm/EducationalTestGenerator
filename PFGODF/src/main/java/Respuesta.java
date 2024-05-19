public class Respuesta {
    private String texto;
    private String nombreDeEstilo;

    public Respuesta(String texto, String nombreDeEstilo) {
        this.texto = texto;
        this.nombreDeEstilo = nombreDeEstilo;
    }

    public Respuesta() {
        this.texto = "";
        this.nombreDeEstilo = "";
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getNombreDeEstilo() {
        return nombreDeEstilo;
    }

    public void setNombreDeEstilo(String nombreDeEstilo) {
        this.nombreDeEstilo = nombreDeEstilo;
    }

    public Respuesta obtenerCopiaRecursiva() {
        Respuesta resp = new Respuesta(this.getTexto(), this.getNombreDeEstilo());
        return resp;
    }
}
