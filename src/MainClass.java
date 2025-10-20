/*
Задача 2. Банковский счёт
Консольное меню: открыть счёт, положить деньги, снять деньги, показать баланс, вывести список транзакций,
искать по атрибутам.

справка по командам - help
*/


import javax.swing.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;


public class MainClass {
    private BankAccount bank;
    private PersonCabinet pc;
    private static final String DATA_FILE = "bank_data.dat";

    public static void main(String[] args) {
        MainClass prog = new MainClass();
        try {
            prog.run();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void run(){
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveData));
        Scanner scanner = new Scanner(System.in);

        loadData();

        System.out.println("Cправка по командам - help(как для кабинета так и для счёта)");
        while (true) {
            String cur = pc.getAccStr();
            System.out.print(cur);
            String text = scanner.nextLine();
            String[] command = text.trim().split("\\s+");
//            for (int i = 0; i < command.length; i++){
//                command[i] = command[i].trim();
//                System.out.println(command[i]);
//            }

            if (command[0].equals("exit") && cur.equals("/>")) {
                break;
            }

            if (cur.equals("/>")) {
                if (in_cab(command)) {
                    break;
                }
            } else {
                in_acc(command);
            }
        }

        saveData();
    }

    private void in_acc(String[] command){
        try {
            switch (command[0]) {
                case "put":
                    double putAmount = Double.parseDouble(command[1]);
                    bank.putMoney(putAmount);
                    System.out.println("Успешно пополнено: " + putAmount + " ₽");
                    break;

                case "take":
                    double takeAmount = Double.parseDouble(command[1]);
                    bank.takeMoney(takeAmount);
                    System.out.println("Успешно снято: " + takeAmount + " ₽");
                    break;

                case "balance":
                    double m = bank.getMoney();
                    System.out.println("Ваш баланс: " + m + " ₽");
                    break;

                case "history":
                    bank.printHistory();
                    break;

                case "info":
                    System.out.println(bank);
                    break;

                case "exit":
                    pc.setAcc(null);
                    bank = null;
                    System.out.println("Возврат в личный кабинет");
                    break;

                case "help":
                    printHelp();
                    break;

                default:
                    System.out.println("Неверная команда счёта");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Ошибка: недостаточно аргументов для команды");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: неверный формат числа");
        } catch (NullPointerException e) {
            System.out.println("Ошибка: счет не выбран");
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
    }

    private boolean in_cab(String[] command){
        try {
            switch (command[0]) {
                case "cd":
                    ArrayList<BankAccount> tmp_one;
                    if (command.length == 2) {
                        tmp_one = pc.find("-name", command[1], String::equals);
                    }
                    else{
                        tmp_one = pc.find(command[1], command[2], String::equals);
                    }

                    if (tmp_one.size() == 1) {
                        pc.setAcc(tmp_one.getFirst());
                        bank = pc.getAccObj();
                    } else if (tmp_one.isEmpty()) {
                        System.out.println("Счет не найден");
                    } else {
                        System.out.println("Найдено несколько счетов: " + tmp_one.size());
                    }
                    break;

                case "ls":
                    ArrayList<BankAccount> tmp_list = pc.getAccounts();
                    if (tmp_list.isEmpty()) {
                        System.out.println("Нет открытых счетов");
                    } else {
                        for (BankAccount ba : tmp_list) {
                            System.out.println(ba);
                        }
                    }
                    break;

                case "info":
                    System.out.println(pc);
                    break;

                case "add":
                    ArrayList<BankAccount> tmp_ch = pc.find("-name", command[1], String::equals);
                    if (tmp_ch.isEmpty()) {
                        pc.createAccount(Double.parseDouble(command[2]), command[1]);
                    } else {
                        System.out.println("Аккаунт с таким именем уже существует");
                    }
                    break;

                case "find":
                    ArrayList<BankAccount> tmp_find = pc.find(command[1], command[2], String::contains);
                    if (tmp_find.isEmpty()) {
                        System.out.println("Счета не найдены");
                    } else {
                        for (BankAccount ba : tmp_find) {
                            System.out.println(ba);
                        }
                    }
                    break;

                case "help":
                    printHelp();
                    break;

                case "exit":
                    return true;

                default:
                    System.out.println("Неверная команда кабинета.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Ошибка: недостаточно аргументов для команды '" + command[0] + "'");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: неверный формат числа '" + command[2] + "'");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Ошибка: объект не инициализирован");
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
        return false;
    }

    private void printHelp() {

        System.out.println("─────────────────────────────────────────");

        if (bank == null) {
            System.out.println("║           КОМАНДЫ КАБИНЕТА            ║");
            System.out.println("║───────────────────────────────────────║");
            System.out.println("║ ls              - список всех счетов  ║");
            System.out.println("║ info            - информация о себе   ║");
            System.out.println("║ add <имя> <сумма>  -создать счет      ║");
            System.out.println("║ cd -flag <знач>  - перейти в счет     ║");
            System.out.println("║ find -flag <знач>  - поиск счетов     ║");
            System.out.println("║ exit            - выход из программы  ║");
            System.out.println("║ help             - эта справка        ║");
            System.out.println("║───────────────────────────────────────║");
            System.out.println("║ Вместо флвга для cd/find: -name,      ║");
            System.out.println("║                  -id, -kpp, -bik      ║");
            System.out.println("║ *find ищет подстроки в строке         ║");
            System.out.println("║ *cd по умолчанию flag==\"-name\"        ║");
        } else {
            System.out.println("║           КОМАНДЫ СЧЕТА              ║");
            System.out.println("║──────────────────────────────────────║");
            System.out.println("║ put <сумма>     - пополнить счет     ║");
            System.out.println("║ take <сумма>    - снять со счета     ║");
            System.out.println("║ balance         - показать баланс    ║");
            System.out.println("║ history         - история операций   ║");
            System.out.println("║ info            - информация о счете ║");
            System.out.println("║ exit            - вернуться в кабинет║");
            System.out.println("║ help            - эта справка        ║");
        }

        System.out.println("╚═══════════════════════════════════════╝");
    }


    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                pc = (PersonCabinet) ois.readObject();
                pc.setNullAcc();
                System.out.println("Данные загружены из файла: " + DATA_FILE);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Ошибка загрузки данных: " + e.getMessage());
                System.out.println("Создан новый личный кабинет");
                pc = new PersonCabinet("Artem", 18);
            }
        } else {
            System.out.println("Файл данных не найден. Создан новый личный кабинет");
            pc = new PersonCabinet("Artem", 18);
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(pc);
            System.out.println("Данные сохранены в файл: " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения данных: " + e.getMessage());
        }
    }


}
