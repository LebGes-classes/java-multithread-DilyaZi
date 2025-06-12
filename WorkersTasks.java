import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class WorkersTasks {
    private final List<Thread> threads; // хранение списка потоков
    private final List<Worker> workers;

    public WorkersTasks() {
        this.threads = new ArrayList<>();
        this.workers = new ArrayList<>();
    }

    public void processWorkerTasks(Worker worker) {
        double workTimePerDay = 0;
        Iterator<Task> iterator = worker.tasks.iterator();
        while (iterator.hasNext() && workTimePerDay < 8) {
            Task task = iterator.next();
            double hoursOnTaskToday = Math.min(8 - workTimePerDay, Math.min(task.timeLeft, 8));
            if (hoursOnTaskToday > 0) {
                task.timeLeft -= hoursOnTaskToday;
                workTimePerDay += hoursOnTaskToday;
                worker.totalWorkTime += hoursOnTaskToday;
                if (task.timeLeft <= 0) { // если задача выполнена, удаляем ее
                    iterator.remove();
                }
            }
        }
        if (workTimePerDay < 8) { // если все задачи выполнены и осталось время, учитываем время простоя
            worker.wastedTime += 8 - workTimePerDay;
        }
    }

    // запускаем потоки сотрудников
    public void startProcessing() throws InterruptedException {
        for (Worker worker : workers) {
            Thread thread = new Thread(() -> processWorkerTasks(worker));
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    public void readExcel() throws IOException {
        try (FileInputStream file = new FileInputStream("РаботникиИЗадачи.xlsx");
             Workbook wbook = new XSSFWorkbook(file)) {
            Sheet sheet = wbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String name = row.getCell(0).getStringCellValue();
                Worker worker = new Worker(name);
                for (int i = 1; i < row.getLastCellNum(); i += 2) {
                    String taskName = row.getCell(i) != null ? row.getCell(i).getStringCellValue() : "";
                    double time = row.getCell(i + 1) != null ?
                            row.getCell(i + 1).getNumericCellValue() : 0;
                    if (!taskName.isEmpty() && time > 0) {
                        worker.tasks.add(new Task(taskName, time));
                    }
                }
                workers.add(worker);
            }
        }
    }

    public void saveDailyInfo() throws IOException {
        try (Workbook wbook = new XSSFWorkbook();
             FileOutputStream file = new FileOutputStream("ОтчетЗаДень.xlsx")) {
            Sheet sheet = wbook.createSheet("Отчет за день");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Работник");
            header.createCell(1).setCellValue("Задача");
            header.createCell(2).setCellValue("Оставшееся время");
            header.createCell(3).setCellValue("Общее время работы");
            header.createCell(4).setCellValue("Время простоя");

            int rowNum = 1;
            for (Worker worker : workers) {
                if (worker.tasks.isEmpty()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(worker.name);
                    row.createCell(1).setCellValue("Все задачи выполнены");
                    row.createCell(2).setCellValue(0);
                    row.createCell(3).setCellValue(worker.totalWorkTime);
                    row.createCell(4).setCellValue(worker.wastedTime);
                } else {
                    for (Task task : worker.tasks) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(worker.name);
                        row.createCell(1).setCellValue(task.taskName);
                        row.createCell(2).setCellValue(task.timeLeft);
                        row.createCell(3).setCellValue(worker.totalWorkTime);
                        row.createCell(4).setCellValue(worker.wastedTime);
                    }
                }
            }
            wbook.write(file);
        }

    }

    public void printStatistics() {
        System.out.println("*** Статистика эффективности работников ***");
        System.out.printf("%-15s %-15s %-15s %-15s%n",
                "Работник", "Время работы", "Время простоя", "Эффективность %");
        for (Worker worker: workers) {
            double eff = (worker.totalWorkTime / (worker.totalWorkTime+worker.wastedTime)) * 100;
            System.out.printf("%-15s %-15.2f %-15.2f %-15.2f%n",
                    worker.name, worker.totalWorkTime, worker.wastedTime, eff);
        }
    }
}
