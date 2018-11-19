import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Таня on 13.11.2018.
 */
public class PdfTest {
   @Test
    public void Cyrillic() throws IOException, DocumentException {
        Document document = new Document();
        String filename = "C:\\Users\\Таня\\IdeaProjects\\diplom\\src\\test\\resources\\test.pdf";
        Font font = new Font(BaseFont.createFont("Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED));
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        document.add(new Paragraph("ТЕСТОВАЯ СТРОКА", font));
        document.close();
    }
}
