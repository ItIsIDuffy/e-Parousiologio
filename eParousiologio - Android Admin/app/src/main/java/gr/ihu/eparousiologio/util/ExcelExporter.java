package gr.ihu.eparousiologio.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import gr.ihu.eparousiologio.model.AttendanceEntry;
import gr.ihu.eparousiologio.model.Course;
import gr.ihu.eparousiologio.model.Section;
import gr.ihu.eparousiologio.model.Student;
import gr.ihu.eparousiologio.repository.AttendanceRepository;

public class ExcelExporter {

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public static void exportCourse(Course course,
                                    AttendanceRepository.AttendanceSnapshot snap,
                                    Output output) throws Exception {

        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle hdr = wb.createCellStyle();
            Font bold = wb.createFont();
            bold.setBold(true);
            hdr.setFont(bold);

            Map<String, Integer> totalSessionsByLab = snap.sessionsByLab != null ? snap.sessionsByLab : Collections.emptyMap();
            Map<String, Map<String, List<AttendanceEntry>>> entriesByLabAndAem =
                    snap.entriesByLabAndAem != null ? snap.entriesByLabAndAem : Collections.emptyMap();

            // 1) Index φοιτητών από ΟΛΑ τα sections -> (AEM -> Student)
            Map<String, Student> studentByAem = new HashMap<>();
            Map<String, Section> sectionByLab = new HashMap<>();
            if (course.getSections() != null) {
                for (Section s : course.getSections()) {
                    if (s.getLabId() != null) sectionByLab.put(s.getLabId(), s);
                    if (s.getStudents() == null) continue;
                    for (Student st : s.getStudents()) {
                        studentByAem.put(st.getStudentAEM(), st);
                    }
                }
            }

            LinkedHashSet<String> labIds = new LinkedHashSet<>();
            if (course.getSections() != null) {
                for (Section s : course.getSections()) {
                    if (s.getLabId() != null) labIds.add(s.getLabId());
                }
            }
            labIds.addAll(entriesByLabAndAem.keySet());
            if (labIds.isEmpty()) labIds.add("no_lab");

            for (String labId : labIds) {
                Section section = sectionByLab.get(labId);
                String secName = (section != null && section.getName() != null) ? section.getName() : labId;

                Sheet sh = wb.createSheet(sanitizeSheetName(secName));
                int r = 0;

                Row r0 = sh.createRow(r++);
                setCell(r0, 0, course.getTitle(), hdr);

                Row r1 = sh.createRow(r++);
                setCell(r1, 0, "Εργαστήριο", hdr);
                setCell(r1, 1, secName + " (" + labId + ")", null);

                Row r2 = sh.createRow(r++);
                setCell(r2, 0, "Συνολικές συνεδρίες Lab", hdr);
                int totalSessions = totalSessionsByLab.getOrDefault(labId, 0);
                setCell(r2, 1, String.valueOf(totalSessions), null);

                r++;

                Row th = sh.createRow(r++);
                String[] headers = {
                        "AEM",
                        "Παρουσίες/Σύνολο",
                        "Αναλυτικά (ημερομηνία, lab)"
                };
                for (int c = 0; c < headers.length; c++) setCell(th, c, headers[c], hdr);

                Map<String, List<AttendanceEntry>> byAemHere = entriesByLabAndAem.getOrDefault(labId, Collections.emptyMap());

                LinkedHashSet<String> aems = new LinkedHashSet<>();
                if (section != null && section.getStudents() != null) {
                    for (Student st : section.getStudents()) aems.add(st.getStudentAEM());
                }
                aems.addAll(Objects.requireNonNull(byAemHere).keySet());

                List<String> aemList = new ArrayList<>(aems);
                Collections.sort(aemList);

                for (String aem : aemList) {
                    Student st = studentByAem.get(aem);
                    String fullName = st != null ? st.getFullName() : "";
                    String enrolled = st != null ? (st.isEnrolled() ? "NAI" : "OXI") : "";

                    int countHere = byAemHere.getOrDefault(aem, Collections.emptyList()).size();

                    List<String> detailed = new ArrayList<>();
                    for (Map.Entry<String, Map<String, List<AttendanceEntry>>> bucket : entriesByLabAndAem.entrySet()) {
                        String thatLab = bucket.getKey();
                        List<AttendanceEntry> entries = bucket.getValue().get(aem);
                        if (entries == null) continue;
                        for (AttendanceEntry e : entries) {
                            Date d = e.getAt() != null ? e.getAt().toDate() : null;
                            String when = d != null ? DATE_FMT.format(d) : "";
                            String where = e.getLabId() != null ? e.getLabId() : thatLab;
                            detailed.add(when + " [" + where + "]");
                        }
                    }
                    Collections.sort(detailed);

                    Row row = sh.createRow(r++);
                    setCell(row, 0, aem, null);
                    setCell(row, 1, countHere + " / " + totalSessions, null);
                    setCell(row, 2, String.join(", ", detailed), null);
                }

                setColWidths(sh, new int[]{12, 18, 80});
            }

            String fileName = course.getCourseId() + "_attendance.xlsx";
            try (OutputStream os = output.open(fileName)) {
                wb.write(os);
                os.flush();
            }
        }
    }

    private static void setCell(Row row, int col, String val, CellStyle style) {
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(val != null ? val : "");
        if (style != null) cell.setCellStyle(style);
    }

    private static void setColWidths(Sheet sh, int[] charWidths) {
        for (int c = 0; c < charWidths.length; c++) {
            int width256 = Math.max(256 * charWidths[c], 256 * 8);
            sh.setColumnWidth(c, Math.min(width256, 255 * 256));
        }
    }

    private static String sanitizeSheetName(String s) {
        if (s == null || s.isEmpty()) return "sheet";
        String t = s.replaceAll("[\\[\\]:*?/\\\\]", " ");
        return t.length() > 31 ? t.substring(0, 31) : t;
    }

    public interface Output {
        OutputStream open(String suggestedFileName) throws Exception;

        void close(OutputStream os) throws Exception;

        String getLastFilePathOrName();
    }
}
