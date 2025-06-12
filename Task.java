public class Task {
    String taskName;
    double taskTime; // время выполнения задачи
    double timeLeft; // оставшееся время для завершения задачи
                        // (если задача длинная, за один день не получится выполнить, остается время на след день)

    public Task(String taskName, double taskTime) {
        this.taskName = taskName;
        this.taskTime = taskTime;
        this.timeLeft = taskTime;
    }
}
