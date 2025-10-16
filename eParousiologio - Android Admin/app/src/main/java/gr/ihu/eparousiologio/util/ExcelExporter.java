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
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import gr.ihu.eparousiologio.model.AttendanceEntry;
import gr.ihu.eparousiologio.model.AttendanceSnapshot;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.CourseNote;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;

public final class ExcelExporter {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private ExcelExporter() {
    }

    public static void exportCourseNew(
            Course course,
            Map<String, Section> sectionsByLabId,
            List<AttendanceSnapshot> snapshots,
            List<CourseNote> courseNotes,
            Output output,
            String suggestedFileName
    ) throws Exception {
        OutputStream os = null;

        Map<String, List<AttendanceSnapshot>> snapsByLab =
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

            for (Map.Entry<String, List<AttendanceSnapshot>> e : snapsByLab.entrySet()) {
                String labId = e.getKey();
                List<AttendanceSnapshot> labSnaps = e.getValue();

                String labName = firstNonEmpty(
                        labSnaps.stream().map(AttendanceSnapshot::getLabName)
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
                createCell(hdr, 0, "ΑΕΜ", header);
                fit.accept(0, "ΑΕΜ");
                createCell(hdr, 1, "ΟΝΟΜΑΤΕΠΩΝΥΜΟ", header);
                fit.accept(1, "ΟΝΟΜΑΤΕΠΩΝΥΜΟ");
                createCell(hdr, 2, "Παρουσίες/Σύνολο", header);
                fit.accept(2, "Παρουσίες/Σύνολο");

                List<AttendanceSnapshot> sortedSnaps = new ArrayList<>(labSnaps);
                sortedSnaps.sort(Comparator.comparing(ExcelExporter::snapshotSortKey));
                int col = 3;
                for (AttendanceSnapshot s : sortedSnaps) {
                    String colTitle = snapshotHeaderTitle(s);
                    createCell(hdr, col, colTitle, header);
                    fit.accept(col, colTitle);
                    col++;
                }

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

                List<Set<String>> presentBySnap = new ArrayList<>();
                for (AttendanceSnapshot snap : sortedSnaps) {
                    Set<String> presentAems = new LinkedHashSet<>();
                    List<AttendanceEntry> entries = snap.getAttendanceEntries();
                    if (entries != null) {
                        for (AttendanceEntry ne : entries) {
                            if (ne == null) continue;
                            String aem = nz(ne.getStudentAEM());
                            if (aem.isEmpty()) continue;
                            if (!aemToName.containsKey(aem)) {
                                String nm = nz(ne.getFullName());
                                aemToName.put(aem, nm.isEmpty() ? "." : nm);
                            }
                            if (ne.isWasPresent()) {
                                presentAems.add(aem);
                            }
                        }
                    }
                    presentBySnap.add(presentAems);
                }

                List<String> allAems = new ArrayList<>(aemToName.keySet());
                Collections.sort(allAems);

                int rowIdx = startRow + 1;
                for (String aem : allAems) {
                    Row r = getOrCreateRow(sh, rowIdx++);
                    String name = nz(aemToName.get(aem));
                    int sessionsTotal = sortedSnaps.size();
                    int presentCount = 0;
                    for (Set<String> set : presentBySnap) {
                        if (set.contains(aem)) presentCount++;
                    }

                    if (courseNotes != null && !courseNotes.isEmpty()) {
                        long extraPresences = courseNotes.stream()
                                .filter(n -> n != null)
                                .filter(n -> !Boolean.TRUE.equals(n.isLog()))
                                .filter(n -> nz(n.getLabId()).equalsIgnoreCase(nz(labId)))
                                .filter(n -> nz(n.getAem()).equalsIgnoreCase(nz(aem)))
                                .map(n -> new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .format(n.getCreatedAt()))
                                // κρατάμε μόνο 1 αναπλήρωση ανά μέρα
                                .distinct()
                                .count();

                        presentCount += (int) extraPresences;
                    }

                    String presStr = presentCount + " / " + sessionsTotal;

                    createCell(r, 0, aem, normal);
                    fit.accept(0, aem);
                    createCell(r, 1, name.isEmpty() ? "." : name, normal);
                    fit.accept(1, name);
                    createCell(r, 2, presStr, normal);
                    fit.accept(2, presStr);

                    int c = 3;
                    for (Set<String> set : presentBySnap) {
                        String val = set.contains(aem) ? "ΟΚ" : "ΕΛΕΙΠΕ";
                        createCell(r, c, val, normal);
                        fit.accept(c, val);
                        c++;
                    }
                }

                for (int c = 0; c <= Math.max(maxColUsed.get(), 3 + sortedSnaps.size()); c++) {
                    int maxChars = colMaxChars.getOrDefault(c, 0);
                    int width = (maxChars + 2) * 256;
                    if (width > 255 * 256) width = 255 * 256;
                    if (width < 8 * 256) width = 8 * 256;
                    sh.setColumnWidth(c, width);
                }

                // --- ΝΕΟΣ ΠΙΝΑΚΑΣ ΑΝΑΠΛΗΡΩΣΕΙΣ ---
                if (courseNotes != null && !courseNotes.isEmpty()) {
                    List<CourseNote> replacements = courseNotes.stream()
                            .filter(n -> n != null)
                            .filter(n -> !Boolean.TRUE.equals(n.isLog()))
                            .filter(n -> nz(n.getLabId()).equalsIgnoreCase(nz(labId)))
                            .filter(n -> !nz(n.getAem()).isEmpty())
                            .sorted(Comparator.comparing(CourseNote::getCreatedAt, Comparator.nullsLast(Date::compareTo)))
                            .collect(Collectors.toList());

                    System.out.println("LabId: " + labId + " -> replacements found: " + replacements.size());

                    if (!replacements.isEmpty()) {

                        // Ομαδοποιούμε ανά ημέρα και φοιτητή -> κρατάμε την πιο πρόσφατη κάθε ημέρας
                        Map<String, Map<String, CourseNote>> byDateAndAem = new LinkedHashMap<>();

                        for (CourseNote note : replacements) {
                            if (note.getCreatedAt() == null) continue;
                            String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(note.getCreatedAt()); // ΜΟΝΟ ημερομηνία
                            String aemKey = note.getAem();

                            byDateAndAem.putIfAbsent(dateKey, new LinkedHashMap<>());
                            Map<String, CourseNote> byAem = byDateAndAem.get(dateKey);

                            // Αν υπάρχει ήδη, κράτα την πιο πρόσφατη createdAt
                            if (!byAem.containsKey(aemKey)
                                    || (note.getCreatedAt().after(byAem.get(aemKey).getCreatedAt()))) {
                                byAem.put(aemKey, note);
                            }
                        }

                        // Ταξινομημένες ημερομηνίες
                        List<String> dateKeys = new ArrayList<>(byDateAndAem.keySet());
                        Collections.sort(dateKeys);

                        // Συλλογή φοιτητών που έχουν έστω μία αναπλήρωση
                        Set<String> aemsWithNotes = byDateAndAem.values().stream()
                                .flatMap(m -> m.keySet().stream())
                                .collect(Collectors.toCollection(LinkedHashSet::new));

                        int replStartRow = sh.getLastRowNum() + 2;
                        Row replHeader = getOrCreateRow(sh, replStartRow);
                        createCell(replHeader, 0, "ΑΝΑΠΛΗΡΩΣΕΙΣ", header);

                        int colIdx = 1;
                        for (String dateStr : dateKeys) {
                            createCell(replHeader, colIdx, dateStr, header);
                            colIdx++;
                        }

                        int replRow = replStartRow + 1;
                        for (String aem : aemsWithNotes) {
                            Row rr = getOrCreateRow(sh, replRow++);
                            createCell(rr, 0, aem, normal);

                            int c = 1;
                            for (String dateStr : dateKeys) {
                                CourseNote note = byDateAndAem.getOrDefault(dateStr, Collections.emptyMap()).get(aem);
                                String msg = (note != null) ? note.getMessage() : "";
                                createCell(rr, c, msg, normal);
                                c++;
                            }
                        }

                    }

                }
            }

            // --- SHEET ΣΗΜΕΙΩΣΕΙΣ & LOGS ---
            List<CourseNote> allNotes = courseNotes == null
                    ? Collections.emptyList()
                    : new ArrayList<>(courseNotes);

            List<CourseNote> normalNotes = allNotes.stream()
                    .filter(n -> n != null
                            && !Boolean.TRUE.equals(n.isLog())
                            && (n.getAem() == null || n.getLabId() == null))
                    .collect(Collectors.toList());

            List<CourseNote> logNotes = allNotes.stream()
                    .filter(n -> n != null
                            && Boolean.TRUE.equals(n.isLog())
                            && (n.getAem() == null || n.getLabId() == null))
                    .collect(Collectors.toList());

            Comparator<CourseNote> byDate = (n1, n2) -> {
                Date d1 = n1 != null ? n1.getCreatedAt() : null;
                Date d2 = n2 != null ? n2.getCreatedAt() : null;
                if (d1 == null && d2 == null) return 0;
                if (d1 == null) return -1;
                if (d2 == null) return 1;
                return d1.compareTo(d2);
            };
            normalNotes.sort(byDate);
            logNotes.sort(byDate);

            if (!normalNotes.isEmpty()) {
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
                createCell(titleRow, 0, "ΗΜΕΡΟΜΗΝΙΑ", header);
                createCell(titleRow, 1, "ΣΗΜΕΙΩΣΗ", header);
                nfit.accept(0, "ΗΜΕΡΟΜΗΝΙΑ");
                nfit.accept(1, "ΣΗΜΕΙΩΣΗ");

                int nrow = 1;
                for (CourseNote n : normalNotes) {
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
            }

            if (!logNotes.isEmpty()) {
                String logsSheetName = WorkbookUtil.createSafeSheetName("LOGS");
                Sheet logsSheet = wb.createSheet(logsSheetName);
                Map<Integer, Integer> logsColMax = new LinkedHashMap<>();
                AtomicInteger logsMaxCol = new AtomicInteger(0);
                BiConsumer<Integer, String> lfit = (column, text) -> {
                    int len = (text == null) ? 0 : text.length();
                    logsColMax.merge(column, len, Math::max);
                    logsMaxCol.updateAndGet(prev -> Math.max(prev, column));
                };

                Row titleRow = getOrCreateRow(logsSheet, 0);
                createCell(titleRow, 0, "ΗΜΕΡΟΜΗΝΙΑ", header);
                createCell(titleRow, 1, "LOG", header);
                lfit.accept(0, "ΗΜΕΡΟΜΗΝΙΑ");
                lfit.accept(1, "LOG");

                int lrow = 1;
                for (CourseNote n : logNotes) {
                    Row nr = getOrCreateRow(logsSheet, lrow++);
                    String ts = n != null && n.getCreatedAt() != null ? DATE_FMT.format(n.getCreatedAt()) : "";
                    String msg = (n != null && n.getMessage() != null) ? n.getMessage() : "";
                    createCell(nr, 0, ts, normal);
                    createCell(nr, 1, msg, normal);
                    lfit.accept(0, ts);
                    lfit.accept(1, msg);
                }

                for (int col = 0; col <= Math.max(logsMaxCol.get(), 1); col++) {
                    int maxChars = logsColMax.getOrDefault(col, 0);
                    int width = (maxChars + 2) * 256;
                    if (width > 255 * 256) width = 255 * 256;
                    if (width < 8 * 256) width = 8 * 256;
                    logsSheet.setColumnWidth(col, width);
                }
            }

            os = output.open(suggestedFileName);
            wb.write(os);

        } finally {
            if (wb != null) try {
                wb.close();
            } catch (Exception ignore) {
            }
            if (os != null) try {
                output.close(os);
            } catch (Exception ignore) {
            }
        }
    }

    private static String snapshotHeaderTitle(AttendanceSnapshot s) {
        String id = s != null ? s.getSnapshotId() : null;
        if (id != null && id.length() >= 10) return id.substring(0, 10);
        return "Ημερομηνία";
    }

    private static String snapshotSortKey(AttendanceSnapshot s) {
        String id = s != null ? s.getSnapshotId() : null;
        if (id != null && id.length() >= 10) return id.substring(0, 10);
        return "";
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

    public interface Output {
        OutputStream open(String suggestedFileName) throws Exception;

        void close(OutputStream os) throws Exception;

        String getLastFilePathOrName();
    }
}
