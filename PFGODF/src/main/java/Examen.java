import java.util.ArrayList;

public class Examen {
    private String version;
    private ArrayList<Pregunta> preguntas;

    public Examen() {
        version = "";
        preguntas = new ArrayList<>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(ArrayList<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }
}
