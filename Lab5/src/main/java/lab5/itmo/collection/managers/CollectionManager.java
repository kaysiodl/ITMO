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

    public void add(Person person) throws NullFieldException {
        if (person == null) {
            throw new NullFieldException("Person cannot be null.");
        }

        Integer id = this.idGenerator.getNextId();

        if (person.getId() == null) {
            id = this.idGenerator.getNextId();
            while (!isIdFree(id)) {
                id = this.idGenerator.getNextId();
            }
            person.setId(id);
        }
        if (person.getCreationDate() == null) {
            person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
        }
        person.validate();
        collection.put(id, person);
        System.out.println("New element added to collection successfully. ");
    }

    private boolean isIdFree(Integer id) {
        for(Person person: collection.values()){
            if (Objects.equals(id, person.getId())) return false;
        }
        return true;
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

    public List sort(){
        List<Integer> sortedList = new ArrayList<>(collection.keySet());
        Collections.sort(sortedList);
        return sortedList;
    }


    public boolean removeById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }

        if (collection.containsKey(id)) {
            collection.remove(id);
            System.out.println("Element with id " + id + " has been removed.");
            return true;
        } else {
            System.out.println("Element with id " + id + " not found.");
            return false;
        }
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

    @Override
    public String toString() {
        if (collection.isEmpty()) {
            return " Collection is empty.";
        }
        System.out.println(collection);
        StringBuilder string = new StringBuilder();
        return string.toString().trim();
    }

}
