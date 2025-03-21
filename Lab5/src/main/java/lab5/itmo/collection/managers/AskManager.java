package lab5.itmo.collection.managers;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.models.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class AskManager {

    public static class Break extends Exception {
    }

    public static Person personFromScript(String[] data) {
        try {
            String name = data[0];
            Coordinates coordinates = new Coordinates(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            float height = Float.parseFloat(data[3]);
            Color eyeColor = Color.valueOf(data[4].toUpperCase());
            Color hairColor = Color.valueOf(data[5].toUpperCase());
            Country nationality = Country.valueOf(data[6].toUpperCase());
            Location location = new Location(Float.parseFloat(data[7]),
                    Long.parseLong(data[8]),
                    Long.parseLong(data[9]),
                    data[10]);
            coordinates.validate();
            Person person = new Person(name, coordinates, height, eyeColor,hairColor, nationality, location);
            return person;
        }catch (IllegalStateException e){
            throw new IllegalArgumentException("Failed reading data, " + e.getMessage());
        }
    }

    public static Person askPerson(Console console) throws Break {
        try {
            String name;
            while (true) {
                console.print("name: ");
                name = console.read().trim();
                if (name.equals("exit")) throw new Break();
                if (!name.isEmpty()) break;
            }
            Coordinates coordinates = askCoordinates(console);
            float height = askHeight(console);
            Color eyeColor = askEyeColor(console);
            Color hairColor = askHairColor(console);
            Country nationality = askCountry(console);
            Location location = askLocation(console);
            return new Person(name, coordinates, height, eyeColor, hairColor, nationality, location);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Failed reading data.");
            return null;
        }
    }

    public static Coordinates askCoordinates(Console console) throws Break {
        try {
            int x;
            while (true) {
                console.print("coordinates x: ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        x = Integer.parseInt(line);
                        if (x < -517) {
                            console.printError("Coordinate x can't be lower than -517. PLease try again");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            int y;
            while (true) {
                console.print("coordinates y: ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        y = Integer.parseInt(line);
                        if (y < -652) {
                            console.printError("Coordinate y can't be lower than -652. PLease try again");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return new Coordinates(x, y);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Failed reading coordinates.");
            return null;
        }
    }


    public static float askHeight(Console console) throws Break {
        try {
            float height;
            while (true) {
                console.print("Height: ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.equals("")) {
                    try {
                        height = Float.parseFloat(line);
                        if (height <= 0) {
                            console.printError("Height must be higher than 0. PLease try again");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return height;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Failed reading height.");
            return 0;
        }
    }

    public static Color askEyeColor(Console console) throws Break {
        try {
            Color eyeColor;
            while (true) {
                console.print("Eye color(BLUE, GREEN, ORANGE, WHITE, BROWN): ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        switch (line.toUpperCase()) {
                            case "BLUE":
                                eyeColor = Color.BLUE;
                                break;
                            case "GREEN":
                                eyeColor = Color.GREEN;
                                break;
                            case "ORANGE":
                                eyeColor = Color.ORANGE;
                                break;
                            case "WHITE":
                                eyeColor = Color.WHITE;
                                break;
                            case "BROWN":
                                eyeColor = Color.BROWN;
                                break;
                            default:
                                console.printError("Invalid color. Please choose from BLUE, GREEN, ORANGE, WHITE, BROWN.");
                                continue;

                        }
                        return eyeColor;
                    } catch (Exception e) {
                        console.printError("Failed to parse eye color.");
                    }
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Failed reading eye color.");
            return null;
        }
    }

    public static Color askHairColor(Console console) throws Break {
        try {
            Color hairColor;
            while (true) {
                console.print("Hair color(BLUE, ORANGE, WHITE, BROWN): ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        switch (line.toUpperCase()) {
                            case "BLUE":
                                hairColor = Color.BLUE;
                                break;
                            case "ORANGE":
                                hairColor = Color.ORANGE;
                                break;
                            case "WHITE":
                                hairColor = Color.WHITE;
                                break;
                            case "BROWN":
                                hairColor = Color.BROWN;
                                break;
                            default:
                                console.printError("Invalid color. Please choose from BLUE, ORANGE, WHITE, BROWN.");
                                continue;

                        }
                        return hairColor;
                    } catch (Exception e) {
                        console.printError("Failed to parse hair color.");
                    }
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Failed reading hair color.");
            return null;
        }
    }

    public static Country askCountry(Console console) throws Break {
        try {
            Country nationallity;
            while (true) {
                console.print("Country(FRANCE, SPAIN, THAILAND): ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        switch (line.toUpperCase()) {
                            case "THAILAND":
                                nationallity = Country.THAILAND;
                                break;
                            case "SPAIN":
                                nationallity = Country.SPAIN;
                                break;
                            case "FRANCE":
                                nationallity = Country.FRANCE;
                                break;
                            default:
                                console.printError("Invalid country. Please choose from FRANCE, SPAIN, THAILAND.");
                                continue;

                        }
                        return nationallity;
                    } catch (Exception e) {
                        console.printError("Failed to parse country.");
                    }
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Failed reading country.");
            return null;
        }
    }

    public static Location askLocation(Console console) throws Break {
        try {
            console.println("Location: ");
            float x;
            while (true) {
                console.print("coordinates x: ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        x = Float.parseFloat(line);
                        break;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            Long y;
            while (true) {
                console.print("coordinates y: ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        y = Long.parseLong(line);
                        break;
                    } catch (NumberFormatException e) {
                    }
                }
            }

            Long z;
            while (true) {
                console.print("coordinates z: ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        z = Long.parseLong(line);
                        break;
                    } catch (NumberFormatException e) {
                    }
                }
            }

            String name;
            while (true) {
                console.print("Name: ");
                String line = console.read().trim();
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        name = line;
                        break;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return new Location(x, y, z, name);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Failed reading location.");
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}