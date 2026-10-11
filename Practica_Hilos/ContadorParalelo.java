
public class ContadorParalelo {

    /** Hilo que procesa el bloque texto[inicio, fin). */
    static class HiloContador extends Thread {
        private final char[] texto;
        private final int inicio;
        private final int fin;
        private final char[] palabra;
        private final char[] secuencia;
        private ContadorTexto.Resultado resultado;

        HiloContador(char[] texto, int inicio, int fin, char[] palabra, char[] secuencia) {
            this.texto = texto;
            this.inicio = inicio;
            this.fin = fin;
            this.palabra = palabra;
            this.secuencia = secuencia;
        }

        @Override
        public void run() {
            resultado = ContadorTexto.contar(texto, inicio, fin, palabra, secuencia);
        }

        ContadorTexto.Resultado getResultado() {
            return resultado;
        }
    }

    
    public static int[] dividir(char[] texto, int numHilos) {
        int n = texto.length;
        int[] limites = new int[numHilos + 1];
        limites[0] = 0;
        limites[numHilos] = n;
        for (int i = 1; i < numHilos; i++) {
            int corte = (int) ((long) n * i / numHilos);
            while (corte < n && Character.isLetterOrDigit(texto[corte])) {
                corte++;
            }
            limites[i] = corte;
        }
        return limites;
    }

    /** Versión paralela con numHilos hilos. */
    public static ContadorTexto.Resultado paralelo(char[] texto, char[] palabra,
                                                   char[] secuencia, int numHilos)
            throws InterruptedException {
        int[] limites = dividir(texto, numHilos);
        HiloContador[] hilos = new HiloContador[numHilos];

        for (int i = 0; i < numHilos; i++) {
            hilos[i] = new HiloContador(texto, limites[i], limites[i + 1], palabra, secuencia);
            hilos[i].start();
        }

        ContadorTexto.Resultado total = new ContadorTexto.Resultado();
        for (HiloContador hilo : hilos) {
            hilo.join();
            total.sumar(hilo.getResultado());
        }
        return total;
    }
}
