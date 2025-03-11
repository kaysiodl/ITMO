package lab5.itmo.collection.managers;

import com.google.gson.*;
import lab5.itmo.exceptions.NullFieldException;
import lab5.itmo.client.io.utility.IdGenerator;
import lab5.itmo.collection.models.Person;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class CollectionManager{
    private final IdGenerator idGenerator = new IdGenerator();

    private Map<Integer, Person> collection = new LinkedHashMap<Integer, Person>();

    private LocalDateTime lastSaveTime;

    private LocalDateTime lastInitTime;

    public void add(Person person){
        Integer id = this.idGenerator.getNextId();

        if (person.getId() == null) {
            id = collection.values().stream().map(Person::getId).max(Integer::compareTo).orElse(0) + 1;
            person.setId(id);
        }
        if (person.getCreationDate() == null) {
            person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
        }
        person.validate();
        collection.put(id, person);
        setLastInitTime(LocalDateTime.now());
        System.out.println("New element added to collection successfully. ");
    }

    public void add(Person person, Integer id){
        person.setId(id);
        if (person.getCreationDate() == null) {
            person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
        }
        person.validate();
        collection.put(id, person);
        setLastInitTime(LocalDateTime.now());
        System.out.println("New element added to collection successfully. ");
    }

    public void saveCollection(Path path) throws IOException {
        if (collection.isEmpty()) {
            System.out.println("Collection is empty. Nothing to save.");
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonCollection = gson.toJson(collection.values());

        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(jsonCollection);
        } catch (IOException e) {
            System.err.printf("Failed when trying to write to %s: %s%n", path.getFileName(), e.getMessage());
            throw e;
        }
        lastSaveTime = LocalDateTime.now();
    }

    public void loadCollection(Path path) throws IOException, NullFieldException {
        List<Person> personList = new DumpManager(path).jsonFileToList();
        for (Person person : personList) {
            this.add(person);
        }
    }

    public List<Integer> sort(){
        List<Integer> sortedList = new ArrayList<>(collection.keySet());
        Collections.sort(sortedList);
        return sortedList;
    }

    public Person getById(Integer id) {
        for (Person person : collection.values()) {
            if (Objects.equals(person.getId(), id)) return person;
        }
        return null;
    }

    public void removeById(Integer id) {
        collection.remove(id);
    }

    public boolean removeAll() {
        collection.clear();
        return true;
    }

    public Map getCollection() {
        return collection;
    }

    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    public void setLastInitTime(LocalDateTime lastInitTime) {
        this.lastInitTime = lastInitTime;
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) {
            return "Collection is empty.";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, Person> entry : collection.entrySet()) {
            int key = entry.getKey();
            Person person = entry.getValue();
            stringBuilder.append(key)
                    .append(": ")
                    .append(person)
                    .append("\n");
        }

        return stringBuilder.toString().trim();
    }

}
