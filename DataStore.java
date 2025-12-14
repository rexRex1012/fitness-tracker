import java.io.*;
import java.util.HashMap;

public class DataStore {

    private final String fileName;

    public DataStore(String fileName) {
        this.fileName = fileName;
    }

    public void save(HashMap<String, User> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(users);
            out.flush();
            System.out.println("Data saved to " + fileName);
        } catch (IOException e) {
            System.out.println("ERROR: Could not save data. " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, User> load() {
        File f = new File(fileName);
        if (!f.exists() || f.length() == 0) {
            return new HashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = in.readObject();
            if (obj instanceof HashMap) {
                return (HashMap<String, User>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ERROR: Could not load data. Starting fresh. " + e.getMessage());
        }

        return new HashMap<>();
    }
}