package com.report.generator.convertor;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class CSVConverter {

    public void convertToCSV(List<Map<String, Object>> rows, String filePath) throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath));

        // Write the header row
        String[] header = rows.get(0).keySet().toArray(new String[0]);
        csvWriter.writeNext(header);

        // Write the data rows
        for (Map<String, Object> row : rows) {
            String[] data = new String[row.size()];
            int i = 0;
            for (Object value : row.values()) {
                if(value!=null)
                    data[i++] = value.toString();
                else
                    data[i++] = null;
            }
            csvWriter.writeNext(data);
        }

        csvWriter.close();
    }
}
