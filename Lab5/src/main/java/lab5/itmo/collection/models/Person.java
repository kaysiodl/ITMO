package lab5.itmo.collection.models;

import lab5.itmo.exceptions.NullFieldException;
import lab5.itmo.client.io.utility.Element;
import lab5.itmo.client.io.utility.Validatable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * class Person
 */
public class Person extends Element implements Validatable, Comparable<Element> {
    private Integer id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float height; //Значение поля должно быть больше 0
    private Color eyeColor; //Поле может быть null
    private Color hairColor; //Поле не может быть null
    private Country nationality; //Поле может быть null
    private Location location; //Поле не может быть null

    private static int idCounter = 1;

    public Person(String name, Coordinates coordinates,
                  float height, Color eyeColor, Color hairColor,
                  Country nationality, Location location){
        this.id = idCounter++;
        this.name = name;
        this.height = height;
        this.coordinates = coordinates;
        this.creationDate = getCreationDate();
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
        validate();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ZonedDateTime getCreationDate(){
        creationDate = ZonedDateTime.now(ZoneId.systemDefault());
        return creationDate;
    }

    @Override
    public int compareTo(Element o) {
        if (this.id == null || o.getId() == null) {
            throw new IllegalStateException("id cannot be null for comparison.");
        }
        return this.getId().compareTo(o.getId());
    }

    //getters
    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Country getNationality() {
        return nationality;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public float getHeight() {
        return height;
    }
    //getters

    public int getSumCoordinates(){return coordinates.sumCoordinates(); }
    @Override
    public void validate() throws IllegalArgumentException{
        if (id<=0 || id == null) throw new IllegalArgumentException("Your id can't be lower than 0");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name can't be null or empty");
        if (coordinates == null) throw new IllegalArgumentException("Coordinates can't be null. Please try again.");
        if (height <= 0) throw new IllegalArgumentException("Height must be greater than 0. Please try again.");
        if (hairColor == null) throw new IllegalArgumentException("Hair color can't be null. Please try again.");
        if (location == null) throw new IllegalArgumentException("Location can't be null. PLease try again.");
    }


    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) && Objects.equals(id, person.id) && Objects.equals(coordinates, person.coordinates) && Objects.equals(creationDate, person.creationDate) && height == person.height && hairColor == person.hairColor && eyeColor == person.eyeColor && nationality == person.nationality && Objects.equals(location, person.location);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, coordinates, creationDate, height, eyeColor, hairColor, nationality, location);
    }
            
    @Override
    public String toString() {
        return "Person {" +
                "\n  name = '" + name + '\'' +
                ",\n  coordinates = " + coordinates +
                ",\n  creationDate = " + creationDate +
                ",\n  height = " + height +
                ",\n  eyeColor = " + eyeColor +
                ",\n  hairColor = " + hairColor +
                ",\n  nationality = " + (nationality != null ? nationality : "unspecified") +
                ",\n  location = " + (location != null ? location : "unspecified") +
                "\n}";
    }
}

