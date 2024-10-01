import java.util.ArrayList;

public class Examen {
    private String version;
    private ArrayList<Pregunta> grupoDePreguntas;

    public Examen() {
        this.version = "";
        this.grupoDePreguntas = new ArrayList<>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<Pregunta> getGrupoDePreguntas() {
        return grupoDePreguntas;
    }

    public void setGrupoDePreguntas(ArrayList<Pregunta> grupoDePreguntas) {
        this.grupoDePreguntas = grupoDePreguntas;
    }

    public Examen(String version, ArrayList<Pregunta> grupoDePreguntas) {
        this.version = version;
        this.grupoDePreguntas = grupoDePreguntas;
    }

    public Examen obtenerCopiaRecursiva() {
        Examen e = new Examen();

        e.setVersion(this.version);

        for (int i = 0; i < this.grupoDePreguntas.size(); i++) {
            e.getGrupoDePreguntas().add(this.grupoDePreguntas.get(i).obtenerCopiaRecursiva());
        }

        return e;
    }
}
