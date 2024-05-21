import java.util.ArrayList;

public class Pregunta {
    private ArrayList<Parrafo> parrafos; // Puede contener varios parrafos
    private ArrayList<String> nombresDeEstilosParrafos;

    private ArrayList<Parrafo> respuestasDePregunta;

    public Pregunta() {
        this.parrafos = new ArrayList<>();
        this.nombresDeEstilosParrafos = new ArrayList<>();
        this.respuestasDePregunta = new ArrayList<>();
    }

    public Pregunta(ArrayList<Parrafo> parrafos, ArrayList<String> nombresDeEstilosParrafos, ArrayList<Parrafo> respuestasDePregunta) {
        this.parrafos = parrafos;
        this.nombresDeEstilosParrafos = nombresDeEstilosParrafos;
        this.respuestasDePregunta = respuestasDePregunta;
    }

    public ArrayList<Parrafo> getParrafos() {
        return parrafos;
    }

    public void setParrafos(ArrayList<Parrafo> parrafos) {
        this.parrafos = parrafos;
    }

    public ArrayList<String> getNombresDeEstilosParrafos() {
        return nombresDeEstilosParrafos;
    }

    public void setNombresDeEstilosParrafos(ArrayList<String> nombresDeEstilosParrafos) {
        this.nombresDeEstilosParrafos = nombresDeEstilosParrafos;
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

        for (int i = 0; i < this.nombresDeEstilosParrafos.size(); i++) {
            pReturn.getNombresDeEstilosParrafos().add(this.nombresDeEstilosParrafos.get(i));
        }

        for (int i = 0; i < this.respuestasDePregunta.size(); i++) {
            pReturn.getRespuestasDePregunta().add(this.respuestasDePregunta.get(i).obtenerCopiaRecursiva());
        }

        return pReturn;
    }
}
