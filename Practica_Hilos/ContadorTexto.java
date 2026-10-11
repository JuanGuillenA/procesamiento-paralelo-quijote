import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ContadorTexto {

    /** Resultados parciales o totales del conteo. */
    public static class Resultado {
        long totalPalabras;
        long aparicionesPalabra;
        long palabrasConSecuencia;

        /** Suma otro resultado a este (servirá para combinar los hilos). */
        void sumar(Resultado otro) {
            totalPalabras += otro.totalPalabras;
            aparicionesPalabra += otro.aparicionesPalabra;
            palabrasConSecuencia += otro.palabrasConSecuencia;
        }

        @Override
        public String toString() {
            return "Total de palabras: " + totalPalabras
                    + "\nApariciones de la palabra: " + aparicionesPalabra
                    + "\nPalabras con la secuencia: " + palabrasConSecuencia;
        }
    }

    /**
     * Recorre texto[inicio, fin) una sola vez y cuenta las tres métricas.
     * palabra y secuencia deben venir ya en minúsculas.
     */
    public static Resultado contar(char[] texto, int inicio, int fin,
                                   char[] palabra, char[] secuencia) {
        Resultado r = new Resultado();
        int i = inicio;
        while (i < fin) {
            while (i < fin && !Character.isLetterOrDigit(texto[i])) {
                i++;
            }
            if (i >= fin) {
                break;
            }
            int inicioPalabra = i;
            while (i < fin && Character.isLetterOrDigit(texto[i])) {
                i++;
            }
            int finPalabra = i;

            r.totalPalabras++;
            if (esIgual(texto, inicioPalabra, finPalabra, palabra)) {
                r.aparicionesPalabra++;
            }
            if (contiene(texto, inicioPalabra, finPalabra, secuencia)) {
                r.palabrasConSecuencia++;
            }
        }
        return r;
    }

    private static boolean esIgual(char[] texto, int ini, int fin, char[] objetivo) {
        if (fin - ini != objetivo.length) {
            return false;
        }
        for (int k = 0; k < objetivo.length; k++) {
            if (Character.toLowerCase(texto[ini + k]) != objetivo[k]) {
                return false;
            }
        }
        return true;
    }

    private static boolean contiene(char[] texto, int ini, int fin, char[] sec) {
        for (int p = ini; p + sec.length <= fin; p++) {
            int k = 0;
            while (k < sec.length && Character.toLowerCase(texto[p + k]) == sec[k]) {
                k++;
            }
            if (k == sec.length) {
                return true;
            }
        }
        return false;
    }

    /** Versión secuencial: un único flujo de ejecución sobre todo el texto. */
    public static Resultado secuencial(char[] texto, char[] palabra, char[] secuencia) {
        return contar(texto, 0, texto.length, palabra, secuencia);
    }

    public static void main(String[] args) throws IOException {
        String ruta = args.length > 0 ? args[0] : "quijote.txt";
        String palabra = args.length > 1 ? args[1] : "Quijote";
        String secuencia = args.length > 2 ? args[2] : "ción";

        // La lectura se hace ANTES de medir: el archivo queda cargado en memoria.
        char[] texto = new String(Files.readAllBytes(Paths.get(ruta)),
                StandardCharsets.UTF_8).toCharArray();

        long t0 = System.nanoTime();
        Resultado r = secuencial(texto,
                palabra.toLowerCase().toCharArray(),
                secuencia.toLowerCase().toCharArray());
        long t1 = System.nanoTime();

        System.out.println("Palabra buscada: " + palabra + " | Secuencia: " + secuencia);
        System.out.println(r);
        System.out.printf("Tiempo secuencial: %.3f ms%n", (t1 - t0) / 1_000_000.0);
    }
}
