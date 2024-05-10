
import java.util.ArrayList;

public class Pregunta {
    private String texto;
    private String nombreDeEstilo;
    private ArrayList<Respuesta> respuestasDePregunta;

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

    public ArrayList<Respuesta> getRespuestasDePregunta() {
        return respuestasDePregunta;
    }

    public void setRespuestasDePregunta(ArrayList<Respuesta> respuestasDePregunta) {
        this.respuestasDePregunta = respuestasDePregunta;
    }
}
