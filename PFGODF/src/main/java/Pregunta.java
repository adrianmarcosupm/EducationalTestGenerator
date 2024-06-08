import java.util.ArrayList;
import java.util.HashMap;

public class Pregunta {
    private ArrayList<Parrafo> parrafos; // Puede contener varios parrafos
    private ArrayList<Parrafo> respuestasDePregunta;
    private HashMap<String, String> metadatos;

    public Pregunta() {
        this.parrafos = new ArrayList<>();
        this.respuestasDePregunta = new ArrayList<>();
        this.metadatos = new HashMap<>();
    }

    public Pregunta(ArrayList<Parrafo> parrafos, ArrayList<Parrafo> respuestasDePregunta, HashMap<String, String> metadatos) {
        this.parrafos = parrafos;
        this.respuestasDePregunta = respuestasDePregunta;
        this.metadatos = metadatos;
    }

    public ArrayList<Parrafo> getParrafos() {
        return parrafos;
    }

    public void setParrafos(ArrayList<Parrafo> parrafos) {
        this.parrafos = parrafos;
    }

    public ArrayList<Parrafo> getRespuestasDePregunta() {
        return respuestasDePregunta;
    }

    public void setRespuestasDePregunta(ArrayList<Parrafo> respuestasDePregunta) {
        this.respuestasDePregunta = respuestasDePregunta;
    }

    public HashMap<String, String> getMetadatos() {
        return metadatos;
    }

    public void setMetadatos(HashMap<String, String> metadatos) {
        this.metadatos = metadatos;
    }

    public Pregunta obtenerCopiaRecursiva() {
        Pregunta pReturn = new Pregunta();

        for (int i = 0; i < this.parrafos.size(); i++) {
            pReturn.getParrafos().add(this.parrafos.get(i).obtenerCopiaRecursiva());
        }

        for (int i = 0; i < this.respuestasDePregunta.size(); i++) {
            pReturn.getRespuestasDePregunta().add(this.respuestasDePregunta.get(i).obtenerCopiaRecursiva());
        }

        for (String s : this.getMetadatos().keySet()) {
            pReturn.getMetadatos().put(s, this.getMetadatos().get(s));
        }

        return pReturn;
    }
}
