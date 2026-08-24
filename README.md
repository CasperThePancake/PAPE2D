# PAPE2D

### *Pretty Average 2D Physics Engine*

![GitHub last commit](https://img.shields.io/github/last-commit/CasperThePancake/PAPE2D) ![Project Status](https://img.shields.io/badge/Status-Stable-brightgreen?style=flat-square) ![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square) [![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)

PAPE2D is a custom, constraint-based 2D rigid-body physics engine built entirely from scratch. Inspired by engines like Box2D, this project avoids external physics libraries to focus on a pure, bottom-up implementation of fundamental rigid-body dynamics.

---

## 🚀 The Vision

I started building this library while following an undergraduate course on _Classical Mechanics_. It piqued my interest in applying the non-Newtonian approaches to mechanics computationally. Once I discovered _Gauss's principle of least constraint_, I was hooked on the physics background of these engines. 8 months later I reached the first stable release of PAPE2D, with fast and accurate forces and  collisions.

It was not easy to get this up and running: I hit many brick walls where I spent hours researching how to solve a problem, which algorithm to use, which equations to derive, ... So for those who come after: this project includes a 70-page PDF course outlining all the physics and coding in mathematical detail. Or you can start from nothing, like I did!

Now I want to keep building the library, to make it more accessible and actually useful as a tool for other developers and researchers. This means implementing more (complex) features, as well as side-tools like sprite rendering.

### Most recent video demonstration (YouTube)
[![Most recent demonstration video](https://img.youtube.com/vi/NnrIy0QZcTs/0.jpg)](https://www.youtube.com/watch?v=NnrIy0QZcTs)
---

## 📅 Timeline

| Milestone                | Date              |
|:-------------------------|:------------------|
| **Research Started**     | December 24, 2025 |
| **Development Started**  | March 29, 2026    |
| **Testing Started**      | June 21, 2026     |
| **First Stable Release** | July 11, 2026     |

---

## 🛠️ Roadmap

### Being worked on
(For the next update)

- Body sleeping to weaken numerical drift (since 24/08/2026)
- Z-ordering for sprite rendering (since 24/08/2026)

### Planned features
(In order of planned importance)

- Area Of Effect objects for more control over forces/attributes per world area
- Support for concave polygons using fixture system
