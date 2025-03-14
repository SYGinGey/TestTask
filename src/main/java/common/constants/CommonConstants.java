package common.constants;

public class CommonConstants {

    public final static String ADDRESS = System.getenv().getOrDefault("ADDRESS", "localhost:8080");
    public final static String BASE_URL = "http://" + ADDRESS;
    public final static String ADMIN_LOGIN = "admin";       // VAULT.get("ADMIN_LOGIN");
    public final static String ADMIN_PASSWORD = "admin";    // VAULT.get("ADMIN_PASSWORD");
}
