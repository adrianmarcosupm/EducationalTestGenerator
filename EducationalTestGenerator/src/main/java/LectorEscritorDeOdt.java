
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static org.fusesource.jansi.Ansi.ansi;


public class LectorEscritorDeOdt {
    private int nivelDeLog = 3;
    OdfTextDocument documentoOdtBanco;
    OdfTextDocument documentoOdtCabecera;

    // Los tags se envian como regex en algunos metodos, asi que hay que utilizar caracteres de escape.
    private final String tagVersion = "\\{\\{\\¡VERSION\\¡\\}\\}";
    private final String tagPregunta = "\\{\\{\\¡PREGUNTA\\¡\\}\\}";
    private final String tagRespuestas = "\\{\\{\\¡RESPUESTAS\\¡\\}\\}";
    private final String tagMetadatos = "\\{\\{\\¡METADATOS\\¡\\}\\}";
    File fileBanco;
    File fileCabecera;
    Path pathDirectorioDeSalida;


    /* Adaptaciones especiales */
    private boolean dificultadAdaptada;
    private int dificultadMinima;
    private int dificultadMaxima;
    private boolean tamanioDeLetraAdaptadoSiNo;
    private int tamanioDeLetraAdaptado;
    private int tamanioMinimoDeLetra;


    // Constructor
    public LectorEscritorDeOdt(File banco, File cabecera, Path directorioAEscribir, int nivelDeLog) throws Exception {
        this.nivelDeLog = nivelDeLog;
        this.fileBanco = banco;
        this.fileCabecera = cabecera;
        this.pathDirectorioDeSalida = directorioAEscribir;

        try {
            documentoOdtBanco = OdfTextDocument.loadDocument(fileBanco);
            if (nivelDeLog >= 4) {
                System.out.println(ansi().render("@|cyan " + "Archivo leido: " + fileBanco + "|@"));
            }
        } catch (Exception ex) {
            throw new Exception("Error al leer el banco de preguntas. " + ex.getMessage());
        }

    }

    public void setDificultadAdaptada(boolean dificultadAdaptada) {
        this.dificultadAdaptada = dificultadAdaptada;
    }

    public void setDificultadMinima(int dificultadMinima) {
        this.dificultadMinima = dificultadMinima;
    }

    public void setDificultadMaxima(int dificultadMaxima) {
        this.dificultadMaxima = dificultadMaxima;
    }

    public void setTamanioDeLetraAdaptadoSiNo(boolean tamanioDeLetraAdaptadoSiNo) {
        this.tamanioDeLetraAdaptadoSiNo = tamanioDeLetraAdaptadoSiNo;
    }

    public void setTamanioDeLetraAdaptado(int tamanioDeLetraAdaptado) {
        this.tamanioDeLetraAdaptado = tamanioDeLetraAdaptado;
    }

    public void setTamanioMinimoDeLetra(int tamanioMinimoDeLetra) {
        this.tamanioMinimoDeLetra = tamanioMinimoDeLetra;
    }

    // Devuelve true si un nodo tiene un nodo imagen buscado recursivamente
    private boolean tieneImagenes(Node nodo, int nivel) {
        //para evitar bucles
        if (nivel > 49) {
            return false;
        }
        nivel++;

        //las imagenes tienen que estar dentro de un frame.
        //un frame puede estar en un parrafo, o dentro de un span de un parrafo.
        NodeList nodosHijos = nodo.getChildNodes();
        for (int indexNodoHijo = 0; indexNodoHijo < nodosHijos.getLength(); indexNodoHijo++) {
            if (nodosHijos.item(indexNodoHijo) instanceof OdfDrawFrame) {
                return tieneImagenes(nodosHijos.item(indexNodoHijo), nivel);
            } else if (nodosHijos.item(indexNodoHijo) instanceof OdfTextSpan) {
                NodeList nodosHijosDeSpan = nodosHijos.item(indexNodoHijo).getChildNodes();
                for (int indexNodoHijoDeSpan = 0; indexNodoHijoDeSpan < nodosHijosDeSpan.getLength(); indexNodoHijoDeSpan++) {
                    if (nodosHijosDeSpan.item(indexNodoHijoDeSpan) instanceof OdfDrawFrame) {
                        return tieneImagenes(nodosHijosDeSpan.item(indexNodoHijoDeSpan), nivel);
                    }
                }
            } else if (nodosHijos.item(indexNodoHijo) instanceof OdfDrawImage) {
                return true;
            }
        }
        return false;
    }

    // Pone los atributos de imagen del parrafo segun los atributos imagen y frame del nodo
    private void buscarEInsertarFrameConImagen(Node nodoDelFrame, Parrafo parrafo) {
        //si este frame tiene imagenes
        if (tieneImagenes(nodoDelFrame, 0)) {
            parrafo.setImagenAncho(nodoDelFrame.getAttributes().getNamedItem("svg:width").getTextContent());
            parrafo.setImagenAlto(nodoDelFrame.getAttributes().getNamedItem("svg:height").getTextContent());

            //anadimos las imagenes
            NodeList nodosHijosDelFrame = nodoDelFrame.getChildNodes();
            for (int indexNodoHijoDelFrame = 0; indexNodoHijoDelFrame < nodosHijosDelFrame.getLength(); indexNodoHijoDelFrame++) {
                if (nodosHijosDelFrame.item(indexNodoHijoDelFrame) instanceof OdfDrawImage) {
                    parrafo.setImagenRuta(nodosHijosDelFrame.item(indexNodoHijoDelFrame).getAttributes().getNamedItem("xlink:href").getTextContent());
                }
            }
        }
    }

    // Obtiene un HashMap de (temas, arraylist de preguntas). Cada tema contiene un arraylist con todas las preguntas de ese tema.
    public HashMap<Integer, ArrayList<Pregunta>> obtenerPreguntas(ArrayList<Integer> temas) {
        // Borramos las barras de las etiquetas
        String tagPNoRegex = tagPregunta.replace("\\", "");
        String tagRNoRegex = tagRespuestas.replace("\\", "");
        String tagMNoRegex = tagMetadatos.replace("\\", "");

        HashMap<Integer, ArrayList<Pregunta>> preguntasReturn = new HashMap<>(); // La lista de preguntas que devolvemos al generador
        for (Integer t : temas) {
            preguntasReturn.put(t, new ArrayList<>());
        }
        Pregunta preguntaTemp = null; // Guardamos la pregunta que encontramos para anadirla a la lista de preguntas

        boolean sigueBuscando = true; // Para salir de los bucles
        boolean textoEncontrado = false; // Para elegir caminos diferentes en los bucles
        boolean exito = false; // Para continuar con la funcion de obtener pregunta

        Node nodo; // El nodo XML
        OdfTextParagraph parrafo; // Tratamos el nodo como un parrafo
        int indexNodo = 0; // Numero de nodo por el que empieza la busqueda
        String lineaLeida = ""; // La linea del documento .odt que leemos

        try {
            documentoOdtBanco = OdfTextDocument.loadDocument(fileBanco);
            NodeList nodeTextPList = documentoOdtBanco.getContentRoot().getElementsByTagName("text:p"); // Buscamos nodos XML con esta etiqueta
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Recorremos todos los nodos del documento hasta que no queden mas o hasta que encontremos las preguntas
            // que hay en numerosDePregunta
            while ((indexNodo < nodeTextPList.getLength()) && !exito) {
                /////////////////////////////////////////////////////////////
                // Recorremos todos los nodos text:p para buscar TAG PREGUNTA
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;
                        // Eliminamos caracteres en blanco por delante y por detras de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if (lineaLeida.equals(tagPNoRegex)) {
                            exito = true;
                            sigueBuscando = false;
                        } else if (lineaLeida.equals(tagRNoRegex)) {
                            if (nivelDeLog >= 2) {
                                System.out.println(ansi().render("@|yellow " + "El documento comienza con respuestas que no estan asignadas a ninguna pregunta." + "|@"));
                            }
                        } else if (lineaLeida.equals(tagMNoRegex)) {
                            if (nivelDeLog >= 2) {
                                System.out.println(ansi().render("@|yellow " + "El documento comienza con metadatos que no estan asignados a ninguna pregunta." + "|@"));
                            }
                        }
                    }
                }
                if (!exito) {
                    return null;
                }
                ////////////////////////////////////
                // Buscamos el texto de la pregunta
                nodo = nodeTextPList.item(indexNodo);
                exito = false;
                sigueBuscando = true;
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;

                        // Eliminamos caracteres en blanco por delante y por detras de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if ((!lineaLeida.equals("")) || tieneImagenes(nodo, 0)) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPNoRegex)) {
                                if (textoEncontrado) {
                                    System.out.println(ansi().render("@|cyan " + "Pregunta sin respuestas: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                    exito = false;
                                    sigueBuscando = false;
                                } else {
                                    if (nivelDeLog >= 2) {
                                        System.out.println(ansi().render("@|yellow " + "Varias etiquetas PREGUNTA seguidas" + "|@"));
                                    }
                                }
                            } else if (lineaLeida.equals(tagRNoRegex)) {
                                ////////////////////////////////
                                // Tag de RESPUESTAS encontrado
                                if (!textoEncontrado) {
                                    System.out.println(ansi().render("@|red " + "No se ha encontrado texto para una pregunta." + "|@"));
                                }
                                sigueBuscando = false;
                            } else if (lineaLeida.equals(tagMNoRegex)) {
                                if (textoEncontrado) {
                                    System.out.println(ansi().render("@|red " + "El orden de las etiquetas debe ser: PREGUNTA, RESPUESTAS, METADATOS. Orden incorrecto en la pregunta: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                    exito = false;
                                } else {
                                    System.out.println(ansi().render("@|red " + "No se ha encontrado texto para una pregunta." + "|@"));
                                }
                                sigueBuscando = false;
                            } else {
                                if (textoEncontrado == false) {
                                    preguntaTemp = new Pregunta();
                                }
                                // Anadimos un nuevo parrafo
                                Parrafo parrafoTemp = new Parrafo();
                                preguntaTemp.getParrafos().add(parrafoTemp);
                                // Buscamos si tiene span o frames
                                NodeList nodosHijos = nodo.getChildNodes();
                                for (int i = 0; i < nodosHijos.getLength(); i++) {
                                    if (nodosHijos.item(i) instanceof OdfDrawFrame) {
                                        buscarEInsertarFrameConImagen(nodosHijos.item(i), parrafoTemp);
                                    } else if (nodosHijos.item(i) instanceof OdfTextSpan) {
                                        //Buscamos si tiene imagenes el span
                                        NodeList nodosHijosDeSpan = nodosHijos.item(i).getChildNodes();
                                        for (int indexNodoHijoDeSpan = 0; indexNodoHijoDeSpan < nodosHijosDeSpan.getLength(); indexNodoHijoDeSpan++) {
                                            if (nodosHijosDeSpan.item(indexNodoHijoDeSpan) instanceof OdfDrawFrame) {
                                                buscarEInsertarFrameConImagen(nodosHijosDeSpan.item(indexNodoHijoDeSpan), parrafoTemp);
                                            }
                                        }

                                        // Guardamos todos los text span del parrafo para guardar sus estilos
                                        OdfTextSpan ts = (OdfTextSpan) nodosHijos.item(i);
                                        preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).getTextosSpan().add(ts.getTextContent());
                                        preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).getNombresDeEstilosTextosSpan().
                                                add(ts.getAttributes().getNamedItem("text:style-name").getNodeValue());
                                    }
                                }
                                //si no tiene nodos textspan anadimos el texto del parrafo
                                if (preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).getTextosSpan().size() == 0) {
                                    preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).setTextoDeParrafo(parrafo.getTextContent());
                                }

                                //Guardamos el estilo del parrafo
                                preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).setNombreDeEstiloParrafo(parrafo.getStyleName());

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
                        // Eliminamos caracteres en blanco por delante y por detras de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if ((!lineaLeida.equals("")) || tieneImagenes(nodo, 0)) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPNoRegex)) {
                                if (!textoEncontrado) {
                                    System.out.println(ansi().render("@|red " + "Pregunta sin respuestas: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                } else {
                                    System.out.println(ansi().render("@|red " + "Pregunta sin metadatos: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                    exito = false;
                                }
                                sigueBuscando = false;
                            } else if (lineaLeida.equals(tagRNoRegex)) {
                                if (!textoEncontrado) {
                                    if (nivelDeLog >= 2) {
                                        System.out.println(ansi().render("@|yellow " + "Varias etiquetas RESPUESTAS seguidas" + "|@"));
                                    }
                                } else {
                                    if (nivelDeLog >= 2) {
                                        System.out.println(ansi().render("@|yellow " + "Etiqueta RESPUESTAS dentro de las respuestas de la pregunta: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                    }
                                }
                            } else if (lineaLeida.equals(tagMNoRegex)) {
                                ////////////////////////////
                                // Tag Metadatos encontrado
                                if (!textoEncontrado) {
                                    System.out.println(ansi().render("@|red " + "Pregunta sin respuestas: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                }
                                sigueBuscando = false;
                            } else {
                                // Anadimos un nuevo parrafo
                                Parrafo parrafoTemp = new Parrafo();
                                preguntaTemp.getRespuestasDePregunta().add(parrafoTemp);
                                // Buscamos si tiene span o frames
                                NodeList nodosHijos = nodo.getChildNodes();
                                for (int i = 0; i < nodosHijos.getLength(); i++) {
                                    if (nodosHijos.item(i) instanceof OdfDrawFrame) {
                                        buscarEInsertarFrameConImagen(nodosHijos.item(i), parrafoTemp);
                                    } else if (nodosHijos.item(i) instanceof OdfTextSpan) {
                                        //Buscamos si tiene imagenes el span
                                        NodeList nodosHijosDeSpan = nodosHijos.item(i).getChildNodes();
                                        for (int indexNodoHijoDeSpan = 0; indexNodoHijoDeSpan < nodosHijosDeSpan.getLength(); indexNodoHijoDeSpan++) {
                                            if (nodosHijosDeSpan.item(indexNodoHijoDeSpan) instanceof OdfDrawFrame) {
                                                buscarEInsertarFrameConImagen(nodosHijosDeSpan.item(indexNodoHijoDeSpan), parrafoTemp);
                                            }
                                        }

                                        // Guardamos todos los text span del parrafo para guardar sus estilos
                                        OdfTextSpan ts = (OdfTextSpan) nodosHijos.item(i);
                                        preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).getTextosSpan().add(ts.getTextContent());
                                        preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).getNombresDeEstilosTextosSpan().
                                                add(ts.getAttributes().getNamedItem("text:style-name").getNodeValue());
                                    }
                                }
                                //si no tiene nodos textspan anadimos el texto del parrafo
                                if (preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).getTextosSpan().size() == 0) {
                                    preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).setTextoDeParrafo(parrafo.getTextContent());
                                }
                                preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).setNombreDeEstiloParrafo(parrafo.getStyleName());

                                textoEncontrado = true;
                                exito = true;
                            }
                        }
                    }
                }
                if (!exito) {
                    return null;
                }
                //////////////////////////////////////
                // Buscamos el texto de los metadatos
                exito = false;
                sigueBuscando = true;
                textoEncontrado = false;
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;
                        // Eliminamos caracteres en blanco por delante y por detras de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if (!lineaLeida.equals("")) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPNoRegex)) {
                                ///////////////////////////
                                // Tag PREGUNTA encontrado
                                if (!textoEncontrado) {
                                    System.out.println(ansi().render("@|red " + "Pregunta sin metadatos: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                }
                                sigueBuscando = false;
                            } else if (lineaLeida.equals(tagRNoRegex)) {
                                System.out.println(ansi().render("@|red " + "El orden de las etiquetas debe ser: PREGUNTA, RESPUESTAS, METADATOS. Orden incorrecto en la pregunta: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                exito = false;
                                sigueBuscando = false;
                            } else if (lineaLeida.equals(tagMNoRegex)) {
                                ////////////////////////////
                                // Tag Metadatos encontrado
                                if (!textoEncontrado) {
                                    if (nivelDeLog >= 2) {
                                        System.out.println(ansi().render("@|yellow " + "Varias etiquetas METADATOS seguidas" + "|@"));
                                    }
                                } else {
                                    if (nivelDeLog >= 2) {
                                        System.out.println(ansi().render("@|yellow " + "Etiqueta METADATOS dentro de los metadatos de la pregunta: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                    }
                                }
                            } else {
                                // Anadimos un nuevo metadato
                                if (lineaLeida.split(" ").length > 1) {
                                    preguntaTemp.getMetadatos().put(lineaLeida.split(" ")[0].toLowerCase(Locale.ROOT), lineaLeida.split(" ")[1].toLowerCase(Locale.ROOT));

                                    textoEncontrado = true;
                                    exito = true;
                                } else {
                                    System.out.println(ansi().render("@|red " + "Los metadatos deben tener el formato nombre valor separados por espacio. Formato incorrecto en la pregunta: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                                    exito = false;
                                    sigueBuscando = false;
                                }
                            }
                        }
                    }
                }
                if (!exito) {
                    return null;
                }

                // Si la pregunta tiene el tema y la dificultad que queremos, la anadimos
                int temaTemp = -1;
                int difTemp = -1;
                try {
                    if (preguntaTemp.getMetadatos().get("tema") != null) {
                        temaTemp = Integer.valueOf(preguntaTemp.getMetadatos().get("tema"));
                    } else {
                        System.out.println(ansi().render("@|red " + "La pregunta no tiene metadato tema: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                        return null;
                    }
                    if (this.dificultadAdaptada) {
                        if (preguntaTemp.getMetadatos().get("dificultad") != null) {
                            difTemp = Integer.valueOf(preguntaTemp.getMetadatos().get("dificultad"));
                        } else {
                            System.out.println(ansi().render("@|red " + "La pregunta no tiene metadato dificultad: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                            return null;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(ansi().render("@|red " + "Error leyendo los metadatos de la pregunta: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                    return null;
                }

                if (temas.contains(temaTemp) &&
                        (
                                (this.dificultadAdaptada &&
                                        ((difTemp >= this.dificultadMinima) && (difTemp <= this.dificultadMaxima))
                                ) || (!this.dificultadAdaptada)
                        )
                ) {
                    preguntasReturn.get(Integer.valueOf(preguntaTemp.getMetadatos().get("tema"))).add(preguntaTemp);

                    if (nivelDeLog >= 4) {
                        int contadorDeRespuestas = 1;
                        System.out.println(ansi().render("@|cyan " + "Pregunta anadida: " + preguntaTemp.getParrafos().get(0).getTextoTotal() + "|@"));
                        if (!preguntaTemp.getParrafos().get(0).getImagenRuta().equals("")) {
                            System.out.println(ansi().render("@|cyan " + "(imagen)" + "|@"));
                        }
                        for (Parrafo r : preguntaTemp.getRespuestasDePregunta()) {
                            System.out.println(ansi().render("@|cyan " + "Anadida respuesta " + contadorDeRespuestas + ": " + r.getTextoTotal() + "|@"));
                            if (!r.getImagenRuta().equals("")) {
                                System.out.println(ansi().render("@|cyan " + "(imagen)" + "|@"));
                            }
                            contadorDeRespuestas++;
                        }
                    }
                }

                // seguimos buscando
                sigueBuscando = true;
                textoEncontrado = false;
                exito = false;
                // Si hemos encontrado un tag de pregunta asi lo volvemos a buscar para comenzar la busqueda
                if (indexNodo != nodeTextPList.getLength()) {
                    indexNodo--;
                }

            }
        } catch (
                Exception ex) {
            System.out.println(ansi().render("@|red " + "Error obteniendo preguntas: " + ex.getMessage() + "|@"));
            preguntasReturn = null;
        }

        return preguntasReturn;
    }

    // Para saber cuantas preguntas hay en el banco
    public int obtenerNumPreguntas() {

        int numPreguntas = 0;

        try {
            documentoOdtBanco = OdfTextDocument.loadDocument(fileBanco);

            // Buscamos el numero de veces que aparece el tag de pregunta
            TextNavigation search = new TextNavigation(tagPregunta, documentoOdtBanco);
            while (search.hasNext()) {
                // Marcamos el texto como encontrado
                search.next();
                numPreguntas++;
            }
            if (nivelDeLog >= 4) {
                System.out.println(ansi().render("@|cyan " + "Numero de preguntas: " + numPreguntas + "|@"));
            }
        } catch (Exception ex) {
            System.out.println(ansi().render("@|red " + "Error obteniendo numero de preguntas: " + ex.getMessage() + "|@"));
        }

        return numPreguntas;
    }

    // Obtiene un nombre de estilo con el nombre pasado como parametro y un numero. El nombre obtenido no se repite en la cabecera
    private String obtenerNuevoNombreDeEstilo(String antiguoNombre, OdfStyleFamily sf) {
        try {
            if (documentoOdtCabecera.getStyleByName(sf, antiguoNombre) == null) {
                return antiguoNombre;
            }
            for (int i = 2; i < 10000; i++) {
                if (documentoOdtCabecera.getStyleByName(sf, antiguoNombre + i) == null) {
                    return (antiguoNombre + i);
                }
            }
            System.out.println(ansi().render("@|red " + "No se ha podido crear el nuevo estilo, hay demasiados estilos con el mismo nombre. Nombre del estilo: " + antiguoNombre + "|@"));
        } catch (Exception e) {
            System.out.println(ansi().render("@|red " + "Error leyendo los estilos. " + e.getMessage() + "|@"));
        }
        return "";
    }

    // Obtiene un nombre de imagen con el nombre pasado como parametro y un numero. El nombre obtenido no se repite en el paquete
    private String obtenerNuevoNombreDeImagen(OdfPackage pa, String antiguoNombre) {
        try {
            if (!pa.contains(antiguoNombre)) {
                return antiguoNombre;
            }
            for (int i = 2; i < 10000; i++) {
                int indexPoint = antiguoNombre.lastIndexOf(".");
                String nombreSinExtension = antiguoNombre.substring(0, indexPoint);
                String extension = antiguoNombre.substring(indexPoint + 1, antiguoNombre.length());

                if (!pa.contains(nombreSinExtension + i + "." + extension)) {
                    return (nombreSinExtension + i + "." + extension);
                }
            }
            System.out.println(ansi().render("@|red " + "No se ha podido insertar la nueva imagen, hay demasiadas imagenes con el mismo nombre. Nombre de la imagen: " + antiguoNombre + "|@"));
        } catch (Exception e) {
            System.out.println(ansi().render("@|red " + "Error leyendo las imagenes. " + e.getMessage() + "|@"));
        }
        return "";
    }

    // Cambia el tamano de letra del documento
    private void cambiarTamanioDeLetra(org.odftoolkit.odfdom.dom.OdfContentOrStylesDomBase dom, int tamanio, String nombre) {
        for (int indexEstilo = 0; indexEstilo < dom.getElementsByTagName("style:text-properties").getLength(); indexEstilo++) {
            Node propDeEstilo = dom.getElementsByTagName("style:text-properties").item(indexEstilo).getAttributes().getNamedItem("fo:font-size");
            if (propDeEstilo != null) {
                if (Integer.valueOf(propDeEstilo.getTextContent().substring(0, propDeEstilo.getTextContent().length() - 2)) < tamanio) {
                    if (nivelDeLog >= 4) {
                        System.out.println(ansi().render("@|cyan " + "Estilo con tamano de letra " + propDeEstilo.getTextContent().substring(0, propDeEstilo.getTextContent().length() - 2) + ". Se va a cambiar al " + nombre + ", " + tamanio + "." + "|@"));
                    }
                    propDeEstilo.setNodeValue(tamanio + "pt");
                    propDeEstilo = dom.getElementsByTagName("style:text-properties").item(indexEstilo).getAttributes().getNamedItem("style:font-size-asian");
                    if (propDeEstilo != null) {
                        propDeEstilo.setNodeValue(tamanio + "pt");
                    }
                    propDeEstilo = dom.getElementsByTagName("style:text-properties").item(indexEstilo).getAttributes().getNamedItem("style:font-size-complex");
                    if (propDeEstilo != null) {
                        propDeEstilo.setNodeValue(tamanio + "pt");
                    }
                }
            }
        }
    }

    // Guarda el archivo del examen
    public boolean guardarExamen(Examen e) {

        try {
            documentoOdtCabecera = OdfTextDocument.loadDocument(fileCabecera);
            if (nivelDeLog >= 4) {
                System.out.println(ansi().render("@|cyan " + "Archivo leido: " + fileCabecera + "|@"));
            }
        } catch (Exception ex) {
            System.out.println(ansi().render("@|red " + "Error al leer la plantilla. " + ex.getMessage() + "|@"));
            return false;
        }

        try {
            /////////////////////////////////////
            // Reemplazamos el tag de la version
            TextNavigation search = new TextNavigation(tagVersion, documentoOdtCabecera);
            while (search.hasNext()) {
                TextSelection selection = search.next();
                selection.replaceWith(e.getVersion());
            }

            //////////////////////////////////////////////
            //Creamos el estilo de las imagenes centradas
            String nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo("estiloDeImagenesCentradas", OdfStyleFamily.Paragraph);
            if (nuevoNombreDeEstilo.equals("")) {
                return false;
            }
            OdfStyle estiloImagenesCentradas = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Paragraph);
            estiloImagenesCentradas.setProperty(OdfParagraphProperties.TextAlign, "center");
            /////////////////////////////////////////////////////////////
            //Creamos el estilo de las imagenes alineadas a la izquierda
            nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo("estiloDeImagenesIzquierda", OdfStyleFamily.Paragraph);
            if (nuevoNombreDeEstilo.equals("")) {
                return false;
            }
            OdfStyle estiloImagenesIzquierda = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Paragraph);
            estiloImagenesIzquierda.setProperty(OdfParagraphProperties.TextAlign, "start");
            estiloImagenesIzquierda.setProperty(OdfParagraphProperties.MarginLeft, "16.29664mm");
            estiloImagenesIzquierda.setProperty(OdfParagraphProperties.LineHeight, "107%");
            estiloImagenesIzquierda.setProperty(OdfParagraphProperties.TextIndent, "-6.29666mm");

            ////////////////////////////////////
            //Creamos los estilos de las listas
            //////////////////////
            //creamos el estilo
            nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo("EstiloDeListaDePreguntas", OdfStyleFamily.List);
            if (nuevoNombreDeEstilo.equals("")) {
                return false;
            }
            org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle estiloDeListaDePreguntas = documentoOdtCabecera.getOrCreateDocumentStyles().newListStyle(nuevoNombreDeEstilo);
            //creamos la numeracion para las preguntas
            org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleNumberElement numeracionListaPreguntas = estiloDeListaDePreguntas.newTextListLevelStyleNumberElement("1", 1);
            numeracionListaPreguntas.setStyleNumSuffixAttribute(".");

            org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement numeracionListaPreguntasProperties = numeracionListaPreguntas.newStyleListLevelPropertiesElement();
            numeracionListaPreguntasProperties.setTextMinLabelWidthAttribute("6.35mm");
            numeracionListaPreguntasProperties.setTextListLevelPositionAndSpaceModeAttribute("label-alignment");

            org.odftoolkit.odfdom.dom.element.style.StyleListLevelLabelAlignmentElement numeracionListaPreguntasAlignment = numeracionListaPreguntasProperties.newStyleListLevelLabelAlignmentElement("listtab");
            numeracionListaPreguntasAlignment.setTextLabelFollowedByAttribute("listtab");

            //creamos la numeracion para las Respuestas
            org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleNumberElement numeracionListaRespuestas = estiloDeListaDePreguntas.newTextListLevelStyleNumberElement("a", 2);
            numeracionListaRespuestas.setStyleNumSuffixAttribute(".");
            numeracionListaRespuestas.setStyleNumLetterSyncAttribute(true);

            org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement numeracionListaRespuestasProperties = numeracionListaRespuestas.newStyleListLevelPropertiesElement();
            numeracionListaRespuestasProperties.setTextMinLabelWidthAttribute("6.35mm");
            numeracionListaRespuestasProperties.setTextListLevelPositionAndSpaceModeAttribute("label-alignment");

            org.odftoolkit.odfdom.dom.element.style.StyleListLevelLabelAlignmentElement numeracionListaRespuestasAlignment = numeracionListaRespuestasProperties.newStyleListLevelLabelAlignmentElement("listtab");
            numeracionListaRespuestasAlignment.setTextLabelFollowedByAttribute("listtab");

            //del texto de la numeracion
            nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo("estiloDelTextoListaPreguntas", OdfStyleFamily.Text);
            if (nuevoNombreDeEstilo.equals("")) {
                return false;
            }
            OdfStyle estiloNumeracionListaPreguntas = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Text);
            estiloNumeracionListaPreguntas.setProperty(OdfTextProperties.FontWeight, "bold");
            estiloNumeracionListaPreguntas.setProperty(OdfTextProperties.FontWeightAsian, "bold");
            estiloNumeracionListaPreguntas.setProperty(OdfTextProperties.FontWeightComplex, "bold");
            estiloNumeracionListaPreguntas.setProperty(OdfTextProperties.FontSize, "12pt");
            estiloNumeracionListaPreguntas.setProperty(OdfTextProperties.FontSizeAsian, "12pt");
            estiloNumeracionListaPreguntas.setProperty(OdfTextProperties.FontSizeComplex, "12pt");
            //le aplicamos el estilo a la numeracion
            numeracionListaPreguntas.setTextStyleNameAttribute(estiloNumeracionListaPreguntas.getStyleNameAttribute());
            numeracionListaRespuestas.setTextStyleNameAttribute(estiloNumeracionListaPreguntas.getStyleNameAttribute());

            //////////////////////////
            // Anadimos las preguntas
            // creamos el elemento lista
            org.odftoolkit.odfdom.dom.element.text.TextListElement elementoListaDePreguntas = documentoOdtCabecera.getContentRoot().newTextListElement();
            elementoListaDePreguntas.setTextContinueNumberingAttribute(true);
            //aplicamos el estilo a la lista
            elementoListaDePreguntas.setTextStyleNameAttribute(estiloDeListaDePreguntas.getStyleNameAttribute());

            for (Pregunta p : e.getGrupoDePreguntas()) {
                //creamos elemento dentro del elemento lista
                org.odftoolkit.odfdom.dom.element.text.TextListItemElement nuevoElementoEnLaListaDePreguntas = elementoListaDePreguntas.newTextListItemElement();
                for (int i = 0; i < p.getParrafos().size(); i++) {
                    //creamos el parrafo
                    OdfTextParagraph parrafo = documentoOdtCabecera.newParagraph();

                    //si tiene spans los anadimos
                    for (int i2 = 0; i2 < p.getParrafos().get(i).getTextosSpan().size(); i2++) {
                        //creamos el estilo del span
                        // cambiamos los nombres de los estilos para que no se sobreescriban
                        nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo(p.getParrafos().get(i).getNombresDeEstilosTextosSpan().get(i2), OdfStyleFamily.Text);
                        if (nuevoNombreDeEstilo.equals("")) {
                            return false;
                        }

                        OdfStyle estiloDeSpan = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Text);
                        estiloDeSpan.setProperties(documentoOdtBanco.getStyleByName(OdfStyleFamily.Text, p.getParrafos().get(i).getNombresDeEstilosTextosSpan().get(i2)).getStylePropertiesDeep());

                        //creamos el nodo span
                        TextSpanElement ts = parrafo.newTextSpanElement();

                        //anadimos el texto de la pregunta
                        ts.setTextContent(p.getParrafos().get(i).getTextosSpan().get(i2));

                        //le ponemos el estilo al span
                        ts.setStyleName(nuevoNombreDeEstilo);
                    }

                    //si no tiene nodos span ponemos el texto del parrafo
                    if (p.getParrafos().get(i).getTextosSpan().size() == 0) {
                        parrafo.setTextContent(p.getParrafos().get(i).getTextoTotal());
                    }

                    ////////////////////////////////
                    //creamos el estilo del parrafo
                    // cambiamos los nombres de los estilos para que no se sobreescriban
                    nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo(p.getParrafos().get(i).getNombreDeEstiloParrafo(), OdfStyleFamily.Paragraph);
                    if (nuevoNombreDeEstilo.equals("")) {
                        return false;
                    }
                    OdfStyle estiloDeParrafo = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Paragraph);
                    estiloDeParrafo.setProperties(documentoOdtBanco.getStyleByName(OdfStyleFamily.Paragraph, p.getParrafos().get(i).getNombreDeEstiloParrafo()).getStylePropertiesDeep());

                    //alinear a la izquierda
                    estiloDeParrafo.setProperty(OdfParagraphProperties.MarginTop, "2.11582mm");
                    estiloDeParrafo.setProperty(OdfParagraphProperties.MarginBottom, "0.35052mm");
                    estiloDeParrafo.setProperty(OdfParagraphProperties.MarginLeft, "6.29666mm");
                    estiloDeParrafo.setProperty(OdfParagraphProperties.LineHeight, "107%");
                    estiloDeParrafo.setProperty(OdfParagraphProperties.TextIndent, "-6.29666mm");

                    //lo anadimos al elemento en la lista de preguntas
                    nuevoElementoEnLaListaDePreguntas.appendChild(parrafo);

                    //////////////////////////////////
                    //si tiene una imagen la anadimos
                    if (!p.getParrafos().get(i).getImagenRuta().equals("")) {
                        //creamos el parrafo
                        OdfTextParagraph parrafoDeImagen = documentoOdtCabecera.newParagraph();

                        InputStream datosStream = documentoOdtBanco.getPackage().getInputStream(p.getParrafos().get(i).getImagenRuta());
                        OdfPackage pa = documentoOdtCabecera.getPackage();
                        String nuevoNombreParaLaImagen = obtenerNuevoNombreDeImagen(pa, p.getParrafos().get(i).getImagenRuta());
                        pa.insert(datosStream, nuevoNombreParaLaImagen, "");

                        org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement frameDelParrafo = parrafoDeImagen.newDrawFrameElement();
                        frameDelParrafo.setSvgWidthAttribute(p.getParrafos().get(i).getImagenAncho());
                        frameDelParrafo.setSvgHeightAttribute(p.getParrafos().get(i).getImagenAlto());
                        frameDelParrafo.setSvgXAttribute("0mm");
                        frameDelParrafo.setSvgYAttribute("0mm");
                        frameDelParrafo.setStyleRelWidthAttribute("scale");
                        frameDelParrafo.setStyleRelHeightAttribute("scale");
                        frameDelParrafo.setTextAnchorTypeAttribute("as-char");
                        org.odftoolkit.odfdom.dom.element.draw.DrawImageElement imagenDelFrame = frameDelParrafo.newDrawImageElement();
                        imagenDelFrame.setXlinkHrefAttribute(nuevoNombreParaLaImagen);
                        imagenDelFrame.setXlinkTypeAttribute("simple");
                        imagenDelFrame.setXlinkShowAttribute("embed");
                        imagenDelFrame.setXlinkActuateAttribute("onLoad");

                        //lo anadimos al elemento en la lista de preguntas
                        nuevoElementoEnLaListaDePreguntas.appendChild(parrafoDeImagen);

                        //le aplicamos el estilo al parrafo
                        parrafoDeImagen.setStyleName(estiloImagenesCentradas.getStyleNameAttribute());
                    }

                    //le aplicamos el estilo al parrafo
                    parrafo.setStyleName(nuevoNombreDeEstilo);
                }

                //////////////////////////
                //Anadimos las respuestas
                //creamos el elemento lista
                org.odftoolkit.odfdom.dom.element.text.TextListElement elementoListaDeRespuestas = documentoOdtCabecera.getContentRoot().newTextListElement();
                elementoListaDePreguntas.getLastChild().appendChild(elementoListaDeRespuestas);
                elementoListaDeRespuestas.setTextContinueNumberingAttribute(true);
                //aplicamos el estilo a la lista
                elementoListaDeRespuestas.setTextStyleNameAttribute("EstiloDeListaDePreguntas");

                for (int i = 0; i < p.getRespuestasDePregunta().size(); i++) {
                    //creamos el parrafo
                    OdfTextParagraph parrafo = documentoOdtCabecera.newParagraph();

                    for (int i2 = 0; i2 < p.getRespuestasDePregunta().get(i).getTextosSpan().size(); i2++) {
                        //creamos el estilo del span
                        // cambiamos los nombres de los estilos para que no se sobreescriban
                        nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo(p.getRespuestasDePregunta().get(i).getNombresDeEstilosTextosSpan().get(i2), OdfStyleFamily.Text);
                        if (nuevoNombreDeEstilo.equals("")) {
                            return false;
                        }
                        OdfStyle estiloDeSpan = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Text);
                        estiloDeSpan.setProperties(documentoOdtBanco.getStyleByName(OdfStyleFamily.Text, p.getRespuestasDePregunta().get(i).getNombresDeEstilosTextosSpan().get(i2)).getStylePropertiesDeep());

                        //creamos el nodo span
                        TextSpanElement ts = parrafo.newTextSpanElement();

                        //anadimos el texto de la respuesta
                        ts.setTextContent(p.getRespuestasDePregunta().get(i).getTextosSpan().get(i2));

                        //le aplicamos el estilo
                        ts.setStyleName(nuevoNombreDeEstilo);
                    }
                    //si no tiene nodos span ponemos el texto del parrafo
                    if (p.getRespuestasDePregunta().get(i).getTextosSpan().size() == 0) {
                        parrafo.setTextContent(p.getRespuestasDePregunta().get(i).getTextoTotal());
                    }

                    ////////////////////////////////
                    //creamos el estilo del parrafo
                    // cambiamos los nombres de los estilos para que no se sobreescriban
                    nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo(p.getRespuestasDePregunta().get(i).getNombreDeEstiloParrafo(), OdfStyleFamily.Paragraph);
                    if (nuevoNombreDeEstilo.equals("")) {
                        return false;
                    }
                    OdfStyle estiloDeParrafo = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Paragraph);
                    estiloDeParrafo.setProperties(documentoOdtBanco.getStyleByName(OdfStyleFamily.Paragraph, p.getRespuestasDePregunta().get(i).getNombreDeEstiloParrafo()).getStylePropertiesDeep());

                    //alinear a la izquierda
                    estiloDeParrafo.setProperty(OdfParagraphProperties.TextAlign, "start");
                    estiloDeParrafo.setProperty(OdfParagraphProperties.MarginLeft, "16.29664mm");
                    estiloDeParrafo.setProperty(OdfParagraphProperties.LineHeight, "107%");
                    estiloDeParrafo.setProperty(OdfParagraphProperties.TextIndent, "-6.29666mm");

                    //creamos elemento dentro del elemento lista
                    org.odftoolkit.odfdom.dom.element.text.TextListItemElement nuevoElementoEnLaListaDeRespuestas = elementoListaDeRespuestas.newTextListItemElement();
                    nuevoElementoEnLaListaDeRespuestas.appendChild(parrafo);

                    //////////////////////////////////
                    //si tiene una imagen la anadimos
                    if (!p.getRespuestasDePregunta().get(i).getImagenRuta().equals("")) {
                        //creamos el parrafo
                        OdfTextParagraph parrafoDeImagen = documentoOdtCabecera.newParagraph();

                        InputStream datosStream = documentoOdtBanco.getPackage().getInputStream(p.getRespuestasDePregunta().get(i).getImagenRuta());
                        OdfPackage pa = documentoOdtCabecera.getPackage();
                        String nuevoNombreParaLaImagen = obtenerNuevoNombreDeImagen(pa, p.getRespuestasDePregunta().get(i).getImagenRuta());
                        pa.insert(datosStream, nuevoNombreParaLaImagen, "");

                        org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement frameDelParrafo = parrafoDeImagen.newDrawFrameElement();
                        frameDelParrafo.setSvgWidthAttribute(p.getRespuestasDePregunta().get(i).getImagenAncho());
                        frameDelParrafo.setSvgHeightAttribute(p.getRespuestasDePregunta().get(i).getImagenAlto());
                        frameDelParrafo.setSvgXAttribute("0mm");
                        frameDelParrafo.setSvgYAttribute("0mm");
                        frameDelParrafo.setStyleRelWidthAttribute("scale");
                        frameDelParrafo.setStyleRelHeightAttribute("scale");
                        frameDelParrafo.setTextAnchorTypeAttribute("as-char");
                        org.odftoolkit.odfdom.dom.element.draw.DrawImageElement imagenDelFrame = frameDelParrafo.newDrawImageElement();
                        imagenDelFrame.setXlinkHrefAttribute(nuevoNombreParaLaImagen);
                        imagenDelFrame.setXlinkTypeAttribute("simple");
                        imagenDelFrame.setXlinkShowAttribute("embed");
                        imagenDelFrame.setXlinkActuateAttribute("onLoad");

                        //lo anadimos al elemento en la lista de preguntas
                        nuevoElementoEnLaListaDeRespuestas.appendChild(parrafoDeImagen);

                        //le aplicamos el estilo al parrafo
                        parrafoDeImagen.setStyleName(estiloImagenesIzquierda.getStyleNameAttribute());
                    }

                    //le aplicamos el estilo al parrafo
                    parrafo.setStyleName(nuevoNombreDeEstilo);
                }
            }

            //aplicamos el tamano minimo de letra si es necesario
            //para estilos
            cambiarTamanioDeLetra(documentoOdtCabecera.getStylesDom(), this.tamanioMinimoDeLetra, "minimo");
            //para elementos
            cambiarTamanioDeLetra(documentoOdtCabecera.getContentDom(), this.tamanioMinimoDeLetra, "minimo");

            // Creamos los directorios para el archivo de salida
            pathDirectorioDeSalida.toFile().mkdirs();
            // Creamos el archivo del examen
            // sin adaptar
            File documentoExamen = new File(pathDirectorioDeSalida.resolve("examen_version_" + e.getVersion() + ".odt").toString());
            documentoOdtCabecera.save(documentoExamen);
            if (nivelDeLog >= 3) {
                System.out.println(ansi().render("@|green " + "Examen guardado: Version " + e.getVersion() + " en: " + documentoExamen + "|@"));
            }

            //adaptado
            if (this.tamanioDeLetraAdaptadoSiNo) {
                //aplicamos el tamano adaptado de letra si es necesario
                //para estilos
                cambiarTamanioDeLetra(documentoOdtCabecera.getStylesDom(), this.tamanioDeLetraAdaptado, "adaptado");
                //para elementos
                cambiarTamanioDeLetra(documentoOdtCabecera.getContentDom(), this.tamanioDeLetraAdaptado, "adaptado");

                //ponemos la version _ad (adaptada)
                e.setVersion(e.getVersion() + "_ad");
                documentoExamen = new File(pathDirectorioDeSalida.resolve("examen_version_" + e.getVersion() + ".odt").toString());
                documentoOdtCabecera.save(documentoExamen);
                if (nivelDeLog >= 3) {
                    System.out.println(ansi().render("@|green " + "Examen guardado: Version " + e.getVersion() + " con tamano de letra: " + this.tamanioDeLetraAdaptado + " en: " + documentoExamen + "|@"));
                }
            }
        } catch (Exception ex) {
            System.out.println(ansi().render("@|red " + "Error guardando examen: " + ex.getMessage() + "|@"));
        }

        return true;
    }

}
