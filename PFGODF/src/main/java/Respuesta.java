import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;

public class Respuesta {
    private String texto;
    private String nombreDeEstilo;
    private OdfStyle estilo;

    public Respuesta(String texto, String nombreDeEstilo, OdfStyle estilo) {
        this.texto = texto;
        this.nombreDeEstilo = nombreDeEstilo;
        this.estilo = estilo;

    }

    public Respuesta() {
        this.texto = "";
        this.nombreDeEstilo = "";
        this.estilo = null;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getNombreDeEstilo() {
        return nombreDeEstilo;
    }

    public void setNombreDeEstilo(String nombreDeEstilo) {
        this.nombreDeEstilo = nombreDeEstilo;
    }

    public OdfStyle getEstilo() {
        return estilo;
    }

    public void setEstilo(OdfStyle estilo) {
        this.estilo = estilo;
    }

    public Respuesta obtenerCopiaRecursiva() {
        Respuesta resp = new Respuesta(this.getTexto(), this.getNombreDeEstilo(), this.getEstilo());
        return resp;
    }
}
