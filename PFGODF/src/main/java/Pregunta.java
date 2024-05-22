import java.util.ArrayList;

public class Pregunta {
    private ArrayList<Parrafo> parrafos; // Puede contener varios parrafos
    private ArrayList<Parrafo> respuestasDePregunta;

    public Pregunta() {
        this.parrafos = new ArrayList<>();
        this.respuestasDePregunta = new ArrayList<>();
    }

    public Pregunta(ArrayList<Parrafo> parrafos, ArrayList<String> nombresDeEstilosParrafos, ArrayList<Parrafo> respuestasDePregunta) {
        this.parrafos = parrafos;
        this.respuestasDePregunta = respuestasDePregunta;
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

    public Pregunta obtenerCopiaRecursiva() {
        Pregunta pReturn = new Pregunta();

        for (int i = 0; i < this.parrafos.size(); i++) {
            pReturn.getParrafos().add(this.parrafos.get(i).obtenerCopiaRecursiva());
        }

        for (int i = 0; i < this.respuestasDePregunta.size(); i++) {
            pReturn.getRespuestasDePregunta().add(this.respuestasDePregunta.get(i).obtenerCopiaRecursiva());
        }

        return pReturn;
    }
}
