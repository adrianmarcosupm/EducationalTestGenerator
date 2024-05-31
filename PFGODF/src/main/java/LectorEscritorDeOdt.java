import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;


public class LectorEscritorDeOdt {

    OdfTextDocument documentoOdtBanco;
    OdfTextDocument documentoOdtCabecera;

    // Los tags se envian como regex en algunos métodos, así que hay que utilizar caracteres de escape.
    private final String tagVersion = "\\{\\{\\¡VERSION\\¡\\}\\}";
    private final String tagPregunta = "\\{\\{\\¡PREGUNTA\\¡\\}\\}";
    private final String tagRespuestas = "\\{\\{\\¡RESPUESTAS\\¡\\}\\}";
    File fileBanco;
    File fileCabecera;
    Path pathDirectorioDeSalida;

    private static final Logger logger = LogManager.getLogger();
    private static final String lineaDeGuiones = "------------------------------------------------------";
    private static final char[] letrasParaLasRespuestas = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /* Adaptaciones especiales */
    private boolean dificultadAdaptada;
    private int dificultadMinima;
    private int dificultadMaxima;
    private boolean tamanioDeLetraAdaptadoSiNo;
    private int tamanioDeLetraAdaptado;
    private int tamanioMinimoDeLetra;


    // Constructor
    public LectorEscritorDeOdt(File banco, File cabecera, Path directorioAEscribir) {
        this.fileBanco = banco;
        this.fileCabecera = cabecera;
        this.pathDirectorioDeSalida = directorioAEscribir;

        try {
            documentoOdtBanco = OdfTextDocument.loadDocument(fileBanco);
            logger.debug("Archivo leido: " + fileBanco);
        } catch (Exception ex) {
            logger.error("Error al leer el archivo .odt " + fileBanco.toString() + " " + ex.getMessage());
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

    public ArrayList<Pregunta> obtenerPreguntas(ArrayList<Integer> numerosDePregunta) {
        // Borramos las barras de las etiquetas
        String tagPNoRegex = tagPregunta.replace("\\", "");
        String tagRNoRegex = tagRespuestas.replace("\\", "");

        ArrayList<Pregunta> preguntasReturn = new ArrayList<>(); // La lista de preguntas que devolvemos al generador
        Pregunta preguntaTemp = null; // Guardamos la pregunta que encontramos para añadirla a la lista de preguntas
        Parrafo parrafoTemp = null; // Guardamos la respuesta que encontramos para añadirla a la lista de respuestas

        boolean sigueBuscando = true; // Para salir de los bucles
        boolean textoEncontrado = false; // Para elegir caminos diferentes en los bucles
        boolean exito = false; // Para continuar con la funcion de obtener pregunta

        Node nodo; // El nodo XML
        OdfTextParagraph parrafo; // Tratamos el nodo como un parrafo
        int indexNodo = 0; // Numero de nodo por el que empieza la busqueda
        String lineaLeida = ""; // La linea del documento .odt que leemos
        int contadorDePreguntas = 0; // Para numerar las preguntas del banco

        try {
            documentoOdtBanco = OdfTextDocument.loadDocument(fileBanco);
            NodeList nodeTextPList = documentoOdtBanco.getContentRoot().getElementsByTagName("text:p"); // Buscamos nodos XML con esta etiqueta
            for (OdfStyle s : documentoOdtBanco.getStylesDom().getAutomaticStyles().getAllStyles()) {
//                System.out.println(s.toString()); TODO: obtiene estilos
            }
            for (OdfStyle s : documentoOdtBanco.getStylesDom().getOfficeStyles().getAllStyles()) {
//                System.out.println(s.toString()); TODO: obtiene estilos
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
                exito = false;
                sigueBuscando = true;
                for (; (indexNodo < nodeTextPList.getLength()) && sigueBuscando == true; indexNodo++) {
                    nodo = nodeTextPList.item(indexNodo);
                    if (nodo instanceof OdfTextParagraph) {
                        parrafo = (OdfTextParagraph) nodo;
                        //todo buscamos recursivamente imagenes, pueden estar en un p o en un span dentro de un p
//                        List<OdfDrawImage> l = OdfDrawImage.getImages(documentoOdtBanco);
//                        for (OdfDrawImage d : documentoOdtBanco)
//                            logger.info(d.toString());
                        // Eliminamos caracteres en blanco por delante y por detrás de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if (!lineaLeida.equals("")) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPNoRegex)) {
                                if (textoEncontrado) {
                                    logger.error("Pregunta sin respuestas: " + preguntaTemp.getParrafos().get(0).getTextoTotal());
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
                                // Añadimos un nuevo parrafo
                                preguntaTemp.getParrafos().add(new Parrafo());
                                // Guardamos todos los text span del parrafo para guardar sus estilos
                                NodeList nodosHijos = nodo.getChildNodes();
                                for (int i = 0; i < nodosHijos.getLength(); i++) {
//                                    System.out.println(nodosHijos.item(i).getLocalName()); //span todo quitar
//                                    System.out.println(nodosHijos.item(i).getNodeName()); //text:span
//                                    System.out.println(nodosHijos.item(i).getTextContent()); todo end quitar
                                    if (nodosHijos.item(i) instanceof OdfDrawFrame) {
//todo
                                        logger.info(nodosHijos.item(i).toString());

                                    }
                                    if (nodosHijos.item(i) instanceof OdfTextSpan) {
                                        OdfTextSpan ts = (OdfTextSpan) nodosHijos.item(i);
                                        preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).getTextosSpan().add(ts.getTextContent());
                                        preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).getNombresDeEstilosTextosSpan().
                                                add(ts.getAttributes().getNamedItem("text:style-name").getNodeValue());
                                        //Guardamos el estilo del parrafo TODO quitar
                                        logger.debug("getStyleName" + ts.getStyleName());
                                        logger.debug("getTextStyleNameAttribute" + ts.getTextStyleNameAttribute());
                                        logger.debug("getAutomaticStyle" + ts.getAutomaticStyle());
                                        logger.debug("getDocumentStyle" + ts.getDocumentStyle());
                                        //////////////todo end quitar
                                    }
                                }
                                //si no tiene nodos textspan añadimos el texto del parrafo
                                if (preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).getTextosSpan().size() == 0) {
                                    preguntaTemp.getParrafos().get(preguntaTemp.getParrafos().size() - 1).setTextoDeParrafo(parrafo.getTextContent());
                                }

                                // TODO quitar
                                logger.debug("getStyleName" + parrafo.getStyleName());
                                logger.debug("getTextStyleNameAttribute" + parrafo.getTextStyleNameAttribute());
                                logger.debug("getAutomaticStyle" + parrafo.getAutomaticStyle());
                                logger.debug("getDocumentStyle" + parrafo.getDocumentStyle());
                                //////////////todo end quitar
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
                        // Eliminamos caracteres en blanco por delante y por detrás de la cadena.
                        lineaLeida = parrafo.getTextContent().replaceAll("(^\\h*)|(\\h*$)", "");
                        if (!lineaLeida.equals("")) { // Si no es una linea con solo espacios
                            if (lineaLeida.equals(tagPNoRegex)) {
                                if (!textoEncontrado) {
                                    logger.error("Pregunta sin respuestas: " + preguntaTemp.getParrafos().get(0).getTextoTotal());
                                }
                                sigueBuscando = false;
                            } else if (lineaLeida.equals(tagRNoRegex)) {
                                if (!textoEncontrado) {
                                    logger.warn("Varias etiquetas RESPUESTAS seguidas");
                                } else {
                                    logger.warn("Etiqueta RESPUESTAS dentro de las respuestas de la pregunta: " + preguntaTemp.getParrafos().get(0).getTextoTotal());
                                }
                            } else {
                                // Añadimos una nueva respuesta
                                preguntaTemp.getRespuestasDePregunta().add(new Parrafo());
                                // Guardamos todos los text span del parrafo para guardar sus estilos
                                NodeList nodosHijos = nodo.getChildNodes();
                                for (int i = 0; i < nodosHijos.getLength(); i++) {
//                                    System.out.println(nodosHijos.item(i).getLocalName()); //span todo quitar
//                                    System.out.println(nodosHijos.item(i).getNodeName()); //text:span
//                                    System.out.println(nodosHijos.item(i).getTextContent()); todo end quitar
                                    if (nodosHijos.item(i) instanceof OdfTextSpan) {
                                        OdfTextSpan ts = (OdfTextSpan) nodosHijos.item(i);
                                        preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).getTextosSpan().add(ts.getTextContent());
                                        preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).getNombresDeEstilosTextosSpan().
                                                add(ts.getAttributes().getNamedItem("text:style-name").getNodeValue());
                                        //Guardamos el estilo del parrafo TODO quitar
                                        logger.debug("getStyleName" + ts.getStyleName());
                                        logger.debug("getTextStyleNameAttribute" + ts.getTextStyleNameAttribute());
                                        logger.debug("getAutomaticStyle" + ts.getAutomaticStyle());
                                        logger.debug("getDocumentStyle" + ts.getDocumentStyle());
                                        //////////////todo end quitar
                                    }
                                }
                                //si no tiene nodos textspan añadimos el texto del parrafo
                                if (preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).getTextosSpan().size() == 0) {
                                    preguntaTemp.getRespuestasDePregunta().get(preguntaTemp.getRespuestasDePregunta().size() - 1).setTextoDeParrafo(parrafo.getTextContent());
                                }
                                //Guardamos el estilo del parrafo TODO quitar
                                logger.debug("getStyleName" + parrafo.getStyleName());
                                logger.debug("getTextStyleNameAttribute" + parrafo.getTextStyleNameAttribute());
                                logger.debug("getAutomaticStyle" + parrafo.getAutomaticStyle());
                                logger.debug("getDocumentStyle" + parrafo.getDocumentStyle());
                                //////////////todo end quitar
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

                contadorDePreguntas++;

                // Si la pregunta encontrada esta en la lista de las que queremos, la añadimos
                //TODO: falta si es del tema que queremos o de la dificultad que queremos
                if (numerosDePregunta.contains(contadorDePreguntas)) {
                    int contadorDeRespuestas = 1; //TODO: Para debugear
                    preguntasReturn.add(preguntaTemp);
                    logger.debug("Pregunta añadida " + contadorDePreguntas + ": " + preguntaTemp.getParrafos().get(0).getTextoTotal());
                    for (Parrafo r : preguntaTemp.getRespuestasDePregunta()) {
                        logger.debug("Añadida respuesta " + contadorDeRespuestas + ": " + r.getTextoTotal());
                        contadorDeRespuestas++; //TODO: para debug
                    }

                }

                // Si no hemos encontrado la ultima pregunta, seguimos buscando
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
            documentoOdtBanco = OdfTextDocument.loadDocument(fileBanco);

            // Buscamos el numero de veces que aparece el tag de pregunta
            TextNavigation search = new TextNavigation(tagPregunta, documentoOdtBanco);
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
            logger.error("No se ha podido crear el nuevo estilo, hay demasiados estilos con el mismo nombre.");
        } catch (Exception e) {
            logger.error("Error creando los estilos. " + e.getMessage());
        }
        return "";
    }


    public boolean guardarExamen(Examen e) {

        try {
            documentoOdtCabecera = OdfTextDocument.loadDocument(fileCabecera);
            logger.debug("Archivo leido: " + fileCabecera);
        } catch (Exception ex) {
            logger.error("Error al leer el archivo .odt " + fileCabecera.toString() + " " + ex.getMessage());
            return false;
        }

//        File documentoExamen;
//        OdfContentDom dom;
        try {
            //Cargamos la plantilla que tiene la cabecera
//            documentoOdt = (OdfTextDocument) OdfTextDocument.loadDocument(fileBanco);
//            dom = documentoOdt.getContentDom();
            /////////////////////////////////////
            // Reemplazamos el tag de la version
            TextNavigation search = new TextNavigation(tagVersion, documentoOdtCabecera);
            while (search.hasNext()) {
                TextSelection selection = search.next();
                selection.replaceWith(e.getVersion());
            }
            //////////////////////////
            // Añadimos las preguntas
            for (Pregunta p : e.getGrupoDePreguntas()) {
                for (int i = 0; i < p.getParrafos().size(); i++) {
//                    OdfTextParagraph par = new OdfTextParagraph(dom, p.getNombreDeEstilos().get(i),p.getTextos().get(i));

                    //creamos el parrafo
                    OdfTextParagraph parrafo = documentoOdtCabecera.newParagraph();

                    //si tiene spans los añadimos
                    for (int i2 = 0; i2 < p.getParrafos().get(i).getTextosSpan().size(); i2++) {
                        //creamos el estilo del span
                        // cambiamos los nombres de los estilos para que no se sobreescriban
                        String nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo(p.getParrafos().get(i).getNombresDeEstilosTextosSpan().get(i2), OdfStyleFamily.Text);
                        if (nuevoNombreDeEstilo.equals("")) {
                            return false;
                        }

                        OdfStyle estiloDeSpan = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Text);

                        //ponemos el tamaño de letra minimo
                        if (estiloDeSpan.getAttribute("style:font-size") < this.tamanioMinimoDeLetra) {
                            logger.warn("El tamaño del texto: " + p.getParrafos().get(i).getTextosSpan().get(i2) + " es " + estiloDeSpan.getAttribute("style:font-size") + ". Se escribirá con el mínimo: " + this.tamanioMinimoDeLetra);
                            estiloDeSpan.setAttribute("style:font-size", String.valueOf(this.tamanioMinimoDeLetra));
                            estiloDeSpan.setAttribute("style:font-size-asian", String.valueOf(this.tamanioMinimoDeLetra));
                            estiloDeSpan.setAttribute("style:font-size-complex", String.valueOf(this.tamanioMinimoDeLetra));
                        }

                        estiloDeSpan.setProperties(documentoOdtBanco.getStyleByName(OdfStyleFamily.Text, p.getParrafos().get(i).getNombresDeEstilosTextosSpan().get(i2)).getStylePropertiesDeep());

                        //creamos el nodo span
                        TextSpanElement ts = parrafo.newTextSpanElement();

                        // si es el primer parrafo y primer span añadimos el numero de pregunta
                        //añadimos el texto de la pregunta
                        if (i == 0 && i2 == 0) {
                            ts.setTextContent((e.getGrupoDePreguntas().indexOf(p) + 1) + ".  " + p.getParrafos().get(i).getTextosSpan().get(i2));
                        } else {
                            ts.setTextContent(p.getParrafos().get(i).getTextosSpan().get(i2));
                        }

                        //le ponemos el estilo al span
                        ts.setStyleName(nuevoNombreDeEstilo);

                    }
                    //si no tiene nodos span ponemos el texto del parrafo
                    if (p.getParrafos().get(i).getTextosSpan().size() == 0) {
                        // si es el primer parrafo añadimos el numero de pregunta
                        if (i == 0) {
                            parrafo.setTextContent((e.getGrupoDePreguntas().indexOf(p) + 1) + ".  " + p.getParrafos().get(i).getTextoTotal());
                        } else {
                            parrafo.setTextContent(p.getParrafos().get(i).getTextoTotal());
                        }

                    }

                    //creamos el estilo del parrafo
                    // cambiamos los nombres de los estilos para que no se sobreescriban
                    String nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo(p.getParrafos().get(i).getNombreDeEstiloParrafo(), OdfStyleFamily.Paragraph);
                    if (nuevoNombreDeEstilo.equals("")) {
                        return false;
                    }
                    OdfStyle estiloDeParrafo = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Paragraph);
                    //todo test
                    if (!p.getParrafos().get(i).getNombreDeEstiloParrafo().equals(estiloDeParrafo.getStyleNameAttribute())) {
                        logger.info("SIUUU " + p.getParrafos().get(i).getNombreDeEstiloParrafo() + " " + estiloDeParrafo.getStyleNameAttribute());
                    }
                    estiloDeParrafo.setProperties(documentoOdtBanco.getStyleByName(OdfStyleFamily.Paragraph, p.getParrafos().get(i).getNombreDeEstiloParrafo()).getStylePropertiesDeep());
                    logger.info("DES " + estiloDeParrafo.getStyleNameAttribute());//todo endtest
                    //le aplicamos el estilo al parrafo
                    parrafo.setStyleName(nuevoNombreDeEstilo);

//                    dom.getRootElement().appendChild(par);
//                    documentoOdtBanco.addText(t); funciona pero todo en el mismo parrafo
//                    documentoOdtBanco.newParagraph().addContent(t); funciona
                }

                //////////////////////////
                //Añadimos las respuestas
                for (int i = 0; i < p.getRespuestasDePregunta().size(); i++) {
//                    OdfTextParagraph par = new OdfTextParagraph(dom, p.getNombreDeEstilos().get(i),p.getTextos().get(i));

                    //creamos el parrafo
                    OdfTextParagraph parrafo = documentoOdtCabecera.newParagraph();

                    for (int i2 = 0; i2 < p.getRespuestasDePregunta().get(i).getTextosSpan().size(); i2++) {
                        //creamos el estilo del span
                        // cambiamos los nombres de los estilos para que no se sobreescriban
                        String nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo(p.getRespuestasDePregunta().get(i).getNombresDeEstilosTextosSpan().get(i2), OdfStyleFamily.Text);
                        if (nuevoNombreDeEstilo.equals("")) {
                            return false;
                        }
                        OdfStyle estiloDeSpan = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Text);
                        estiloDeSpan.setProperties(documentoOdtBanco.getStyleByName(OdfStyleFamily.Text, p.getRespuestasDePregunta().get(i).getNombresDeEstilosTextosSpan().get(i2)).getStylePropertiesDeep());

                        //creamos el nodo span
                        TextSpanElement ts = parrafo.newTextSpanElement();
                        // si es el primer span añadimos el numero de pregunta
                        if (i2 == 0) {
                            ts.setTextContent(letrasParaLasRespuestas[i] + ".  " + p.getRespuestasDePregunta().get(i).getTextosSpan().get(i2));
                        } else {
                            ts.setTextContent(p.getRespuestasDePregunta().get(i).getTextosSpan().get(i2));
                        }

                        //le aplicamos el estilo
                        ts.setStyleName(nuevoNombreDeEstilo);
                    }
                    //si no tiene nodos span ponemos el texto del parrafo
                    if (p.getRespuestasDePregunta().get(i).getTextosSpan().size() == 0) {
                        // añadimos el numero de pregunta
                        parrafo.setTextContent(letrasParaLasRespuestas[i] + ".  " + p.getRespuestasDePregunta().get(i).getTextoTotal());
                    }

                    //creamos el estilo del parrafo
                    // cambiamos los nombres de los estilos para que no se sobreescriban
                    String nuevoNombreDeEstilo = obtenerNuevoNombreDeEstilo(p.getRespuestasDePregunta().get(i).getNombreDeEstiloParrafo(), OdfStyleFamily.Paragraph);
                    if (nuevoNombreDeEstilo.equals("")) {
                        return false;
                    }
                    OdfStyle estiloDeParrafo = documentoOdtCabecera.getOrCreateDocumentStyles().newStyle(nuevoNombreDeEstilo, OdfStyleFamily.Paragraph);
                    estiloDeParrafo.setProperties(documentoOdtBanco.getStyleByName(OdfStyleFamily.Paragraph, p.getRespuestasDePregunta().get(i).getNombreDeEstiloParrafo()).getStylePropertiesDeep());

                    //le aplicamos el estilo al parrafo
                    parrafo.setStyleName(nuevoNombreDeEstilo);

//                    dom.getRootElement().appendChild(par);
//                    documentoOdtBanco.addText(t); funciona pero todo en el mismo parrafo
//                    documentoOdtBanco.newParagraph().addContent(t); funciona
                }
            }

            // Creamos los directorios para el archivo de salida
            pathDirectorioDeSalida.toFile().mkdirs();
            // Creamos el archivo del examen
            File documentoExamen = new File(pathDirectorioDeSalida.resolve("examen_version_" + e.getVersion() + ".odt").toString());
            documentoOdtCabecera.save(documentoExamen);

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
        return true;
    }


}
