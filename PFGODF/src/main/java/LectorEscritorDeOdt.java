import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;


public class LectorEscritorDeOdt {

    OdfTextDocument documentoOdt;

    // Los tags se envian como regex, así que hay que utilizar caracteres de escape.
    private final String tagVersion = "\\{\\{\\¡VERSION\\¡\\}\\}";
    private final String tagPregunta = "\\{\\{\\¡PREGUNTA\\¡\\}\\}";
    private final String tagRespuestas = "\\{\\{\\¡RESPUESTAS\\¡\\}\\}";
    File fileArchivoALeerOdt;
    Path pathDirectorioDeSalida;

    private static final Logger logger = LogManager.getLogger();
    private static final String lineaDeGuiones = "------------------------------------------------------";

    // Constructor
    public LectorEscritorDeOdt(File archivoALeer, Path directorioAEscribir) {
        this.fileArchivoALeerOdt = archivoALeer;
        this.pathDirectorioDeSalida = directorioAEscribir;

        try {
            documentoOdt = OdfTextDocument.loadDocument(fileArchivoALeerOdt);
            logger.debug("Archivo leido: " + fileArchivoALeerOdt);
        } catch (Exception ex) {
            logger.error("Error al leer el archivo " + archivoALeer + ".odt. " + ex.getMessage());
        }
    }

    public ArrayList<Pregunta> obtenerPreguntas(ArrayList<Integer> numerosDePregunta) {
        Pregunta preguntaTemp = new Pregunta();
        String estilo;
        OdfElement parrafo;
        try {
            int contador = 1;
            // Buscamos el tag de pregunta el numero de veces
            TextNavigation search = new TextNavigation(tagPregunta, documentoOdt);

            while (search.hasNext() && contador < numeroDePregunta) {
                // Marcamos el texto como encontrado
                search.next();
                contador++;
            }
            // El siguiente parrafo le copiamos
            if (search.hasNext()) {
//                search.next().getIndex()
//                parrafo = search.next().getContainerElement().getComponent().getChildren().get(0);
//                preguntaTemp.setContenido(parrafo);
                OdfTextDocument nd = OdfTextDocument.loadDocument("plantilla2.odt");
                OdfTextParagraph sourceParagraph = null;
                NodeList nodeList = documentoOdt.getContentRoot().getElementsByTagName("text:p");
                int countt = 0;
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node instanceof OdfTextParagraph) {
                        OdfTextParagraph para = (OdfTextParagraph) node;
                        if (para.getTextContent().contains("PREGUNTA")) {
                            // Hemos encontrado el tag, copiar los siguientes parrafos hasta encontrar tag de respuestas
                            for (int i2 = i + 1; i2 < nodeList.getLength(); i2++) {
                                if (!nodeList.item(i2).getTextContent().trim().equals("")) {
                                    System.out.println("PREGUNTA " + countt);
                                    System.out.println("-----------------------------------------------");
                                    System.out.println(nodeList.item(i2).getTextContent());
                                    System.out.println("-----------------------------------------------");
                                    countt++;
                                    break;
                                }

                            }

                        }
                    }
                }
                if (sourceParagraph != null) {
                    OdfTextParagraph destParagraph = nd.newParagraph();
                    destParagraph.setTextContent(sourceParagraph.getTextContent());
                }
            }
            logger.debug("Pregunta encontrada: " + numeroDePregunta);
        } catch (Exception ex) {
            logger.error("Error obteniendo número de preguntas: " + ex.getMessage());
        }

        return preguntaTemp;
    }

    // Para saber cuantas preguntas hay en el banco
    public int obtenerNumPreguntas() {

        int numPreguntas = 0;

        try {
            documentoOdt = OdfTextDocument.loadDocument(fileArchivoALeerOdt);

            // Buscamos el numero de veces que aparece el tag de pregunta
            TextNavigation search = new TextNavigation(tagPregunta, documentoOdt);
            while (search.hasNext()) {
                // Marcamos el texto como encontrado
                search.next();
                numPreguntas++;
            }
            logger.debug("Número de preguntas: " + numPreguntas);
        } catch (Exception ex) {
            logger.error("Error obteniendo número de preguntas: " + ex.getMessage());
        }

        return numPreguntas;
    }

    public void guardarExamen(String version, ArrayList<Pregunta> preguntas, ArrayList<Respuesta> respuestas) {

        OdfTextDocument documentoOdt;
        File documentoExamen;
        try {
            documentoOdt = OdfTextDocument.loadDocument(fileArchivoALeerOdt);

            // Reemplazamos el tag de la version
            TextNavigation search = new TextNavigation(tagVersion, documentoOdt);
            while (search.hasNext()) {
                TextSelection selection = search.next();
                selection.replaceWith(version);
            }
            // Añadimos las preguntas
            for (Pregunta p : preguntas) {

            }
            // Creamos los directorios para el archivo de salida
            pathDirectorioDeSalida.toFile().mkdirs();
            // Creamos el examen
            documentoExamen = new File(pathDirectorioDeSalida.resolve("examen_version_" + version + ".odt").toString());
            documentoOdt.save(documentoExamen);
//            documentoOdt.insertDocument();
            logger.info("Examen guardado: Version " + version + " en: " + documentoExamen);
        } catch (Exception ex) {
            logger.error("Error guardando examen: " + ex.getMessage());
        }

        // Para añadir contenido a otro odt:
//        public static void main (String[]args){
//            FusionarDocumentos fusionador = new FusionarDocumentos();
//            File archivoASerFusionado = new File("ruta/al/archivo/a/fusionar.odt");
//            fusionador.fusionarOdt(archivoASerFusionado);
//        }
//
//        void fusionarOdt (File archivoASerFusionado){
//            TextDocument maestro = TextDocument.loadDocument(archivoMaestro);
//            TextDocument esclavo = TextDocument.loadDocument(archivoASerFusionado);
//            maestro.insertContentFromDocumentAfter(esclavo, maestro.getParagraphByReverseIndex(0, false), true);
//            maestro.save(archivoMaestro);
//        }
    }


}
