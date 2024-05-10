
import java.util.ArrayList;

public class Pregunta {
    private ArrayList<String> texto; // Puede contener varios parrafos
    private ArrayList<String> nombreDeEstilo; // Puede contener varios estilos
    private ArrayList<Respuesta> respuestasDePregunta;

    public Pregunta() {
        texto = new ArrayList<>();
        nombreDeEstilo = new ArrayList<>();
        respuestasDePregunta = new ArrayList<>();
    }

    public ArrayList<String> getTexto() {
        return texto;
    }

    public void setTexto(ArrayList<String> texto) {
        this.texto = texto;
    }

    public ArrayList<String> getNombreDeEstilo() {
        return nombreDeEstilo;
    }

    public void setNombreDeEstilo(ArrayList<String> nombreDeEstilo) {
        this.nombreDeEstilo = nombreDeEstilo;
    }

    public ArrayList<Respuesta> getRespuestasDePregunta() {
        return respuestasDePregunta;
    }

    public void setRespuestasDePregunta(ArrayList<Respuesta> respuestasDePregunta) {
        this.respuestasDePregunta = respuestasDePregunta;
    }
}
