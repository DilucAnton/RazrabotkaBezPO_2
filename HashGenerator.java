import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class HashGenerator {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {  // Используем try-with-resources для автоматического закрытия Scanner
            // Ввод пароля
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();

            // Ввод алгоритма хэширования
            System.out.print("Введите алгоритм (например, SHA-256 или MD5): ");
            String algorithm = scanner.nextLine();

            // Вычисление и вывод хэша
            String hash = generateHash(password, algorithm);
            if (hash != null) {
                System.out.println("Хэш для пароля \"" + password + "\" с алгоритмом " + algorithm + ": " + hash);
            }
        } // scanner будет закрыт автоматически при выходе из блока try
    }

    // Метод для вычисления хэша
    public static String generateHash(String password, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Ошибка: алгоритм \"" + algorithm + "\" не найден.");
            return null;
        }
    }
}
