import java.util.ArrayList;

public class Parrafo {
    private ArrayList<String> textosSpan;
    private ArrayList<String> nombresDeEstilosTextosSpan;
    private String textoDeParrafo;
    private String nombreDeEstiloParrafo;
    private String imagenRuta;
    private String imagenAncho;
    private String imagenAlto;

    public Parrafo() {
        this.textosSpan = new ArrayList<>();
        this.nombresDeEstilosTextosSpan = new ArrayList<>();
        this.textoDeParrafo = "";
        this.nombreDeEstiloParrafo = "";
        this.imagenRuta = "";
        this.imagenAncho = "";
        this.imagenAlto = "";
    }

    public Parrafo(ArrayList<String> textosSpan, ArrayList<String> nombresDeEstilosTextosSpan, String textoDeParrafo, String nombreDeEstiloParrafo, String imagenRuta, String imagenAncho, String imagenAlto) {
        this.textosSpan = textosSpan;
        this.nombresDeEstilosTextosSpan = nombresDeEstilosTextosSpan;
        this.textoDeParrafo = textoDeParrafo;
        this.nombreDeEstiloParrafo = nombreDeEstiloParrafo;
        this.imagenRuta = imagenRuta;
        this.imagenAncho = imagenAncho;
        this.imagenAlto = imagenAlto;
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

    public String getImagenRuta() {
        return imagenRuta;
    }

    public void setImagenRuta(String imagenRuta) {
        this.imagenRuta = imagenRuta;
    }

    public String getImagenAncho() {
        return imagenAncho;
    }

    public void setImagenAncho(String imagenAncho) {
        this.imagenAncho = imagenAncho;
    }

    public String getImagenAlto() {
        return imagenAlto;
    }

    public void setImagenAlto(String imagenAlto) {
        this.imagenAlto = imagenAlto;
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

    // Obtiene una copia del objeto
    public Parrafo obtenerCopiaRecursiva() {
        Parrafo pReturn = new Parrafo();

        for (int i = 0; i < this.textosSpan.size(); i++) {
            pReturn.getTextosSpan().add(this.textosSpan.get(i));
        }

        for (int i = 0; i < this.nombresDeEstilosTextosSpan.size(); i++) {
            pReturn.getNombresDeEstilosTextosSpan().add(this.nombresDeEstilosTextosSpan.get(i));
        }

        pReturn.setTextoDeParrafo(this.textoDeParrafo);

        pReturn.setNombreDeEstiloParrafo(this.nombreDeEstiloParrafo);

        pReturn.setImagenRuta(this.imagenRuta);
        pReturn.setImagenAncho(this.imagenAncho);
        pReturn.setImagenAlto(this.imagenAlto);

        return pReturn;
    }
}
