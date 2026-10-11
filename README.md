# Actividad práctica 01 – Procesamiento secuencial y paralelo de textos con Java Threads

Aplicación Java que analiza *Don Quijote de la Mancha* (TXT de Project Gutenberg) y compara una implementación secuencial con una paralela basada directamente en la clase `Thread`.

Para el texto se calcula:

1. Número total de palabras.
2. Número de apariciones de una palabra (por defecto `Quijote`).
3. Número de palabras que contienen una secuencia de caracteres (por defecto `ción`).

La búsqueda no distingue entre mayúsculas y minúsculas.

## Estructura

| Archivo | Contenido |
|---|---|
| `ContadorTexto.java` | Implementación secuencial y método `contar()` que procesa un rango del texto. |
| `ContadorParalelo.java` | Implementación paralela: división en bloques sin cortar palabras, `start()`, `join()` y combinación de resultados. |
| `Pruebas.java` | Medición de tiempos con `System.nanoTime()` para 1, 2 y 4 hilos, con el texto original y el ampliado (x100). |
| `quijote.txt` | Texto de entrada en UTF-8. |

## Requisitos

- JDK 11 o superior (probado con OpenJDK Temurin 17.0.15).
- Aproximadamente 1 GB de memoria libre para la versión ampliada del texto.

## Cómo ejecutar

Compilar:

```bash
javac *.java
```

Solo la versión secuencial:

```bash
java ContadorTexto quijote.txt Quijote ción
```

Comparación completa (secuencial, 2 hilos y 4 hilos; original y ampliado):

```bash
java Pruebas quijote.txt Quijote ción
```

Los tres argumentos son opcionales: archivo, palabra a buscar y secuencia de caracteres.

## Criterios de procesamiento

- Una palabra es una secuencia continua de letras o dígitos (`Character.isLetterOrDigit`); cualquier otro carácter actúa como separador.
- El archivo se carga completo en memoria antes de medir. Los tiempos incluyen únicamente el procesamiento (en la versión paralela, también la división, la creación de hilos y la combinación de resultados).
- Antes de medir se realizan 5 ejecuciones de calentamiento para que el compilador JIT optimice el código.
- Cada configuración se repite 5 veces y se reporta el promedio.
- La versión ampliada repite el texto original 100 veces en memoria.

## Resultados obtenidos

Equipo: Intel Core i5-1038NG7 a 2.00 GHz, 4 núcleos físicos / 8 lógicos, 16 GB de RAM, macOS.

| Conjunto de datos | Hilos | Tiempo promedio (ms) | Aceleración |
|---|---|---|---|
| Original | 1 (secuencial) | 17.502 | 1.00 |
| Original | 2 | 8.977 | 1.95 |
| Original | 4 | 19.830 | 0.88 |
| Ampliado | 1 (secuencial) | 1536.175 | 1.00 |
| Ampliado | 2 | 813.658 | 1.89 |
| Ampliado | 4 | 425.127 | 3.61 |

En todos los casos el resultado paralelo coincide con el secuencial: 386 793 palabras, 2 245 apariciones de "Quijote" y 950 palabras con "ción" en el texto original.

El análisis completo está en el informe HTML (`informe.html`).
