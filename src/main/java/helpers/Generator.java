package helpers;

import lombok.extern.slf4j.Slf4j;
import java.util.Random;

@Slf4j
public class Generator {

    static String lat = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String symb = "|;".toLowerCase();
    static String num = "0123456789".toLowerCase();
    static String all = lat + symb + num;
    private static final Random rnd = new Random();

    /***
     * @param start Стартовое число для генерации
     * @param end конечное число для генерации
     * @return Возвращает сгенерированное число     */
    public static int randInt(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    public static float randFloat(int start, int end) {
        return rnd.nextFloat(start, end);
    }

    public static double randDouble(int start, int end) {
        return rnd.nextDouble(start, end);
    }

    public static boolean randBool() {
        return rnd.nextBoolean();
    }

    public static long randLong(long start, long end) {
        return rnd.nextLong(start, end);
    }

    /**
     * @param min  мин значение генерируемой строки
     * @param max  макс значение генерируемой строки
     * @param type 0 - генерация латинцы, 1 - генерация спец символов, 2 - генерация чисел, 3 - латиницы и цифр, 4 - all
     * @return Возвращает рандомносгенерённую строку указанной длинны и типа
     */
    public static String randString(int min, int max, int type) {
        String returnString = "";
        int lengthName = randInt(min, max);
        switch (type) {
            case 0 -> returnString = getRandomString(lengthName, lat);
            case 1 -> returnString = getRandomString(lengthName, symb);
            case 2 -> returnString = getRandomString(lengthName, num);
            case 3 -> returnString = getRandomString(lengthName, lat + num);
            case 4 -> returnString = getRandomString(lengthName, all);
            default -> log.error("Введён неверный тип для генерации");
        }
        return returnString;
    }

    private static synchronized String getRandomString(int length, String sourceChars) {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            returnString.append(sourceChars.charAt(rnd.nextInt(sourceChars.length())));
        }
        return returnString.toString();
    }
}
