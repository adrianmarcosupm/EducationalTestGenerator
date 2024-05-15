import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
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
    private int numVersiones;
    private final String stringAjusteNumVersiones = "Número de versiones";
    private ArrayList<Integer> temas = new ArrayList<>();
    private final String stringAjusteTemas = "Temas a incluir (separados por comas)";
    private int numPreguntas;
    private final String stringAjusteNumPreguntas = "Número de preguntas";
    private int numPreguntasMismoLugar;
    private final String stringAjusteNumPreguntasMismoLugar = "Número de preguntas permitidas en el mismo lugar";
    private int numRespuestasMismoLugar;
    private final String stringAjusteNumRespuestasMismoLugar = "Número de respuestas permitidas en el mismo lugar";

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
                    } else if (ajuste.equals(stringAjusteNumVersiones)) {
                        numVersiones = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteTemas)) {
                        for (String tema : valor.split(",")) {
                            temas.add(Integer.parseInt(tema.strip()));
                        }
                    } else if (ajuste.equals(stringAjusteNumPreguntas)) {
                        numPreguntas = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteNumPreguntasMismoLugar)) {
                        numPreguntasMismoLugar = Integer.parseInt(valor);
                    } else if (ajuste.equals(stringAjusteNumRespuestasMismoLugar)) {
                        numRespuestasMismoLugar = Integer.parseInt(valor);
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
        logger.info(stringAjusteNumVersiones + " : " + numVersiones);
        for (int i = 0; i < temas.size(); i++)
            logger.info(stringAjusteTemas + " : " + temas.get(i));
        logger.info(stringAjusteNumPreguntas + " : " + numPreguntas);
        logger.info(stringAjusteNumPreguntasMismoLugar + " : " + numPreguntasMismoLugar);
        logger.info(stringAjusteNumRespuestasMismoLugar + " : " + numPreguntasMismoLugar);
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
        // Para no coger repetidos
        ArrayList<Integer> candidatos = new ArrayList<>();
        for (int i = 1; i <= numPreguntas; i++) {
            candidatos.add(i);
        }
        for (int i = 0; i < numPreguntas; i++) {
            // Añadimos la pregunta a la lista de preguntas que queremos
            int candidato = random.nextInt(0, candidatos.size());
            preguntasACoger.add(candidatos.get(candidato));
            candidatos.remove(candidato);
        }
        logger.debug("Tamaño de pac: " + preguntasACoger.size());

        // Obtenemos esas preguntas del banco de preguntas
        preguntasParaMezclar = parseadorBanco.obtenerPreguntas(preguntasACoger);
        logger.debug("Tamaño de ppm: " + preguntasParaMezclar.size());

        if (preguntasParaMezclar == null) {
            return; // Los errores son mostrados antes de llegar aqui
        }

        // Para sacar el numero de versiones tenemos que saber cuantas respuestas tienen las preguntas
        // No podemos suponer que todas tienen 4 porque si hay una con 3, no vamos a poder hacer las mismas versiones,
        // así que nos quedamos con el mínimo valor.
        int numeroMinRespuestas = 0; // Donde guardamos el numero de respuestas mínimo de las preguntas que hemos seleccionado
        // Recorremos la lista de preguntas seleccionadas y miramos sus respuestas
        for (int i = 0; i < preguntasParaMezclar.size(); i++) {
            if (numeroMinRespuestas == 0) {
                numeroMinRespuestas = preguntasParaMezclar.get(i).getRespuestasDePregunta().size();
            } else {
                if (preguntasParaMezclar.get(i).getRespuestasDePregunta().size() < numeroMinRespuestas) {
                    numeroMinRespuestas = preguntasParaMezclar.get(i).getRespuestasDePregunta().size();
                }
            }
        }

        try {
            BigInteger numVersionesDiferentes = factorial(Math.min(preguntasParaMezclar.size(), numeroMinRespuestas));
            logger.info("Las preguntas seleccionadas permiten un máximo de " + numVersionesDiferentes + " versiones diferentes.");
            BigInteger numVersionesTemp = BigInteger.valueOf(numVersiones);
            if (numVersiones > numVersionesDiferentes.intValue()) {
                logger.error("Se han seleccionado preguntas que no permiten ese número de versiones.");
            }
        } catch (Exception e) {
            logger.error("No se ha podido calcular el número de versiones máximo. " + e.getMessage());
            return;
        }

        // Comenzamos con la generación del exámen.
        Examen examenTemp;
        // Para cada version
        for (int indexExamen = 0; indexExamen < numVersiones; indexExamen++) {
            examenTemp = new Examen();
            examenTemp.setVersion(obtenerVersion(indexExamen, numVersiones));
            logger.debug("Examen creado con version: " + examenTemp.getVersion());
            ///////////////////////////
            // Mezclamos las preguntas
            // Creamos un array de arrays que contienen los números candidatos en cada posición, para no volverlos a elegir.
            ArrayList<ArrayList<Integer>> arrayCandidatosPreguntas = new ArrayList<>();
            // Insertamos arrays para cada posición de pregunta
            for (int i = 0; i < preguntasParaMezclar.size(); i++) {
                arrayCandidatosPreguntas.add(new ArrayList<>());
            }
            // Rellenamos todas las arrays con los posibles valores
            for (int i = 0; i < arrayCandidatosPreguntas.size(); i++) {
                for (int i2 = 0; i2 < numPreguntas; i2++) {
                    arrayCandidatosPreguntas.get(i).add(i2);
                }
            }
            for (int indexP = 0; indexP < arrayCandidatosPreguntas.size(); indexP++) {
                int candidato = random.nextInt(0, arrayCandidatosPreguntas.get(indexP).size());
                logger.debug("Candidatos en " + indexP + " : ");
                // Para debug
                String candidatosStr = "";
                for (int a = 0; a < arrayCandidatosPreguntas.get(indexP).size(); a++) {
                    candidatosStr = candidatosStr + arrayCandidatosPreguntas.get(indexP).get(a) + " ";
                }
                /////////////
                logger.debug(candidatosStr);
                logger.debug("Candidato seleccionado en " + indexP + " : " + candidato);
                examenTemp.getPreguntas().add(preguntasParaMezclar.get(candidato));
                logger.debug("Candidato seleccionado texto: " + preguntasParaMezclar.get(candidato).getTextos().get(0));
                arrayCandidatosPreguntas.get(indexP).remove(candidato);
                logger.debug("Candidatos despues en " + indexP + " : ");
                // Para debug
                candidatosStr = "";
                for (int a = 0; a < arrayCandidatosPreguntas.get(indexP).size(); a++) {
                    candidatosStr = candidatosStr + arrayCandidatosPreguntas.get(indexP).get(a) + " ";
                }
                logger.debug(candidatosStr);
                /////////////
            }

            // Lo incluimos en la lista de exámenes generados.
            examenes.add(examenTemp);
        }

        ////////////////////////////
        // Mezclamos las respuestas
        // Creamos un array de arrays que contienen los números candidatos en cada posición, para no volverlos a elegir.
        ArrayList<ArrayList<Integer>> arrayCandidatosRespuestas = new ArrayList<>();
        ArrayList<Respuesta> respuestasTemp; // Guardamos aqui las respuestas sin mezclar.
        // Insertamos arrays para cada posición de pregunta
        for (int i = 0; i < examenTemp.getPreguntas().size(); i++) {
            arrayCandidatosRespuestas.add(new ArrayList<>());
        }
        // Rellenamos todas las arrays con los posibles valores
        for (int i = 0; i < arrayCandidatosRespuestas.size(); i++) {
            for (int i2 = 0; i2 < examenTemp.getPreguntas().get(i).getRespuestasDePregunta().size(); i2++) {
                arrayCandidatosRespuestas.get(i).add(i2);
            }
        }
        for (int indexR = 0; indexR < arrayCandidatosRespuestas.size(); indexR++) {
            int candidato = random.nextInt(0, arrayCandidatosRespuestas.get(indexR).size());
            logger.debug("Candidatos en " + indexR + " : ");
            // Para debug
            String candidatosStr = "";
            for (int a = 0; a < arrayCandidatosRespuestas.get(indexR).size(); a++) {
                candidatosStr = candidatosStr + arrayCandidatosRespuestas.get(indexR).get(a) + " ";
            }
            /////////////
            logger.debug(candidatosStr);
            logger.debug("Candidato seleccionado en " + indexR + " : " + candidato);
            examenTemp.getPreguntas().get(indexR).getRespuestasDePregunta().set(indexR, respuestasTemp.get(candidato));
            logger.debug("Candidato seleccionado texto: " + preguntasParaMezclar.get(candidato).getTextos().get(0));
            arrayCandidatosRespuestas.get(indexR).remove(candidato);
            logger.debug("Candidatos despues en " + indexR + " : ");
            // Para debug
            candidatosStr = "";
            for (int a = 0; a < arrayCandidatosRespuestas.get(indexR).size(); a++) {
                candidatosStr = candidatosStr + arrayCandidatosRespuestas.get(indexR).get(a) + " ";
            }
            logger.debug(candidatosStr);
            /////////////
        }

        // Para debug
        for (int i = 0; i < examenes.size(); i++) {
            logger.debug(lineaDeGuiones);
            logger.debug("Exámen " + i + " : ");
            logger.debug("Version: " + examenes.get(i).getVersion());
            logger.debug("Preguntas:");
            for (int j = 0; j < examenes.get(i).getPreguntas().size(); j++) {
                logger.debug("Pregunta " + j + " : " + examenes.get(i).getPreguntas().get(j).getTextos().get(0));
                for (int k = 0; k < examenes.get(i).getPreguntas().get(j).getRespuestasDePregunta().size(); k++) {
                    logger.debug("respuesta " + k + " : " + examenes.get(i).getPreguntas().get(j).getRespuestasDePregunta().get(k));
                }
            }
        }
        logger.debug(lineaDeGuiones);
        /////////////

        // Mezclamos las respuestas de manera que no se repita ninguna en ninguna version (  ""  )
        //TODO: Mezclamos las preguntas de manera que no se repita ninguna en ninguna version, (o como mucho las admitidas en la configuracion)
        // TODO: guardamos el examen con la version y las preguntas
//        logger.info(lineaDeGuiones);
//        logger.info("Guardando exámenes");
//        logger.info(lineaDeGuiones);
//        parseadorPlantilla.guardarExamen("A", preguntasParaMezclar);
    }

    // Mezcla las respuestas las preguntas
    private mezclarExamenes(ArrayList<Examen> examenesParaMezclar)
    {
        // Es importante mezclar las respuestas y luego las preguntas en ese orden.
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

    public BigInteger factorial(int n) {
        if (n < 0) {
            logger.error("El factorial debe ser mayor o igual a cero.");
            return null;
        }
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

}
