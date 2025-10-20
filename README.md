Descripción
----------
Proyecto de estructuras de datos y algoritmos desarrollado con NetBeans. Contiene el código fuente del ejercicio/entrega para la asignatura.

Estructura del repositorio
--------------------------
- `nbproject/` : configuración del proyecto NetBeans (no subir archivos privados).
- `src/`       : código fuente Java.
- `build/`     : artefactos de compilación (generados).
- `build.xml`  : script de Ant para compilar/ejecutar.
- `manifest.mf`: metadatos del proyecto.

Requisitos
----------
- Java JDK 8+ (ajusta según la versión que uses).
- NetBeans IDE (opcional).
- Ant (si usas la línea de comandos).

Cómo compilar y ejecutar
-----------------------
Desde NetBeans:
1. Importa/abre el proyecto (File → Open Project → selecciona la carpeta del repo).
2. Run → Clean and Build, o Run para ejecutar.

Desde la terminal (si tienes Ant instalado):
```bash
cd ruta/al/proyecto
ant
ant run    # si el build.xml define una tarea 'run'
```

Notas sobre Git
---------------
- He incluido un .gitignore local para evitar subir carpetas de build y settings privados de NetBeans.
- La rama principal es `main` y el repositorio remoto está en https://github.com/Dcarvajal13/Proyecto1-EDD

Autor
-----
Derek Carvajal (Dcarvajal13)
