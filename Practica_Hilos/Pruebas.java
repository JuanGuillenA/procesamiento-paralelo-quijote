import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;


public class Pruebas {

    static final int REPETICIONES = 5;
    static final int CALENTAMIENTO = 5;   
    static final int FACTOR_AMPLIADO = 100;
    static final int[] HILOS = {1, 2, 4};

    public static void main(String[] args) throws IOException, InterruptedException {
        String ruta = args.length > 0 ? args[0] : "quijote.txt";
        char[] palabra = (args.length > 1 ? args[1] : "Quijote").toLowerCase().toCharArray();
        char[] secuencia = (args.length > 2 ? args[2] : "ción").toLowerCase().toCharArray();

        // Carga en memoria (fuera de las mediciones)
        char[] original = new String(Files.readAllBytes(Paths.get(ruta)),
                StandardCharsets.UTF_8).toCharArray();
        char[] ampliado = ampliar(original, FACTOR_AMPLIADO);

        System.out.println("Procesadores lógicos disponibles para la JVM: "
                + Runtime.getRuntime().availableProcessors());
        System.out.println("Caracteres original: " + original.length
                + " | ampliado: " + ampliado.length);

        // Calentamiento: deja que el compilador JIT optimice el código antes de medir,
        // para que la primera configuración medida no salga penalizada.
        for (int i = 0; i < CALENTAMIENTO; i++) {
            for (int h : HILOS) {
                ejecutar(original, palabra, secuencia, h);
            }
        }

        System.out.println();
        System.out.println("Conjunto de datos | Hilos | Tiempo promedio (ms) | Aceleración | Coincide");
        medirConjunto("Original", original, palabra, secuencia);
        medirConjunto("Ampliado", ampliado, palabra, secuencia);
    }

    /** 1 hilo = versión secuencial; más de 1 = versión paralela con Thread. */
    static ContadorTexto.Resultado ejecutar(char[] texto, char[] palabra,
                                            char[] secuencia, int hilos)
            throws InterruptedException {
        if (hilos == 1) {
            return ContadorTexto.secuencial(texto, palabra, secuencia);
        }
        return ContadorParalelo.paralelo(texto, palabra, secuencia, hilos);
    }

    static void medirConjunto(String nombre, char[] texto, char[] palabra, char[] secuencia)
            throws InterruptedException {
        ContadorTexto.Resultado referencia = null;
        double tiempoSecuencial = 0;

        for (int h : HILOS) {
            long suma = 0;
            ContadorTexto.Resultado r = null;
            StringBuilder detalle = new StringBuilder();
            for (int rep = 0; rep < REPETICIONES; rep++) {
                long t0 = System.nanoTime();
                r = ejecutar(texto, palabra, secuencia, h);
                long t1 = System.nanoTime();
                suma += (t1 - t0);
                detalle.append(String.format(Locale.US, "%.3f ", (t1 - t0) / 1_000_000.0));
            }
            double promedioMs = suma / (double) REPETICIONES / 1_000_000.0;

            if (h == 1) {
                referencia = r;
                tiempoSecuencial = promedioMs;
            }
            boolean coincide = r.totalPalabras == referencia.totalPalabras
                    && r.aparicionesPalabra == referencia.aparicionesPalabra
                    && r.palabrasConSecuencia == referencia.palabrasConSecuencia;

            System.out.printf(Locale.US, "%-17s | %-5s | %20.3f | %11.2f | %s%n",
                    nombre, h == 1 ? "1 (sec)" : String.valueOf(h),
                    promedioMs, tiempoSecuencial / promedioMs, coincide ? "SI" : "NO");
            System.out.println("    repeticiones (ms): " + detalle.toString().trim());
        }
        System.out.println("    " + nombre + " -> palabras: " + referencia.totalPalabras
                + ", apariciones: " + referencia.aparicionesPalabra
                + ", con secuencia: " + referencia.palabrasConSecuencia);
    }

    static char[] ampliar(char[] original, int veces) {
        int bloque = original.length + 1;
        char[] resultado = new char[bloque * veces];
        for (int i = 0; i < veces; i++) {
            System.arraycopy(original, 0, resultado, i * bloque, original.length);
            resultado[i * bloque + original.length] = '\n';
        }
        return resultado;
    }
}
