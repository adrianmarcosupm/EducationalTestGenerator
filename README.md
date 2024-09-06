# EducationalTestGenerator

- [English](#english)

- [Español](#español)

---

## English

### Description
This repository contains the final degree project of a multiple-choice exam generator with random questions from a question bank and a template.

It was developed using IntelliJ.
For other IDEs, the ".idea" folder is not necessary.

The libraries used in the project, included in the "pom.xml" file, are:

- **org.odftoolkit** version: **0.12.0**
- **org.apache.logging.log4j** version: **2.23.1**

### Usage Instructions:

1. IntelliJ with the [Maven](https://maven.apache.org/) extension or any IDE that supports [Maven](https://maven.apache.org/) is required.

2. The "pom.xml" file contains the necessary libraries for its use.

3. The software has been validated with open JDK 21.0.1.

4. Edit the sample template file: "Plantilla.odt". Go to the [template file](#template-file) section for more instructions.

5. Edit the sample question bank: "Banco_De_Preguntas.odt". Go to the [question bank](#question-bank) section for more instructions.

6. Adjust the configuration file: "GeneradorConfiguracion.txt". Go to the [configuration file](#configuration-file) section for more instructions.

7. Compile and run the project.

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

---

## Español

### Descripcion
Este repositorio contiene el proyecto fin de grado de un generador de exámenes tipo test con preguntas aleatorias a partir de un banco de preguntas y una plantilla.

Se ha desarrollado utilizando IntelliJ.
Para otros IDE no es necesaria la carpeta ".idea".

Las librerías que utiliza el proyecto, incluídas en el archivo "pom.xml" son:

- **org.odftoolkit** version: **0.12.0**
- **org.apache.logging.log4j** version: **2.23.1**

### Instrucciones de uso:

1. Se requiere IntelliJ con la extensión [Maven](https://maven.apache.org/) o cualquier IDE que soporte [Maven](https://maven.apache.org/).

2. El archivo "pom.xml" contiene las librerías necesarias para su uso.

3. El software se ha validado con open JDK 21.0.1.

4. Edita la plantilla de ejemplo: "Plantilla.odt". Ve a la sección [archivo de la plantilla](#archivo-de-la-plantilla) para más instrucciones.

5. Edita el banco de preguntas de ejemplo: "Banco_De_Preguntas.odt". Ve a la sección [banco de preguntas](#banco-de-preguntas) para más instrucciones.

6. Ajusta el archivo de configuración: "GeneradorConfiguracion.txt". Ve a la sección [archivo de configuracion](#archivo-de-configuración) para más instrucciones.

7. Compila y ejecuta el proyecto.

### Archivo de la plantilla:

Cada examen contendrá este archivo que puede usarse para dar instrucciones a los estudiantes.

Las preguntas se añadirán después del último carácter de la plantilla. Si quieres que el examen empiece en la segunda página puedes añadir espacios en blanco.

Puedes usar la etiqueta **{{¡VERSION¡}}** en cualquier lugar de la plantilla para mostrar la versión del examen.

### Banco de preguntas:

Los exámenes obtendrán las preguntas y respuestas de este archivo.

Cada pregunta debe seguir este formato y orden:

1. Etiqueta **{{¡PREGUNTA¡}}** Las líneas siguientes contendrán la pregunta. Esta sección puede incluir una imagen.

2. Etiqueta **{{¡RESPUESTAS¡}}** Las líneas siguientes contendrán las posibles respuestas. Cada línea representa una posible respuesta y puede incluir una imagen.

3. Etiqueta **{{¡METADATOS¡}}** Las líneas siguientes contendrán los metadatos de la pregunta. Cada línea debe seguir este formato:
    ~~~ 
    clave valor 
    ~~~
    - Los metadatos soportados actualmente son: 
        - **Tema** (OBLIGATORIO) El tema al que corresponde la pregunta.
        - **Dificultad** (OBLIGATORIO si **Dificultad adaptada** está en **si**) La dificultad que el profesor considera que tiene la pregunta.

### Archivo de configuración:

El archivo de configuración permite a los profesores ajustar el software sin requerir conocimientos técnicos.

---
