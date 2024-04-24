# ocr a diccionario japonés
 ![ocr-a-diccionario](https://github.com/facundolucasjimenez/ocr-a-diccionario-japones/assets/103152905/96d00bae-dfae-4733-950e-dc8dfec2a69a)

· Programa escrito en Java para capturar caracteres japoneses en imágenes. Pensado para casos de uso donde no podrían ser copiados de otra forma (principalmente en programas que usan Exclusive Fullscreen<sup>1</sup>), busca simplificar el proceso desde la captura hasta la entrada en un diccionario web en una ventana emergente

· Guarda lecturas en un objeto serializable para practicar con un quiz de opción múltiple (últimos X o X aleatorios)

· Dos motores OCR
 - Google Cloud Vision (requiere setear variable de entorno de sistema GOOGLE_APPLICATION_CREDENTIALS con una api key de google)
 - Tesseract

· Sentido de lectura horizontal o vertical (solo tesseract)

· Solo windows

## Requisitos

· Java 8

## Créditos
Clases MenuPrincipal, Configuración y FrameArea basadas en https://github.com/Kevin-Medzorian/Advanced-Java-Snipping-Tool/

### Notas

1. > "Fullscreen Exclusive mode gives your game complete ownership of the display and allocation of resources of your graphics card"

https://devblogs.microsoft.com/directx/demystifying-full-screen-optimizations/

La transición desde una aplicación en modo pantalla completa exclusiva a otra en modo ventana es tosca y dificulta el proceso de copiado. Es el caso de uso que inspiró el programa en primer lugar
