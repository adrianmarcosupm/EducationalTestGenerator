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

        ArrayList<Pregunta> preguntasParaMezclar; // Donde guardamos las preguntas para mezclarlas
        ArrayList<Integer> preguntasACoger = new ArrayList<>(); // La lista con los numeros de preguntas que vamos a querer
        int numPreguntasDelBanco = parseadorBanco.obtenerNumPreguntas(); // Numero de preguntas que hay en el banco

        // Generamos una secuencia de numeros entre 1 y las preguntas del banco, para seleccionar preguntas
        for (int i = 0; i < numPreguntas; i++) {
            // Añadimos la pregunta a la lista de preguntas que queremos
            preguntasACoger.add(random.nextInt(1, numPreguntasDelBanco));
        }

        // Obtenemos esas preguntas del banco de preguntas
        preguntasParaMezclar = parseadorBanco.obtenerPreguntas(preguntasACoger);

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
        // Comenzamos con la generación de versiones.
        for (int i = 0; i < numVersiones; i++) {

        }

        // Mezclamos las respuestas de manera que no se repita ninguna en ninguna version (  ""  )
        //TODO: Mezclamos las preguntas de manera que no se repita ninguna en ninguna version, (o como mucho las admitidas en la configuracion)
        // TODO: guardamos el examen con la version y las preguntas
//        logger.info(lineaDeGuiones);
//        logger.info("Guardando exámenes");
//        logger.info(lineaDeGuiones);
//        parseadorPlantilla.guardarExamen("A", preguntasParaMezclar);
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
