package org.jabref.gui.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControlHelperTest {

    private final String TEXT = "abcdef";
    private final int MAX_CHARACTERS = 5;
    private final int DEFAULT_MAX_CHARACTERS = -1;
    private final String ELLIPSIS_STRING = "***";
    private final ControlHelper.EllipsisPosition ELLIPSIS_POSITION = ControlHelper.EllipsisPosition.ENDING;

    // Caso de teste 1 e 2
    @ParameterizedTest
    @NullAndEmptySource
    void truncateWithTextNullAndEmptyReturnsSource(String text){
        String truncatedText = ControlHelper.truncateString(text, MAX_CHARACTERS, ELLIPSIS_STRING, ELLIPSIS_POSITION);
        assertEquals(text, truncatedText);
    }

    // Caso de teste 3
    @Test
    void truncateWithDefaultMaxCharactersReturnsText(){
        String truncatedText = ControlHelper.truncateString(TEXT, DEFAULT_MAX_CHARACTERS, ELLIPSIS_STRING, ELLIPSIS_POSITION);
        assertEquals(TEXT, truncatedText);
    }

    // Caso de teste 4
    @Test
    void truncateWithEllipsisPositionBeginningReturnsTruncatedText(){
        String truncatedText = ControlHelper.truncateString(TEXT, MAX_CHARACTERS, ELLIPSIS_STRING, ControlHelper.EllipsisPosition.BEGINNING);
        assertEquals("***ef", truncatedText);
    }

    // Caso de teste 5
    @Test
    void truncateWithEllipsisPositionCenterReturnsTruncatedText(){
        String truncatedText = ControlHelper.truncateString(TEXT, MAX_CHARACTERS, ELLIPSIS_STRING, ControlHelper.EllipsisPosition.CENTER);
        assertEquals("a***f", truncatedText);
    }

    // Caso de teste 6
    @Test
    void truncateWithDefaultMaxCharactersAndNullEllipsisAndPositionEndingReturnsTruncatedText(){
        String text = "a".repeat(75) + "b".repeat(25);
        String truncatedText = ControlHelper.truncateString(text, DEFAULT_MAX_CHARACTERS, null, ControlHelper.EllipsisPosition.ENDING);
        assertEquals("a".repeat(75), truncatedText);
    }

    // Caso de teste 7
    @ParameterizedTest
    @NullSource
    void truncateWithNullEllipsisPositionThrowsNullPointerException(ControlHelper.EllipsisPosition ellipsisPosition){
        assertThrows(
            NullPointerException.class,
            () -> ControlHelper.truncateString(TEXT, MAX_CHARACTERS, ELLIPSIS_STRING, ellipsisPosition)
        );
    }
}
