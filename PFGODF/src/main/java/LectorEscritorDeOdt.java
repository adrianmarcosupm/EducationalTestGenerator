import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
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

        ArrayList<Pregunta> preguntasReturn = new ArrayList<>(); // La lista de preguntas que devolvemos al generador
        Pregunta preguntaTemp = null; // Guardamos la pregunta que encontramos para añadirla a la lista de preguntas
        Respuesta respuestaTemp = null; // Guardamos la respuesta que encontramos para añadirla a la lista de respuestas

        boolean sigueBuscando = true; // Para salir de los bucles
        boolean textoEncontrado = false; // Para elegir caminos diferentes en los bucles
        boolean exito = false; // Para continuar con la funcion de obtener pregunta

        Node nodo; // El nodo XML
        OdfTextParagraph parrafo; // Tratamos el nodo como un parrafo
        int indexNodo = 0; // Numero de nodo por el que empieza la busqueda
        String lineaLeida = ""; // La linea del documento .odt que leemos
        int contadorDePreguntas = 0; // Para numerar las preguntas del banco

        try {
            documentoOdt = OdfTextDocument.loadDocument(fileArchivoALeerOdt);
            NodeList nodeTextPList = documentoOdt.getContentRoot().getElementsByTagName("text:p"); // Buscamos nodos XML con esta etiqueta
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Recorremos todos los nodos del documento hasta que no queden más o hasta que encontremos las preguntas
            // que hay en numerosDePregunta
            while ((indexNodo < nodeTextPList.getLength()) && !exito) {
                /////////////////////////////////////////////////////////////
                // Recorremos todos los nodos text:p para buscar TAG PREGUNTA
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;
                        lineaLeida = parrafo.getTextContent().trim();
                        if (lineaLeida.equals(tagPregunta)) {
                            exito = true;
                            sigueBuscando = false;
                        } else if (lineaLeida.equals(tagRespuestas)) {
                            logger.warn("El documento comienza con respuestas que no están asignadas a ninguna pregunta.");
                        }
                    }
                }
                if (!exito) {
                    return null;
                }
                ////////////////////////////////////
                // Buscamos el texto de la pregunta
                exito = false;
                sigueBuscando = true;
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;
                        lineaLeida = parrafo.getTextContent().trim();
                        if (!lineaLeida.equals("")) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPregunta)) {
                                if (textoEncontrado) {
                                    logger.error("Pregunta sin respuestas: " + preguntaTemp.getTextos().get(0));
                                    exito = false;
                                    sigueBuscando = false;
                                } else {
                                    logger.warn("Varias etiquetas PREGUNTA seguidas");
                                }
                            } else if (lineaLeida.equals(tagRespuestas)) {
                                ////////////////////////////////
                                // Tag de RESPUESTAS encontrado
                                if (!textoEncontrado) {
                                    logger.error("No se ha encontrado texto para una pregunta.");
                                }
                                sigueBuscando = false;
                            } else {
                                if (textoEncontrado == false) {
                                    preguntaTemp = new Pregunta();
                                }
                                preguntaTemp.getTextos().add(lineaLeida);
                                preguntaTemp.getNombreDeEstilos().add(parrafo.getStyleName());
                                textoEncontrado = true;
                                exito = true;
                            }
                        }
                    }
                }
                if (!exito) {
                    return null;
                }
                ////////////////////////////////////
                // Buscamos texto de las respuestas
                exito = false;
                sigueBuscando = true;
                textoEncontrado = false;
                int contadorDeRespuestas = 0; //TODO: Para debugear
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;
                        lineaLeida = parrafo.getTextContent().trim();
                        if (!lineaLeida.equals("")) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPregunta)) {
                                if (!textoEncontrado) {
                                    logger.error("Pregunta sin respuestas: " + preguntaTemp.getTextos().get(0));
                                }
                                sigueBuscando = false;
                            } else if (lineaLeida.equals(tagRespuestas)) {
                                if (!textoEncontrado) {
                                    logger.warn("Varias etiquetas RESPUESTAS seguidas");
                                } else {
                                    logger.warn("Etiqueta RESPUESTAS dentro de las respuestas de la pregunta: " + preguntaTemp.getTextos().get(0));
                                }
                            } else {
                                contadorDeRespuestas++; //TODO: para debug
                                respuestaTemp = new Respuesta(lineaLeida, parrafo.getStyleName());
                                preguntaTemp.getRespuestasDePregunta().add(respuestaTemp);
                                logger.debug("Añadida respuesta " + contadorDeRespuestas + ": " + respuestaTemp.getTexto());
                                textoEncontrado = true;
                                exito = true;
                            }
                        }
                    }
                }
                if (!exito) {
                    return null;
                }

                // Si hemos encotrado la ultima respuesta (vienen en orden de menor a mayor)
                // Enviamos las preguntas al generador y no seguimos buscando
                if (numerosDePregunta.getLast().equals(contadorDePreguntas)) {
                    exito = true;
                } else {
                    sigueBuscando = true;
                    textoEncontrado = false;
                    exito = false;
                }
            }
//            return preguntasReturn;


            // Buscamos el tag de pregunta el numero de veces
//            TextNavigation search = new TextNavigation(tagPregunta, documentoOdt);

//            while (search.hasNext() && contador < numeroDePregunta) {
//                // Marcamos el texto como encontrado
//                search.next();
//                contador++;
//            }
            // El siguiente parrafo le copiamos
//            if (search.hasNext()) {
//                search.next().getIndex()
//                parrafo = search.next().getContainerElement().getComponent().getChildren().get(0);
//                preguntaTemp.setContenido(parrafo);


//            do {
//                pregunta = obtenerPregunta(0, nodeTextPList, numerosDePregunta);
//                preguntasReturn.add(pregunta);
//                //TODO: para debug
//                countP++;
//                logger.debug("PREGUNTA " + countP);
//                logger.debug(lineaDeGuiones);
//                logger.debug("estilo: " + pregunta.getNombreDeEstilo());
//                logger.debug("texto: " + pregunta.getTexto());
//                logger.debug(lineaDeGuiones);
//            } while (pregunta != null);
//
//
//            if (!exito)
//                preguntasReturn = null;
////                if (sourceParagraph != null) {
////                    OdfTextParagraph destParagraph = nd.newParagraph();
////                    destParagraph.setTextContent(sourceParagraph.getTextContent());
////                }
////            }
////            logger.debug("Pregunta encontrada: " + numeroDePregunta);
        } catch (Exception ex) {
            logger.error("Error obteniendo preguntas: " + ex.getMessage());
            preguntasReturn = null;
        }

        return preguntasReturn;
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

    public void guardarExamen(String version, ArrayList<Pregunta> preguntas) {

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
                // TODO: añadir preguntas traidas ya mezcladas desde la clase Generador
                //            documentoOdt.insertDocument();
            }
            // Creamos los directorios para el archivo de salida
            pathDirectorioDeSalida.toFile().mkdirs();
            // Creamos el archivo del examen
            documentoExamen = new File(pathDirectorioDeSalida.resolve("examen_version_" + version + ".odt").toString());
            documentoOdt.save(documentoExamen);

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
