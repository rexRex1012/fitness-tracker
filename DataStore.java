import java.io.*;
import java.util.HashMap;

public class DataStore {

    private final String fileName;

    public DataStore(String fileName) {
        this.fileName = fileName;
    }

    public HashMap<String, User> load() {
        File f = new File(fileName);
        if (!f.exists() || f.length() == 0) {
            return new HashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = in.readObject();
            if (obj instanceof HashMap) {
                @SuppressWarnings("unchecked")
                HashMap<String, User> users = (HashMap<String, User>) obj;
                return users;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ERROR loading data, starting fresh: " + e.getMessage());
        }

        return new HashMap<>();
    }

    public void save(HashMap<String, User> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(users);
            out.flush();
            System.out.println("Saved to " + fileName);
        } catch (IOException e) {
            System.out.println("ERROR saving data: " + e.getMessage());
        }
    }
}