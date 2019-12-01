package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        assert args.length % 2 == 0;
        String mode = "enc";
        int key = 0;
        String data = null;
        String inFilename = null;
        String outFilename = null;
        String alg = "shift";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String value = args[++i];
            switch (arg) {
                case "-mode":
                    mode = value;
                    break;
                case "-key":
                    key = Integer.parseInt(value);
                    break;
                case "-data":
                    data = value;
                    break;
                case "-in":
                    inFilename = value;
                    break;
                case "-out":
                    outFilename = value;
                    break;
                case "-alg":
                    alg = value;
                    break;
                default:
                    System.out.println("Unknown a argument: " + arg);
                    break;
            }
        }
        if (data == null && inFilename == null) {
            data = "";
        } else if (data == null) {
            data = readData(inFilename);
        }
        String result;
        AlgorithmFactory algorithmFactory = new AlgorithmFactory();
        Algorithm algorithm = algorithmFactory.getAlgorithm(alg);
        if ("dec".equals(mode)) {
            result = algorithm.decrypt(data, key);
        } else {
            result = algorithm.encrypt(data, key);
        }
        if (outFilename == null) {
            System.out.println(result);
        } else {
            writeResult(outFilename, result);
        }
    }

    private static String readData(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            String data = "";
            while (scanner.hasNextLine()) {
                //noinspection StringConcatenationInLoop
                data += scanner.nextLine();
            }
            return data;
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    private static void writeResult(String filename, String result) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(result);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class AlgorithmFactory {
        Algorithm getAlgorithm(String name) {
            if ("unicode".equals(name)) {
                return new UnicodeAlgorithm();
            } else if ("shift".equals(name)) {
                return new ShiftAlgorithm();
            } else {
                return null;
            }
        }
    }

    abstract static class Algorithm {
        public abstract String encrypt(String text, int key);

        public abstract String decrypt(String cyphertext, int key);

    }

    static class UnicodeAlgorithm extends Algorithm {
        @SuppressWarnings("StringConcatenationInLoop")
        public String encrypt(String text, int key) {
            String cyphertext = "";
            for (char ch : text.toCharArray()) {
                cyphertext += (char) (ch + key);
            }
            return cyphertext;
        }

        @SuppressWarnings("StringConcatenationInLoop")
        public String decrypt(String cyphertext, int key) {
            String message = "";
            for (char ch : cyphertext.toCharArray()) {
                message += (char) (ch - key);
            }
            return message;
        }
    }

    static class ShiftAlgorithm extends Algorithm {
        @SuppressWarnings("StringConcatenationInLoop")
        public String encrypt(String text, int key) {
            String cyphertext = "";
            for (char ch : text.toCharArray()) {
                if (Character.isLetter(ch)) {
                    char newCh = (char) (ch + key);
                    if (Character.isUpperCase(ch)) {
                        if (newCh > 'Z') {
                            cyphertext += (char) (newCh % 'Z' + 'A' - 1);
                        } else {
                            cyphertext += newCh;
                        }
                    } else {
                        if (newCh > 'z') {
                            cyphertext += (char) (newCh % 'z' + 'a' - 1);
                        } else {
                            cyphertext += newCh;
                        }
                    }
                } else {
                    cyphertext += ch;
                }
            }
            return cyphertext;
        }

        @SuppressWarnings("StringConcatenationInLoop")
        public String decrypt(String cyphertext, int key) {
            String message = "";
            for (char ch : cyphertext.toCharArray()) {
                if (Character.isLetter(ch)) {
                    char newCh = (char) (ch - key);
                    if (Character.isUpperCase(ch)) {
                        if (newCh < 'A') {
                            message += (char) ('Z' + 1 + newCh - 'A');
                        } else {
                            message += newCh;
                        }
                    } else {
                        if (newCh < 'a') {
                            message += (char) ('z' + 1 + newCh - 'a');
                        } else {
                            message += newCh;
                        }
                    }
                } else {
                    message += ch;
                }
            }
            return message;
        }
    }
}
