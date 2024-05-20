
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;

import java.util.ArrayList;

public class Pregunta {
    private ArrayList<String> textos; // Puede contener varios parrafos
    private ArrayList<String> nombreDeEstilos; // Puede contener varios estilos
    private ArrayList<Respuesta> respuestasDePregunta;
    private ArrayList<OdfStyle> estilos;

    public Pregunta() {
        textos = new ArrayList<>();
        nombreDeEstilos = new ArrayList<>();
        respuestasDePregunta = new ArrayList<>();
        estilos = new ArrayList<>();
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

    public ArrayList<OdfStyle> getEstilos() {
        return estilos;
    }

    public void setEstilos(ArrayList<OdfStyle> estilos) {
        this.estilos = estilos;
    }

    public Pregunta obtenerCopiaRecursiva() {
        Pregunta preg = new Pregunta();
        for (int i = 0; i < this.getTextos().size(); i++) {
            preg.getTextos().add(this.getTextos().get(i));
        }
        for (int i = 0; i < this.getNombreDeEstilos().size(); i++) {
            preg.getNombreDeEstilos().add(this.getNombreDeEstilos().get(i));
        }
        for (int i = 0; i < this.getRespuestasDePregunta().size(); i++) {
            preg.getRespuestasDePregunta().add(this.getRespuestasDePregunta().get(i).obtenerCopiaRecursiva());
        }
        for (int i = 0; i < this.getEstilos().size(); i++) {
            preg.getEstilos().add(this.getEstilos().get(i));
        }

        return preg;
    }
}
