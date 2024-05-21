import java.util.ArrayList;

public class Parrafo {
    private ArrayList<String> textosSpan;
    private ArrayList<String> nombresDeEstilosTextosSpan;
    private String nombreDeEstiloParrafo;

    public Parrafo() {
        this.textosSpan = new ArrayList<>();
        this.nombresDeEstilosTextosSpan = new ArrayList<>();
        this.nombreDeEstiloParrafo = "";
    }

    public Parrafo(ArrayList<String> textosSpan, ArrayList<String> nombresDeEstilosTextosSpan, String nombreDeEstiloParrafo) {
        this.textosSpan = textosSpan;
        this.nombresDeEstilosTextosSpan = nombresDeEstilosTextosSpan;
        this.nombreDeEstiloParrafo = nombreDeEstiloParrafo;
    }

    public ArrayList<String> getTextosSpan() {
        return textosSpan;
    }

    public void setTextosSpan(ArrayList<String> textosSpan) {
        this.textosSpan = textosSpan;
    }

    public ArrayList<String> getNombresDeEstilosTextosSpan() {
        return nombresDeEstilosTextosSpan;
    }

    public void setNombresDeEstilosTextosSpan(ArrayList<String> nombresDeEstilosTextosSpan) {
        this.nombresDeEstilosTextosSpan = nombresDeEstilosTextosSpan;
    }

    public String getNombreDeEstiloParrafo() {
        return nombreDeEstiloParrafo;
    }

    public void setNombreDeEstiloParrafo(String nombreDeEstiloParrafo) {
        this.nombreDeEstiloParrafo = nombreDeEstiloParrafo;
    }

    public Parrafo obtenerCopiaRecursiva() {
        Parrafo pReturn = new Parrafo();

        for (int i = 0; i < this.textosSpan.size(); i++) {
            pReturn.getTextosSpan().add(this.textosSpan.get(i));
        }

        for (int i = 0; i < this.nombresDeEstilosTextosSpan.size(); i++) {
            pReturn.getNombresDeEstilosTextosSpan().add(this.nombresDeEstilosTextosSpan.get(i));
        }

        pReturn.nombreDeEstiloParrafo = this.getNombreDeEstiloParrafo();

        return pReturn;
    }
}
