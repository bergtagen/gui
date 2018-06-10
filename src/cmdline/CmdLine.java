package cmdline;

import java.util.HashMap;

public class CmdLine {

    private String name = "";
    private int threadNum;
    private String volumeSize;
    boolean sfx;
    boolean shared;
    boolean delete;
    private String password;
    boolean encrypt;

    String getVolumeSize() {
        return volumeSize;
    }

    void setVolumeSize(String volumeSize) {
        this.volumeSize = volumeSize;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    // key - cpu threads, value - index
    int getThreadNumKey(int threadNum) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(2, 0);
        map.put(4, 1);
        map.put(6, 2);
        map.put(8, 3);
        map.put(10, 4);
        map.put(12, 5);
        map.put(14, 6);
        map.put(16, 7);
        return map.get(threadNum);
    }

    int getDictionarySize(String level) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put(Level.fast.name(), 1);
        map.put(Level.normal.name(), 16);
        map.put(Level.maximum.name(), 32);
        return map.get(level);
    }

    int getThreadNum() {
        return threadNum;
    }

    void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    CmdLine() {
    }

    public static void main(String[] args) {
        Win w = new Win();
        w.construct();
    }
}
