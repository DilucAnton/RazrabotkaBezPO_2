import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

public class PasswordCracker {
    // Заданные хэш-значения для поиска
    private static final String[] TARGET_HASHES_SHA256 = {
        "1115dd800feaacefdf481f1f9070374a2a81e27880f187396db67958b207cbad",
        "3a7bd3e2360a3d29eea436fcfb7e44c735d117c42d1c1835420b6b9942dd4f1b",
        "74e1bb62f8dabb8125a58852b63bdf6eaef667cb56ac7f7cdba6d7305c50a22f"
    };
    
    private static final String[] TARGET_HASHES_MD5 = {
        "7a68f09bd992671bb3b19a5e70b7827e",
        "7cf54dfe813ef3b32b7b762b61b3400c"
    };
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PasswordCracker cracker = new PasswordCracker();

        System.out.println("Выберите режим:");
        System.out.println("1. Однопоточный режим");
        System.out.println("2. Многопоточный режим");
        int modeChoice = scanner.nextInt();

        if (modeChoice == 1) {
            // Однопоточный режим
            System.out.println("Запуск однопоточного режима...");
            long singleThreadStart = System.currentTimeMillis();
            cracker.bruteForceSingleThread("SHA-256");
            cracker.bruteForceSingleThread("MD5");
            long singleThreadEnd = System.currentTimeMillis();
            System.out.println("Время выполнения (однопоточный): " + (singleThreadEnd - singleThreadStart) + " мс\n");

        } else if (modeChoice == 2) {
            // Многопоточный режим
            System.out.print("Введите количество потоков: ");
            int threadCount = scanner.nextInt();

            System.out.println("Запуск многопоточного режима...");
            long multiThreadStart = System.currentTimeMillis();
            cracker.bruteForceMultiThread("SHA-256", threadCount);
            cracker.bruteForceMultiThread("MD5", threadCount);
            long multiThreadEnd = System.currentTimeMillis();
            System.out.println("Время выполнения (многопоточный): " + (multiThreadEnd - multiThreadStart) + " мс\n");

        } else {
            System.out.println("Неверный выбор режима. Завершение программы.");
        }

        scanner.close();
    }

    // Однопоточный перебор
    public void bruteForceSingleThread(String algorithm) {
        try {
            for (char a = 'a'; a <= 'z'; a++) {
                for (char b = 'a'; b <= 'z'; b++) {
                    for (char c = 'a'; c <= 'z'; c++) {
                        for (char d = 'a'; d <= 'z'; d++) {
                            for (char e = 'a'; e <= 'z'; e++) {
                                String password = "" + a + b + c + d + e;
                                String hash = getHash(password, algorithm);
                                if (isTargetHash(hash, algorithm)) {
                                    System.out.println("Пароль найден: " + password + " для хэша " + hash);
                                }
                            }
                        }
                    }
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Алгоритм не найден: " + e.getMessage());
        }
    }

    // Многопоточный перебор
    public void bruteForceMultiThread(String algorithm, int threadCount) {
        int partitionSize = 26 / threadCount;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            char startChar = (char) ('a' + i * partitionSize);
            char endChar = (char) ((i == threadCount - 1) ? 'z' : ('a' + (i + 1) * partitionSize - 1));
            threads[i] = new Thread(new PasswordCrackerThread(startChar, endChar, algorithm));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Проверка, является ли хэш целевым для данного алгоритма
    private boolean isTargetHash(String hash, String algorithm) {
        return (algorithm.equals("SHA-256") && Arrays.asList(TARGET_HASHES_SHA256).contains(hash)) ||
               (algorithm.equals("MD5") && Arrays.asList(TARGET_HASHES_MD5).contains(hash));
    }

    // Хэширование строки по заданному алгоритму
    private String getHash(String password, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Класс для многопоточного поиска
    class PasswordCrackerThread implements Runnable {
        private final char startChar;
        private final char endChar;
        private final String algorithm;

        public PasswordCrackerThread(char startChar, char endChar, String algorithm) {
            this.startChar = startChar;
            this.endChar = endChar;
            this.algorithm = algorithm;
        }

        @Override
        public void run() {
            try {
                for (char a = startChar; a <= endChar; a++) {
                    for (char b = 'a'; b <= 'z'; b++) {
                        for (char c = 'a'; c <= 'z'; c++) {
                            for (char d = 'a'; d <= 'z'; d++) {
                                for (char e = 'a'; e <= 'z'; e++) {
                                    String password = "" + a + b + c + d + e;
                                    String hash = getHash(password, algorithm);
                                    if (isTargetHash(hash, algorithm)) {
                                        System.out.println("Пароль найден: " + password + " для хэша " + hash);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Алгоритм не найден: " + e.getMessage());
            }
        }
    }
}
