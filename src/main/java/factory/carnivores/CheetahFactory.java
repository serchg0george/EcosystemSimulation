package factory.carnivores;

import static enums.AnimalType.CARNIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import factory.AnimalCreator;
import factory.AnimalFactory;

import java.util.Set;

public class CheetahFactory extends AnimalFactory {
    @Override
    protected AnimalCreator createNewAnimal(String groupName) {
        return new Cheetah(Set.of(SAVANNA), 0, true, 30, 60, 5, LAND, CARNIVORE, ALONE, "Cheetah", false, 110, "Loners", 15);
    }
}
