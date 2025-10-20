import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.BiFunction;

class PersonCabinet implements Serializable {
    private final ArrayList<BankAccount> list;
    private final String name;
    private final int age;
    private BankAccount current_acc = null;

    public PersonCabinet(String name, int age){
        this.age = age;
        this.name = name;
        this.list = new ArrayList<>();
    }

    public void createAccount(double m, String name){
        BankAccount obj = new BankAccount(m, name);
        list.add(obj);
    }

    public ArrayList<BankAccount> find(String mode, String str, BiFunction<String, String, Boolean> func){
        return switch (mode){
            case "-name" -> _find(BankAccount::getName, str, func);
            case "-id" -> _find(BankAccount::getId, str, func);
            case "-kpp" -> _find(BankAccount::getKpp, str, func);
            case "-bik" -> _find(BankAccount::getBik, str, func);
            default -> throw new IllegalArgumentException("Invalid mode: " + mode);
        };
    }

    private ArrayList<BankAccount> _find(Function<BankAccount, String> atr, String str, BiFunction<String, String, Boolean> func){
        ArrayList<BankAccount> tmp = new ArrayList<>();
        for (BankAccount acc : list){
            String value = atr.apply(acc);
            if (func.apply(value, str))
                tmp.add(acc);
        }

        return tmp;
    }

    @Override
    public String toString() {
        return "- name=" + name + " " + "age=" + age;

    }

    public ArrayList<BankAccount> getAccounts(){
        return list;
    }
    public String getAccStr() {
        if (current_acc == null)
            return "/>";
        else
            return  current_acc.getName() + "/>";
    }
    public BankAccount getAccObj(){
        return  current_acc;
    }
    public void setAcc(BankAccount ba){ current_acc = ba;}
    public void setNullAcc() { current_acc = null; }
}


public class BankAccount implements Serializable {
    private double money;
    private final String[] list = new String[100];
    private int current_index_list = 0;
    private final String name;
    private String id_acc;
    private String kpp;
    private String bik;
    private transient ServiceAccaunt service;

    public BankAccount(double money, String name) {
        this.money = money;
        this.name = name;
        this.service = new ServiceAccaunt();
        setData();
    }

    public void putMoney(double amount){
        money += amount;
        logs("put", money);
    }

    public void takeMoney(double amount) {
        if (amount > money) {
            System.out.println("Нет столько денег на счету");
            return;
        }
        money -= amount;
        logs("take", money);
    }

    public void printHistory() {
        for (int i = current_index_list - 1; i >= 0; i--){
            if (list[i] == null) break;
            System.out.println(list[i]);
        }
        for (int i = 99; i > current_index_list; i--){
            if (list[i] == null) break;
            System.out.println(list[i]);
        }
    }

    private void logs(String mode, double money) {
        if (current_index_list == 100){
            current_index_list = 0;
        }
        switch (mode) {
            case "create":
                list[current_index_list++] = "Счёт был создан; начальная сумма: " + money + ";" + LocalTime.now();
                break;
            case "put":
                list[current_index_list++] = "Положили деньги; количество: " + money + ";" + LocalTime.now();
                break;
            case "take":
                list[current_index_list++] = "Сняли деньги; количество: " + money + ";" + LocalTime.now();
                break;
        }
    }

    private void setData(){
        id_acc = service.generateId(this.name);
        kpp = service.generateKpp();
        bik = service.generateBik();
        logs("create", money);
    }

    public double getMoney() { return money; }
    public String getName() { return name; }
    public String getId() { return id_acc; }
    public String getKpp() { return kpp; }
    public String getBik() { return bik; }


    @Override
    public String toString() {
        return "┌───────────────────────────────────────┐\n" +
                "│              БАНКОВСКИЙ СЧЕТ          │\n" +
                "├───────────────────────────────────────┤\n" +
                "│  Наименование: " + String.format("%-22s", name) + " │\n" +
                "│  КПП:         " + String.format("%-23s", kpp) + " │\n" +
                "│  БИК:         " + String.format("%-23s", bik) + " │\n" +
                "│  Баланс:      " + String.format("%-21.2f", money) + " ₽ │\n" +
                "└───────────────────────────────────────┘";
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.service = new ServiceAccaunt();
    }
}
