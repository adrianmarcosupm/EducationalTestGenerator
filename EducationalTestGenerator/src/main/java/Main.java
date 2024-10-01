import org.fusesource.jansi.AnsiConsole;

import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        String entrada = "";

        while (!entrada.equalsIgnoreCase("exit")) {
            System.out.println("Presiona enter para ejecutar el generador o escribe exit para salir.");
            entrada = scanner.nextLine();
            switch (entrada) {
                case "": // Ejecutar el generador
                {
                    long startTime = System.currentTimeMillis();

                    AnsiConsole.systemInstall();

                    int nivelDeLog = 3; // Info por defecto

                    if (args.length > 0) {
                        for (int i = 0; i < args.length; i++) {
                            if ("log".equals(args[i])) {
                                if (i + 1 < args.length) {
                                    try {
                                        nivelDeLog = Integer.parseInt(args[i + 1]);
                                        if (nivelDeLog < 1 || nivelDeLog > 5) {
                                            nivelDeLog = 3; // Lo establecemos a info por defecto
                                            throw new NumberFormatException();
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println(ansi().render("@|red El nivel de mensajes de registro debe se un numero entre 1 y 5|@"));
                                    }
                                } else {
                                    System.out.println(ansi().render("@|red Falta el nivel de mensajes de registro. Debe ser un numero entre 1 y 5|@"));
                                }
                            }
                        }
                    }

                    Generador generador = new Generador(nivelDeLog);
                    generador.generar();

                    AnsiConsole.systemUninstall();

                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;

                    if (nivelDeLog >= 4) {
                        System.out.println("Tiempo de ejecucion: " + elapsedTime + " milisegundos");
                    }

                    break;
                }
                default: {
                    break;
                }
            }

        }

        scanner.close();

    }
}