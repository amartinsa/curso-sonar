# GUIÓN — clase de 1 hora sobre SonarQube · **versión reducida**

**Objetivo:** que quien entre sin saber nada salga sabiendo qué hace SonarQube,
verlo analizar un proyecto real delante de ellos y tocar el resultado con sus
propias manos.

**Proyecto:** `demo-app/` — Java 17, Maven, **sin base de datos**, todo en memoria.
Contiene defectos a propósito. Está pensado para que el primer análisis salga
**rojo** y el segundo **verde**.

**Servidor:** `TU-SONARQUBE` (Community 10.4.0.87286, compartido).
**Antes de empezar:** tener preparado todo lo de `PREPARACION.md` (§1 a §3),
con el proyecto asociado al gate **`Curso — Demo (corta)`**.

> Esta versión es la **reducida**: 5 diapositivas y un alcance más estrecho.
> Para la versión completa, `GUION.md` en la raíz.

---

## Minuto a minuto

| | Min | Qué pasa |
|---|---|---|
| **1** | 0–6 | Qué es SonarQube y por qué |
| **2** | 6–20 | **Primer análisis en vivo** → gate en rojo |
| **3** | 20–33 | Evaluación: vocabulario sobre la marcha |
| **4** | 33–53 | **Corregir en directo** y re-analizar → verde |
| **5** | 53–60 | Cierre: lo que te llevas y los enlaces |

---

## Min 0–6 — Qué es SonarQube

- **SonarQube es una herramienta de visualización de Sonar**: un sistema que
  evalúa la calidad de tu código.
- Tres piezas: el **analyzer** (corre al construir, en local), el **servidor**
  (recibe, guarda y evalúa las condiciones) y la **interfaz** (visualización del
  resultado, explicaciones, consejos de mejora, seguimiento e histórico).
- **No es una herramienta de pruebas.** No sustituye las pruebas unitarias ni las
  de rendimiento: solo evalúa agujeros y errores de programación, no la lógica
  de negocio.
- El plan de la hora, en cuatro pasos: **analizar → evaluar → corregir → re-analizar**.
- **Quality Gate** = las condiciones que decides cumplir; si una falla, el
  proyecto sale en rojo. Se asocia a cada proyecto.
  **Quality Profile** = el conjunto de reglas activas por lenguaje; cambiarlo no
  cambia las condiciones del proyecto.
- Demo del día: `demo-app`, un procesador de pedidos en memoria. Sin BBDD, sin
  frameworks: solo Java y Maven, para que nadie se pierda en el stack.

---

## Min 6–20 — Primer análisis en vivo (diapositiva 02)

### Generar el token

**My Account → Security → Generate Tokens.** Que vean que se muestra una sola
vez y que no se guarda en ningún sitio.

### Comando literal

```powershell
cd "C:\Users\amartin\PROYECTOS\CTI\Mio Workspace\curso-sonar\demo-app"

mvn clean verify sonar:sonar `
  "-Dsonar.host.url=TU-SONARQUBE" `
  "-Dsonar.token=squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
```

> **Las comillas no son estética.** PowerShell parte `-Dsonar.host.url=http://…`
> por la `//` si va sin comillas, Maven recibe un prefijo de plugin basura y el
> análisis ni se lanza. Verificado: sin comillas falla, con comillas va.

### Qué debe salir

```
[INFO] BUILD SUCCESS
[INFO] ANALYSIS SUCCESSFUL, you can check TU-SONARQUBE/dashboard?id=curso-sonar-demo
```

### Y en la interfaz

El proyecto **`Curso Sonar - Demo`** con el gate **`Curso — Demo (corta)` en ROJO**.

Números esperados (si difieren, manda lo que diga la pantalla en ese momento):

| Métrica | Valor esperado |
|---|---|
| Bugs | **1** — `java:S1206` en `Cliente.java` |
| Vulnerabilities | **0 o más** — ver nota abajo |
| Security Hotspots | **1** — `java:S4790` en `ServicioHuellas.java` |
| Code Smells | La mayoría de los issues — el gate no los mira |

**Nota sobre Vulnerabilities:** `java:S2076` se detecta con taint analysis y solo
salta como *vulnerability* si el analizador considera la entrada como externa.
- **Sale 0** → se enseña igual: es una limitación del análisis estático y por
  eso la revisión humana no es opcional. El gate se pone verde igualmente.
- **Sale > 0** → enseñar el **flujo de datos** que muestra Sonar y corregirlo.

**Lo que NO se toca en esta sesión:** los code smells y la duplicación. Van a
quedar ahí a propósito — el gate no los mira, y esa es exactamente la lección de
por qué defines las condiciones que defines.

Ábrelo en este orden:

1. **Cabecera del proyecto** — el gate en rojo y **cuáles** de sus condiciones
   fallan. Esto es lo primero que mira cualquiera al entrar.
2. **Issues → Bugs** — abrir `Cliente.java`. `equals` está reescrito y `hashCode`
   no: dos objetos con el mismo id se consideran iguales pero no tienen la misma
   posición en un `HashMap`. **Esto es un bug de verdad.**
3. **Security Hotspots** — abrir `ServicioHuellas.java`. El recuadro de revisión:
   explicar los estados **To Review → Acknowledged / Fixed / Safe** y que aquí
   decide una persona, no la herramienta.
4. **Code** — enseñar el código fuente con los issues marcados en la línea:
   el `==` de `esVip`, los 8 parámetros de `registrar`, el `System.out`,
   los bloques idénticos de `procesar`, el `TODO`.

---

## Min 20–33 — Evaluación (diapositiva 03)

**Enunciado que está en pantalla:** todos los issues son **posibles** problemas
de código de distinto nivel de criticidad que se pueden corregir.
Resalta «posibles» al decirlo: es la palabra que cambia todo.

Vocabulario, **apuntando a lo que ya está en pantalla**. Lo que hay que salir
sabiendo distinguir:

| Término | En una frase |
|---|---|
| **Issue** | Cualquier cosa que el analizador encuentra: bug, vulnerability, hotspot o code smell. |
| **Bug** (Reliability) | Código probablemente erróneo. Ej.: `equals()` sin `hashCode()` → `java:S1206`, o un posible `NullPointerException`. |
| **Vulnerability** (Security) | Código que puede provocar errores de datos. Se detecta por **flujo de datos**: desde una fuente hasta un *sink*. Ej.: `Runtime.exec("ping " + host)` con entrada del usuario → `java:S2076`. |
| **Security Hotspot** | Código que debe revisar un humano: posible error de lógica o no cumple el estándar. Ej.: `MessageDigest.getInstance("MD5")` → `java:S4790`. **Hay que revisarlo a mano.** |
| **Code Smell** (Maintainability) | Código difícil de leer o mantener. Ej.: `tipo == "VIP"` en vez de `equals()`, o código que nunca se ejecuta. |
| **Severidad** | Blocker > Critical > Major > Minor > Info. Es el impacto **de la regla**, no de tu proyecto. |
| **Deuda técnica** | Tiempo estimado en minutos que costaría arreglarlo. |

> **Nota del formador · vulnerability ≠ hotspot.** La diferencia se olvida
> enseguida: una **vulnerability** Sonar la da por real y te enseña el flujo de
> datos; un **hotspot** es un aviso que dice «esto depende de tu contexto,
> míralo tú». Por eso el flujo del hotspot es manual (To Review → Acknowledged /
> Fixed / Safe) y por eso el gate exige que **todos** los hotspots nuevos estén
> revisados: si lo dejas pasar sin mirar, la condición no se cumple aunque el
> código ya esté corregido.

**Cierra con estas dos frases**, que son las que se recuerdan:

- Un **hotspot** no tiene por qué ser un error de código: puede serlo de
  concepto o de lógica de negocio.
- Los **code smells** no afectan al Quality Gate, aunque sean la mayoría.

---

## Min 33–53 — Corregir en directo (diapositiva 04)

**Enunciado que está en pantalla:** tres condiciones para conseguir el gate en verde.

| Condición | De qué vale |
|---|---|
| **New Bugs = 0** | Ningún bug nuevo |
| **New Vulnerabilities = 0** | Ninguna vulnerabilidad nueva |
| **New Security Hotspots Reviewed = 100 %** | Todo hotspot revisado |

**Aquí lo explicas tú — no está en la web:**

- **Reevaluar desde el punto anterior.** El segundo análisis no vuelve a mirar
  el proyecto entero: vuelve a mirar desde la referencia del análisis anterior.
- **Qué es el código nuevo.** El gate mira **solo lo que ha cambiado** desde el
  último análisis. El proyecto puede ser un desastre heredado y pasa verde
  mientras **no empeores**. Es la idea central de todo el curso.

### `NOSONAR`

**Comentario que excluye esa línea del análisis.** Se pega al final de la línea:

```java
String tipo = "VIP"; // NOSONAR
```

Mételo en directo sobre un code smell de `ProcesadorPedidos`: el issue
desaparece en el siguiente análisis. Frase de cierre: *huye de problemas, no
los resuelve* — si esa línea era un bug real, sigue ahí y ya nadie te lo cuenta.
**Quítalo después de la demo.**

### Los dos cambios en el editor

Se hacen a mano, delante de la gente.

#### 1. El bug: `Cliente.java` → añadir `hashCode()`

```java
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
```

#### 2. El hotspot: `ServicioHuellas.java` → cambiar el algoritmo

```java
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
```

Y en la interfaz, sobre el hotspot que aparece en `ServicioHuellas.java`:
**marcarlo como `Fixed`**. Un hotspot sin revisar deja el gate en rojo aunque
el código ya esté corregido: es lo que el gate está pidiendo.

> **Nota del formador · por qué ese `==` ahí si «funciona».** `esVip` compara
> con `==` y **funciona** en los tests porque los literales de cadena están
> internados en la JVM y son el mismo objeto. Justamente por eso es un bug
> latente: deja de ser cierto en cuanto el valor llega de fuera. Buen momento
> para la frase del curso: *«que pase los tests no significa que esté bien»*.

### Re-análisis

```powershell
mvn clean verify sonar:sonar `
  "-Dsonar.host.url=TU-SONARQUBE" `
  "-Dsonar.token=squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
```

**Resultado esperado: gate en verde.**

Errores críticos corregidos. Código mejorado. Calidad subiendo. Los code smells
y la duplicación siguen ahí, pero eres consciente de que están.

### Pestaña Activity

Puedes ver el progreso de tu análisis, con gráficas y evolución. Ahora sí hay
dos análisis y se ve la curva: 1 bug → 0, hotspots → 100 % revisados,
rojo → verde.

> **Nota del formador · por qué usamos otro gate.** Con un proyecto que trae
> defectos a propósito, `Sonar way` es imposible de poner verde en una hora:
> exige **cero** issues nuevos y además ≤ 3 % de duplicación. Por eso se crea
> `Curso — Demo (corta)` con tres condiciones alcanzables — bugs nuevos a 0,
> vulnerabilidades nuevas a 0 y hotspots revisados al 100 % — **sin marcarlo como
> por defecto**. La lección es la misma: un gate sirve si lo puedes cumplir. Si lo
> pones tan estricto que siempre está en rojo, la gente deja de mirarlo.

---

## Min 53–60 — Cierre (diapositiva 05)

**Lo que te llevas:**

- Un **issue** es un bug, una vulnerability, un hotspot o un code smell.
- El **hotspot** lo cierra una persona, no la herramienta.
- El gate mira **lo nuevo**, no el proyecto entero.
- Un gate que no puedes cumplir, deja de mirarse.

**La frase de cierre:** el gate rojo en el primer análisis no es un fallo. Es el
resultado esperado: está diciendo que algo no llega al listón.

**Enlaces:** documentación de SonarSource y la tabla de Quality Gates — están en
la diapositiva 05 y en la chuleta.

> **Las tres trampas quedan fuera de esta versión** — para cuando lo veáis más a
> fondo. Siguen estando en el **Plan B** de abajo y en `07-trampas.html` de la
> versión completa.

---

## Plan B — si algo falla

### `No plugin found for prefix 'sonar'`

Dos causas posibles y las dos se ven en el mensaje de error:

1. **Falta el plugin en el `pom.xml`.** Sin el epígrafe
   `org.sonarsource.scanner.maven:sonar-maven-plugin`, Maven no sabe qué significa
   el prefijo `sonar`. Tiene que ir con **versión fija**.
2. **El `-Dsonar.host.url` va sin comillas.** PowerShell parte el argumento por
   la `//` de la URL, Maven recibe un prefijo de plugin basura
   (`No plugin found for prefix '.host.url=http'`) y no llega a analizar nada.

```powershell
# MAL: PowerShell parte la URL
mvn clean verify sonar:sonar -Dsonar.host.url=TU-SONARQUBE

# BIEN
mvn clean verify sonar:sonar "-Dsonar.host.url=TU-SONARQUBE" "-Dsonar.token=squ_XXX"
```

**Alternativa que esquiva el problema del todo:** usar variables de entorno y
no pasar ninguna URL por la línea de comandos — que es justo lo que hace
`sonar.ps1`.

### El build falla y el análisis no llega a ejecutarse

**Causa:** si un test falla, Maven se detiene en la fase `test`. El comando
`mvn clean verify sonar:sonar` muere ahí: `sonar:sonar` no corre.

**Qué decir:** "el build es el primer gate, y el nuestro es el segundo".
Arreglar el test y volver a lanzar.

### El proyecto no aparece

Plan B de `PREPARACION.md` §2: el primer análisis crea el proyecto solo,
siempre que la key sea `curso-sonar-demo`.

### Error de autenticación

Token caducado o mal copiado → generar otro en **My Account → Security**.
No hace falta borrar el proyecto.

### La conexión no va

Comprobar si el servidor es `http://` o `https://` y si hay proxy corporativo:

```powershell
Test-NetConnection TU-SONARQUBE -Port 9000
```

### El gate sale verde en el primer análisis

No debería. Si pasa, revisa `PREPARACION.md` §3: el proyecto apunta a
`Sonar way` o al gate completo en vez de a `Curso — Demo (corta)`.

### El gate sigue rojo después de corregir

1. ¿Se re-analizó? El botón **Refresh** de la cabecera no analiza de nuevo.
2. ¿Se revisó el hotspot? Sin `Fixed`/`Safe`, esa condición sigue fallando.

---

## Después de la clase

`PREPARACION.md` §7: borrar token, borrar proyecto `curso-sonar-demo`, borrar
**los dos gates** del curso y comprobar que `Sonar way` sigue siendo el Default.
