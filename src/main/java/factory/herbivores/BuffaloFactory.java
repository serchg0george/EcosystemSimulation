package factory.herbivores;

import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.GROUP;
import factory.AnimalCreator;
import factory.AnimalFactory;

import java.util.Set;

public class BuffaloFactory extends AnimalFactory {
    @Override
    protected AnimalCreator createNewAnimal(String groupName) {
        return new Buffalo(Set.of(SAVANNA), 0, true, 35, 800, 9, LAND, HERBIVORE, "Buffalo", GROUP, true, 40, groupName);
    }
}
