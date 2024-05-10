import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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

        //TODO: terminar algoritmo para mezclar preguntas

        LectorEscritorDeOdt parseadorPlantilla = new LectorEscritorDeOdt(plantilla, directorioSalida);
        LectorEscritorDeOdt parseadorBanco = new LectorEscritorDeOdt(bancoDePreguntas, directorioSalida);

        ArrayList<Pregunta> preguntasParaMezclar = new ArrayList<>(); // Donde guardamos las preguntas para mezclarlas

        //TODO: obtenemos numeros aleatorios entre (1 y numero de Preguntas del banco de preguntas) (en este ejemplo son 15)
        // sacamos esas preguntas del banco de preguntas
        int numPreguntasDelBanco = parseadorBanco.obtenerNumPreguntas();
        ArrayList<Integer> preguntasACoger = new ArrayList<Integer>();
        for (int i = 2; i < 12; i++) { //obtenemos 10 preguntas de ejemplo, de la 2 a la 11
            preguntasACoger.add(i);
        }

        preguntasParaMezclar = parseadorBanco.obtenerPreguntas(preguntasACoger);

        // TODO: calculamos el numero de versiones (min( factorial(preguntas), factorial(respuestas) )

        //TODO: Mezclamos las preguntas de manera que no se repita ninguna en ninguna version, (o como mucho las admitidas en la configuracion)
        // Mezclamos las respuestas de manera que no se repita ninguna en ninguna version (  ""  )

        // TODO: guardamos el examen con la version y las preguntas
        logger.info(lineaDeGuiones);
        logger.info("Guardando exámenes");
        logger.info(lineaDeGuiones);
        parseadorPlantilla.guardarExamen("A", preguntasParaMezclar);
    }

    // Convierte los si en true y el resto en false
    private boolean getBoolean(String palabra) {
        return (palabra.trim().toLowerCase().equals("si"));
    }

}
