import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class LectorEscritorDeOdt {

    OdfTextDocument documentoOdt;

    // Los tags se envian como regex en algunos métodos, así que hay que utilizar caracteres de escape.
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
        // Borramos las barras de las etiquetas
        String tagPNoRegex = tagPregunta.replace("\\", "");
        String tagRNoRegex = tagRespuestas.replace("\\", "");

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
            NodeList nodeTextPList2 = documentoOdt.getContentRoot().getElementsByTagName("text:span");
            for (OdfStyle s : documentoOdt.getStylesDom().getAutomaticStyles().getAllStyles()){
//                System.out.println(s.toString());
            }
            for (OdfStyle s : documentoOdt.getStylesDom().getOfficeStyles().getAllStyles()){
//                System.out.println(s.toString());
            }
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
                        // Eliminamos caracteres en blanco por delante y por detrás de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if (lineaLeida.equals(tagPNoRegex)) {
                            exito = true;
                            sigueBuscando = false;
                        } else if (lineaLeida.equals(tagRNoRegex)) {
                            logger.warn("El documento comienza con respuestas que no están asignadas a ninguna pregunta.");
                        }
                    }
                }
                if (!exito) {
                    return null;
                }
                ////////////////////////////////////
                // Buscamos el texto de la pregunta
                nodo = nodeTextPList.item(indexNodo);
                if (nodo instanceof OdfTextParagraph) {
                    NodeList nl = nodo.getChildNodes();
                    for (int i = 0; i < nl.getLength(); i++) {
                        System.out.println(nl.item(i).getLocalName());
                        System.out.println(nl.item(i).getNodeName());
                        System.out.println(nl.item(i).getNodeType());
                        System.out.println(nl.item(i).getNodeValue());
                        System.out.println(nl.item(i).getTextContent());
                        if (nl.item(i).getAttributes() != null)
                        {
                            System.out.println(nl.item(i).getAttributes().getNamedItem("text:style-name").getNodeValue());
                        }

                        System.out.println("----");
                    }
                }
                exito = false;
                sigueBuscando = true;
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;
                        // Eliminamos caracteres en blanco por delante y por detrás de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if (!lineaLeida.equals("")) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPNoRegex)) {
                                if (textoEncontrado) {
                                    logger.error("Pregunta sin respuestas: " + preguntaTemp.getTextos().get(0));
                                    exito = false;
                                    sigueBuscando = false;
                                } else {
                                    logger.warn("Varias etiquetas PREGUNTA seguidas");
                                }
                            } else if (lineaLeida.equals(tagRNoRegex)) {
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
                                preguntaTemp.getNombreDeEstilos().add(parrafo.getTextStyleNameAttribute());
                                preguntaTemp.getEstilos().add(parrafo.getOrCreateAutomaticStyles().getStyle(parrafo.getTextStyleNameAttribute(),OdfStyleFamily.Text));

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
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;
                        // Eliminamos caracteres en blanco por delante y por detrás de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if (!lineaLeida.equals("")) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPNoRegex)) {
                                if (!textoEncontrado) {
                                    logger.error("Pregunta sin respuestas: " + preguntaTemp.getTextos().get(0));
                                }
                                sigueBuscando = false;
                            } else if (lineaLeida.equals(tagRNoRegex)) {
                                if (!textoEncontrado) {
                                    logger.warn("Varias etiquetas RESPUESTAS seguidas");
                                } else {
                                    logger.warn("Etiqueta RESPUESTAS dentro de las respuestas de la pregunta: " + preguntaTemp.getTextos().get(0));
                                }
                            } else {
                                respuestaTemp = new Respuesta(lineaLeida, parrafo.getStyleName(), parrafo.getAutomaticStyle());
                                preguntaTemp.getRespuestasDePregunta().add(respuestaTemp);
                                textoEncontrado = true;
                                exito = true;
                            }
                        }
                    }
                }
                if (!exito) {
                    return null;
                }

                contadorDePreguntas++;

                // Si la pregunta encontrada esta en la lista de las que queremos, la añadimos
                if (numerosDePregunta.contains(contadorDePreguntas)) {
                    int contadorDeRespuestas = 1; //TODO: Para debugear
                    preguntasReturn.add(preguntaTemp);
                    logger.debug("Pregunta añadida " + contadorDePreguntas + ": " + preguntaTemp.getTextos().get(0));
                    for (Respuesta r : preguntaTemp.getRespuestasDePregunta()) {
                        logger.debug("Añadida respuesta " + contadorDeRespuestas + ": " + r.getTexto());
                        contadorDeRespuestas++; //TODO: para debug
                    }

                }

                // Si no hemos encotrado la ultima pregunta, seguimos buscando
                if (!Collections.max(numerosDePregunta).equals(contadorDePreguntas)) {
                    sigueBuscando = true;
                    textoEncontrado = false;
                    exito = false;
                    // Hemos encontrado un tag de pregunta asi que lo volvemos a buscar para comenzar la busqueda
                    if (indexNodo != nodeTextPList.getLength()) {
                        indexNodo--;
                    }
                }
            }
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

    public void guardarExamen(Examen e) {

        OdfTextDocument documentoOdt;
        File documentoExamen;
        OdfContentDom dom;
        try {
            documentoOdt = (OdfTextDocument) OdfTextDocument.loadDocument(fileArchivoALeerOdt);
            dom = documentoOdt.getContentDom();
            // Reemplazamos el tag de la version
            TextNavigation search = new TextNavigation(tagVersion, documentoOdt);
            while (search.hasNext()) {
                TextSelection selection = search.next();
                selection.replaceWith(e.getVersion());
            }
            // Añadimos las preguntas
            for (Pregunta p : e.getPreguntas()) {
                for (int i = 0; i < p.getTextos().size(); i++) {
//                    OdfTextParagraph par = new OdfTextParagraph(dom, p.getNombreDeEstilos().get(i),p.getTextos().get(i));
                    StyleStyleElement targetStyle = documentoOdt.getStyleByName( OdfStyleFamily.Text,p.getNombreDeEstilos().get(i));
                    if (targetStyle != null) {
                        logger.debug(targetStyle.toString());
                    }

                    if (targetStyle == null) {

                        // Crear el nuevo estilo en el documento destino si no existe
                        targetStyle = documentoOdt.getOrCreateDocumentStyles().newStyle(p.getNombreDeEstilos().get(i), OdfStyleFamily.Text);

                        // Copiar propiedades del estilo fuente al estilo destino
                        targetStyle.setProperties(documentoOdt.getStyleByName( OdfStyleFamily.Text,p.getNombreDeEstilos().get(i)).getStylePropertiesDeep());
                    }
                    OdfTextParagraph par2 = documentoOdt.newParagraph(p.getTextos().get(i));

                    par2.setStyleName(p.getNombreDeEstilos().get(i));
//                    dom.getRootElement().appendChild(par);
//                    documentoOdt.addText(t); funciona pero todo en el mismo parrafo
//                    documentoOdt.newParagraph().addContent(t); funciona
                }



                for (Respuesta r : p.getRespuestasDePregunta()) {
//                    OdfTextParagraph par = new OdfTextParagraph(dom, r.getNombreDeEstilo(),r.getTexto());
                    OdfStyle targetStyle = documentoOdt.getOrCreateDocumentStyles().getStyle(r.getNombreDeEstilo(), r.getEstilo().getFamily());
                    if (targetStyle == null) {
                        // Crear el nuevo estilo en el documento destino si no existe
                        targetStyle = documentoOdt.getOrCreateDocumentStyles().newStyle(r.getNombreDeEstilo(), r.getEstilo().getFamily());

                        // Copiar propiedades del estilo fuente al estilo destino
                        targetStyle.setProperties(r.getEstilo().getStylePropertiesDeep());
                    }
                    OdfTextParagraph par2 = documentoOdt.newParagraph(r.getTexto());
                    par2.setStyleName(r.getNombreDeEstilo());
//                    dom.getRootElement().appendChild(par);
//                    documentoOdt.addText(t); funciona pero todo en el mismo parrafo
//                    documentoOdt.newParagraph().addContent(t); funciona

                }


//                documentoOdt.insertDocument(documentoOdt, documentoOdt.getDocumentPath());
                //            documentoOdt.insertDocument();
            }
            // Creamos los directorios para el archivo de salida
            pathDirectorioDeSalida.toFile().mkdirs();
            // Creamos el archivo del examen
            documentoExamen = new File(pathDirectorioDeSalida.resolve("examen_version_" + e.getVersion() + ".odt").toString());
            documentoOdt.save(documentoExamen);

            logger.info("Examen guardado: Version " + e.getVersion() + " en: " + documentoExamen);
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
