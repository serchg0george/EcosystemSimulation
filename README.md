# Living Ecosystem Simulation

## Overview
This Java application simulates wildlife ecosystems, modeling animal behaviors such as predation, reproduction, hunger mechanics, and group dynamics. 
The simulation tracks how populations evolve over time in different biomes according to biological rules and environmental constraints.

## Key Features
- **Biome-based ecosystems** (Savanna, Tundra, Tropical Forest, Desert)
- **Animal types**:
    - Carnivores (lions, tigers, cheetahs, hyenas)
    - Herbivores (zebras, hares, gazelles, buffaloes)
- **Core simulation mechanics**:
    - Predator-prey interactions with attack success calculations
    - Hunger management for carnivores
    - Group hunting and herd defense behaviors
    - Reproduction based on age and iteration cycles
    - Age progression and mortality
- **Extinction detection** for animal types
- **Runtime ecosystem configuration**

## Project Structure
```
src/
├── enums/          # Type definitions
│   ├── AnimalType.java
│   ├── Biome.java
│   ├── Habitat.java
│   └── LivingType.java
├── exceptions/     # Custom exceptions
│   ├── AnimalNotFoundException.java
│   └── EcosystemNotFoundException.java
├── models/         # Core domain objects
│   ├── Animal.java
│   ├── Carnivore.java
│   ├── Ecosystem.java
│   └── Herbivore.java
└── services/       # Application logic
    ├── AnimalFactory.java
    ├── Main.java
    ├── ProbabilitiesService.java
    └── SimulationRunner.java
```

## Getting Started

### Prerequisites
- Java 21 or higher

### Running the Simulation
1. Compile the project:
   ```bash
   javac -d out src/services/Main.java
   ```
2. Run the application:
   ```bash
   java -cp out services.Main
   ```

### Usage
1. **Select an ecosystem**:
    - Choose from predefined biomes (Savanna, Tundra, Desert)
    - Or create a new custom ecosystem

2. **Populate the ecosystem**:
    - Add animals by species and group name
    - Specify quantities for each animal type
    - Supported animals:
        - Herbivores: Zebra, Hare, Gazelle, Buffalo
        - Carnivores: Lion, Cheetah, Tiger, Hyena

3. **Observe the simulation**:
    - Animals age each iteration
    - Breeding occurs when age matches iteration count
    - Carnivores hunt herbivores to reduce hunger
    - Starvation occurs if hunger reaches 100%
    - Extinction events end the simulation

## Design Notes
- **Extensible architecture**: Easily add new animals via `AnimalFactory`
- **Probability-based mechanics**: Attack success uses weighted probabilities
- **Group dynamics**: Herds provide defense bonuses, hunting parties share food
- **Realistic hunger system**: Hunger increases each iteration, feeding reduces hunger based on prey weight
- **Lifecycle management**: Automatic removal of dead animals and extinct groups

## Supported Biomes
- Savanna (preconfigured with default animals)
- Tundra
- Tropical Forest
- Desert

## Simulation Rules
- Attack success depends on:
    - Animal age and physical condition
    - Group vs. individual hunting
    - Relative weights of predator and prey
- Hunger decreases based on prey-to-predator weight ratio
- Herbivores in groups get 30% escape bonus
- Solitary carnivores have 50% reduced attack chance
- Animals breed when current age is divisible by iteration count