package lab5.itmo.collection.models;

public class Location {
    private float x;

    private Long y; //Поле не может быть null

    private Long z; //Поле не может быть null

    private String name; //Поле не может быть null

    public Location(float x, Long y, Long z, String name){
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    @Override
    public String toString(){
        return x + ", " + y + ", " + z + ", " + name;
    }

}
