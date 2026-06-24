package com.example.fitnessapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GymUserTest {

    @Test
    void testGymUserConstructorAndGetters() {

        int expectedId = 99;
        String expectedPesel = "98765432100";
        String expectedFirstName = "Adam";
        String expectedLastName = "Nowak";
        String expectedEmail = "adam@fitness.pl";
        String expectedPassword = "tajnehaslo";
        String expectedRole = "CLIENT";
        String expectedStatus = "ACTIVE";

        GymUser user = new GymUser(expectedId, expectedPesel, expectedFirstName, expectedLastName,
                expectedEmail, expectedPassword, expectedRole, expectedStatus);

        assertEquals(expectedId, user.getId());
        assertEquals(expectedFirstName, user.getFirstName());
        assertEquals(expectedLastName, user.getLastName());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(expectedPassword, user.getPasswordHash());
        assertEquals(expectedRole, user.getRole());
        assertEquals(expectedStatus, user.getStatus());
    }

    //sprawdzenie poprawności hasła
    @Test
    void testCheckPassword_BusinessLogic() {

        GymUser user = new GymUser();
        user.setPasswordHash("tajnehaslo123");

        assertTrue(user.checkPassword("tajnehaslo123"), "Powinno przepuścić poprawne hasło");
        assertFalse(user.checkPassword("zlehaslo"), "Powinno odrzucić błędne hasło");
        assertFalse(user.checkPassword(null), "Powinno odrzucić null");
    }
    //test formatu email
    @Test
    void testIsValidEmail_BusinessLogic() {
        GymUser user = new GymUser();

        assertTrue(user.isValidEmail("jan@kowalski.pl"), "Powinno zaakceptować poprawny email");

        assertFalse(user.isValidEmail("jankowalski.pl"), "Brak małpy (@) - powinno odrzucić");
        assertFalse(user.isValidEmail("jan@kowalski"), "Brak kropki - powinno odrzucić");
        assertFalse(user.isValidEmail(null), "Null - powinno odrzucić");
    }

    //test poprawności wpisanego peselu
    @Test
    void testIsValidPesel_BusinessLogic() {
        GymUser user = new GymUser();

        assertTrue(user.isValidPesel("12345678901"), "Powinno zaakceptować poprawny, 11-cyfrowy PESEL");

        assertFalse(user.isValidPesel("12345"), "Za krótki PESEL - powinno odrzucić");
        assertFalse(user.isValidPesel("123456789012"), "Za długi PESEL - powinno odrzucić");
        assertFalse(user.isValidPesel("123456789ab"), "Zawiera litery - powinno odrzucić");
        assertFalse(user.isValidPesel(null), "Null - powinno odrzucić");
    }

}