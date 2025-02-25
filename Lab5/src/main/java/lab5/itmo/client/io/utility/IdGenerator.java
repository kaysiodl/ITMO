package lab5.itmo.client.io.utility;

public class IdGenerator {
    private static Integer nextId;

    public IdGenerator(){
        this.nextId = 0;
    }

    public Integer getNextId(){
        nextId++;
        return nextId;
    }
}
