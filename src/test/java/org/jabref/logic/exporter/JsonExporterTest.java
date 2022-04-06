package org.jabref.logic.exporter;

import org.jabref.logic.layout.LayoutFormatterPreferences;
import org.jabref.logic.xmp.XmpPreferences;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibEntryTypesManager;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Answers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class JsonExporterTest {

    public BibDatabaseContext databaseContext;
    private Exporter exporter;

    @BeforeEach
    public void setUp() {
        List<TemplateExporter> customFormats = new ArrayList<>();
        LayoutFormatterPreferences layoutPreferences = mock(LayoutFormatterPreferences.class, Answers.RETURNS_DEEP_STUBS);
        SavePreferences savePreferences = mock(SavePreferences.class);
        XmpPreferences xmpPreferences = mock(XmpPreferences.class);
        BibEntryTypesManager entryTypesManager = mock(BibEntryTypesManager.class);

        ExporterFactory exporterFactory = ExporterFactory.create(customFormats, layoutPreferences, savePreferences, xmpPreferences, BibDatabaseMode.BIBTEX, entryTypesManager);

        exporter = exporterFactory.getExporterByName("json").get();
        databaseContext = new BibDatabaseContext();
    }

    @ParameterizedTest
    @EnumSource(
            value = StandardField.class,
            names = { "AUTHOR", "URL", "DOI" }
    )
    public final void exportsSingleEntryWithSingleStringField(StandardField field, @TempDir Path tempFile) throws Exception {
        BibEntry entry = new BibEntry(StandardEntryType.Article)
                .withField(field, "valor");

        Path file = tempFile.resolve("TDDTestFileName");
        Files.createFile(file);

        exporter.export(databaseContext, file, Collections.singletonList(entry));

        List<String> expected = List.of(
                "{",
                "  \"references\": [",
                "    {",
                "      \"type\": \"article\",",
                "      \"" + field.getName() + "\": \"valor\"",
                "    }",
                "  ]",
                "}"
                );

        assertEquals(expected, Files.readAllLines(file));
    }

    @ParameterizedTest
    @EnumSource(
            value = StandardField.class,
            names = { "YEAR", "NUMBER", "EDITION" }
    )
    public final void exportsSingleEntryWithSingleNumericField(StandardField field, @TempDir Path tempFile) throws Exception {
        BibEntry entry = new BibEntry(StandardEntryType.Book)
                .withField(field, "2000");

        Path file = tempFile.resolve("TDDTestFileName");
        Files.createFile(file);

        exporter.export(databaseContext, file, Collections.singletonList(entry));

        List<String> expected = List.of(
                "{",
                "  \"references\": [",
                "    {",
                "      \"type\": \"book\",",
                "      \"" + field.getName() + "\": 2000",
                "    }",
                "  ]",
                "}"
        );

        assertEquals(expected, Files.readAllLines(file));
    }
}
