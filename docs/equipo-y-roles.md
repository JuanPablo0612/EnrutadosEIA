# Equipo y roles — Enrutados EIA

Documento para armar el equipo del proyecto. Está en español a propósito: su audiencia son
estudiantes de la Universidad EIA que se están postulando, no los mantenedores del código.

Tres premisas:

- **Casi nadie va a llegar sabiendo las herramientas del proyecto.** No se ven en clase. Los roles
  están definidos para que eso no sea un problema — con una excepción, iOS, que sí exige experiencia.
- **Hasta hoy lo he desarrollado yo solo.** Nadie más conoce el proyecto, así que cada persona que
  entra cuesta tiempo mío antes de devolvérmelo. Por eso el equipo no se arma de una vez: se arma
  por tandas, en el orden de más abajo.
- **Los cupos no pesan igual.** Ocho a once personas suena a mucho, pero solo cuatro o cinco de
  esos cupos escriben código, que es lo único que me suma cola de revisión. Los demás trabajan en
  paralelo sin depender de mí.

La app ya existe y funciona en Android: viajes, reservas, chat, calificaciones, vehículos y
contactos de emergencia están construidos. Nadie llega a inventar nada desde cero — se llega a
extender algo que ya tiene una forma clara, con varios ejemplos de cada cosa que hay que escribir.

---

## Los seis roles

Más el de **líder técnico**, que ocupo yo: revisar los cambios de todos, decidir el rumbo y
acompañar a quien se estanque.

### Desarrollo de la app · 2–3 cupos

Construir las pantallas y la lógica detrás de ellas. Es donde más manos se necesitan.

- **Necesitas:** haber programado en algo —Java, C#, Python, lo que sea—. El lenguaje y las
  herramientas de este proyecto se aprenden aquí.
- **Primer aporte real:** una o dos semanas.

### iOS · 1 cupo · requiere Mac y experiencia

El único rol que **no** es de entrada. La app está escrita en una tecnología que comparte casi todo
el código entre Android y iPhone, así que esto no es empezar de cero: los dos objetivos de iOS ya
están declarados en el proyecto y las siete piezas que dependen de la plataforma ya tienen su
versión de iPhone escrita. Faltan dos cosas concretas:

1. **Ser la primera persona que compile esto en un Mac.** Ese código nunca ha pasado por un
   compilador de iOS. Es previsible que la primera vez duela.
2. **Terminar tres piezas que hoy están vacías a propósito:** el mapa, la llamada de emergencia y
   compartir ubicación. El mapa es la importante — sin él, elegir un lugar no funciona en iPhone.

- **Necesitas:** un MacBook, y haber hecho algo antes con estas tecnologías. Aquí sí es requisito
  y no preferencia: **soy el único que conoce el proyecto y no sé iOS**, así que esta persona no va
  a tener a quién preguntarle. Tiene que poder sola.
- **Puede no ser un estudiante activo:** un egresado, alguien que ya trabaje en móviles, o un
  profesor. Vale la pena buscar por fuera de la convocatoria estudiantil.

### Datos y Firebase · 1 cupo

La base de datos y, sobre todo, los permisos: que cada persona solo pueda ver lo suyo. Es el rol
más técnico, porque un error aquí no se ve como un error — se ve como que alguien lee el chat o los
contactos de emergencia de otro.

Escribir esas reglas ya no es el trabajo difícil; comprobar que hacen lo que uno cree, sí. Por eso
este rol no necesita una segunda persona, sino dos costumbres: **todo cambio de permisos lo reviso
yo**, y cada regla queda cubierta por una prueba contra el emulador de Firebase, que ya está
configurado en el proyecto. La prueba dice, en una línea, que un pasajero no puede leer lo de otro.

- **Necesitas:** haber tocado alguna base de datos. Firebase se enseña.

### Diseño y contenido · 2 cupos

Cómo se ven y cómo suenan las pantallas: los flujos maquetados en Figma, los íconos, y que los
textos digan qué hacer y no solo qué falló. Dos personas porque el diseño mejora cuando hay con
quién discutirlo, y porque una sola persona diseñando para toda la app se convierte en el embudo
del equipo.

- **Necesitas:** Figma o buen criterio visual, y escribir bien. Basta con que una de las dos maquete.
- **No se programa.**

### Pruebas y calidad · 2 cupos

Usar la app antes que los usuarios reales y reportar lo que se rompe, de forma que se pueda
reproducir. Son dos y no una por una razón concreta: **casi todo lo interesante de esta app pasa
entre dos personas.** Publicar un viaje, reservar un cupo, aceptar la reserva, chatear, calificar
al otro — no se puede probar bien desde un solo teléfono. Hacen falta dos, uno de conductor y otro
de pasajero, probando el mismo viaje a la vez.

- **Necesitas:** ser metódico y tener un teléfono Android.
- **No se programa** — y es la mejor entrada al proyecto para quien quiere aprender y todavía no
  programa.

### Producto y campus · 1–2 cupos

Decidir qué se construye primero, hablar con conductores y pasajeros reales, y conseguir que la
app se use. Este último punto es el problema más difícil del proyecto y no es técnico: **sin
conductores publicando viajes, la app está vacía y ningún pasajero vuelve a abrirla.**

También es el rol dueño de las preguntas a los usuarios de la universidad, como se explica abajo.

- **Necesitas:** ser organizado, sociable y tener red en el campus.
- **No se programa.**

---

## Cómo entra lo que dicen los usuarios

Preguntar en la universidad qué mejoraría la adopción no necesita un rol nuevo, y tampoco funciona
como "esto lo hace todo el equipo": lo que es de todos termina siendo de nadie, y las
conversaciones que nadie anota se evaporan. **Un dueño, muchos recolectores.**

- **Dueño: producto y campus.** Define las cuatro o cinco preguntas, mantiene un solo lugar donde
  se anota todo, y hace el reparto de abajo.
- **Recolecta todo el equipo.** Cualquiera que hable con un estudiante deja la nota en ese mismo
  lugar. Las dos personas de pruebas son un canal especialmente bueno: son las que ven dónde se
  traba la gente.
- **Se revisa en la reunión semanal de 30 minutos.** Ningún proceso más pesado que eso.

Lo que **no** conviene es la cadena "usuarios → diseño → código", porque manda por el camino
equivocado la mayoría de los hallazgos. En una app de carpooling, lo que frena la adopción casi
nunca es la interfaz: es *"no hay viajes a mi hora"*, *"no conozco al conductor"*, *"cuánto le pago
y cómo"*. Nada de eso se arregla en Figma. Cada hallazgo va a **una** de tres salidas:

| Si el hallazgo es… | Va a… |
|---|---|
| Un problema de interfaz — no se entiende, no se encuentra, se ve mal | Diseño, y de ahí a código |
| Un problema de producto o de reglas — precio, horarios, verificación, confianza | Directo al backlog, sin pasar por diseño |
| Algo que la app no arregla — falta masa crítica, faltan rutas, faltan aliados | Campus |

---

## En qué orden llegan

Recibir a ocho personas el mismo día, viniendo de un proyecto de una sola persona, hace que el
proyecto avance más lento que ahora. Tres tandas:

**Primera — tres personas, ninguna toca el código.** Producto y campus, y las dos de pruebas. No me
suman cola de revisión, y responden la pregunta más urgente que tiene el proyecto, que no es
técnica: ¿alguien va a usar esto? Mientras tanto yo dejo listas las credenciales y unas cuantas
tareas de entrada.

**Segunda — una sola persona en desarrollo, más las dos de diseño.** La de desarrollo es la más
sólida de las que se postulen, no la más entusiasta: esa primera persona termina siendo el segundo
par de ojos del proyecto y es la que destraba a todos los que lleguen después. Hasta que alguien
más pueda revisar código, yo soy el cuello de botella.

**Tercera — el resto.** El segundo y tercer cupo de desarrollo, datos, e iOS.

**iOS puede adelantarse, y la primera tanda es la que lo decide.** Si resulta que una parte grande
de la universidad usa iPhone, entonces no es una mejora para después: es lo que impide llegar a
tener suficientes conductores y pasajeros al mismo tiempo. Esa pregunta cabe en la primera encuesta
de campus y se responde en la primera semana. Si la respuesta es "la mayoría usa Android", iOS
espera sin culpa.

Lo que hay que contar de una vez, no descubrirlo a mitad de camino: **mientras el equipo crece, mi
propia producción de código baja.** Ese es el costo de dejar de trabajar solo, y se paga una vez.

---

## Cómo entra alguien que no conoce las herramientas

1. **Semana 1 — que la app corra en tu teléfono.** Suena trivial y no lo es. La meta es un cambio
   diminuto y real, para recorrer todo el flujo de trabajo sin la presión de que el cambio importe.
2. **Semanas 2 y 3 — una pantalla apadrinada.** Una tarea pequeña, con la pantalla parecida ya
   señalada como referencia y alguien disponible para preguntas.
3. **Mes 2 — algo de punta a punta**, o el cambio a otra área si la primera no era la indicada.

**Tres reglas y nada más:** nadie sube cambios sin que otro los revise; las tareas se escriben
para terminarse en una semana de estudiante; una reunión de 30 minutos por semana.

---

## Antes de convocar

Lo único que me bloquea hoy: **nadie puede compilar el proyecto sin unas credenciales que, por
seguridad, no están en el repositorio.** Si llegan seis personas y se estrellan el primer día, se
van. Hay que resolver cómo se reparten esas claves —lo más sano es un entorno de pruebas separado
del real— y dejar escritas unas cuantas tareas de entrada antes de abrir las inscripciones.
