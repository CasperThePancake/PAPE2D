# PAPE2D

### *Pretty Average 2D Physics Engine*

![GitHub last commit](https://img.shields.io/github/last-commit/CasperThePancake/PAPE2D) ![Project Status](https://img.shields.io/badge/Status-In_Active_Development-orange?style=flat-square) ![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square) [![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)

PAPE2D is a custom, constraint-based 2D rigid-body physics engine built entirely from scratch. Inspired by engines like Box2D, this project avoids external physics libraries to focus on a pure, bottom-up implementation of fundamental rigid-body dynamics.

---

## 🚀 The Vision

I am currently developing the foundation of PAPE2D alongside a comprehensive, textbook-style course detailing the physics and mathematics powering the engine. 

* **The Ultimate Goal:** Recreate the first level of *Angry Birds* using PAPE2D—the definitive benchmark for a reliable 2D physics engine.
* **Development Note:** Progress comes in sporadic bursts between balancing this project and my Master’s degree in Physics!

---

## 📅 Timeline

| Milestone               | Date              |
|:------------------------|:------------------|
| **Research Started**    | December 24, 2025 |
| **Development Started** | March 29, 2026    |
| **Testing Started**     | June 21, 2026     |

---

## 🛠️ Roadmap & Progress

### Core & Collision Detection
- [x] Implement Sweep and Prune (Broadphase)
- [x] Implement abstract `Body` class
- [x] Implement `Circle` primitive
- [x] Implement `Polygon` primitive
- [x] Implement `Rectangle` & `Square` primitives
- [x] Implement Separating Axis Theorem (SAT / Narrowphase)

### Dynamics & Forces
- [x] Implement basic solver logic
- [x] Implement `ForceGenerator` base
- [x] Implement `Gravity` force
- [x] Implement `AirResistance` force
- [x] Implement `Spring` force

### Constraints & Resolution
- [x] Implement abstract `Constraint` class
- [x] Implement constraint solver logic
- [x] Implement `DistanceConstraint`
- [x] Implement `ContactConstraint`
- [x] Implement `FrictionConstraint`

### Next Steps & Application
- [x] Implement remaining core methods
- [x] Write geometry rendering methods (with antialiasing)
- [x] Implement rendering per `Body` class

### Testing
- [x] Body rendering without motion
  - [x] Circle
  - [x] Polygon
  - [x] Rectangle
  - [x] Square
  - [x] Rotated versions
- [x] Fixed-velocity motion
- [x] Forces
  - [x] Gravity
  - [x] Air resistance
  - [x] Springs
- [x] Constraints
  - [x] Distance constraint
  - [x] Collisions
- [x] General checks
  - [x] Antialiasing
- [x] Bugs to fix later
  - [x] Semi-horizontal edges show rendering artifacts

### Additional features
- [ ] Optional visual indicator for Spring force
- [ ] Body visual sprites
- [x] Option for a Body to not have collision
- [ ] General gravity force

### Angry Birds
- [ ] Build the *Angry Birds* demonstration

> *More milestones will be added as the engine evolves.*
