# 🎮 Pac-Man Clone — Java

Clon del clásico Pac-Man desarrollado en Java 21 con Swing, como proyecto académico.

---

## 📋 Descripción

Implementación del videojuego Pac-Man original usando Java puro con Swing para la interfaz gráfica.
El juego incluye mecánicas fieles al original: fantasmas con inteligencia artificial, power pellets,
sistema de niveles, cinemáticas y puntuación.

---

## 🕹️ Cómo jugar

| Tecla | Acción |
|-------|--------|
| ← → ↑ ↓ | Mover a Pac-Man |

**Objetivo:** Come todos los pellets del mapa sin que los fantasmas te atrapen.

- 🔵 **Pellet normal** → 10 puntos
- ⚪ **Power Pellet** → 50 puntos + fantasmas asustados por 10 segundos
- 👻 **Comer fantasma asustado** → 200, 400, 800, 1600 puntos (acumulativo)
- 🍒 **Fruta** → puntos bonus según el nivel

---

## 👾 Fantasmas

Cada fantasma tiene dos modos de comportamiento:

- **Normal** → movimiento aleatorio, persigue a Pac-Man si está a 150px o menos
- **Asustado** → movimiento aleatorio lento, Pac-Man puede comérselos
- **Muerto** → regresa a la casa usando búsqueda A*

---

## 🗺️ Niveles

| Nivel | Fruta | Puntos fruta |
|-------|-------|-------------|
| 1 | 🍒 Cereza | 100 |
| 2 | 🍓 Fresa | 200 |
| 3 | 🍎 Manzana | 300 |
| 4 | 🍊 Naranja | 400 |

Entre el nivel 2 y 3 se reproduce una cinemática especial.

---

## 🏗️ Arquitectura del proyecto

```
src/main/
├── java/com/mycompany/pacman/
│   ├── PacmanFrame.java          # Ventana principal y orquestador
│   ├── PacmanPanel.java          # Panel de juego y renderizado
│   ├── HudPanel.java             # Barra de puntos, vidas y nivel
│   │
│   ├── MovimientoPacman.java     # Hilo de movimiento de Pac-Man
│   ├── MovimientoFantasma.java   # Hilo de movimiento de fantasmas
│   ├── AnimacionPacman.java      # Gestión de sprites de Pac-Man
│   ├── AnimacionMuerte.java      # Secuencia de muerte de Pac-Man
│   │
│   ├── Fantasma.java             # Datos y sprites de un fantasma
│   ├── EstadoFantasma.java       # Enum: NORMAL, ASUSTADO, MUERTO
│   ├── EstadoJuego.java          # Enum: STARTING, PLAYING, DYING...
│   │
│   ├── SistemaWaypoints.java     # Grafo de navegación del mapa
│   ├── Waypoint.java             # Nodo del grafo
│   ├── AStarPacman.java          # Búsqueda A* para fantasmas muertos
│   ├── NodoAStarPacman.java      # Nodo de búsqueda A*
│   │
│   ├── Pellet.java               # Punto del mapa
│   ├── Fruta.java                # Fruta bonus
│   ├── Colisiones.java           # Detección de colisiones por píxel
│   ├── SistemaPuntos.java        # Puntuación y multiplicador
│   ├── GestorNivel.java          # Lógica de niveles y cinemáticas
│   ├── Vidas.java                # Sistema de vidas
│   │
│   ├── SonidoManager.java        # Gestión de audio
│   ├── PanelCinematica.java      # Cinemática entre niveles
│   └── PanelFinal.java           # Pantalla de fin de juego
│
└── resources/
    ├── img/                      # Sprites y assets visuales
    └── sound/                    # Efectos de sonido y música
```

---

## 🧠 Conceptos técnicos implementados

- **Detección de colisiones por color de píxel** — el mapa es una imagen PNG; las paredes se detectan analizando el color azul de cada píxel
- **Sistema de waypoints** — grafo de navegación que define los caminos válidos del laberinto
- **Búsqueda A\*** — los fantasmas muertos calculan la ruta óptima de regreso a casa
- **Máquina de estados** — el juego y cada fantasma tienen estados bien definidos
- **Hilos (Threads)** — cada personaje corre en su propio hilo de ejecución
- **Movimiento sinusoidal** — las cinemáticas usan `Math.sin` para el efecto ondulante

---

## ⚙️ Requisitos

- Java 21
- Apache Maven 3.x
- NetBeans 18+ (recomendado) o cualquier IDE con soporte Maven

---

## 🚀 Cómo ejecutar

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/pacman-java.git

# Entrar al directorio
cd pacman-java

# Compilar y ejecutar
mvn clean package
mvn exec:java
```

O simplemente abrir el proyecto en NetBeans y presionar **Run**.

---

## 📝 Notas

- Los sprites de Pac-Man son propiedad de **Bandai Namco**. Este proyecto es exclusivamente académico y no tiene fines comerciales.
- El mapa fue diseñado visualmente como imagen PNG y procesado por código.

---

## 👤 Autor

Desarrollado como proyecto académico — Java 21, Swing, NetBeans.