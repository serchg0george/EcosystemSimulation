package factory.carnivores;

import static enums.AnimalType.CARNIVORE;
import static enums.Biome.SAVANNA;
import static enums.Habitat.LAND;
import static enums.LivingType.ALONE;
import factory.AnimalCreator;
import factory.AnimalFactory;

import java.util.Set;

public class TigerFactory extends AnimalFactory {
    @Override
    protected AnimalCreator createNewAnimal(String groupName) {
        return new Tiger(Set.of(SAVANNA), 0, true, 20, 200, 6, LAND, CARNIVORE, ALONE, "Tiger", false, 75, "Loners", 18);
    }
}
