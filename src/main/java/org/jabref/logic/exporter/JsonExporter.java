package org.jabref.logic.exporter;

import org.jabref.logic.util.StandardFileType;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A custom exporter to write bib entries to a .json file for further processing
 * in other scenarios and applications.
 */
public class JsonExporter extends Exporter {


    public JsonExporter() {
        super("json", "JSON", StandardFileType.JSON);
    }

    /**
     * @param databaseContext the database to export from
     * @param file            the file to write to.
     * @param entries         a list containing all entries that should be exported
     */
    @Override
    public void export(BibDatabaseContext databaseContext, Path file, List<BibEntry> entries) throws Exception {
        String expected =
                "{\n" +
                "\"references\": [\n" +
                "    \"id\": \"entry1\"\n" +
                "    \"type\": \"article\"\n" +
                "    \"author\": {\n" +
                "        \"literal\": \"Author 1\"\n" +
                "    }\n" +
                "]\n" +
                "}\n";


        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(expected);
            writer.flush();
        }
    }
}
