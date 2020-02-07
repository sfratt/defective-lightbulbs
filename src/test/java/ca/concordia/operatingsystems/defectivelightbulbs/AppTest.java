package ca.concordia.operatingsystems.defectivelightbulbs;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {

    @Test
    public final void whenAllLightsInListAreOnThenTrue() {
        // Arrange
        int[] inputArray = { 1, 1, 1, 1, 1, 1, 1, 1 };

        // Act
        boolean status = App.isLightOn(inputArray);

        // Assert
        assertEquals(true, status);
    }

    @Test
    public final void whenAnyLightInListIsOffThenFalse() {
        // Arrange
        int[] inputArray = { 1, 1, 1, 0, 1, 1, 1, 1 };

        // Act
        boolean status = App.isLightOn(inputArray);

        // Assert
        assertEquals(false, status);
    }
}
