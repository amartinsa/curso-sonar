# GUIÓN — clase de 1 hora sobre SonarQube

**Objetivo:** que quien entre sin saber nada salga sabiendo qué hace SonarQube,
verlo analizar un proyecto real delante de ellos y tocar el resultado con sus
propias manos.

**Proyecto:** `demo-app/` — Java 17, Maven, **sin base de datos**, todo en memoria.
Contiene defectos a propósito. Está pensado para que el primer análisis salga
**rojo** y el segundo **verde**.

**Servidor:** `TU-SONARQUBE` (Community 10.4.0.87286, compartido).
**Antes de empezar:** tener preparado todo lo de `PREPARACION.md` (§1 a §3).

> **¿Vas a dar la versión reducida?** Usa `corta/GUION.md`: 5 diapositivas de
> `corta/`, sin comparativa de ediciones y sin cobertura ni JaCoCo.

---

## Minuto a minuto

| | Min | Qué pasa |
|---|---|---|
| **1** | 0–8 | Qué es SonarQube y por qué |
| **2** | 8–18 | Vocabulario: issues, severidad, deuda, cobertura |
| **3** | 18–32 | **Primer análisis en vivo** → gate en rojo |
| **4** | 32–42 | Leer el resultado en la interfaz |
| **5** | 42–55 | **Corregir en directo** y re-analizar → verde |
| **6** | 55–60 | Qué te da Community y qué te da la versión de pago |

---

## Min 0–8 — Qué es SonarQube

- **SonarQube es una herramienta de visualización de Sonar**: un sistema que
  evalúa la calidad de tu código. Analiza en cada ejecución y guarda el
  resultado para poder compararlo con el anterior.
- Tres piezas: el **analyzer** (corre al construir, en local), el **servidor**
  (recibe, guarda y evalúa las condiciones) y la **interfaz** (visualización del
  resultado, explicaciones, consejos de mejora, seguimiento e histórico).
- **No es una herramienta de pruebas.** No sustituye las pruebas unitarias ni
  las de rendimiento: solo evalúa agujeros y errores de programación, no la
  lógica de negocio.
- El producto es **Community** (gratuito, código abierto) y hay versiones de pago
  para ramas, informes y gobernanza. **Sin precios en esta sesión.**
- Demo del día: `demo-app`, un procesador de pedidos en memoria. Sin BBDD, sin
  frameworks: solo Java y Maven, para que nadie se pierda en el stack.

---

## Min 8–18 — Vocabulario (diapositiva 02)

Lo que hay que salir sabiendo distinguir:

| Término | En una frase |
|---|---|
| **Issue** | Cualquier cosa que el analizador encuentra: bug, vulnerability, hotspot o code smell. |
| **Bug** (Reliability) | Código probablemente erróneo. Ej.: `equals()` sin `hashCode()` → `java:S1206`, o un posible `NullPointerException`. |
| **Vulnerability** (Security) | Código que puede provocar errores de datos. Se detecta por **flujo de datos**: desde una fuente hasta un *sink*. Ej.: `Runtime.exec("ping " + host)` con entrada del usuario → `java:S2076`. |
| **Security Hotspot** | Código que debe revisar un humano: posible error de lógica o no cumple el estándar. Ej.: `MessageDigest.getInstance("MD5")` → `java:S4790`. **Hay que revisarlo a mano.** |
| **Code Smell** (Maintainability) | Código difícil de leer o mantener. Ej.: `tipo == "VIP"` en vez de `equals()`, o código que nunca se ejecuta. |
| **Severidad** | Blocker > Critical > Major > Minor > Info. Es el impacto **de la regla**, no de tu proyecto. |
| **New Code** | Solo lo que ha cambiado desde el último análisis. **Todo el resto del mundo puede estar hecho un desastre y el gate pasa.** Esta es la idea central de SonarQube. |
| **Quality Gate** | Las condiciones que decides cumplir para que el análisis pase. Si una falla → **rojo**. |
| **Quality Profile** | El conjunto de reglas activas para un lenguaje. |
| **Deuda técnica** | Tiempo estimado en minutos que costaría arreglarlo. |

> **Nota del formador · vulnerability ≠ hotspot.** La diferencia se olvida
> enseguida: una **vulnerability** Sonar la da por real y te enseña el flujo de
> datos; un **hotspot** es un aviso que dice «esto depende de tu contexto,
> míralo tú». Por eso el flujo del hotspot es manual (To Review → Acknowledged /
> Fixed / Safe) y por eso el gate exige que **todos** los hotspots nuevos estén
> revisados: si lo dejas pasar sin mirar, la condición no se cumple aunque el
> código ya esté corregido.

### Cobertura de test (JaCoCo)

Qué porcentaje de líneas se **ejecuta** cuando corren tus tests. JaCoCo es la
librería que instrumenta el código durante `mvn test` y escribe el informe;
SonarQube **solo lo lee**.

**Y no, no mide que funcione.** Eso lo decide Maven: si un test falla, el build
se detiene y el análisis ni llega a ejecutarse — y el informe de JaCoCo, que se
genera en una fase posterior a `test`, tampoco. La cobertura mide **alcance**,
no corrección: un test sin afirmaciones da el 100 % y cero valor.

**Trampa habitual:** 0 % en Sonar y 90 % en tu informe local = Sonar no encuentra
el XML. En el log sale `No report imported, no coverage information will be
imported`. Revisa `sonar.coverage.jacoco.xmlReportPaths`.

---

## Min 18–32 — Primer análisis en vivo

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

Antes de eso, genera el token en directo: **My Account → Security → Generate
Tokens**. Que vean que se muestra una sola vez y que no se guarda en ningún sitio.

> **Nota del formador · dónde se configura el análisis.** En Maven, en el
> `pom.xml`: propiedades como `sonar.coverage.jacoco.xmlReportPaths`,
> `sonar.java.binaries` y `sonar.tests`. El fichero `sonar-project.properties`
> solo lo lee el **scanner suelto** (`sonar-scanner`); con `mvn sonar:sonar`,
> Maven **no lo mira**. Está en el repo por si alguien prefiere el scanner
> independiente, y para dejar por escrito que **la URL del servidor y el token
> nunca van escritos ahí**.

### Qué debe salir

```
[INFO] BUILD SUCCESS
[INFO] ANALYSIS SUCCESSFUL, you can check TU-SONARQUBE/dashboard?id=curso-sonar-demo
```

### Y en la interfaz

El proyecto **`Curso Sonar - Demo`** con el gate **`Curso — Demo` en ROJO**.

Números esperados (si difieren, manda lo que diga la pantalla en ese momento):

| Métrica | Valor esperado |
|---|---|
| Bugs | **1** — `java:S1206` en `Cliente.java` |
| Vulnerabilities | **0 o más** — ver nota abajo |
| Security Hotspots | **1** — `java:S4790` en `ServicioHuellas.java` |
| Code Smells | La mayoría de los issues — el gate no los mira |
| Coverage | **~26 %** |
| Duplicated Lines | hay |

**Nota sobre Vulnerabilities:** `java:S2076` se detecta con taint analysis y solo
salta como *vulnerability* si el analizador considera la entrada como externa.
- **Sale 0** → se enseña igual: es una limitación del análisis estático y por
  eso la revisión humana no es opcional. El gate se pone verde sin esa condición.
- **Sale > 0** → enseñar el **flujo de datos** que muestra Sonar y corregirlo.

**Lo que NO se toca en esta sesión:** los code smells y la duplicación. Van a
quedar ahí a propósito — el gate no los mira, y esa es exactamente la lección de
por qué defines las condiciones que defines.

> **Nota del formador · por qué usamos otro gate.** Con un proyecto que trae
> defectos a propósito, `Sonar way` es imposible de poner verde en una hora:
> exige **cero** issues nuevos y además ≥ 80 % de cobertura y ≤ 3 % de
> duplicación. Por eso se crea `Curso — Demo` con cuatro condiciones
> alcanzables — bugs nuevos a 0, vulnerabilidades nuevas a 0, hotspots
> revisados al 100 % y cobertura ≥ 60 % — **sin marcarlo como por defecto**.
> La lección para quien escucha es la misma: un gate sirve si lo puedes cumplir.
> Si lo pones tan estricto que siempre está en rojo, la gente deja de mirarlo.

---

## Min 32–42 — Leer el resultado

Abrir en este orden:

1. **Cabecera del proyecto** — el gate en rojo y **cuáles** de sus condiciones
   fallan. Esto es lo primero que mira cualquiera al entrar.
2. **Issues → Bugs** — abrir `Cliente.java`. `equals` está reescrito y `hashCode`
   no: dos objetos con el mismo id se consideran iguales pero no tienen la misma
   posición en un `HashMap`. **Esto es un bug de verdad**, no una preferencia de
   estilo.
3. **Security Hotspots** — abrir `ServicioHuellas.java`. El recuadro de revisión:
   explicar los estados **To Review → Acknowledged / Fixed / Safe** y que aquí
   decide una persona, no la herramienta.
4. **Code** — enseñar el código fuente con los issues marcados en la línea:
   el `==` de `esVip`, los 8 parámetros de `registrar`, el `System.out`,
   los bloques idénticos de `procesar`, el `TODO`.
5. **Measures → Coverage** — el gráfico y las clases sin test.

> **Gráfica de evolución:** `Activity`, dentro del proyecto. Muestra hasta 3
> métricas a lo largo del tiempo y los cambios de estado del gate. **Esto
> Community ya lo trae** — se enseña al final, en la comparación de ediciones,
> y es el mejor argumento de que ya estás cubierto para lo básico.

---

## Min 42–55 — Corregir en directo

Tres cambios. Se hacen a mano, en el editor, delante de la gente.

### 1. El bug: `Cliente.java` → añadir `hashCode()`

```java
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
```

### 2. El hotspot: `ServicioHuellas.java` → cambiar el algoritmo

```java
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
```

Y en la interfaz, sobre el hotspot que aparece en `ServicioHuellas.java`:
**marcarlo como `Fixed`**. Un hotspot sin revisar deja el gate en rojo aunque
el código ya esté corregido: es lo que el gate está pidiendo.

### 3. La cobertura: crear un test nuevo

Crear `demo-app/src/test/java/com/demo/malo/ProcesadorPedidosTest.java` con:

```java
package com.demo.malo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcesadorPedidosTest {

    private final ProcesadorPedidos procesador = new ProcesadorPedidos();

    @Test
    void un_pedido_normal_llega_al_total_esperado() {
        Pedido pedido = procesador.registrar("P-1", "C-1", "NORMAL", "Calle Mayor 1", 80, 100.0, false, "nota");

        assertEquals(105.0, procesador.procesar(pedido), 0.001);
    }

    @Test
    void un_pedido_vip_urgente_y_grande_llega_al_total_esperado() {
        Pedido pedido = procesador.registrar("P-2", "C-2", "VIP", "Poligono", 1500, 1000.0, true, "10.0");

        assertEquals(1105.0, procesador.procesar(pedido), 0.001);
    }

    @Test
    void las_notas_no_numericas_se_ignoran() {
        Pedido pedido = procesador.registrar("P-3", "C-3", "NORMAL", "", 600, 50.0, false, "sin numero");

        assertEquals(75.0, procesador.procesar(pedido), 0.001);
    }

    @Test
    void el_historial_acumula_los_pedidos_registrados() {
        procesador.registrar("P-4", "C-4", "VIP", "", 10, 10.0, false, "");
        procesador.registrar("P-5", "C-5", "NORMAL", "", 10, 10.0, false, "");

        assertEquals(2, procesador.getHistorial().size());
        assertEquals(1, procesador.contarPorTipo("VIP"));
        assertEquals(1, procesador.contarPorTipo("NORMAL"));
    }
}
```

**Comprobar antes de mirar la gente** (ya está verificado, pero si has tocado
el código, revíralo):

```powershell
mvn -B clean verify
```

Resultado medido: la cobertura pasa de **~26 %** a **~85 %**.

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

**Resultado esperado: gate `Curso — Demo` en VERDE.**

Y enseñar **Activity**: ahora sí hay dos análisis y se ve la curva.
26 % → 85 %, 1 bug → 0, rojo → verde.

---

## Min 55–60 — Community vs pago

Cinco bloques, sin precios. Las **notas del formador** de esta sección están al
final de los bloques, con el enlace a la documentación oficial.

> **1 · Ramas y PR** — Hoy: solo `main`. Con la versión de pago: el PR se analiza
> antes de mergear y el resultado sale en GitHub/GitLab.
> **2 · Reglas** — Hoy: perfiles propios sí, reglas nuevas escribiendo un plugin
> Java. Con pago: más reglas de fábrica, regla priorizada en el gate, patrones
> de secretos, config del motor de seguridad.
> **3 · Informes** — Hoy: gráficas de evolución, historial, variación vs análisis
> anterior. Con pago: portfolios, Applications, PDF y reportes de cumplimiento
> (OWASP/CWE/PCI, regulatory).
> **4 · Quién hizo qué** — Hoy: changelog de cada issue y eventos en Activity.
> Con pago: audit log de acciones administrativas.
> **5 · IDE** — Hoy: el IDE conectado, pero solo la rama principal. Con pago:
> rama y PR dentro del editor.

Y si preguntan por dinero: **los precios no se tocan en esta sesión** — SonarSource
no publica precio de lista para self-managed, y no vamos a inventar números.

> **Nota del formador · dos correcciones que te acreditan delante de la gente.**
>
> **«Crear reglas propias es de pago» — no es cierto.** La documentación lo
> describe sin restricción de edición: se crea desde una *regla plantilla*
> (XPath para XML) o escribiendo un **plugin Java** en `extensions/plugins`,
> que obliga a reiniciar el servidor. Lo que sí se compra es otra cosa: más
> reglas de fábrica y poder priorizar una regla concreta en el gate.
>
> **«El registro de actividad es de pago» — a medias.** El **changelog de cada
> issue** y los eventos de **Activity** ya están en Community. El **audit log**
> de acciones administrativas es de Enterprise. Distingue entre ambas.
>
> Fuentes: [ediciones de SonarQube Server](https://docs.sonarsource.com/sonarqube-server/discovering/sonarqube-server-editions)
> y [tabla de comparación de Community Build](https://docs.sonarsource.com/sonarqube-community-build/feature-comparison-table).

---

## Plan B — si algo falla

### `No plugin found for prefix 'sonar'`

Dos causas posibles y las dos se ven en el mensaje de error:

1. **Falta el plugin en el `pom.xml`.** Sin el epígrafe
   `org.sonarsource.scanner.maven:sonar-maven-plugin`, Maven no sabe qué significa
   el prefijo `sonar`: `org.sonarsource.scanner.maven` no es un grupo de plugins
   por defecto. Tiene que ir con **versión fija** — sin ella se coge la última
   y te puede romper de un día para otro.
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
`mvn clean verify sonar:sonar` muere ahí: `sonar:sonar` no corre, y JaCoCo une su
`report` a una fase posterior a `test`, así que **ni siquiera se genera el
informe de cobertura**.

**Qué decir:** "el build es el primer gate, y el nuestro es el segundo".
Arreglar el test y volver a lanzar.

### Sonar muestra 0 % de cobertura

En el log: `No report imported, no coverage information will be imported`.
No encuentra el XML. Comprobar:

```powershell
Test-Path target\site\jacoco\jacoco.xml
```

Si no existe → el `report` de JaCoCo no corrió (fase `prepare-package`):
fuerza `mvn clean verify` antes del análisis. Si existe → revisar
`sonar.coverage.jacoco.xmlReportPaths` en el `pom.xml`.

**Fíjate:** con 0 %, la condición de cobertura del gate **falla igualmente**.
No es un error de Sonar, es exactamente lo que estás pidiendo.

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
`Sonar way` en vez de a `Curso — Demo`.

### El gate sigue rojo después de corregir

1. ¿Se re-analizó? El botón **Refresh** de la cabecera no analiza de nuevo.
2. ¿Se revisó el hotspot? Sin `Fixed`/`Safe`, esa condición sigue fallando.
3. ¿Pasó el test? Si el build no llegó a `verify`, no hay informe de cobertura.

---

## Después de la clase

`PREPARACION.md` §7: borrar token, borrar proyecto `curso-sonar-demo`, borrar
el gate `Curso — Demo`, comprobar que `Sonar way` sigue siendo el Default.
