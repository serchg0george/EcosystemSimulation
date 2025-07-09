package factory.carnivores;

import static enums.AnimalType.CARNIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.GROUP;
import factory.AnimalCreator;
import factory.AnimalFactory;

import java.util.Set;

public class HyenaFactory extends AnimalFactory {
    @Override
    protected AnimalCreator createNewAnimal(String groupName) {
        return new Hyena(Set.of(SAVANNA), 0, true, 24, 50, 5, LAND, CARNIVORE, GROUP, "Hyena", true, 80, groupName, 14);
    }
}
