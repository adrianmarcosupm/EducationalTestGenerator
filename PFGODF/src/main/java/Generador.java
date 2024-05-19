import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private boolean tamanioVariableDeLetra;
    private final String stringAjusteTamanioVariableDeLetra = "Tamaño variable de letra si/no";
    private int tamanioMinimoDeLetra;
    private final String stringAjusteTamanioMinimoDeLetra = "Tamaño mínimo de letra";
    private int tamanioMaximoDeLetra;
    private final String stringAjusteTamanioMaximoDeLetra = "Tamaño máximo de letra";


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


        try {
            BufferedReader reader = new BufferedReader(new FileReader("GeneradorConfiguracion.txt"));
            String linea, ajuste, valor;
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
                    } else if (ajuste.equals(stringAjusteTamanioVariableDeLetra)) {
                        tamanioVariableDeLetra = getBoolean(valor);
                    } else if (ajuste.equals(stringAjusteTamanioMinimoDeLetra)) {
                        tamanioMinimoDeLetra = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteTamanioMaximoDeLetra)) {
                        tamanioMaximoDeLetra = Integer.parseInt(valor);
                    }
                }
            }
            // Imprimimos los valores para que los vea el usuario
            imprimirConfiguracion();
        } catch (Exception e) {
            logger.error("Error al leer el archivo de configuracion: " + e.getMessage());
        }
    }

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
        logger.info(stringAjusteTamanioVariableDeLetra + " : " + tamanioVariableDeLetra);
        logger.info(stringAjusteTamanioMinimoDeLetra + " : " + tamanioMinimoDeLetra);
        logger.info(stringAjusteTamanioMaximoDeLetra + " : " + tamanioMaximoDeLetra);
        logger.info(lineaDeGuiones);
    }

    public void generar() {
        Random random = new Random();
//      LectorEscritorDeOdt parseadorPlantilla = new LectorEscritorDeOdt(plantilla, directorioSalida);
        LectorEscritorDeOdt parseadorBanco = new LectorEscritorDeOdt(bancoDePreguntas, directorioSalida);

        ArrayList<Examen> examenes = new ArrayList<>(); // Donde guardamos los exámenes generados.
        ArrayList<Pregunta> preguntasParaMezclar; // Donde guardamos las preguntas para mezclarlas
        ArrayList<Integer> preguntasACoger = new ArrayList<>(); // La lista con los numeros de preguntas que vamos a querer
        int numPreguntasDelBanco = parseadorBanco.obtenerNumPreguntas(); // Numero de preguntas que hay en el banco

        // Generamos una secuencia de numeros entre 1 y las preguntas del banco, para seleccionar preguntas
        // Para no coger repetidas
        ArrayList<Integer> candidatos = new ArrayList<>();
        for (int i = 1; i <= numPreguntasDelBanco; i++) {
            candidatos.add(i);
        }
        for (int i = 0; i < numPreguntas; i++) {
            // Añadimos la pregunta a la lista de preguntas que queremos
            int candidato = random.nextInt(0, candidatos.size());
            preguntasACoger.add(candidatos.get(candidato));
            candidatos.remove(candidato);
        }
        logger.debug("Tamaño de preguntasACoger: " + preguntasACoger.size());

        // Obtenemos esas preguntas del banco de preguntas
        preguntasParaMezclar = parseadorBanco.obtenerPreguntas(preguntasACoger);
        logger.debug("Tamaño de preguntasParaMezclar: " + preguntasParaMezclar.size());

        if (preguntasParaMezclar == null) {
            return; // Los errores son mostrados antes de llegar aqui
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

        int numVersionesDiferentes = Math.min(preguntasParaMezclar.size(), numeroMinRespuestas);
        logger.info("Las preguntas seleccionadas permiten un máximo de " + numVersionesDiferentes + " versiones diferentes.");
        logger.info("Has seleccionado un máximo de " + numMaxDeVersiones + " versiones diferentes.");
        if (numMaxDeVersiones < numVersionesDiferentes) {
            numVersionesDiferentes = numMaxDeVersiones;
        }

        ////////////////////////////////////////////////
        // Comenzamos con la generación de los exámenes
        Examen examenTemp;
        Pregunta preguntaTemp;
        Respuesta respuestaTemp;
        // Para cada version
        for (int indexExamen = 0; indexExamen < numVersionesDiferentes; indexExamen++) {
            examenTemp = new Examen();
            examenTemp.setVersion(obtenerVersion(indexExamen, numVersionesDiferentes));
            logger.debug("Examen creado con version: " + examenTemp.getVersion());
            for (Pregunta p : preguntasParaMezclar) {
                examenTemp.getPreguntas().add(p.obtenerCopiaRecursiva());
            }

            // Lo incluimos en la lista de exámenes generados.
            examenes.add(examenTemp);
        }

        // Los mezclamos
        mezclarExamenes(examenes);

        // Para debug
        for (int i = 0; i < examenes.size(); i++) {
            logger.debug(lineaDeGuiones);
            logger.debug("Exámen: " + i);
            logger.debug("Version: " + examenes.get(i).getVersion());
            logger.debug("Preguntas:");
            for (int j = 0; j < examenes.get(i).getPreguntas().size(); j++) {
                logger.debug("Pregunta: " + j + " : " + examenes.get(i).getPreguntas().get(j).getTextos().get(0).toString());
                for (int k = 0; k < examenes.get(i).getPreguntas().get(j).getRespuestasDePregunta().size(); k++) {
                    logger.debug("Respuesta: " + k + " : " + examenes.get(i).getPreguntas().get(j).getRespuestasDePregunta().get(k).getTexto());
                }
            }
        }
        logger.debug(lineaDeGuiones);
        /////////////


        //TODO: Mezclamos las preguntas de manera que no se repita ninguna en ninguna version, (o como mucho las admitidas en la configuracion)
        // TODO: guardamos el examen con la version y las preguntas
//        logger.info(lineaDeGuiones);
//        logger.info("Guardando exámenes");
//        logger.info(lineaDeGuiones);
//        parseadorPlantilla.guardarExamen("A", preguntasParaMezclar);
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
        for (int indexP = 0; indexP < examenesParaMezclar.get(0).getPreguntas().size(); indexP++) {
            // Rellenamos el array con las posibles versiones de orden de respuestas
            for (int i = 0; i < examenesParaMezclar.get(0).getPreguntas().get(0).getRespuestasDePregunta().size(); i++) {
                numVersionesArr.add(i);
            }
            // Mezclamos esta pregunta en todos los examenes, cada uno con un orden diferente
            for (Examen e : examenesParaMezclar) {
                numDeVarianteIndex = random.nextInt(0, numVersionesArr.size());
                numDeVariante = numVersionesArr.get(numDeVarianteIndex);
                numVersionesArr.remove(numDeVarianteIndex);
                mezclarRespuestas(e.getPreguntas().get(indexP), numDeVariante);
            }
        }
        numVersionesArr.clear();
        /////////////////////
        // Mezclar preguntas
        // Rellenamos el array con las posibles versiones de orden de preguntas
        for (int i = 0; i < examenesParaMezclar.get(0).getPreguntas().size(); i++) {
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
            for (int indexP = 0; indexP < (e.getPreguntas().size() - 1); indexP = indexP + 2) {
                intercambiarP(e, indexP, indexP + 1);
            }
            for (int indexP = 1; indexP < (e.getPreguntas().size() - 1); indexP = indexP + 2) {
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
        Pregunta pTemp = e.getPreguntas().get(a); // Guardamos a
        e.getPreguntas().set(a, e.getPreguntas().get(b)); // Ponemos b en a
        e.getPreguntas().set(b, pTemp); // Ponemos a en b
    }

    // Intercambia dos respuestas de una pregunta
    private void intercambiarR(Pregunta p, int a, int b) {
        Respuesta rTemp = p.getRespuestasDePregunta().get(a);
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
        return (palabra.trim().toLowerCase().equals("si"));
    }

}
