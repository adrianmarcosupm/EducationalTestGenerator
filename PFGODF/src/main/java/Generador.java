import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Generador {

    /* Ajustes generales */
    private File plantilla;
    private final String stringAjustePlantilla = "Archivo de plantilla";
    private File bancoDePreguntas;
    private final String stringAjusteBancoDePreguntas = "Archivo del banco de preguntas";
    private Path directorioSalida;
    private final String stringAjusteDirectorioSalida = "Directorio para guardar los exámenes";


    /* Ajustes de examen */
    private int numMaxDeVersiones;
    private final String stringAjusteNumMaxDeVersiones = "Número máximo de versiones";
    private ArrayList<Integer> temas = new ArrayList<>();
    private final String stringAjusteTemas = "Temas a incluir (separados por comas)";
    private int numPreguntas;
    private final String stringAjusteNumPreguntas = "Número de preguntas";

    /* Adaptaciones especiales */
    private boolean dificultadAdaptada;
    private final String stringAjusteDificultadAdaptada = "Dificultad adaptada";
    private int dificultadMinima;
    private final String stringAjusteDificultadMinima = "Dificultad adaptada mínima";
    private int dificultadMaxima;
    private final String stringAjusteDificultadMaxima = "Dificultad adaptada máxima";
    private boolean tamanioDeLetraAdaptadoSiNo;
    private final String stringAjusteTamanioDeLetraAdaptadoSiNo = "Tamaño de letra adaptado si/no";
    private int tamanioDeLetraAdaptado;
    private final String stringAjusteTamanioDeLetraAdaptado = "Tamaño de letra adaptado";
    private int tamanioMinimoDeLetra;
    private final String stringAjusteTamanioMinimoDeLetra = "Tamaño mínimo de letra";


    // Logger para mostrar mensajes al usuario
    private static final Logger logger = LogManager.getLogger();
    private static final String lineaDeGuiones = "------------------------------------------------------";
    private static final char[] letrasDeVersiones = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    // Constructor por defecto
    public Generador() {
        // Cargamos la configuracion
        cargarConfiguracion();

    }

    // Cargar archivo de configuracion inicial
    private void cargarConfiguracion() {
        if (!Files.exists(Paths.get("GeneradorConfiguracion.txt"))) {
            logger.warn("Creando archivo de configuración: GeneradorConfiguracion.txt");
            try {
                FileWriter fw = new FileWriter("GeneradorConfiguracion.txt");
                fw.write("*****************************************************************\n");
                fw.write("*\t\t\tAjustes generales\t\t\t*\n");
                fw.write("*****************************************************************\n");
                fw.write("Archivo de plantilla \t\t\t= Plantilla.odt\n");
                fw.write("Archivo del banco de preguntas \t\t= Banco_De_Preguntas.odt\n");
                fw.write("Directorio para guardar los exámenes \t= examenes_generados\n");
                fw.write("\n");
                fw.write("*****************************************************************\n");
                fw.write("*\t\t\tAjustes de exámen\t\t\t*\n");
                fw.write("*****************************************************************\n");
                fw.write("Número máximo de versiones            =  4\n");
                fw.write("Temas a incluir (separados por comas) =  1,2,3,4,5,6,7,8,9,10\n");
                fw.write("Número de preguntas                   =  15\n");
                fw.write("\n");
                fw.write("*****************************************************************\n");
                fw.write("*\t\t     Adaptaciones especiales\t\t\t*\n");
                fw.write("*****************************************************************\n");
                fw.write("Dificultad adaptada        \t= no\n");
                fw.write("Dificultad adaptada mínima \t= 60\n");
                fw.write("Dificultad adaptada máxima \t= 70\n");
                fw.write("Tamaño de letra adaptado si/no \t= si\n");
                fw.write("Tamaño de letra adaptado       \t= 11\n");
                fw.write("Tamaño mínimo de letra         \t= 9\n");

                fw.flush();
                fw.close();
            } catch (Exception e) {
                logger.error("Error creando archivo de configuración. " + e.getMessage());
            }
        }

        String linea = "", ajuste, valor;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("GeneradorConfiguracion.txt"));
            while ((linea = reader.readLine()) != null) {
                if (linea.contains("=")) {
                    linea = linea.trim();
                    ajuste = linea.split("=")[0].strip();
                    valor = linea.split("=")[1].strip();
                    if (ajuste.equals(stringAjustePlantilla)) {
                        plantilla = Paths.get(valor).toFile();
                    } else if (ajuste.equals(stringAjusteBancoDePreguntas)) {
                        bancoDePreguntas = Paths.get(valor).toFile();
                    } else if (ajuste.equals(stringAjusteDirectorioSalida)) {
                        directorioSalida = Paths.get(valor);
                    } else if (ajuste.equals(stringAjusteNumMaxDeVersiones)) {
                        numMaxDeVersiones = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteTemas)) {
                        for (String tema : valor.split(",")) {
                            temas.add(Integer.parseInt(tema.strip()));
                        }
                    } else if (ajuste.equals(stringAjusteNumPreguntas)) {
                        numPreguntas = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteDificultadAdaptada)) {
                        dificultadAdaptada = getBoolean(valor);
                    } else if (ajuste.equals(stringAjusteDificultadMinima)) {
                        dificultadMinima = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteDificultadMaxima)) {
                        dificultadMaxima = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteTamanioDeLetraAdaptadoSiNo)) {
                        tamanioDeLetraAdaptadoSiNo = getBoolean(valor);
                    } else if (ajuste.equals(stringAjusteTamanioDeLetraAdaptado)) {
                        tamanioDeLetraAdaptado = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteTamanioMinimoDeLetra)) {
                        tamanioMinimoDeLetra = Integer.parseInt(valor);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error al leer el archivo de configuracion: " + e.getMessage());
            logger.error("Ultima linea leida: " + linea);
            logger.error("Usando valores por defecto");

            plantilla = new File("Plantilla.odt");
            bancoDePreguntas = new File("Banco_De_Preguntas.odt");
            directorioSalida = Paths.get("examenes_generados");
            numMaxDeVersiones = 4;
            temas = new ArrayList<>();
            for (int i = 1; i < 11; i++) {
                temas.add(i);
            }
            numPreguntas = 15;
            dificultadAdaptada = false;
            dificultadMinima = 60;
            dificultadMaxima = 70;
            tamanioDeLetraAdaptadoSiNo = true;
            tamanioDeLetraAdaptado = 11;
            tamanioMinimoDeLetra = 9;
        } finally {
            // Imprimimos los valores para que los vea el usuario
            imprimirConfiguracion();
        }
    }

    //Imprime la configuracion cargada al usuario
    private void imprimirConfiguracion() {
        logger.info(lineaDeGuiones);
        logger.info("Configuración cargada con los siguientes valores:");
        logger.info(lineaDeGuiones);
        logger.info(stringAjustePlantilla + " : " + plantilla);
        logger.info(stringAjusteBancoDePreguntas + " : " + bancoDePreguntas);
        logger.info(stringAjusteDirectorioSalida + " : " + directorioSalida);
        logger.info(stringAjusteNumMaxDeVersiones + " : " + numMaxDeVersiones);
        for (int i = 0; i < temas.size(); i++)
            logger.info(stringAjusteTemas + " : " + temas.get(i));
        logger.info(stringAjusteNumPreguntas + " : " + numPreguntas);
        logger.info(stringAjusteDificultadAdaptada + " : " + dificultadAdaptada);
        logger.info(stringAjusteDificultadMinima + " : " + dificultadMinima);
        logger.info(stringAjusteDificultadMaxima + " : " + dificultadMaxima);
        logger.info(stringAjusteTamanioDeLetraAdaptadoSiNo + " : " + tamanioDeLetraAdaptadoSiNo);
        logger.info(stringAjusteTamanioDeLetraAdaptado + " : " + tamanioDeLetraAdaptado);
        logger.info(stringAjusteTamanioMinimoDeLetra + " : " + tamanioMinimoDeLetra);
        logger.info(lineaDeGuiones);
    }

    //Genera los examenes, los mezcla y los guarda
    public void generar() {
        Random random = new Random();
        LectorEscritorDeOdt lectorEscritorDeOdt = new LectorEscritorDeOdt(bancoDePreguntas, plantilla, directorioSalida);

        //Ponemos los ajustes de adaptaciones especiales al lector de odt
        lectorEscritorDeOdt.setDificultadAdaptada(this.dificultadAdaptada);
        lectorEscritorDeOdt.setDificultadMinima(this.dificultadMinima);
        lectorEscritorDeOdt.setDificultadMaxima(this.dificultadMaxima);
        lectorEscritorDeOdt.setTamanioDeLetraAdaptadoSiNo(this.tamanioDeLetraAdaptadoSiNo);
        lectorEscritorDeOdt.setTamanioDeLetraAdaptado(this.tamanioDeLetraAdaptado);
        lectorEscritorDeOdt.setTamanioMinimoDeLetra(this.tamanioMinimoDeLetra);

        ArrayList<Examen> examenes = new ArrayList<>(); // Donde guardamos los exámenes generados.
        HashMap<Integer, ArrayList<Pregunta>> preguntasPorTemas; // Donde guardamos todas las preguntas del banco que cumplen las condiciones
        ArrayList<Pregunta> preguntasParaMezclar = new ArrayList<>(); // Donde guardamos las preguntas para mezclarlas
        int numPreguntasDelBanco = lectorEscritorDeOdt.obtenerNumPreguntas(); // Numero de preguntas que hay en el banco

        if (numPreguntasDelBanco < numPreguntas) {
            logger.error("No hay tantas preguntas en el banco de preguntas.");
            return;
        }

        //Obtenemos sólo las preguntas del banco de preguntas que cumplen las condiciones
        preguntasPorTemas = lectorEscritorDeOdt.obtenerPreguntas(this.temas);

        if (preguntasPorTemas == null) {
            return; // Los errores son mostrados antes de llegar aqui
        }

        //comprobamos que todos los temas tienen al menos alguna pregunta y entre todas suman el numero de preguntas que queremos
        int nPTemp = 0;
        for (Integer clave : preguntasPorTemas.keySet()) {
            if (preguntasPorTemas.get(clave).size() == 0) {
                logger.warn("No se han encontrado preguntas del tema " + clave + " que cumplan los requisitos.");
            } else {
                nPTemp = nPTemp + preguntasPorTemas.get(clave).size();
            }
        }
        if (nPTemp < numPreguntas) {
            logger.error("No se han encontrado " + numPreguntas + " preguntas que cumplan los requisitos.");
            return;
        }

        // Cogemos una aleatoria de un tema aleatorio para que queden proporcionales
        ArrayList<Integer> temasCandidatos = new ArrayList<>();
        // Generamos numeros entre 0 y el numero de preguntas de un tema
        for (int i = 0; i < numPreguntas; i++) {
            //si ya hemos cogido preguntas de todos los temas
            if (temasCandidatos.size() == 0) {
                for (Integer tema : preguntasPorTemas.keySet()) {
                    //si quedan preguntas de ese tema lo añadimos
                    if (preguntasPorTemas.get(tema).size() != 0) {
                        temasCandidatos.add(tema);
                    }
                }
            }
            // Añadimos la pregunta a la lista de preguntas que queremos
            int TcandidatoIndex = random.nextInt(0, temasCandidatos.size());
            Integer Tcandidato = temasCandidatos.get(TcandidatoIndex);
            int Pcandidata = random.nextInt(0, preguntasPorTemas.get(Tcandidato).size());
            preguntasParaMezclar.add(preguntasPorTemas.get(Tcandidato).get(Pcandidata));
            // Las eliminamos para no coger repetidas
            temasCandidatos.remove(TcandidatoIndex);
            preguntasPorTemas.get(Tcandidato).remove(Pcandidata);
            if (preguntasPorTemas.get(Tcandidato).size() == 0) {
                preguntasPorTemas.remove(Tcandidato);
            }
        }

        ////////////////////////////////////////////////////
        // Calculamos el número de versiones que va a haber.
        // Para sacar el numero de versiones tenemos que saber cuantas respuestas tienen las preguntas
        // No podemos suponer que todas tienen 4 porque si hay una con 3, no vamos a poder hacer las mismas versiones,
        // así que nos quedamos con el mínimo valor.
        int numeroMinRespuestas = 0; // Donde guardamos el numero de respuestas mínimo de las preguntas que hemos seleccionado
        // Recorremos la lista de preguntas seleccionadas y miramos su número de respuestas
        for (int i = 0; i < preguntasParaMezclar.size(); i++) {
            if (numeroMinRespuestas == 0) {
                numeroMinRespuestas = preguntasParaMezclar.get(i).getRespuestasDePregunta().size();
            } else {
                if (preguntasParaMezclar.get(i).getRespuestasDePregunta().size() < numeroMinRespuestas) {
                    numeroMinRespuestas = preguntasParaMezclar.get(i).getRespuestasDePregunta().size();
                }
            }
        }

        int numVersionesDiferentes;
        //si es tipo test
        if (numeroMinRespuestas == 2) {
            numVersionesDiferentes = preguntasParaMezclar.size();
            logger.info("Como es un exámen con preguntas tipo test, no se tendrá en cuenta si se repiten las respuestas.");
        } else {
            numVersionesDiferentes = numeroMinRespuestas;
        }
        logger.info("Las preguntas seleccionadas permiten un máximo de " + numVersionesDiferentes + " versiones diferentes.");
        logger.info("Has seleccionado un máximo de " + numMaxDeVersiones + " versiones diferentes.");

        if (numMaxDeVersiones < numVersionesDiferentes) {
            numVersionesDiferentes = numMaxDeVersiones;
        }

        ////////////////////////////////////////////////
        // Comenzamos con la generación de los exámenes
        Examen examenTemp;
        // Para cada version
        for (int indexExamen = 0; indexExamen < numVersionesDiferentes; indexExamen++) {
            examenTemp = new Examen();
            examenTemp.setVersion(obtenerVersion(indexExamen, numVersionesDiferentes));

            for (Pregunta p : preguntasParaMezclar) {
                examenTemp.getGrupoDePreguntas().add(p.obtenerCopiaRecursiva());
            }
            logger.debug("Examen creado con version: " + examenTemp.getVersion());

            // Lo incluimos en la lista de exámenes generados.
            examenes.add(examenTemp);
        }

        // Los mezclamos
        mezclarExamenes(examenes);

        // Para debug
        for (int i = 0; i < examenes.size(); i++) {
            logger.trace(lineaDeGuiones);
            logger.trace("Exámen: " + i);
            logger.trace("Version: " + examenes.get(i).getVersion());
            logger.trace("Preguntas:");
            for (int j = 0; j < examenes.get(i).getGrupoDePreguntas().size(); j++) {
                logger.trace("Pregunta: " + j + " : " + examenes.get(i).getGrupoDePreguntas().get(j).getParrafos().get(0).getTextoTotal());
                if (!examenes.get(i).getGrupoDePreguntas().get(j).getParrafos().get(0).getImagenRuta().equals("")) {
                    logger.trace("(imagen)");
                }
                for (int k = 0; k < examenes.get(i).getGrupoDePreguntas().get(j).getRespuestasDePregunta().size(); k++) {
                    logger.trace("Respuesta: " + k + " : " + examenes.get(i).getGrupoDePreguntas().get(j).getRespuestasDePregunta().get(k).getTextoTotal());
                    if (!examenes.get(i).getGrupoDePreguntas().get(j).getRespuestasDePregunta().get(k).getImagenRuta().equals("")) {
                        logger.trace("(imagen)");
                    }
                }
            }
        }
        logger.trace(lineaDeGuiones);
        /////////////

        //Guardamos los examenes
        logger.info(lineaDeGuiones);
        logger.info("Guardando exámenes");
        logger.info(lineaDeGuiones);
        for (Examen e : examenes) {
            if (!lectorEscritorDeOdt.guardarExamen(e)) {
                return;
            }
        }

    }

    // Mezcla las respuestas y las preguntas
    private void mezclarExamenes(ArrayList<Examen> examenesParaMezclar) {
        Random random = new Random();
        // Es importante mezclar las respuestas y luego las preguntas en ese orden.
        // Creamos un array que rellenaremos con las posibles versiones de orden de las preguntas o de las respuestas
        ArrayList<Integer> numVersionesArr = new ArrayList<>();
        int numDeVarianteIndex; // Index del array numVersionesArr
        int numDeVariante; // Valor del elemento de numVersionesArr
        //////////////////////
        // Mezclar respuestas
        for (int indexP = 0; indexP < examenesParaMezclar.get(0).getGrupoDePreguntas().size(); indexP++) {
            // Mezclamos esta pregunta en todos los examenes, cada uno con un orden diferente
            for (Examen e : examenesParaMezclar) {
                if (numVersionesArr.size() == 0) {
                    // Rellenamos el array con las posibles versiones de orden de respuestas
                    for (int i = 0; i < examenesParaMezclar.get(0).getGrupoDePreguntas().get(indexP).getRespuestasDePregunta().size(); i++) {
                        numVersionesArr.add(i);
                    }
                }
                numDeVarianteIndex = random.nextInt(0, numVersionesArr.size());
                numDeVariante = numVersionesArr.get(numDeVarianteIndex);
                numVersionesArr.remove(numDeVarianteIndex);
                mezclarRespuestas(e.getGrupoDePreguntas().get(indexP), numDeVariante);
            }
        }
        numVersionesArr.clear();
        /////////////////////
        // Mezclar preguntas
        // Rellenamos el array con las posibles versiones de orden de preguntas
        for (int i = 0; i < examenesParaMezclar.get(0).getGrupoDePreguntas().size(); i++) {
            numVersionesArr.add(i);
        }
        // Mezclamos las preguntas en todos los examenes, cada uno con un orden diferente
        for (Examen e : examenesParaMezclar) {
            numDeVarianteIndex = random.nextInt(0, numVersionesArr.size());
            numDeVariante = numVersionesArr.get(numDeVarianteIndex);
            numVersionesArr.remove(numDeVarianteIndex);
            mezclarPreguntas(e, numDeVariante);
        }
    }

    // Mezcla las preguntas de un examen
    private void mezclarPreguntas(Examen e, int numDeVariante) {
        for (int i = 0; i < numDeVariante; i++) {
            for (int indexP = 0; indexP < (e.getGrupoDePreguntas().size() - 1); indexP = indexP + 2) {
                intercambiarP(e, indexP, indexP + 1);
            }
            for (int indexP = 1; indexP < (e.getGrupoDePreguntas().size() - 1); indexP = indexP + 2) {
                intercambiarP(e, indexP, indexP + 1);
            }
        }
    }

    // Mezcla las respuestas de una pregunta
    private void mezclarRespuestas(Pregunta p, int numDeVariante) {
        for (int i = 0; i < numDeVariante; i++) {
            for (int indexR = 0; indexR < (p.getRespuestasDePregunta().size() - 1); indexR = indexR + 2) {
                intercambiarR(p, indexR, indexR + 1);
            }
            for (int indexR = 1; indexR < (p.getRespuestasDePregunta().size() - 1); indexR = indexR + 2) {
                intercambiarR(p, indexR, indexR + 1);
            }
        }
    }

    // Intercambia dos preguntas de un examen
    private void intercambiarP(Examen e, int a, int b) {
        Pregunta pTemp = e.getGrupoDePreguntas().get(a); // Guardamos a
        e.getGrupoDePreguntas().set(a, e.getGrupoDePreguntas().get(b)); // Ponemos b en a
        e.getGrupoDePreguntas().set(b, pTemp); // Ponemos a en b
    }

    // Intercambia dos respuestas de una pregunta
    private void intercambiarR(Pregunta p, int a, int b) {
        Parrafo rTemp = p.getRespuestasDePregunta().get(a);
        p.getRespuestasDePregunta().set(a, p.getRespuestasDePregunta().get(b));
        p.getRespuestasDePregunta().set(b, rTemp);
    }

    // Obtiene la letra de la version
    private String obtenerVersion(int numero, int nvers) {
        StringBuilder resultado = new StringBuilder();
        // Si solo hay una letra de versión
        if (letrasDeVersiones.length == 1) {
            for (int i = 0; i < numero; i++) {
                resultado.append(letrasDeVersiones[0]);
            }
            return resultado.toString();
        }

        // Si hay varias letras para la version
        if (numero < letrasDeVersiones.length) {
            return String.valueOf(letrasDeVersiones[numero]);
        }

        while (numero > 0) {
            resultado.insert(0, letrasDeVersiones[numero % letrasDeVersiones.length]);
            numero = numero / letrasDeVersiones.length;
        }

        while (resultado.length() < obtenerNumLetrasVersiones(nvers)) {
            resultado.insert(0, letrasDeVersiones[0]);
        }

        return resultado.toString();
    }

    // Obtiene la longitud de la cadena de version
    private int obtenerNumLetrasVersiones(int num) {
        int numLetras = 1;
        // Si solo hay una letra de version
        if (letrasDeVersiones.length == 1) {
            numLetras = num;
        } else {
            // Si hay varias letras para la version
            while (num > letrasDeVersiones.length) {
                numLetras++;
                num = num / letrasDeVersiones.length;
            }
        }

        logger.debug("Numero de letras para la version: " + numLetras);
        return numLetras;
    }

    // Convierte los si en true y el resto en false
    private boolean getBoolean(String palabra) {
        return (palabra.trim().toLowerCase(Locale.ROOT).equals("si"));
    }

}
