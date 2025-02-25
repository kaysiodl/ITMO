package lab5.itmo.collection.models;

public enum Color {
    GREEN,
    BLUE,
    ORANGE,
    WHITE,
    BROWN;

    public static String names(){
        StringBuilder colorsList = new StringBuilder();
        for(Color color: values()){
            colorsList.append(color.name()).append(", ");
        }
        return colorsList.substring(0, colorsList.length()-2);
    }
}