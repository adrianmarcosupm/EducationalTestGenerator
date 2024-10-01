# EducationalTestGenerator

- [English](#english)

- [Espanol](#espanol)

---

## English

### Description
This repository contains the final degree project of a multiple-choice exam generator with random questions from a question bank and a template.

It was developed using IntelliJ.
For other IDEs, the ".idea" folder is not necessary.

The libraries used in the project, included in the "pom.xml" file, are:

- **org.odftoolkit odfdom-java** version: **0.12.0**
- **org.fusesource.jansi jansi** version: **2.4.1**
- **org.apache.maven.plugins maven-shade-plugin** version: **3.6.0**
- **com.akathist.maven.plugins.launch4j launch4j-maven-plugin** version: **2.5.1**
- **org.apache.maven.plugins maven-resources-plugin** version: **3.3.1**

### Compilation instructions:

1. IntelliJ with the [Maven](https://maven.apache.org/) extension or any IDE that supports [Maven](https://maven.apache.org/) is required.

2. You must have downloaded JDK version 17.0.12 or later.

3. The "pom.xml" file contains the necessary libraries for its use.

4. Open the project with the chosen IDE and select JDK version 17.0.12 or a more recent version.

5. In the chosen IDE, search for and execute the Maven install or package action.

6. Go to the "target/" directory and check the software has been compiled.

### Usage Instructions:

1. The Java Virtual Machine is required. Tested with Java JRE 17.0.12. Newer versions are also supported.

2. Edit the sample template file: "Plantilla.odt". Go to the [template file](#template-file) section for more instructions.

3. Edit the sample question bank: "Banco_De_Preguntas.odt". Go to the [question bank](#question-bank) section for more instructions.

4. Adjust the configuration file: "GeneradorConfiguracion.txt". Go to the [configuration file](#configuration-file) section for more instructions.

5. Ensure that the template, question bank, and configuration files are in the same directory as the program, or their paths are correctly configured in the [configuration file](#configuration-file).

6. If you are on Windows, to run the generator simply go to the "target/" directory and open the **EducationalTestGenerator-1.0.exe** file, or, if preferred, type **java -jar EducationalTestGenerator-1.0.jar** in your operating system's terminal.

7. If you are on another operating system, go to the "target/" directory and type **java -jar EducationalTestGenerator-1.0.jar** in your operating system's terminal.

### Optional Usage Instructions:

It is possible to make the software display additional information in the log messages by running it with the argument **log num**, where num is a number from 1 to 5. Level 1 corresponds to the minimum level of log messages, which shows only errors, and 5 is the maximum level of log messages. If the argument is not specified, the default log level is 3, which shows informational messages.
Example of use: "java -jar EducationalTestGenerator-1.0.jar log 4"

### Template file:

Every exam will contain this file that can be used to give instructions to students.

Questions will be added after the last character of the template. If you want the exam to start on the second page, you can add blank spaces.

You can use the tag **{{¡VERSION¡}}** anywhere in the template file to display the exam version.

### Question bank:

Exams will retrieve questions and answers from this file.

Each question must follow this format and order:

1. Tag **{{¡PREGUNTA¡}}** The lines that follow will contain the question. This section can include one image.

2. Tag **{{¡RESPUESTAS¡}}** The lines that follow will list the possible answers. Each line represents one possible answer and can include one image.

3. Tag **{{¡METADATOS¡}}** The lines that follow will contain metadata for the question. Each line should follow this format:
    ~~~ 
    key value 
    ~~~
    - The currently supported metadata are: 
        - **Tema** (REQUIRED) The topic to which the question belongs.
        - **Dificultad** (REQUIRED if **Dificultad adaptada** set to **si**) The difficulty level as determined by the teacher.

### Configuration file:

The configuration file allows teachers to adjust software settings without requiring technical knowledge.

- Archivo de plantilla: Specifies the name and location of the template file. A path can be specified, for example, “templates/Template_2024.odt.”

- Archivo del banco de preguntas: Defines the name and location of the file that contains the questions.

- Directorio para guardar los examenes: Indicates the directory where the generated exams will be saved.
---

## Espanol

### Descripcion
Este repositorio contiene el proyecto fin de grado de un generador de examenes tipo test con preguntas aleatorias a partir de un banco de preguntas y una plantilla.

Se ha desarrollado utilizando IntelliJ.
Para otros IDE no es necesaria la carpeta ".idea".

Las librerias que utiliza el proyecto, incluidas en el archivo "pom.xml" son:

- **org.odftoolkit odfdom-java** version: **0.12.0**
- **org.fusesource.jansi jansi** version: **2.4.1**
- **org.apache.maven.plugins maven-shade-plugin** version: **3.6.0**
- **com.akathist.maven.plugins.launch4j launch4j-maven-plugin** version: **2.5.1**
- **org.apache.maven.plugins maven-resources-plugin** version: **3.3.1**

### Instrucciones de compilacion:

1. Se requiere IntelliJ con la extension [Maven](https://maven.apache.org/) o cualquier IDE que soporte [Maven](https://maven.apache.org/).

2. Es necesario tener descargada la version JDK 17.0.12 como minimo.

3. El archivo "pom.xml" contiene las librerias necesarias para su uso.

4. Abre el proyecto con el IDE elegido y selecciona como JDK la version 17.0.12 o una mas reciente.

5. En el IDE elegido, busca y ejecuta la accion de maven install o package.

6. Ve al directorio "target/" y asegurate de que el software se ha compilado.

### Instrucciones de uso:

1. Es necesaria la maquina virtual de java. Validado usando java JRE 17.0.12. Tambien son compatibles versiones mas recientes.

2. Edita la plantilla de ejemplo: "Plantilla.odt". Ve a la seccion [archivo de la plantilla](#archivo-de-la-plantilla) para mas instrucciones.

3. Edita el banco de preguntas de ejemplo: "Banco_De_Preguntas.odt". Ve a la seccion [banco de preguntas](#banco-de-preguntas) para mas instrucciones.

4. Ajusta el archivo de configuracion: "GeneradorConfiguracion.txt". Ve a la seccion [archivo de configuracion](#archivo-de-configuracion) para mas instrucciones.

5. Asegurate de que los archivos de la plantilla, del banco de preguntas y la configuracion estan en el mismo directorio que el programa, o has configurado correctamente su direccion en el [archivo de configuracion](#archivo-de-configuracion).

6. Si estas en windows, para ejecutar el generador simplemente ve al directorio "target/" y abre el archivo **EducationalTestGenerator-1.0.exe** o, si lo prefieres, escribe **java -jar EducationalTestGenerator-1.0.jar** en la terminal de tu sistema operativo.

7. Si estas en otro sistema operativo, ve al directorio "target/" y escribe **java -jar EducationalTestGenerator-1.0.jar** en la terminal de tu sistema operativo.

### Instrucciones opcionales de uso:

Es posible hacer que el software muestre informacion adicional en los mensajes de registro si se ejecuta con el argumento **log num** donde num es un numero de 1 a 5. El nivel 1 corresponde al nivel minimo de mensajes de registro, el cual muestra solo errores, y 5 el nivel maximo de mensajes de registro. Si no se especifica el argumento, el nivel de mensajes de registro por defecto es 3, el cual muestra mensajes de informacion.
Ejemplo de uso: "java -jar EducationalTestGenerator-1.0.jar log 4"

### Archivo de la plantilla:

Cada examen contendra este archivo que puede usarse para dar instrucciones a los estudiantes.

Las preguntas se anadiran despues del ultimo caracter de la plantilla. Si quieres que el examen empiece en la segunda pagina puedes anadir espacios en blanco.

Puedes usar la etiqueta **{{¡VERSION¡}}** en cualquier lugar de la plantilla para mostrar la version del examen.

### Banco de preguntas:

Los examenes obtendran las preguntas y respuestas de este archivo.

Cada pregunta debe seguir este formato y orden:

1. Etiqueta **{{¡PREGUNTA¡}}** Las lineas siguientes contendran la pregunta. Esta seccion puede incluir una imagen.

2. Etiqueta **{{¡RESPUESTAS¡}}** Las lineas siguientes contendran las posibles respuestas. Cada linea representa una posible respuesta y puede incluir una imagen.

3. Etiqueta **{{¡METADATOS¡}}** Las lineas siguientes contendran los metadatos de la pregunta. Cada linea debe seguir este formato:
    ~~~ 
    clave valor 
    ~~~
    - Los metadatos soportados actualmente son: 
        - **Tema** (OBLIGATORIO) El tema al que corresponde la pregunta.
        - **Dificultad** (OBLIGATORIO si **Dificultad adaptada** esta en **si**) La dificultad que el profesor considera que tiene la pregunta.

### Archivo de configuracion:

El archivo de configuracion permite a los profesores ajustar el software sin requerir conocimientos tecnicos.

- Archivo de plantilla: Especifica el nombre y la ubicacion del archivo de plantilla. Se puede especificar una ruta, por ejemplo “plantillas/Plantilla_2024.odt”.

- Archivo del banco de preguntas: Define el nombre y la ubicacion del archivo que contiene las preguntas.

- Directorio para guardar los examenes: Indica el directorio donde se guardaran los examenes generados.

---
