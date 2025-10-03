package gr.ihu.eparousiologio.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.CourseNote;
import gr.ihu.eparousiologio.model.NewAttendanceEntry;
import gr.ihu.eparousiologio.model.NewAttendanceSnapshot;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;

public final class ExcelExporterNew {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private ExcelExporterNew() {}

    public static void exportCourseNew(
            Course course,
            Map<String, Section> sectionsByLabId,
            List<NewAttendanceSnapshot> snapshots,
            List<CourseNote> courseNotes,
            ExcelExporter.Output output,
            String suggestedFileName
    ) throws Exception {
        OutputStream os = null;

        Map<String, List<NewAttendanceSnapshot>> snapsByLab =
                snapshots == null ? Collections.emptyMap()
                        : snapshots.stream().collect(Collectors.groupingBy(s -> nz(s.getLabId())));

        Workbook wb = null;
        try {
            wb = new XSSFWorkbook();

            CellStyle bold = wb.createCellStyle();
            Font boldFont = wb.createFont();
            boldFont.setBold(true);
            bold.setFont(boldFont);

            CellStyle header = wb.createCellStyle();
            header.cloneStyleFrom(bold);
            header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            header.setBorderBottom(BorderStyle.THIN);

            CellStyle normal = wb.createCellStyle();
            normal.setWrapText(false);

            for (Map.Entry<String, List<NewAttendanceSnapshot>> e : snapsByLab.entrySet()) {
                String labId = e.getKey();
                List<NewAttendanceSnapshot> labSnaps = e.getValue();

                String labName = firstNonEmpty(
                        labSnaps.stream().map(NewAttendanceSnapshot::getLabName)
                                .filter(Objects::nonNull).findFirst().orElse(null),
                        sectionsByLabId != null && sectionsByLabId.get(labId) != null
                                ? sectionsByLabId.get(labId).getName()
                                : null,
                        labId
                );

                String sheetName = WorkbookUtil.createSafeSheetName(labName);
                Sheet sh = wb.createSheet(sheetName);

                Map<Integer, Integer> colMaxChars = new LinkedHashMap<>();
                AtomicInteger maxColUsed = new AtomicInteger(0);
                BiConsumer<Integer, String> fit = (column, text) -> {
                    int len = (text == null) ? 0 : text.length();
                    colMaxChars.merge(column, len, Math::max);
                    maxColUsed.updateAndGet(prev -> Math.max(prev, column));
                };

                rowCell(sh, 0, 0, course != null ? nz(course.getTitle()) : "", bold);
                fit.accept(0, course != null ? nz(course.getTitle()) : "");

                rowCell(sh, 1, 0, "Εργαστήριο", bold);
                fit.accept(0, "Εργαστήριο");
                rowCell(sh, 1, 1, labName, bold);
                fit.accept(1, labName);

                rowCell(sh, 2, 0, "Συνολικές συνεδρίες Lab", bold);
                fit.accept(0, "Συνολικές συνεδρίες Lab");
                rowCell(sh, 2, 1, String.valueOf(labSnaps.size()), bold);
                fit.accept(1, String.valueOf(labSnaps.size()));

                int startRow = 4;
                Row hdr = getOrCreateRow(sh, startRow);
                createCell(hdr, 0, "ΑΕΜ", header); fit.accept(0, "ΑΕΜ");
                createCell(hdr, 1, "ΟΝΟΜΑΤΕΠΩΝΥΜΟ", header); fit.accept(1, "ΟΝΟΜΑΤΕΠΩΝΥΜΟ");
                createCell(hdr, 2, "Παρουσίες/Σύνολο", header); fit.accept(2, "Παρουσίες/Σύνολο");

                Map<String, String> aemToName = new LinkedHashMap<>();
                Section sec = sectionsByLabId != null ? sectionsByLabId.get(labId) : null;
                if (sec != null && sec.getStudents() != null) {
                    for (Student s : sec.getStudents()) {
                        if (s == null) continue;
                        String aem = nz(s.getStudentAEM());
                        String nm = fullNameOrDot(s);
                        if (!aem.isEmpty()) aemToName.put(aem, nm);
                    }
                }

                Map<String, Integer> presentCountByAem = new LinkedHashMap<>();
                for (NewAttendanceSnapshot snap : labSnaps) {
                    List<NewAttendanceEntry> entries = snap.getAttendanceEntries();
                    if (entries == null) continue;
                    for (NewAttendanceEntry ne : entries) {
                        if (ne == null) continue;
                        String aem = nz(ne.getStudentAEM());
                        if (aem.isEmpty()) continue;

                        if (!aemToName.containsKey(aem)) {
                            String nm = nz(ne.getFullName());
                            aemToName.put(aem, nm.isEmpty() ? "." : nm);
                        }

                        if (ne.isWasPresent()) {
                            presentCountByAem.merge(aem, 1, Integer::sum);
                        }
                    }
                }

                List<String> allAems = new ArrayList<>(aemToName.keySet());
                Collections.sort(allAems);

                int rowIdx = startRow + 1;
                for (String aem : allAems) {
                    Row r = getOrCreateRow(sh, rowIdx++);
                    String name = nz(aemToName.get(aem));
                    int sessionsTotal = labSnaps.size();
                    int presentCount = presentCountByAem.getOrDefault(aem, 0);
                    String presStr = presentCount + " / " + sessionsTotal;

                    createCell(r, 0, aem, normal); fit.accept(0, aem);
                    createCell(r, 1, name.isEmpty() ? "." : name, normal); fit.accept(1, name);
                    createCell(r, 2, presStr, normal); fit.accept(2, presStr);
                }

                for (int col = 0; col <= Math.max(maxColUsed.get(), 3); col++) {
                    int maxChars = colMaxChars.getOrDefault(col, 0);
                    int width = (maxChars + 2) * 256;
                    if (width > 255 * 256) width = 255 * 256;
                    if (width < 8 * 256) width = 8 * 256;
                    sh.setColumnWidth(col, width);
                }
            }

            String notesSheetName = WorkbookUtil.createSafeSheetName("ΣΗΜΕΙΩΣΕΙΣ");
            Sheet notesSheet = wb.createSheet(notesSheetName);
            Map<Integer, Integer> notesColMax = new LinkedHashMap<>();
            AtomicInteger notesMaxCol = new AtomicInteger(0);
            BiConsumer<Integer, String> nfit = (column, text) -> {
                int len = (text == null) ? 0 : text.length();
                notesColMax.merge(column, len, Math::max);
                notesMaxCol.updateAndGet(prev -> Math.max(prev, column));
            };

            Row titleRow = getOrCreateRow(notesSheet, 0);
            createCell(titleRow, 0, "ΣΗΜΕΙΩΣΕΙΣ", bold);
            nfit.accept(0, "ΣΗΜΕΙΩΣΕΙΣ");

            List<CourseNote> notes = courseNotes == null ? Collections.emptyList() : new ArrayList<>(courseNotes);
            notes.sort((n1, n2) -> {
                Date d1 = n1 != null ? n1.getCreatedAt() : null;
                Date d2 = n2 != null ? n2.getCreatedAt() : null;
                if (d1 == null && d2 == null) return 0;
                if (d1 == null) return -1;
                if (d2 == null) return 1;
                return d1.compareTo(d2);
            });

            int nrow = 1;
            for (CourseNote n : notes) {
                Row nr = getOrCreateRow(notesSheet, nrow++);
                String ts = n != null && n.getCreatedAt() != null ? DATE_FMT.format(n.getCreatedAt()) : "";
                String msg = (n != null && n.getMessage() != null) ? n.getMessage() : "";
                createCell(nr, 0, ts, normal);
                createCell(nr, 1, msg, normal);
                nfit.accept(0, ts);
                nfit.accept(1, msg);
            }

            for (int col = 0; col <= Math.max(notesMaxCol.get(), 1); col++) {
                int maxChars = notesColMax.getOrDefault(col, 0);
                int width = (maxChars + 2) * 256;
                if (width > 255 * 256) width = 255 * 256;
                if (width < 8 * 256) width = 8 * 256;
                notesSheet.setColumnWidth(col, width);
            }

            os = output.open(suggestedFileName);
            wb.write(os);

        } finally {
            if (wb != null) try { wb.close(); } catch (Exception ignore) {}
            if (os != null) try { output.close(os); } catch (Exception ignore) {}
        }
    }

    private static String fullNameOrDot(Student s) {
        String name = s.getFullName() == null ? "" : s.getFullName().trim();
        return name.isEmpty() ? "." : name;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    private static String firstNonEmpty(String... vals) {
        if (vals == null) return "";
        for (String v : vals) if (v != null && !v.trim().isEmpty()) return v.trim();
        return "";
    }

    private static Row getOrCreateRow(Sheet sh, int rowIndex) {
        Row r = sh.getRow(rowIndex);
        return r != null ? r : sh.createRow(rowIndex);
    }

    private static void rowCell(Sheet sh, int row, int col, String val, CellStyle st) {
        Row r = getOrCreateRow(sh, row);
        createCell(r, col, val, st);
    }

    private static void createCell(Row r, int col, String val, CellStyle st) {
        Cell c = r.getCell(col);
        if (c == null) c = r.createCell(col, CellType.STRING);
        c.setCellValue(val == null ? "" : val);
        if (st != null) c.setCellStyle(st);
    }
}
