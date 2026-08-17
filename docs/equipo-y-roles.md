# Equipo y roles — Enrutados EIA

Documento para armar el equipo del proyecto. Está en español a propósito: su audiencia son
estudiantes de la Universidad EIA que se están postulando, no los mantenedores del código.

Dos premisas:

- **Casi nadie va a llegar sabiendo las herramientas del proyecto.** No se ven en clase. Los roles
  están definidos para que eso no sea un problema.
- **El equipo va a ser pequeño.** Entre cinco y ocho personas es un buen tamaño; con cuatro ya se
  puede arrancar.
- **Hasta hoy lo he desarrollado yo solo.** Nadie más conoce el proyecto, así que cada persona que
  entra cuesta tiempo mío antes de devolvérmelo. Por eso el equipo no se arma de una vez: se arma
  por tandas, en el orden de abajo.

La app ya existe y funciona: viajes, reservas, chat, calificaciones, vehículos y contactos de
emergencia están construidos. Nadie llega a inventar nada desde cero — se llega a extender algo que
ya tiene una forma clara, con varios ejemplos de cada cosa que hay que escribir.

---

## Los cinco roles

Más el de **líder técnico**, que ocupo yo: revisar los cambios de todos, decidir el rumbo y
acompañar a quien se estanque.

### Desarrollo de la app · 2–3 cupos

Construir las pantallas y la lógica detrás de ellas. Es donde más manos se necesitan.

- **Necesitas:** haber programado en algo —Java, C#, Python, lo que sea—. El lenguaje y las
  herramientas de este proyecto se aprenden aquí.
- **Primer aporte real:** una o dos semanas.

### Datos y Firebase · 1 cupo

La base de datos y, sobre todo, los permisos: que cada persona solo pueda ver lo suyo. Es el rol
más técnico, porque un error aquí no se ve como un error — se ve como que alguien lee el chat o los
contactos de emergencia de otro.

Escribir esas reglas ya no es el trabajo difícil; comprobar que hacen lo que uno cree, sí. Por eso
este rol no necesita una segunda persona, sino dos costumbres: **todo cambio de permisos lo reviso
yo**, y cada regla queda cubierta por una prueba contra el emulador de Firebase, que ya está
configurado en el proyecto. La prueba dice, en una línea, que un pasajero no puede leer lo de otro.

- **Necesitas:** haber tocado alguna base de datos. Firebase se enseña.

### Diseño y contenido · 1 cupo

Cómo se ven y cómo suenan las pantallas: los flujos en Figma, los íconos, y que los textos digan
qué hacer y no solo qué falló.

- **Necesitas:** Figma o buen criterio visual, y escribir bien.
- **No se programa.**

### Pruebas y calidad · 1 cupo

Usar la app antes que los usuarios reales y reportar lo que se rompe, de forma que se pueda
reproducir. Es la mejor entrada al proyecto para alguien que quiere aprender sin saber programar
todavía.

- **Necesitas:** ser metódico y tener un teléfono Android.
- **No se programa.**

### Producto y campus · 1–2 cupos

Decidir qué se construye primero, hablar con conductores y pasajeros reales, y conseguir que la
app se use. Este último punto es el problema más difícil del proyecto y no es técnico: **sin
conductores publicando viajes, la app está vacía y ningún pasajero vuelve a abrirla.**

- **Necesitas:** ser organizado, sociable y tener red en el campus.
- **No se programa.**

---

## En qué orden llegan

Recibir a cinco personas el mismo día, viniendo de un proyecto de una sola persona, hace que el
proyecto avance más lento que ahora. Tres tandas:

**Primera — dos personas, ninguna toca el código.** Producto y campus, y pruebas. No me suman cola
de revisión, y responden la pregunta más urgente que tiene el proyecto, que no es técnica: ¿alguien
va a usar esto? Mientras tanto yo dejo listas las credenciales y unas cuantas tareas de entrada.

**Segunda — una sola persona en desarrollo.** La más sólida de las que se postulen, no la más
entusiasta: esa primera persona termina siendo el segundo par de ojos del proyecto y es la que
destraba a todos los que lleguen después. Hasta que alguien más pueda revisar código, yo soy el
cuello de botella.

**Tercera — el resto.** El segundo y tercer cupo de desarrollo, datos, y diseño.

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
