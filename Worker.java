import java.util.*;

public class Worker {
    public String name;
    public List<Task> tasks;
    double totalWorkTime; // общее время работы
    double wastedTime; //время простоя

    public Worker (String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
        this.totalWorkTime = 0;
        this.wastedTime = 0;
    }
}
