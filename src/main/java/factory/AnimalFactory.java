package factory;

public abstract class AnimalFactory {
    public AnimalCreator create(String animalKind, String groupName) {
        AnimalCreator animal = createNewAnimal(groupName);
        animal.create(animalKind);
        return animal;
    }
    protected abstract AnimalCreator createNewAnimal(String groupName);
}
