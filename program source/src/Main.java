import java.util.Calendar;
import java.util.*;
import java.text.*;

class Scaduler {
    String date; // yyyy-MM-dd
    String startTime; // HH:mm
    String endTime;   // HH:mm
    String name;
    boolean isRepeat;
    String repeatType; // 매주, 매월, 매년 등

    public Scaduler(String date, String startTime, String endTime, String name, boolean isRepeat, String repeatType) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.isRepeat = isRepeat;
        this.repeatType = repeatType;
    }

    public String toString() {
        return String.format("%s %s~%s %s %s", date, startTime, endTime, name, isRepeat ? "(" + repeatType + ")" : "");
    }
}

class ToDo {
    String scheduleName; // 관련 일정명 or "기타"
    String dueDate; // yyyy-MM-dd
    String dueTime; // HH:mm
    String task;

    public ToDo(String scheduleName, String dueDate, String dueTime, String task) {
        this.scheduleName = scheduleName;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.task = task;
    }

    public String toString() {
        return String.format("%-10s | %-8s | %-20s | %-10s", scheduleName, dueDate + " " + dueTime, task, "");
    }
}

class WeekScaduler {
    List<Scaduler> scadulers = new ArrayList<>();
    List<ToDo> todos = new ArrayList<>();
    Scanner sc = new Scanner(System.in);
    String inputDate; // yyyy-MM-dd

    public void PrintWeekScaduler() throws Exception {
        System.out.print("날짜를 입력하세요 (예: 2025-06-20): ");
        inputDate = sc.nextLine().trim();
        if (inputDate.isEmpty()) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(inputDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        int diff = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        cal.add(Calendar.DATE, -diff);

        System.out.println("\n[주별 캘린더]");
        for (int i = 0; i < 7; i++) {
            Date d = cal.getTime();
            System.out.print(new SimpleDateFormat("MM-dd(E)").format(d) + "  ");
            cal.add(Calendar.DATE, 1);
        }
        System.out.println("\n");

        System.out.println("[입력한 날짜의 일정]");
        boolean found = false;
        for (Scaduler s : scadulers) {
            if (s.date.equals(inputDate)) {
                System.out.println(s);
                found = true;
            }
        }
        if (!found) System.out.println("등록된 일정이 없습니다.");
    }

    public void AddScaduler() {
        while (true) {
            System.out.print("시작 시간(HH:mm, Enter시 종료): ");
            String start = sc.nextLine().trim();
            if (start.isEmpty()) break;
            System.out.print("종료 시간(HH:mm): ");
            String end = sc.nextLine().trim();
            if (end.isEmpty()) continue;
            System.out.print("일정 이름: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) continue;
            System.out.print("반복 여부(y/n): ");
            String rep = sc.nextLine().trim();
            if (rep.isEmpty()) rep = "n";
            boolean isRepeat = rep.equalsIgnoreCase("y");
            String repeatType = "-";
            if (isRepeat) {
                System.out.print("반복 주기(매주/매월/매년): ");
                repeatType = sc.nextLine().trim();
            }
            scadulers.add(new Scaduler(inputDate, start, end, name, isRepeat, repeatType));
            System.out.println("일정이 추가되었습니다.");
        }
    }

    public void ChangeScaduler() {
        while (true) {
            System.out.print("수정할 일정의 시작 시간(Enter시 종료): ");
            String start = sc.nextLine().trim();
            if (start.isEmpty()) break;
            System.out.print("수정할 일정의 이름: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) continue;

            Scaduler target = null;
            for (Scaduler s : scadulers) {
                if (s.date.equals(inputDate) && s.startTime.equals(start) && s.name.equals(name)) {
                    target = s;
                    break;
                }
            }
            if (target == null) {
                System.out.println("잘못 입력했습니다. 다시 입력하세요.");
                continue;
            }

            System.out.print("무엇을 수정할까요? (1.시간 2.이름 3.반복주기, Enter시 모두 Enter): ");
            String what = sc.nextLine().trim();
            if (what.isEmpty()) {
                System.out.print("삭제하시겠습니까?(y/n): ");
                String del = sc.nextLine().trim();
                if (del.isEmpty()) del = "n";
                if (del.equalsIgnoreCase("y")) {
                    if (target.isRepeat) {
                        System.out.print("반복 일정입니다. 이후 일정 모두 삭제(y), 이 일정만 삭제(n): ");
                        String delAll = sc.nextLine().trim();
                        if (delAll.isEmpty()) delAll = "n";
                        if (delAll.equalsIgnoreCase("y")) {
                            Iterator<Scaduler> it = scadulers.iterator();
                            while (it.hasNext()) {
                                Scaduler s = it.next();
                                if (s.name.equals(target.name) && s.isRepeat && !(s.date.compareTo(target.date) < 0)) {
                                    it.remove();
                                }
                            }
                        } else {
                            scadulers.remove(target);
                        }
                    } else {
                        scadulers.remove(target);
                    }
                    System.out.println("삭제되었습니다.");
                }
                break;
            }
            if (what.equals("1")) {
                System.out.print("새 시작 시간: ");
                String newStart = sc.nextLine().trim();
                if (!newStart.isEmpty()) target.startTime = newStart;
                System.out.print("새 종료 시간: ");
                String newEnd = sc.nextLine().trim();
                if (!newEnd.isEmpty()) target.endTime = newEnd;
            } else if (what.equals("2")) {
                System.out.print("새 이름: ");
                String newName = sc.nextLine().trim();
                if (!newName.isEmpty()) target.name = newName;
            } else if (what.equals("3") && target.isRepeat) {
                System.out.print("새 반복 주기: ");
                String newRep = sc.nextLine().trim();
                if (!newRep.isEmpty()) target.repeatType = newRep;
                System.out.print("반복 일정 모두 수정(y), 이 일정만(n): ");
                String all = sc.nextLine().trim();
                if (all.isEmpty()) all = "n";
                if (all.equalsIgnoreCase("y")) {
                    for (Scaduler s : scadulers) {
                        if (s.name.equals(target.name) && s.isRepeat && !(s.date.compareTo(target.date) < 0)) {
                            s.repeatType = target.repeatType;
                        }
                    }
                }
            }
            System.out.println("수정되었습니다.");
        }
    }

    public void PrintToDo() {
        while (true) {
            System.out.println("\n어떤 일정의 할일을 출력할까요? (Enter시 종료)");
            Set<String> scheduleSet = new LinkedHashSet<>();
            for (Scaduler s : scadulers) scheduleSet.add(s.name);
            scheduleSet.add("기타");
            int idx = 1;
            Map<Integer, String> idx2name = new HashMap<>();
            for (String s : scheduleSet) {
                System.out.println(idx + ". " + s);
                idx2name.put(idx, s);
                idx++;
            }
            System.out.print("번호 입력: ");
            String sel = sc.nextLine().trim();
            if (sel.isEmpty()) break;
            String selName = idx2name.getOrDefault(Integer.parseInt(sel), null);
            if (selName == null) continue;

            List<ToDo> filtered = new ArrayList<>();
            for (ToDo t : todos) if (t.scheduleName.equals(selName)) filtered.add(t);
            filtered.sort((a, b) -> {
                int cmp = a.dueDate.compareTo(b.dueDate);
                if (cmp == 0) return a.dueTime.compareTo(b.dueTime);
                return cmp;
            });

            System.out.println("일정 | 마감 날짜 | 마감 시간 | 할 일");
            for (ToDo t : filtered) {
                System.out.printf("%-10s | %-10s | %-8s | %-20s\n", t.scheduleName, t.dueDate, t.dueTime, t.task);
            }
            if (filtered.isEmpty()) System.out.println("해당 일정의 할일이 없습니다.");
        }
    }

    public void ChangeToDo() {
        while (true) {
            System.out.print("어떤 일정의 할일?(Enter시 종료): ");
            String sch = sc.nextLine().trim();
            if (sch.isEmpty()) break;
            System.out.print("마감일(yyyy-MM-dd): ");
            String date = sc.nextLine().trim();
            System.out.print("마감 시간(HH:mm): ");
            String time = sc.nextLine().trim();
            System.out.print("할일 이름: ");
            String task = sc.nextLine().trim();

            ToDo target = null;
            for (ToDo t : todos) {
                if (t.scheduleName.equals(sch) && t.dueDate.equals(date) && t.dueTime.equals(time) && t.task.equals(task)) {
                    target = t;
                    break;
                }
            }
            if (target == null) {
                System.out.println("잘못 입력했습니다. 다시 입력하세요.");
                continue;
            }
            System.out.print("무엇을 수정할까요? (1.일정 2.마감일 3.마감시간 4.할일, Enter시 모두 Enter): ");
            String what = sc.nextLine().trim();
            if (what.isEmpty()) {
                System.out.print("삭제하시겠습니까?(y/n): ");
                String del = sc.nextLine().trim();
                if (del.isEmpty()) del = "n";
                if (del.equalsIgnoreCase("y")) {
                    todos.remove(target);
                    System.out.println("삭제되었습니다.");
                }
                break;
            }
            if (what.equals("1")) {
                System.out.print("새 일정명: ");
                String newSch = sc.nextLine().trim();
                if (!newSch.isEmpty()) target.scheduleName = newSch;
            } else if (what.equals("2")) {
                System.out.print("새 마감일: ");
                String newDate = sc.nextLine().trim();
                if (!newDate.isEmpty()) target.dueDate = newDate;
            } else if (what.equals("3")) {
                System.out.print("새 마감시간: ");
                String newTime = sc.nextLine().trim();
                if (!newTime.isEmpty()) target.dueTime = newTime;
            } else if (what.equals("4")) {
                System.out.print("새 할일: ");
                String newTask = sc.nextLine().trim();
                if (!newTask.isEmpty()) target.task = newTask;
            }
            System.out.println("수정되었습니다.");
        }
    }

    public void AddToDo() {
        while (true) {
            System.out.print("어떤 일정의 할일?(Enter시 종료, 기타 입력 가능): ");
            String sch = sc.nextLine().trim();
            if (sch.isEmpty()) break;
            System.out.print("마감일(yyyy-MM-dd): ");
            String date = sc.nextLine().trim();
            System.out.print("마감 시간(HH:mm, Enter시 24:00): ");
            String time = sc.nextLine().trim();
            if (time.isEmpty()) time = "24:00";
            System.out.print("할일 이름: ");
            String task = sc.nextLine().trim();
            if (task.isEmpty()) continue;
            todos.add(new ToDo(sch, date, time, task));
            System.out.println("할일이 추가되었습니다.");
        }
    }

    public void ClearToDo() {
        while (true) {
            System.out.print("어떤 일정의 할일?(Enter시 종료): ");
            String sch = sc.nextLine().trim();
            if (sch.isEmpty()) break;
            System.out.print("마감일(yyyy-MM-dd): ");
            String date = sc.nextLine().trim();
            System.out.print("마감 시간(HH:mm): ");
            String time = sc.nextLine().trim();
            System.out.print("할일 이름: ");
            String task = sc.nextLine().trim();

            ToDo target = null;
            for (ToDo t : todos) {
                if (t.scheduleName.equals(sch) && t.dueDate.equals(date) && t.dueTime.equals(time) && t.task.equals(task)) {
                    target = t;
                    break;
                }
            }
            if (target == null) {
                System.out.println("잘못 입력했습니다. 다시 입력하세요.");
                continue;
            }
            todos.remove(target);
            System.out.println("삭제되었습니다.");
        }
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        WeekScaduler ws = new WeekScaduler();
        ws.PrintWeekScaduler();

        // 일정 변동 사항 처리
        while (true) {
            System.out.print("일정에 변동 사항이 있습니까?(Enter시 종료, y/n): ");
            String ans = ws.sc.nextLine().trim();
            if (ans.isEmpty()) ans = "n";
            if (ans.equalsIgnoreCase("n")) break;
            System.out.print("수정(s) / 추가(a): ");
            String op = ws.sc.nextLine().trim();
            if (op.equalsIgnoreCase("s")) ws.ChangeScaduler();
            else if (op.equalsIgnoreCase("a")) ws.AddScaduler();
        }

        // 할일 목록 출력 및 관리
        System.out.print("할일 목록을 출력하시겠습니까?(y/n): ");
        String todoPrint = ws.sc.nextLine().trim();
        if (todoPrint.isEmpty()) todoPrint = "n";
        if (todoPrint.equalsIgnoreCase("y")) ws.PrintToDo();

        while (true) {
            System.out.print("다 한 할일이 있습니까?(Enter시 종료, y/n): ");
            String ans = ws.sc.nextLine().trim();
            if (ans.isEmpty()) ans = "n";
            if (ans.equalsIgnoreCase("n")) break;
            ws.ClearToDo();
        }
        while (true) {
            System.out.print("기존 할일을 수정하시겠습니까?(Enter시 종료, y/n): ");
            String ans = ws.sc.nextLine().trim();
            if (ans.isEmpty()) ans = "n";
            if (ans.equalsIgnoreCase("n")) break;
            ws.ChangeToDo();
        }
        while (true) {
            System.out.print("새 할일을 추가하시겠습니까?(Enter시 종료, y/n): ");
            String ans = ws.sc.nextLine().trim();
            if (ans.isEmpty()) ans = "n";
            if (ans.equalsIgnoreCase("n")) break;
            ws.AddToDo();
        }

        ws.PrintWeekScaduler();
        ws.PrintToDo();
        System.out.print("프로그램을 종료하시겠습니까?(y/n): ");
        String end = ws.sc.nextLine().trim();
        if (end.isEmpty()) end = "n";
        if (end.equalsIgnoreCase("y")) {
            System.out.println("프로그램을 종료합니다.");
        }
    }
}