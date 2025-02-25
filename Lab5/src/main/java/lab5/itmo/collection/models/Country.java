package lab5.itmo.collection.models;

public enum Country {
    FRANCE,
    SPAIN,
    THAILAND;

    public static String names() {
        StringBuilder countriesList = new StringBuilder();
        for (Country country : values()) {
            countriesList.append(country.name()).append(", ");
        }
        return countriesList.substring(0, countriesList.length() - 2);
    }
}
