package factory;

import models.Animal;

public abstract class AnimalFactory {
    public Animal create(String animalKind, String groupName) {
        AnimalCreator animal = createNewAnimal(groupName);
        animal.create(animalKind);
        return (Animal) animal;
    }
    protected abstract AnimalCreator createNewAnimal(String groupName);
}
