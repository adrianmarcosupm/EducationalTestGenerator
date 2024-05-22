import java.util.ArrayList;

public class Parrafo {
    private ArrayList<String> textosSpan;
    private ArrayList<String> nombresDeEstilosTextosSpan;
    private String textoDeParrafo;
    private String nombreDeEstiloParrafo;

    public Parrafo() {
        this.textosSpan = new ArrayList<>();
        this.nombresDeEstilosTextosSpan = new ArrayList<>();
        this.textoDeParrafo = "";
        this.nombreDeEstiloParrafo = "";
    }

    public Parrafo(ArrayList<String> textosSpan, ArrayList<String> nombresDeEstilosTextosSpan, String textoDeParrafo, String nombreDeEstiloParrafo) {
        this.textosSpan = textosSpan;
        this.nombresDeEstilosTextosSpan = nombresDeEstilosTextosSpan;
        this.textoDeParrafo = textoDeParrafo;
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

    public String getTextoDeParrafo() {
        return textoDeParrafo;
    }

    public void setTextoDeParrafo(String textoDeParrafo) {
        this.textoDeParrafo = textoDeParrafo;
    }

    //Obtiene el texto total del parrafo, porque a veces no hay spans o no hay texto de parrafo.
    public String getTextoTotal() {
        if (this.textoDeParrafo != "") {
            return this.textoDeParrafo;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String t : this.textosSpan) {
                sb.append(t);
            }
            return sb.toString();
        }
    }

    public Parrafo obtenerCopiaRecursiva() {
        Parrafo pReturn = new Parrafo();

        for (int i = 0; i < this.textosSpan.size(); i++) {
            pReturn.getTextosSpan().add(this.textosSpan.get(i));
        }

        for (int i = 0; i < this.nombresDeEstilosTextosSpan.size(); i++) {
            pReturn.getNombresDeEstilosTextosSpan().add(this.nombresDeEstilosTextosSpan.get(i));
        }

        pReturn.textoDeParrafo = this.textoDeParrafo;

        pReturn.nombreDeEstiloParrafo = this.nombreDeEstiloParrafo;

        return pReturn;
    }
}
