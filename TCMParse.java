import java.io.*;
import java.util.*;

public class TCMParse {

    public static void main(String[] args) throws IOException {
        String tcmlog = "tcm-execution-results.log";
        Map<String, String> jiraResultMap = new TreeMap<>();
        try (Scanner sc = new Scanner(new FileInputStream(new File(tcmlog)))) {
            while (sc.hasNextLine()) {
                List<String> line = Arrays.asList(sc.nextLine().split("\\|"));
                if (line.size() < 4) {
                    continue;
                }

                List<String> tcmNumbers = new ArrayList<>();
                String jiraTicket = line.get(2);
                jiraTicket = jiraTicket.replace("&", ",");
                jiraTicket = jiraTicket.replace("{", "");
                jiraTicket = jiraTicket.replace("}", "");

                List<String> keyList = Arrays.asList(jiraTicket.split(","));
                for (String key : keyList) {
                    tcmNumbers.add(key.trim());
                }

                Status executionResult = Status.valueOf(line.get(3));

                for (String number : tcmNumbers) {
                    if (jiraResultMap.containsKey(number)) {
                        if (Status.valueOf(jiraResultMap.get(number)).toInt() > executionResult.toInt()) {
                            jiraResultMap.put(number, executionResult.toString());
                        }
                    } else {
                        jiraResultMap.put(number, executionResult.toString());
                    }
                }
            }
        }

        try (BufferedWriter bw_tests = new BufferedWriter(new FileWriter("test_out/tests-jira.txt"))) {
            for (Map.Entry<String, String> entry : jiraResultMap.entrySet()) {
                bw_tests.write(entry.getKey() + " - " + entry.getValue());
                bw_tests.newLine();
            }
        }

        try (BufferedWriter bw_pass = new BufferedWriter(new FileWriter("test_out/tcm_pass.txt"));
             BufferedWriter bw_fail = new BufferedWriter(new FileWriter("test_out/tcm_fail.txt"));
             BufferedWriter bw_block = new BufferedWriter(new FileWriter("test_out/tcm_block.txt"));
             BufferedWriter bw_all = new BufferedWriter(new FileWriter("test_out/tcm_all.txt"))) {
            for (Map.Entry<String, String> entry : jiraResultMap.entrySet()) {
                if (entry.getKey().contains("TCM")) {
                    bw_all.write(entry.getKey() + " - " + entry.getValue());
                    bw_all.newLine();

                    if (entry.getValue().equals(Status.Passed.toString())) {
                        bw_pass.write(entry.getKey() + " ");
                    } else if (entry.getValue().equals(Status.Blocked.toString())) {
                        bw_block.write(entry.getKey() + " ");
                    } else {
                        bw_fail.write(entry.getKey() + " ");
                    }
                }
            }
        }

        try (BufferedWriter bw_pass = new BufferedWriter(new FileWriter("test_out/other_pass.txt"));
             BufferedWriter bw_fail = new BufferedWriter(new FileWriter("test_out/other_fail.txt"));
             BufferedWriter bw_block = new BufferedWriter(new FileWriter("test_out/other_block.txt"));
             BufferedWriter bw_all = new BufferedWriter(new FileWriter("test_out/other_all.txt"))) {
            for (Map.Entry<String, String> entry : jiraResultMap.entrySet()) {
                if (!entry.getKey().contains("TCM")) {
                    bw_all.write(entry.getKey() + " - " + entry.getValue());
                    bw_all.newLine();

                    if (entry.getValue().equals(Status.Passed.toString())) {
                        bw_pass.write(entry.getKey() + " - " + entry.getValue());
                        bw_pass.newLine();
                    } else if (entry.getValue().equals(Status.Blocked.toString())) {
                        bw_block.write(entry.getKey() + " - " + entry.getValue());
                        bw_block.newLine();
                    } else {
                        bw_fail.write(entry.getKey() + " - " + entry.getValue());
                        bw_fail.newLine();
                    }
                }
            }
        }
    }

    private enum Status {
        Failed(1, "Failed"),
        Blocked(2, "Blocked"),
        ApplicationBug(2, "Blocked"),
        DependencyFail(2, "Blocked"),
        Repair(2, "Blocked"),
        Passed(3, "Passed");

        int priority;
        String group;

        Status(int priority, String group) {
            this.priority = priority;
            this.group = group;
        }

        public int toInt() {
            return this.priority;
        }

        @Override
        public String toString() {
            return this.group;
        }
    }
}
