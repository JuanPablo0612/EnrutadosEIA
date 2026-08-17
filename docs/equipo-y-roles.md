# Equipo y roles — Enrutados EIA

Documento para armar el equipo del proyecto. Está en español a propósito: su audiencia son
estudiantes de la Universidad EIA que se están postulando, no los mantenedores del código.

El punto de partida es que **la mayoría del equipo va a llegar sin haber tocado Kotlin
Multiplatform, Compose ni Firebase**. Los roles de abajo están definidos para que eso no sea un
problema: cada área tiene una puerta de entrada que no exige conocer el stack, y una ruta para
crecer dentro de ella.

---

## Cómo está organizado el trabajo

El proyecto ya existe y funciona: ~386 archivos Kotlin, 559 textos de interfaz, y features
completas de autenticación, rutas, viajes, reservas, chat, calificaciones, vehículos, lugares,
notificaciones y seguridad. Nadie llega a inventar la arquitectura; se llega a extender una que
ya está decidida y documentada en `CLAUDE.md` y `AGENTS.md`.

Eso es una ventaja enorme para gente sin experiencia: hay 12 ejemplos de cada cosa que uno tiene
que escribir. La forma de trabajar es "copia el patrón de la pantalla de al lado y adáptalo".

Tres bandas de trabajo, diez roles:

| Banda | Roles | Cupos |
|---|---|---|
| Núcleo técnico | UI, FEAT, DATA, PLAT, QA | 8–10 |
| Producto y diseño | PROD, UX, CONT | 3–4 |
| Operación y campus | TRUST, CAMPUS | 2–3 |

Más el rol de **mantenedor / líder técnico**, que hoy ocupo yo: revisar todos los pull requests,
decidir arquitectura, y proteger la rama `master`.

---

## Núcleo técnico

### UI — Desarrollo de interfaz (Compose)

**2–3 cupos · nivel: principiante · la mejor puerta de entrada si quieres programar**

Construye las pantallas. En este proyecto cada pantalla son dos funciones: una que se conecta a
los datos y otra —`XxxContent`— que solo recibe información y dibuja. Este rol trabaja **solo en
la segunda**, con los componentes y los tokens de color/espaciado que ya existen en
`presentation/ui/components`, y con `@Preview` para verlo sin necesidad de correr la app.

- **Requisitos reales:** haber programado en algún lenguaje orientado a objetos (Java, C#, Python).
  Kotlin y Compose se aprenden aquí.
- **No necesitas:** saber Firebase, corrutinas, ni cómo funcionan los datos.
- **Qué aprendes:** Compose Multiplatform, sistemas de diseño, accesibilidad.
- **Tiempo hasta el primer aporte útil:** 1 a 2 semanas.

### FEAT — Desarrollo de features (ViewModel y casos de uso)

**2 cupos · nivel: intermedio**

Conecta la pantalla con la lógica: el `ViewModel` que expone el estado, las acciones que dispara
el usuario, y los casos de uso del dominio. También el manejo de errores, que en este proyecto es
un patrón estricto: ningún error crudo llega a la pantalla, todo pasa por una clase de error del
dominio y un *mapper* que lo convierte en un texto localizado.

- **Requisitos reales:** cómodo con programación asíncrona en algún lenguaje. Corrutinas y `Flow`
  se pueden aprender sobre la marcha si alguien te acompaña.
- **Qué aprendes:** MVVM, arquitectura limpia, manejo de estado inmutable.
- **Ruta natural:** casi siempre se llega aquí después de 1–2 meses en UI.

### DATA — Datos y Firebase

**2 cupos · nivel: intermedio-avanzado · el rol más crítico**

Los repositorios, los DTO, y sobre todo **las reglas de seguridad de Firestore y Storage**. Es el
área donde un error no se ve feo: se ve como que un estudiante puede leer los viajes, los
contactos de emergencia o el chat de otro. También los índices de Firestore y las migraciones de
datos.

- **Requisitos reales:** haber trabajado con alguna base de datos y entender qué es una consulta.
  Firebase se enseña, pero este rol **no puede quedar en manos de una sola persona sin
  acompañamiento**: todo cambio en reglas lo revisamos entre dos.
- **Qué aprendes:** modelado NoSQL, reglas de seguridad, transacciones.

### PLAT — Infraestructura y publicación

**1 cupo + 1 aprendiz · nivel: intermedio**

Hoy el proyecto **no tiene integración continua ni pruebas automatizadas**. Este rol arranca con
un trabajo concreto y muy visible: montar GitHub Actions para que cada pull request compile,
manejar `google-services.json` y `MAPS_API_KEY` como secretos, firmar el APK, y abrir un canal de
pruebas internas en Play Console para que el piloto se pueda instalar sin pasar APKs por WhatsApp.

- **Requisitos reales:** paciencia con YAML y con la consola. No hace falta saber Kotlin.
- **Qué aprendes:** CI/CD, gestión de secretos, publicación en Play Store.
- **Por qué importa:** sin esto, un equipo de diez personas rompe el build sin darse cuenta.

### QA — Calidad y pruebas

**2 cupos · nivel: principiante · no necesitas programar para entrar**

Probar la app antes que los usuarios reales. Escribir planes de prueba por pantalla, mantener una
matriz de dispositivos y versiones de Android, reportar bugs que se puedan reproducir (pasos,
dispositivo, captura), y verificar cada pull request antes de que se integre.

- **Requisitos reales:** ser metódico y tener un teléfono Android.
- **Ruta de crecimiento:** este rol escribe las **primeras pruebas automatizadas del proyecto**.
  Empezar de cero en pruebas es más fácil que meterse a un código de pruebas ajeno, así que es una
  entrada muy buena a la programación.

---

## Producto y diseño

### PROD — Producto y análisis funcional

**1 cupo · nivel: principiante · no se programa**

Decide qué se construye y en qué orden. Escribe las historias de usuario, mantiene el tablero,
entrevista a conductores y pasajeros reales de la universidad, y —lo más importante— define qué
**no** se hace. Es también el interlocutor con la representación estudiantil.

- **Requisitos reales:** saber escuchar y escribir claro. Sirve gente de administrativa, industrial
  o cualquier carrera.
- **Por qué importa:** un equipo grande de estudiantes sin alguien priorizando construye diez
  features a medias.

### UX — Diseño de interfaz y experiencia

**1–2 cupos · nivel: principiante-intermedio**

Flujos y pantallas en Figma, y consistencia con el sistema de diseño que ya está en el código
(tokens de color, tipografía, espaciado). Un entregable muy concreto de este rol: **los íconos**.
El proyecto prohíbe la librería de íconos de Material extendida, así que cada ícono nuevo entra
como un vector XML en `composeResources/drawable`.

- **Requisitos reales:** Figma y criterio visual. Diseño industrial, arquitectura o autodidactas.
- **Trabaja pegado a:** UI.

### CONT — Contenido y localización

**1 cupo · nivel: principiante · no se programa**

La app tiene 559 textos, en español e inglés, y las dos versiones tienen que mantener exactamente
las mismas claves. Este rol cuida el tono, revisa que los mensajes de error digan qué hacer y no
solo qué falló, y escribe los textos legales: términos de uso y política de tratamiento de datos.

- **Requisitos reales:** escribir bien. Inglés funcional.

---

## Operación y campus

### TRUST — Confianza y seguridad

**1 cupo · nivel: principiante-intermedio · no se programa**

Una app donde estudiantes se suben al carro de otros estudiantes vive o muere por la confianza.
Este rol define las políticas: verificación con correo institucional, qué pasa cuando alguien
reporta un incidente, cómo funcionan los contactos de emergencia (la feature `safety` ya existe en
el código), cuánta ubicación se guarda y por cuánto tiempo, y el cumplimiento de la Ley 1581 de
protección de datos personales.

- **Requisitos reales:** criterio. Derecho, ingeniería administrativa o alguien con interés real
  en el tema.
- **Trabaja pegado a:** DATA, porque estas políticas terminan escritas como reglas de Firestore.

### CAMPUS — Adopción y comunicación

**1–2 cupos · nivel: principiante · no se programa**

El problema más difícil de este proyecto no es técnico: **sin conductores publicando viajes, la app
está vacía y ningún pasajero vuelve**. Este rol resuelve eso: consigue los primeros conductores,
elige una o dos rutas reales para el piloto (por ejemplo un corredor concreto en una franja
horaria concreta), maneja las redes, y coordina con representación estudiantil y bienestar.

- **Requisitos reales:** ser sociable y organizado. Mercadeo, comunicación, o cualquiera con red
  en el campus.

---

## Si no llega tanta gente

**Equipo mínimo viable: 5 personas.**

| Rol | Por qué es imprescindible |
|---|---|
| Mantenedor / líder técnico | Alguien tiene que revisar y decidir |
| DATA | Es el área donde un error tiene consecuencias reales |
| UI | Sin esto no avanza nada visible |
| QA | Un equipo sin pruebas manuales publica bugs a usuarios reales |
| PROD o CAMPUS | Sin usuarios, el código no sirve de nada |

**Equipo completo: 14–17 personas.** Más de eso, con gente sin experiencia, es contraproducente:
el cuello de botella pasa a ser la revisión de código.

---

## Cómo entra alguien que no sabe nada del stack

Esta es la parte que hay que prometer y cumplir, porque es la razón por la que la gente se queda
o se va en la segunda semana.

**Semana 1 — Que la app corra en tu teléfono.** Clonar, configurar, compilar, instalar. Suena
trivial y no lo es: hay dos archivos de configuración que no están en el repositorio y hay que
entregarlos aparte. Meta de la semana: un pull request diminuto y real (un texto, un espaciado, un
color) para pasar por todo el flujo de git y revisión sin la presión de que el cambio importe.

**Semanas 2–3 — Una pantalla apadrinada.** Un issue etiquetado como entrada, con la pantalla
parecida ya señalada en el issue como referencia. Se trabaja con alguien de la misma área
disponible para preguntas. Aquí es donde se aprende Compose de verdad.

**Mes 2 — Una feature de punta a punta**, o el paso a otra área.

**Reglas del equipo, cortas a propósito:**

1. Nadie empuja directo a `master`. Todo entra por pull request con al menos una revisión.
2. Cada área tiene mínimo dos personas. Si alguien se va en parciales, el área no se muere.
3. Issues pequeños. Si un issue no se puede terminar en una semana de estudiante, está mal escrito.
4. Las convenciones no se discuten en cada PR: están en `CLAUDE.md` y `AGENTS.md`.
5. Una reunión semanal de 30 minutos. Sin más reuniones.

---

## Antes de abrir las inscripciones

Tres cosas que hay que dejar listas, o el equipo llega y se estrella:

1. **Acceso a Firebase para quien colabore.** Hoy nadie puede compilar sin `google-services.json`
   ni sin una clave de Google Maps, y ninguno de los dos está en el repositorio. Hay que decidir si
   se crea un proyecto de Firebase de desarrollo aparte del de producción —recomendado— y cómo se
   reparten esas credenciales.
2. **Diez issues de entrada escritos**, cada uno apuntando al archivo parecido que sirve de
   ejemplo. Sin eso, "ayúdame a entender el proyecto" se vuelve la conversación de todos los días.
3. **La integración continua del rol PLAT**, aunque sea solo compilar. Es lo que permite recibir
   pull requests de gente que está aprendiendo sin miedo.
