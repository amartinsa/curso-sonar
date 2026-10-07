# curso-sonar

Curso de **1 hora sobre SonarQube como herramienta de calidad de código**:
desde cero hasta analizar un proyecto real y dejar el *quality gate* en verde.

**Ver el curso →** la web publicada en GitHub Pages.

---

## Contenido

| Fichero | Qué es |
|---|---|
| `index.html` | Portada: barra de tiempos e índice de las 9 diapositivas. Es lo que se publica en Pages. |
| `01-que-es.html` … `09-referencias.html` | Las 9 diapositivas del curso, una por punto. |
| `assets/curso.css` | Estilos compartidos de portada y diapositivas. |
| `assets/curso.js` | Tema, navegación con flechas y pantalla completa. |
| `resumen.html` | Chuleta de una página, imprimible. |
| `corta/` | **Versión reducida**: portada, 5 diapositivas, su propia chuleta y su propio guion. |
| `GUION.md` | Guion minuto a minuto con los comandos literales, el test que se pega en directo, las notas del formador y el plan B. |
| `PREPARACION.md` | Todo lo que se prepara en SonarQube **antes** de la clase y la limpieza de después. |
| `demo-app/` | El proyecto Maven Java 17 **deliberadamente malo** que se analiza en vivo. |
| `sonar.ps1` | Atajo que pregunta la URL y el token y lanza el análisis sin guardar nada. |

Navegación en la web: `←` `→` cambia de diapositiva, `Home` va a la portada,
`End` al cierre y `F` entra en pantalla completa. Sin JS todo sigue funcionando,
porque la navegación son enlaces normales.

## Dos versiones

| | Completa | Reducida |
|---|---|---|
| Entrada | `index.html` | `corta/index.html` |
| Diapositivas | 9 (`01…09`) | 5 (`01…05`) |
| Dura | 60 min | 60 min |
| Quality Gate | `Curso — Demo` (4 condiciones) | `Curso — Demo (corta)` (3 condiciones) |
| Chuleta y guion | `resumen.html` · `GUION.md` | `corta/resumen.html` · `corta/GUION.md` |

La reducida **no** incluye la comparativa de ediciones ni nada de cobertura ni
JaCoCo: no se crea ningún test en clase. El vocabulario aparece **después** de la
práctica, explicado con los issues ya visibles en pantalla.

La barra de tiempos está en las dos: en la portada con su rótulo y en cada
diapositiva marcando el minuto en el que estás.

## Requisitos para hacer la demo

- JDK **17** o superior y Maven 3.9.x.
- Una instancia de SonarQube Server (**Community 10.4** es la probada).
- Administrador de esa instancia, para crear el *quality gate* del curso.

> **SonarQube 10.4 solo soporta Java 8, 11 y 17.** Por eso el proyecto compila
> con `--release 17` aunque en tu máquina tengas un JDK más nuevo.

## Ejecutar la demo

```powershell
cd demo-app
mvn clean verify
```

Y para analizarla contra tu servidor (el token nunca va en un fichero):

```powershell
mvn clean verify sonar:sonar `
  "-Dsonar.host.url=TU-SONARQUBE" `
  "-Dsonar.token=squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
```

En PowerShell las comillas son obligatorias: sin ellas la `//` de la URL parte
el argumento y Maven no encuentra el plugin. Alternativa sin `-D`: exportar
`SONAR_HOST_URL` y `SONAR_TOKEN` como variables de entorno.

O `.\sonar.ps1` en la raíz, que pregunta ambos y no los conserva.

## Publicar

Repo **público** en GitHub, `index.html` en la raíz, `.nojekyll` incluido, y en
**Settings → Pages** seleccionar la rama `main` y la carpeta `/`. La URL queda
siendo `https://<tu-usuario>.github.io/curso-sonar/`.

No hay CI ni `.github/workflows` a propósito: el análisis se lanza a mano.

## Nota sobre credenciales

Ningún fichero de este repositorio contiene una URL real de servidor ni un token.
Todos los sitios donde hace falta usar **`TU-SONARQUBE`** y el ejemplo
**`squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX`**.
