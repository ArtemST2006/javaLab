import java.util.Random;

public class ServiceAccaunt {
    private final Random random = new Random();

    public String generateId(String tag){
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < 12; i++){
            tmp.append(random.nextInt(10));
        }
        tmp.append("_").append(tag);
        return tmp.toString();
    }

    public String generateBik() {
        String countryCode = "04";
        String bankNumber = String.format("%07d", random.nextInt(10000000));

        return countryCode + bankNumber;
    }

    public String generateKpp() {
        String regionCode = String.format("%02d", random.nextInt(99) + 1);
        String taxOfficeCode = String.format("%03d", random.nextInt(999) + 1);
        String reasonCode = String.format("%03d", random.nextInt(999) + 1);

        return regionCode + taxOfficeCode + reasonCode;
    }
}
