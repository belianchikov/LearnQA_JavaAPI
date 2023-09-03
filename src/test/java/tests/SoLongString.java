package tests;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SoLongString {
    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter string");

        String str = myObj.nextLine();
        assertTrue(str.length() > 15, "String is not long enough");
    }
}