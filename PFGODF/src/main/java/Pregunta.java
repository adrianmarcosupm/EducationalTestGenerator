
import java.util.ArrayList;

public class Pregunta {
    private ArrayList<String> textos; // Puede contener varios parrafos
    private ArrayList<String> nombreDeEstilos; // Puede contener varios estilos
    private ArrayList<Respuesta> respuestasDePregunta;

    public Pregunta() {
        textos = new ArrayList<>();
        nombreDeEstilos = new ArrayList<>();
        respuestasDePregunta = new ArrayList<>();
    }

    public ArrayList<String> getTextos() {
        return textos;
    }

    public void setTextos(ArrayList<String> textos) {
        this.textos = textos;
    }

    public ArrayList<String> getNombreDeEstilos() {
        return nombreDeEstilos;
    }

    public void setNombreDeEstilos(ArrayList<String> nombreDeEstilos) {
        this.nombreDeEstilos = nombreDeEstilos;
    }

    public ArrayList<Respuesta> getRespuestasDePregunta() {
        return respuestasDePregunta;
    }

    public void setRespuestasDePregunta(ArrayList<Respuesta> respuestasDePregunta) {
        this.respuestasDePregunta = respuestasDePregunta;
    }
}
