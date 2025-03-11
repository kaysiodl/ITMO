package lab5.itmo.collection.models;

public class Coordinates{
    private int x; //Значение поля должно быть больше -517

    private int y; //Значение поля должно быть больше -652

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int sumCoordinates(){return x + y; }

    @Override
    public String toString() {
        return x + ";" + y;
    }

}
