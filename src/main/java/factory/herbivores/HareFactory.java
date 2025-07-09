package factory.herbivores;

import static enums.AnimalType.HERBIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import factory.AnimalCreator;
import factory.AnimalFactory;

import java.util.Set;

public class HareFactory extends AnimalFactory {
    @Override
    protected AnimalCreator createNewAnimal(String groupName) {
        return new Hare(Set.of(SAVANNA), 0, true, 24, 5, 3, LAND, HERBIVORE, "Hare", ALONE, false, 100, "Loners");
    }
}
