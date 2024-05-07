import org.odftoolkit.odfdom.pkg.OdfElement;

import java.util.ArrayList;

public class Pregunta {
    private OdfElement contenido;

    private ArrayList<Respuesta> respuestasDePregunta;

    public OdfElement getContenido() {
        return contenido;
    }

    public void setContenido(OdfElement contenido) {
        this.contenido = contenido;
    }

    public ArrayList<Respuesta> getRespuestasDePregunta() {
        return respuestasDePregunta;
    }

    public void setRespuestasDePregunta(ArrayList<Respuesta> respuestasDePregunta) {
        this.respuestasDePregunta = respuestasDePregunta;
    }
}
