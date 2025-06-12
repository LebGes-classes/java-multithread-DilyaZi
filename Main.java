import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            WorkersTasks workersTasks = new WorkersTasks();
            workersTasks.readExcel();
            workersTasks.startProcessing();
            workersTasks.saveDailyInfo();
            workersTasks.printStatistics();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}