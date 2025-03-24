package lab5.itmo.collection.managers;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.models.*;

import java.io.IOException;

public class AskManager {

    public static class Break extends Exception {
        public Break() {
            super("Stopped creating new person.");
        }
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
            return new Person(name, coordinates, height, eyeColor, hairColor, nationality, location);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Failed reading data, " + e.getMessage());
        }
    }

    public static Person askPerson(Console console) throws Break {
        try {
            String name;
            do {
                console.print("name: ");
                name = console.read().trim();
                if (name.equals("exit")) throw new Break();
                if (name.isEmpty())
                    console.println("Name can't be empty.");
            } while (name.isEmpty());
            Coordinates coordinates = askCoordinates(console);
            float height = askHeight(console);
            Color eyeColor = askEyeColor(console);
            Color hairColor = askHairColor(console);
            Country nationality = askCountry(console);
            Location location = askLocation(console);
            return new Person(name, coordinates, height, eyeColor, hairColor, nationality, location);
        } catch (IllegalStateException e) {
            console.printError("Failed reading data.");
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Coordinates askCoordinates(Console console) throws Break {
        try {
            int x;
            while (true) {
                console.print("coordinates x: ");
                String line = console.read().trim();
                if (line.isEmpty())
                    console.println("Coordinate x can't be empty.");
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
                        System.out.println("x is not right format. Try again.");
                    }
                }
            }
            int y;
            while (true) {
                console.print("coordinates y: ");
                String line = console.read().trim();
                if (line.isEmpty())
                    console.println("Coordinate y can't be empty.");
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
                        System.out.println("y is not right format. Try again.");
                    }
                }
            }
            return new Coordinates(x, y);
        } catch (IllegalStateException e) {
            console.printError("Failed reading coordinates.");
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static float askHeight(Console console) throws Break {
        try {
            float height;
            while (true) {
                console.print("Height: ");
                String line = console.read().trim();
                if (line.isEmpty()) {
                    console.print("Height can't be empty.\n");
                    continue;
                }
                if (line.equals("exit")) {
                    throw new Break();
                }
                try {
                    height = Float.parseFloat(line);
                    if (height <= 0) {
                        console.printError("Height must be higher than 0. Please try again.");
                        continue;
                    }
                    if (height > Float.MAX_VALUE) {
                        console.printError("Height is too large. Please enter a smaller value.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    console.printError("Height is not in the correct format. Please try again.");
                }
            }
            return height;
        } catch (IllegalStateException e) {
            console.printError("Failed reading height.");
            return 0;
        }
    }

    public static Color askEyeColor(Console console) throws Break {
        try {
            while (true) {
                console.print("Eye color(BLUE, GREEN, ORANGE, WHITE, BROWN): ");
                String line = console.read().trim();
                if (line.isEmpty())
                    console.print("Eye color can't be empty.\n");
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        return Color.valueOf(line.toUpperCase());
                    } catch (Exception e) {
                        console.printError("Failed to parse eye color. Enter one of colors from list.");
                    }
                }
            }
        } catch (IllegalStateException e) {
            console.printError("Failed reading eye color.");
            return null;
        }
    }

    public static Color askHairColor(Console console) throws Break {
        try {
            while (true) {
                console.print("Hair color(BLUE, GREEN, ORANGE, WHITE, BROWN): ");
                String line = console.read().trim();
                if (line.isEmpty())
                    console.print("Hair color can't be empty.\n");
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        return Color.valueOf(line.toUpperCase());
                    } catch (Exception e) {
                        console.printError("Failed to parse hair color. Enter one of colors from list.");
                    }
                }
            }
        } catch (IllegalStateException e) {
            console.printError("Failed reading hair color.");
            return null;
        }
    }

    public static Country askCountry(Console console) throws Break {
        try {
            while (true) {
                console.print("Country(FRANCE, SPAIN, THAILAND): ");
                String line = console.read().trim();
                if (line.isEmpty())
                    console.print("Country can't be empty.\n");
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        return Country.valueOf(line.toUpperCase());
                    } catch (Exception e) {
                        console.printError("Failed to parse country. Enter one of countries from list.");
                    }
                }
            }
        } catch (IllegalStateException e) {
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
                if (line.isEmpty())
                    console.print("Coordinate x can't be empty.\n");
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        x = Float.parseFloat(line);
                        if (x > Float.MAX_VALUE) {
                            console.printError("x is too large. Please enter a smaller value.");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("x is not right format. Try again.");
                    }
                }
            }
            Long y;
            while (true) {
                console.print("coordinates y: ");
                String line = console.read().trim();
                if (line.isEmpty())
                    console.print("Coordinate y can't be empty.\n");
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        y = Long.parseLong(line);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("y is not right format. Try again.");
                    }
                }
            }

            Long z;
            while (true) {
                console.print("coordinates z: ");
                String line = console.read().trim();
                if (line.isEmpty())
                    console.print("Coordinate z can't be empty.\n");
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    try {
                        z = Long.parseLong(line);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("z is not right format. Try again.");
                    }
                }
            }

            String name;
            while (true) {
                console.print("Name: ");
                String line = console.read().trim();
                if (line.isEmpty())
                    console.print("Location name can't be empty.\n");
                if (line.equals("exit")) throw new Break();
                if (!line.isEmpty()) {
                    name = line;
                    break;
                }
            }
            return new Location(x, y, z, name);
        } catch (IllegalStateException e) {
            console.printError("Failed reading location.");
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}