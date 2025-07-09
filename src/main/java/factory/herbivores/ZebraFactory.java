package factory.herbivores;

import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.GROUP;

import factory.AnimalCreator;
import factory.AnimalFactory;

import java.util.Set;

public class ZebraFactory extends AnimalFactory {

    @Override
    protected AnimalCreator createNewAnimal(String groupName) {
        return new Zebra(Set.of(SAVANNA), 0, true, 50, 300, 10, LAND, HERBIVORE, "Zebra", GROUP, true, 80, groupName);
    }
}
