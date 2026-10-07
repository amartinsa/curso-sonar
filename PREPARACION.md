# PREPARACIÓN — lo que haces tú en SonarQube

> Este fichero es **material de apoyo del formador y de quien prepare la sesión**.
> No se lee en clase: se ejecuta antes.
>
> Instancia: **`TU-SONARQUBE`** — sustitúyelo por la URL real de tu servidor.
> Ejemplo de token: **`squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX`** (lo generas tú, nunca está escrito aquí).

La instancia es **compartida**: hay otros proyectos y otros usuarios. Todo lo que
sigue **solo toca un gate y un proyecto nuevos**. Al final hay una comprobación
de que no has movido nada más.

---

## 0. Requisitos

| | |
|---|---|
| JDK | **17 o superior instalado** (`java -version`). El proyecto compila con `--release 17` porque **SonarQube 10.4 solo soporta Java 8, 11 y 17**. |
| Maven | 3.9.x (`mvn -v`) |
| Git | con tu GitHub personal configurado |
| Acceso a SonarQube | permiso de **administrador** (lo tienes) |

---

## 1. Crear los Quality Gates del curso

**Por qué no vale `Sonar way`:** sus 4 condiciones son *cero issues nuevos*,
*hotspots revisados 100 %*, *cobertura ≥ 80 %* y *duplicación ≤ 3 %*. Eso es
imposible de conseguir en una hora con un proyecto que trae defectos a propósito.
Nos quedamos verde, pero con condiciones que se pueden alcanzar.

Hay **dos** gates, uno por versión del curso. Se crean igual: menú lateral →
**Quality Gates** (o directo: `TU-SONARQUBE/admin/quality_gates`) → **Create** →
nombre → condiciones → **Save**. El nombre exacto de la métrica puede variar un
pelo en tu versión; busca el equivalente.

### `Curso — Demo` — versión completa

| Métrica | Operador | Valor | Qué comprueba |
|---|---|---|---|
| **New Bugs** | `>` | `0` | Ningún bug nuevo |
| **New Vulnerabilities** | `>` | `0` | Ninguna vulnerabilidad nueva |
| **New Security Hotspots Reviewed** | `<` | `100` | Todo hotspot revisado |
| **Coverage on New Code** | `<` | `60` | Cobertura ≥ 60 % en código nuevo |

### `Curso — Demo (corta)` — versión reducida

| Métrica | Operador | Valor | Qué comprueba |
|---|---|---|---|
| **New Bugs** | `>` | `0` | Ningún bug nuevo |
| **New Vulnerabilities** | `>` | `0` | Ninguna vulnerabilidad nueva |
| **New Security Hotspots Reviewed** | `<` | `100` | Todo hotspot revisado |

> La versión reducida **no lleva la condición de cobertura**: no se toca JaCoCo
> ni se crea ningún test nuevo en clase.

> **OJO — lo más importante de todo este fichero:**
> **NO marques ninguno de los dos como `Default`.** El gate por defecto de la
> instancia tiene que seguir siendo `Sonar way` para todos los demás proyectos.
> Estos gates solo se asocian al proyecto del curso.

### Comprobación de que no has tocado nada ajeno

- El gate `Sonar way` sigue existiendo y sigue marcado como **Default**.
- `Sonar way` **sigue siendo read-only** con sus 4 condiciones originales.
- En **Projects**, ningún proyecto de otra persona ha cambiado de estado.

Si algo de eso ha cambiado, reviértelo antes de continuar.

---

## 2. Crear el proyecto `curso-sonar-demo`

1. **Projects → Create** (o `TU-SONARQUBE/projects/create`) → **Manually**.
2. Rellena:

   | Campo | Valor |
   |---|---|
   | Name | `Curso Sonar - Demo` |
   | Key | `curso-sonar-demo` |
   | Main branch | `main` |

3. **Create**.

> **Plan B:** si te atascas con la creación manual, no pasa nada. El primer
> análisis crea el proyecto solo — úsalo con la misma key (`curso-sonar-demo`)
> y aparecerá automáticamente.

---

## 3. Asociar el gate al proyecto

1. Entra en **`Curso Sonar - Demo`**.
2. **Project Settings → Quality Gate** (o el engranaje del proyecto → *Quality Gate*).
3. Selecciona el de **la versión que vas a dar**:

   | Versión | Gate |
   |---|---|
   | Completa (`index.html` + `01…09`) | `Curso — Demo` |
   | Reducida (`corta/`) | `Curso — Demo (corta)` |

4. Comprueba que ahora el proyecto muestra ese gate en la cabecera.

`Sonar way` sigue siendo el default de la instancia; **este** proyecto es el
único que apunta a los nuestros.

---

## 4. Generar el token (durante el curso)

Esto se hace **en clase**, delante de la gente, para que vean de dónde sale.

1. Arriba a la derecha → **My Account → Security**.
2. **Generate Tokens**.
3. Nombre tipo `curso-demo`, y si tu versión deja elegir ámbito, elige
   **análisis de proyecto** y `curso-sonar-demo`.
4. **Generate** y **cópialo inmediatamente**: solo se muestra una vez.
5. Se pega en la ventana de PowerShell **sin cerrarla**. No se guarda en ningún
   fichero, no se sube a GitHub, no se pega en ningún chat.

---

## 5. Ejecutar el análisis

```powershell
cd "C:\Users\amartin\PROYECTOS\CTI\Mio Workspace\curso-sonar\demo-app"

mvn clean verify sonar:sonar `
  "-Dsonar.host.url=TU-SONARQUBE" `
  "-Dsonar.token=squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
```

**Las comillas son obligatorias en PowerShell.** Sin ellas la `//` de la URL
parte el argumento, Maven no encuentra ningún plugin y sale
`No plugin found for prefix`. Verificado en local.

O con el atajo (pregunta URL y token, no guarda nada):

```powershell
cd "C:\Users\amartin\PROYECTOS\CTI\Mio Workspace\curso-sonar"
.\sonar.ps1
```

**Qué debe pasar:** `BUILD SUCCESS`, y el proyecto en SonarQube con el gate
asociado (§3) **en rojo**.

### Si el gate sale rojo es correcto

Es lo esperado en el primer análisis. Condiciones que deben fallar:

- **New Bugs** → hay `java:S1206` (`equals` sin `hashCode`) sin corregir.
- **New Security Hotspots Reviewed** → está `java:S4790` (MD5) sin revisar.
- **New Vulnerabilities** → puede fallar o no. Ver el apartado siguiente.
- **Coverage on New Code** → el proyecto arranca en **26 %** y pide 60 %.
  *Solo en la versión completa; la reducida no lleva esta condición.*

---

## 6. Revisión obligatoria tras el primer análisis

Abre **Measures / Issues** y anota los números reales. Son los que saldrán en
pantalla y no puedes equivocarte delante de la gente:

| Métrica | Qué mirar |
|---|---|
| **Bugs** | Debe haber **1**: `java:S1206` en `Cliente.java` |
| **Vulnerabilities** | Fíjate si es 0 o más (ver nota abajo) |
| **Security Hotspots** | Debe haber **1**: `java:S4790` en `ServicioHuellas.java` |
| **Code Smells** | Será la mayoría — el gate no los mira, y esa es la idea |
| **Coverage** | ~26 % *— solo versión completa* |
| **Duplicated Lines** | Hay bloques duplicados a propósito |

### Nota sobre `New Vulnerabilities`

`java:S2076` (inyección de comando en `Runtime.exec`) se detecta con **taint
analysis**: solo se reporta como *vulnerability* si el analizador puede seguir
el dato desde una **fuente** externa hasta el *sink*. En la demo la fuente está
puesta a propósito: `ServicioAlertas.pingDesdePeticion` lee el host con
`HttpServletRequest.getParameter("host")` y lo concatena en la orden del
sistema.

- **Si sale > 0**, que es lo esperado: hay un flujo que enseñar — `getParameter`
  → `ping` → `Runtime.exec` — y hay que corregirlo.
- **Si sale 0**, el analizador no ha reconocido la fuente en tu versión. No rompe
  la clase: es una de las limitaciones del análisis estático y por eso existe la
  revisión humana. El gate sigue fallando por **New Bugs**, que no depende de esto.

En **cualquiera** de los dos casos, el código de `ServicioAlertas` está ahí para
comentarlo: el `ping` con entrada concatenada es un problema real de seguridad
hasta que se demuestra lo contrario.

---

## 7. Limpieza después del curso

1. **My Account → Security** → borra el token del curso.
2. **Projects → `Curso Sonar - Demo`** → **Delete Project**.
3. **Quality Gates** → **Delete** en `Curso — Demo` **y** en `Curso — Demo (corta)`.
4. Vuelve a comprobar que `Sonar way` sigue siendo el Default y que los demás
   proyectos están como estaban.

---

## Referencia: administración de la plataforma

Material de consulta. No se imparte, pero conviene saber dónde están las cosas.

| Qué | Dónde |
|---|---|
| Usuarios y grupos | **Administration → Users** |
| Permisos globales | **Administration → Permission Groups** |
| Proyectos y sus permisos | pestaña **Permissions** dentro de cada proyecto |
| Quality Gates | **Administration → Quality Gates** |
| Quality Profiles (reglas por lenguaje) | **Quality Profiles** en el menú principal |
| Reglas disponibles | **Coding Rules** |
| Ajustes globales de analizador | **Administration → General Settings** |
| Actualización de la instancia | **Administration → Configurations → Update** |
| Logs y fallos de análisis | **Administration → Background Tasks** |
| Copias de seguridad (BBDD) | fuera de SonarQube: es tarea del servidor |

**Permisos que importan para este curso**

- *Administer Quality Profiles* / *Administer Quality Gates* → permisos **globales**,
  necesarios para crear el gate.
- *Administer* sobre el proyecto → para asociar el gate a un proyecto.
- **Ver** un quality gate: lo tienen **todos** por defecto — cualquiera puede
  consultar los gates; editarlos es otra cosa.

**Reglas propias:** se pueden crear en Community, pero dos caminos:
desde una **plantilla** (reglas XPath para XML) o escribiendo un **plugin Java**
que se deja en `extensions/plugins` y obliga a reiniciar el servidor. Lo que de
verdad se compra es otra cosa: más reglas de fábrica, reglas priorizadas en el
gate, patrones de secretos propios y configuración del motor de seguridad.
