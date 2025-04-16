package lab5.itmo.collection.managers;

import java.io.File;

public record BackUpManager(String fileName) {

    public boolean checkBackUpFile() {
        return new File(fileName).exists();
    }

    public void deleteBackUpFile() {
        if (checkBackUpFile()) {
            new File(fileName).delete();
        }
    }
}
