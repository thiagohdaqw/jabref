package org.jabref.logic.exporter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jabref.logic.layout.LayoutFormatterPreferences;
import org.jabref.logic.xmp.XmpPreferences;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibEntryTypesManager;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Answers;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class JsonExporterTest {

    private BibDatabaseContext databaseContext;
    private Exporter exporter;
    private Path file;

    @BeforeEach
    public void setUp(@TempDir Path tempFile) throws Exception{
        List<TemplateExporter> customFormats = new ArrayList<>();
        LayoutFormatterPreferences layoutPreferences = mock(LayoutFormatterPreferences.class, Answers.RETURNS_DEEP_STUBS);
        SavePreferences savePreferences = mock(SavePreferences.class);
        XmpPreferences xmpPreferences = mock(XmpPreferences.class);
        BibEntryTypesManager entryTypesManager = mock(BibEntryTypesManager.class);

        ExporterFactory exporterFactory = ExporterFactory.create(customFormats, layoutPreferences, savePreferences, xmpPreferences, BibDatabaseMode.BIBTEX, entryTypesManager);

        exporter = exporterFactory.getExporterByName("json").get();
        databaseContext = new BibDatabaseContext();

        file = tempFile.resolve("TDDTestFileName");
        Files.createFile(file);
    }

    @ParameterizedTest
    @EnumSource(
            value = StandardField.class,
            names = { "AUTHOR", "URL", "DOI" }
    )
    public void exportsSingleEntryWithSingleStringField(StandardField field) throws Exception {
        BibEntry entry = new BibEntry(StandardEntryType.Article)
                .withField(field, "valor");

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
    public void exportsSingleEntryWithSingleNumericField(StandardField field) throws Exception {
        BibEntry entry = new BibEntry(StandardEntryType.Book)
                .withField(field, "2000");

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

    @Test
    public void exportsIgnoreNullField() throws Exception {
        BibEntry entry = Mockito.spy(new BibEntry(StandardEntryType.Collection))
                .withField(StandardField.AUTHOR, "Dijkstra")
                .withField(StandardField.DATE, "2022-02-02");

        Field authorField = entry.getFields().stream().filter(e -> e.getName().equals(StandardField.AUTHOR.getName())).findFirst().get();
        Mockito.when(entry.getField(authorField)).thenReturn(Optional.empty());

        exporter.export(databaseContext, file, Collections.singletonList(entry));

        List<String> expected = List.of(
                "{",
                "  \"references\": [",
                "    {",
                "      \"type\": \"collection\",",
                "      \"date\": \"2022-02-02\"",
                "    }",
                "  ]",
                "}"
        );

        assertEquals(expected, Files.readAllLines(file));
    }

    @Test
    public void exportsMultipleEntriesWithMultipleFields() throws Exception {
        List<BibEntry> entries = List.of(
            new BibEntry(StandardEntryType.Dataset)
                    .withCitationKey("dij2")
                    .withField(StandardField.TITLE, "Quanto mais barato melhor")
                    .withField(StandardField.CREATIONDATE, "1975-05-02"),
            new BibEntry(StandardEntryType.CodeFragment)
                    .withField(StandardField.BOOKAUTHOR, "Alguem")
                    .withField(StandardField.NUMBER, "2--4")
        );

        exporter.export(databaseContext, file, entries);

        JsonObject jsonExported = new Gson().fromJson(Files.newBufferedReader(file), JsonObject.class);
        assertEquals(2, jsonExported.get("references").getAsJsonArray().size());

        JsonArray references = jsonExported.get("references").getAsJsonArray();

        JsonObject entry1 = references.get(0).getAsJsonObject();
        assertEquals(4, entry1.keySet().size());
        assertEquals("dataset", entry1.get("type").getAsString());
        assertEquals("dij2", entry1.get("citationkey").getAsString());
        assertEquals("Quanto mais barato melhor", entry1.get("title").getAsString());
        assertEquals("1975-05-02", entry1.get("creationdate").getAsString());

        JsonObject entry2 = references.get(1).getAsJsonObject();
        assertEquals(3, entry2.keySet().size());
        assertEquals("codefragment", entry2.get("type").getAsString());
        assertEquals("Alguem", entry2.get("bookauthor").getAsString());
        assertEquals("2--4", entry2.get("number").getAsString());
    }
}
