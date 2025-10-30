# Living Ecosystem Simulation 🦁🌿

[![Java Version](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://www.oracle.com/java/)
[![Build Tool](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)

## Table of Contents
- [Overview](#overview)
- [Key Features](#key-features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Simulation Mechanics](#simulation-mechanics)
- [Design Notes](#design-notes)
- [Testing](#testing)
- [Contributing](#contributing)

## Overview
A sophisticated Java application that simulates dynamic wildlife ecosystems with realistic animal behaviors. The simulation models complex interactions including predation, reproduction, hunger mechanics, and group dynamics across diverse biomes. Watch as populations evolve, compete, and adapt over time according to biological rules and environmental constraints.

## Key Features

### 🌍 Diverse Biomes
- **Savanna** - Vast grasslands with predator-prey dynamics
- **Tundra** - Harsh cold environments
- **Tropical Forest** - Dense vegetation ecosystems
- **Desert** - Arid landscapes with scarce resources

### 🦒 Animal Variety
- **Carnivores**: Lion, Tiger, Cheetah, Hyena
- **Herbivores**: Zebra, Hare, Gazelle, Buffalo

### ⚙️ Realistic Simulation Mechanics
- **Predator-Prey Dynamics**: Sophisticated attack success calculations
- **Hunger System**: Progressive hunger mechanics for carnivores
- **Group Behaviors**: Cooperative hunting and herd defense
- **Breeding**: Age and iteration-based reproduction
- **Lifecycle**: Age progression, natural mortality, and starvation
- **Extinction Detection**: Automatic tracking of species extinction events
- **Dynamic Configuration**: Runtime ecosystem customization

## Project Structure
```
src/
├── main/java/
│   ├── enums/              # Type definitions and constants
│   │   ├── AnimalType.java      # Species identifiers
│   │   ├── Biome.java           # Ecosystem types
│   │   ├── Habitat.java         # Environmental classifications
│   │   └── LivingType.java      # Organism categories
│   ├── exceptions/         # Custom exception handling
│   │   ├── AnimalNotFoundException.java
│   │   ├── EcosystemNotFoundException.java
│   │   └── InvalidBreedingException.java
│   ├── models/             # Core domain objects
│   │   ├── Animal.java          # Base animal class
│   │   ├── Carnivore.java       # Predator implementation
│   │   ├── Herbivore.java       # Prey implementation
│   │   └── Ecosystem.java       # Environment container
│   └── services/           # Business logic and orchestration
│       ├── AnimalCreatorService.java  # Factory for animal instantiation
│       ├── FeedingService.java        # Predation and feeding logic
│       ├── ProbabilitiesService.java  # Attack success calculations
│       ├── SimulationRunner.java      # Main simulation loop
│       └── Main.java                  # Application entry point
└── test/java/              # Unit tests
    ├── models/
    │   ├── CarnivoreTest.java
    │   ├── HerbivoreTest.java
    │   └── EcosystemTest.java
    └── services/
        ├── AnimalCreatorServiceTest.java
        ├── FeedingServiceTest.java
        ├── ProbabilitiesServiceTest.java
        └── SimulationRunnerTest.java
```

## Getting Started

### Prerequisites
- **Java Development Kit (JDK)** 21 or higher
- **Apache Maven** 3.6+ (for building and dependency management)

### Installation & Running

#### Using Maven (Recommended)
1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd EcosystemSimulation
   ```

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the simulation**:
   ```bash
   mvn exec:java -Dexec.mainClass="services.Main"
   ```

#### Alternative: Manual Compilation
1. **Compile the project**:
   ```bash
   javac -d target/classes src/main/java/**/*.java
   ```

2. **Run the application**:
   ```bash
   java -cp target/classes services.Main
   ```

## Usage

### Step-by-Step Walkthrough

#### 1. Select an Ecosystem
Upon starting the simulation, you'll be prompted to choose:
- **Predefined biomes**: Savanna, Tundra, Tropical Forest, Desert
- **Custom ecosystem**: Create your own with a custom name and biome type

#### 2. Populate the Ecosystem
Add animals to your ecosystem:
- **Choose species**: Select from the available carnivores or herbivores
- **Assign group names**: Organize animals into named groups (e.g., "Pride", "Herd")
- **Specify quantities**: Define how many of each animal type to add
- **Repeat**: Continue adding different species until your ecosystem is complete

**Available Animals**:
- 🦁 **Carnivores**: Lion, Tiger, Cheetah, Hyena
- 🦓 **Herbivores**: Zebra, Hare, Gazelle, Buffalo

#### 3. Run the Simulation
Watch the ecosystem evolve through iterations:
- **Aging**: All animals age with each iteration
- **Hunting**: Carnivores hunt herbivores to satisfy hunger
- **Breeding**: Reproduction occurs based on age and iteration cycles
- **Starvation**: Carnivores die when hunger reaches 100%
- **Extinction**: Simulation ends if a species becomes extinct

### Example Interaction
```
Select Biome: Savanna
Add Animals? Yes
Animal Type: Lion
Group Name: Simba Pride
Quantity: 5
Add More? Yes
Animal Type: Zebra
Group Name: Striped Herd
Quantity: 20
Add More? No
Starting simulation...
```

## Simulation Mechanics

### Predator-Prey Interactions
The hunting system uses sophisticated probability calculations:

- **Individual Factors**:
  - Predator age and physical condition
  - Prey age and health status
  - Weight ratios between hunter and target

- **Group Dynamics**:
  - **Hunting Parties**: Multiple predators increase success rates
  - **Herd Defense**: Herbivore groups provide 30% escape bonus
  - **Solitary Penalty**: Solo carnivores have 50% reduced attack chance

### Hunger System
Carnivores must hunt to survive:
- Hunger increases by a fixed amount each iteration
- Successful hunts reduce hunger proportional to prey weight
- Hunger reduction formula: `(prey_weight / predator_weight) * base_reduction`
- Starvation occurs at 100% hunger

### Reproduction
Breeding follows biological principles:
- Animals must reach minimum breeding age
- Reproduction triggered when `current_age % iteration_count == 0`
- Offspring inherit species characteristics
- Population growth balanced against resource constraints

### Lifecycle Management
- **Age Progression**: All animals age each iteration
- **Mortality**: Death from starvation, predation, or old age
- **Cleanup**: Automatic removal of deceased animals
- **Extinction Detection**: Tracks species population counts

## Design Notes

### Architecture Highlights
- **Object-Oriented Design**: Clear separation of concerns with models, services, and enums
- **Extensible Framework**: Easy to add new species via `AnimalCreatorService`
- **Service Layer Pattern**: Business logic encapsulated in dedicated services
- **Custom Exception Handling**: Graceful error management for edge cases

### Key Design Patterns
- **Factory Pattern**: `AnimalCreatorService` creates animal instances
- **Strategy Pattern**: Different behaviors for carnivores vs. herbivores
- **Observer Pattern**: Ecosystem tracks animal state changes

### Technical Features
- **Probability-based mechanics**: Realistic attack outcomes using weighted probabilities
- **Dynamic population management**: Efficient tracking of animal groups
- **Stateful simulation**: Persistent state across iterations
- **Type-safe enumerations**: Strong typing for animals, biomes, and habitats

## Testing

### Running Tests
Execute the test suite using Maven:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CarnivoreTest

# Run tests with coverage report
mvn clean test jacoco:report
```

### Test Coverage
The project includes comprehensive unit tests for:
- **Model Tests**: Animal behavior validation
  - `CarnivoreTest.java`
  - `HerbivoreTest.java`
  - `EcosystemTest.java`

- **Service Tests**: Business logic verification
  - `AnimalCreatorServiceTest.java`
  - `FeedingServiceTest.java`
  - `ProbabilitiesServiceTest.java`
  - `SimulationRunnerTest.java`

## Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/new-species`
3. **Make your changes**: Add new animals, biomes, or mechanics
4. **Write tests**: Ensure your code is well-tested
5. **Commit your changes**: `git commit -am 'Add new species: Wolf'`
6. **Push to the branch**: `git push origin feature/new-species`
7. **Open a Pull Request**

### Ideas for Contributions
- Add new animal species
- Implement additional biomes
- Enhance AI behaviors
- Improve visualization/logging
- Optimize performance
- Add seasonal effects

---

**Built with ❤️ using Java 21 and Maven**