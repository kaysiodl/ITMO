package lab5.itmo.collection.managers;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

import lab5.itmo.client.io.utility.IOHandler;
import lab5.itmo.client.io.utility.ZonedDateTimeAdapter;
import lab5.itmo.collection.models.Person;

public class DumpManager implements IOHandler<JsonElement> {

    protected Path path = Path.of(System.getProperty("filePath", "data.json"));

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .setPrettyPrinting()
            .create();


    protected BufferedInputStream reader;

    protected BufferedWriter writer;


    /**
     * Converts file json into a list of Person type
     *
     * @return
     * @throws IOException
     */
    public List<Person> jsonFileToList() throws IOException {
        JsonElement jsonElement;
        List<Person> personList = new ArrayList<>();

        jsonElement = this.read();

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray.asList()) {
                Person person = gson.fromJson(element, Person.class);
                if (person.getCreationDate() == null) person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
                personList.add(person);
            }
        } else {
            Person person = gson.fromJson(jsonElement, Person.class);
            if (person.getCreationDate() == null) person.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
            personList.add(person);
        }

        return personList;
    }

    /**
     * Reads collection from file
     *
     * @return
     */

    @Override
    public JsonElement read() throws FileNotFoundException {
        reader = new BufferedInputStream(new FileInputStream(path.toFile()));
        StringBuilder stringBuilder = new StringBuilder();
        int nextChar;
        try {
            while ((nextChar = reader.read()) != -1) {
                stringBuilder.append((char) nextChar);
            }
        } catch (IOException e) {
            System.err.println("Can't read file: " + path);
            return null;
        }

        String jsonString = stringBuilder.toString();
        if (jsonString.trim().isEmpty()) {
            System.err.println("File is empty: " + path);
            return null;
        }

        try {
            return JsonParser.parseString(jsonString);
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid json syntax in file: " + path);
            return null;
        }
    }

    /**
     * Writes collection to a file
     *
     * @param collection
     */
    @Override
    public void write(String collection) throws IOException {
        if (collection == null) {
            throw new IllegalArgumentException("Collection cannot be null.");
        }

        if (Files.notExists(path)) {
            Files.createFile(path);
        }

        if (!Files.isWritable(path)) {
            System.err.printf("Cannot write to %s%n", path.getFileName());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(gson.toJson(collection));
        } catch (IOException e) {
            System.err.printf("Failed when trying to write to %s: %s%n", path.getFileName(), e.getMessage());
            throw e;
        }
    }


    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
    }
}
