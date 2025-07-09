package factory.carnivores;

import static enums.AnimalType.CARNIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import factory.AnimalCreator;
import factory.AnimalFactory;

import java.util.Set;

public class LionFactory extends AnimalFactory {
    @Override
    protected AnimalCreator createNewAnimal(String groupName) {
        return new Lion(Set.of(SAVANNA), 0, true, 30, 150, 6, LAND, CARNIVORE, ALONE, "Cheetah", false, 110, "Loners", 20);
    }
}
