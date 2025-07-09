package factory.herbivores;

import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.GROUP;
import factory.AnimalCreator;
import factory.AnimalFactory;

import java.util.Set;

public class GazelleFactory extends AnimalFactory {
    @Override
    protected AnimalCreator createNewAnimal(String groupName) {
        return new Gazelle(Set.of(SAVANNA), 0, true, 25, 25, 5, LAND, HERBIVORE, "Gazelle", GROUP, true, 80, groupName);
    }
}
