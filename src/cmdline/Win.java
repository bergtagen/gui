package cmdline;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Win {
    private final JFrame frame = new JFrame("7z GUI");
    private JPanel panel;
    private JTextField fileTextField;
    private JButton chooseButton;
    private JComboBox<String> volumeBox;
    private JComboBox<String> updateModeBox;
    private JComboBox<String> pathModeBox;
    private JTextField passwordField;
    private JCheckBox SFXArchiveCheckBox;
    private JCheckBox compressSharedFilesCheckBox;
    private JCheckBox deleteAfterCompressionCheckBox;
    private JComboBox<String> encryptBox;
    private JCheckBox encryptCheckBox;
    private JComboBox<String> formatBox;
    private JComboBox<String> levelBox;
    private JComboBox<String> methodBox;
    private JComboBox<String> threadsBox;
    private JTextField memField;
    private JButton OKButton;
    private JButton cancelButton;
    private JTextField dememField;
    private JTextField cmdlineField;
    private JTextField optionsField;
    private JButton checkButton;
    private JTextField maxThreadsField;
    private JCheckBox storeSymlinksCheckBox;
    private JCheckBox storeHardlinksCheckBox;
    private final CmdLine cmdline = new CmdLine();
    private StringBuilder z7name = new StringBuilder();


    Win() {

        z7name.append(Format.z7.name()).reverse();
        frame.setResizable(false);

        cmdline.setThreadNum(Runtime.getRuntime().availableProcessors());
        maxThreadsField.setHorizontalAlignment(4);
        maxThreadsField.setText(" / " + cmdline.getThreadNum());

        formatBox.addItem(z7name.toString());
        formatBox.addItem(Format.zip.name());
        formatBox.addItem(Format.tar.name());

        methodBox.setEnabled(true);
        methodBox.removeAllItems();
        methodBox.addItem(Method.LZMA2.name());
        methodBox.addItem(Method.LZMA.name());

        setLevelBox();
        setThreadsBox(true);
        setVolumeBox();
        setPathModeBox();
        setEncryptBox();
        setUpdateModeBox();
//        setMemoryFields(z7name.toString(), Level.normal.name(), Method.LZMA2.name(), cmdline.getThreadNum());


        formatBox.addActionListener(actionEvent -> {
            if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())
                    || (Objects.equals(formatBox.getSelectedItem(), Format.zip.name())))
                formatBoxInit(Objects.requireNonNull(formatBox.getSelectedItem()).toString(),
                        Level.normal.name(), cmdline.getThreadNum());

            if (Objects.equals(formatBox.getSelectedItem(), Format.tar.name())) {
                levelBox.removeAllItems();
                levelBox.setEnabled(false);
                methodBox.removeAllItems();
                methodBox.setEnabled(false);
                threadsBox.removeAllItems();
                threadsBox.setEnabled(false);
                encryptBox.removeAllItems();
                encryptBox.setEnabled(false);
                volumeBox.setEnabled(false);
                encryptCheckBox.setEnabled(false);
                passwordField.setEnabled(false);
                passwordField.setEditable(false);
                SFXArchiveCheckBox.setEnabled(false);
//                setTarFields();
            }
        });

        levelBox.addActionListener(actionEvent -> {
/*
            int threads;
            if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())
                    && methodBox.getSelectedIndex() == 1)
                threads = 2;
            else
                threads = cmdline.getThreadNum();
            setMemoryFields(formatBox.getSelectedItem().toString(), levelBox.getSelectedItem().toString(),
                    methodBox.getSelectedItem().toString(), threads);
*/
        });

        // for 7z:lzma 2 threads, for any other threads = cpu cores
        methodBox.addActionListener(actionEvent -> {
            if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())
                    && methodBox.getSelectedIndex() == 1) {   // LZMA. don't touch
                setThreadsBox(false);
//                setMemoryFields(z7name.toString(), Level.normal.name(), Method.LZMA.name(), 2);
            }
            else {
                setThreadsBox(true);
/*
                if (Objects.equals(formatBox.getSelectedItem(), z7name.toString()))
                    setMemoryFields(z7name.toString(), Level.normal.name(), Method.LZMA2.name(), cmdline.getThreadNum());
                if (Objects.equals(formatBox.getSelectedItem(), Format.zip.name()))
                    setMemoryFields(Format.zip.name(), Level.normal.name(), Method.Deflate.name(), cmdline.getThreadNum());
                if (Objects.equals(formatBox.getSelectedItem(), Format.tar.name()))
                    setTarFields();
*/
            }
        });

        volumeBox.addActionListener(actionEvent -> {
            String vol;
            if (!Objects.equals(volumeBox.getSelectedItem(), "")) {
                vol = Objects.requireNonNull(volumeBox.getSelectedItem()).toString().split(" ")[0];
                cmdline.setVolumeSize(vol.toLowerCase());
            }
        });

        chooseButton.addActionListener(actionEvent -> {
            String fileName;
            FileDialog fileDialog = new FileDialog(frame, "Choose file", FileDialog.LOAD);
            fileDialog.setFile("*.*");
            fileDialog.setVisible(true);
            fileName = fileDialog.getFile();
            cmdline.setName(fileName);
            try {
            if (!fileName.equals("nullnull"))
                fileTextField.setText(fileName);
            else
                fileTextField.setText(""); }
                catch (NullPointerException e) { }
        });

        checkButton.addActionListener(actionEvent -> {
            cmdlineField.removeAll();
            cmdlineField.setText(buildCommand());
        });

        OKButton.addActionListener(actionEvent -> {
            Process proc;
            fileTextField.setText("");
            cmdlineField.setText("");
/*
            if (cmdline.delete) {
                try {
                    Process del = Runtime.getRuntime().exec("del " + cmdline.getName());
                    del.waitFor();
                    del.destroy();
                } catch (InterruptedException | IOException e) { }
            }
*/
            try {
                proc = Runtime.getRuntime().exec(buildCommand());
                cmdline.setName("");
                proc.waitFor();
                proc.destroy();
            } catch (InterruptedException | IOException e) { }
        });

        cancelButton.addActionListener(actionEvent -> {
            frame.setVisible(false);
            frame.dispose();
        });

        encryptCheckBox.addItemListener(e -> cmdline.encrypt = encryptCheckBox.isSelected());
        SFXArchiveCheckBox.addItemListener(e -> cmdline.sfx = SFXArchiveCheckBox.isSelected());
        compressSharedFilesCheckBox.addItemListener(e -> cmdline.shared = compressSharedFilesCheckBox.isSelected());
        deleteAfterCompressionCheckBox.addItemListener(e -> cmdline.delete = deleteAfterCompressionCheckBox.isSelected());
    }

    //  7z a -t[7z,zip,tar] -m0=[Deflate,LZMA2,LZMA] -mx=[3,5,7] -mmt=threads [-ssw] -v{size}[b,k,m,g]
    private String buildCommand() {
        StringBuilder command = new StringBuilder();
        command.append("7z").append(" ");
        if (updateModeBox.getSelectedItem().toString().toCharArray()[0] == 'A')
            command.append("a").append(" ");
        else
            if (updateModeBox.getSelectedItem().toString().toCharArray()[0] == 'U')
                command.append("u").append(" ");

        command.append("-t");
        if (formatBox.getSelectedItem().toString().equals(z7name.toString()))
            command.append(z7name.toString()).append(" ");
        else
            command.append(formatBox.getSelectedItem().toString()).append(" ");

        if (!formatBox.getSelectedItem().toString().equals(Format.tar.name())) {
            command.append("-mx=");
            if (levelBox.getSelectedItem().toString().equals(Level.fast.name()))
                command.append("3").append(" ");
            if (levelBox.getSelectedItem().toString().equals(Level.normal.name()))
                command.append("5").append(" ");
            if (levelBox.getSelectedItem().toString().equals(Level.maximum.name()))
                command.append("7").append(" ");
        }

        if (formatBox.getSelectedItem().toString().equals(z7name.toString()))
            command.append("-m0=").append(methodBox.getSelectedItem().toString()).append(" ");
        if (formatBox.getSelectedItem().toString().equals(Format.zip.name()))
            command.append("-mm=").append(methodBox.getSelectedItem().toString()).append(" ");
        if (!formatBox.getSelectedItem().toString().equals(Format.tar.name()))
            command.append("-mmt=").append(threadsBox.getSelectedItem().toString()).append(" ");

//        sfx
        if (compressSharedFilesCheckBox.isSelected() && formatBox.getSelectedItem().toString().equals(z7name.toString()))
            command.append("-ssw").append(" ");
        if (!Objects.requireNonNull(volumeBox.getSelectedItem()).toString().equals("")) {
            command.append("-v").append(cmdline.getVolumeSize().toLowerCase()).append(" ");
        }

        if (!optionsField.getText().isEmpty())
            command.append(optionsField.getText()).append(" ");

        if (!cmdline.getName().isEmpty()) {
            if (formatBox.getSelectedItem().toString().equals(z7name.toString()))
                command.append(cmdline.getName().split("\\.")[0])
                        .append(".").append(z7name.toString()).append(" ");
            else
                command.append(cmdline.getName().split("\\.")[0])
                        .append(".").append(formatBox.getSelectedItem().toString()).append(" ");
        }
        else {
            command.delete(0, command.length() - 1);
            command.append("Choose file or directory");
        }

        command.append(cmdline.getName());

        return command.toString();
    }

    private void formatBoxInit(String format, String level, int threads) {
        methodBox.setEnabled(true);
        methodBox.removeAllItems();
        volumeBox.setEnabled(true);
        if (format.equals(z7name.toString())) {
            passwordField.setEnabled(true);
            passwordField.setEditable(true);
            encryptCheckBox.setEnabled(true);
            SFXArchiveCheckBox.setEnabled(true);
            methodBox.addItem(Method.LZMA2.name());
//            setMemoryFields(format, level, Method.LZMA2.name(), threads);
        }
        if (format.equals(Format.zip.name())) {
            passwordField.setEnabled(true);
            passwordField.setEditable(true);
            encryptCheckBox.setEnabled(false);
            SFXArchiveCheckBox.setEnabled(false);
            methodBox.addItem(Method.Deflate.name());
//            setMemoryFields(format, level, Method.Deflate.name(), threads);
        }
        methodBox.addItem(Method.LZMA.name());
        setLevelBox();
        setThreadsBox(true);
        setEncryptBox();
    }

    private void setLevelBox() {
        levelBox.setEnabled(true);
        levelBox.removeAllItems();
        levelBox.addItem(Level.fast.name());
        levelBox.addItem(Level.normal.name());
        levelBox.addItem(Level.maximum.name());
        levelBox.setSelectedItem(Level.normal.name());
    }

    // true: threads = cpu cores, false: threads = 2 for lzma
    private void setThreadsBox(boolean longList) {
        threadsBox.setEnabled(true);
        threadsBox.removeAllItems();
        if (longList) {
            for (int i = 2; i <= 16; i += 2)
                threadsBox.addItem(Integer.toString(i));
            threadsBox.setSelectedIndex(cmdline.getThreadNumKey(cmdline.getThreadNum()));
        }
        else {
            threadsBox.addItem("2");
            threadsBox.setSelectedIndex(cmdline.getThreadNumKey(2));
        }
    }

    private void setMemoryFields(String format, String level, String method, int threads) {
        int memory = 0;
        int dememory = 0;
        if (format.equals(Format.zip.name())) {
            memory = (int) Math.round(33.3 * threads);
            dememory = 2;
        }
        if (format.equals(Format.z7.name())) {
            if (method.equals(Method.LZMA.name()))
                memory = 12 * cmdline.getDictionarySize(level) * threads;
            if (method.equals(Method.LZMA2.name()))
                memory = 11 * cmdline.getDictionarySize(level) * threads;
            dememory = cmdline.getDictionarySize(level) + 2;
        }
        memField.setHorizontalAlignment(4);
        memField.setText(String.valueOf(memory) + " MB");
        dememField.setHorizontalAlignment(4);
        dememField.setText(String.valueOf(dememory) + " MB");
    }

    private void setTarFields() {
        memField.setHorizontalAlignment(4);
        memField.setText(1 + " MB");
        dememField.setHorizontalAlignment(4);
        dememField.setText(1 + " MB");
    }

    private void setVolumeBox() {
        volumeBox.setEnabled(true);
        volumeBox.removeAllItems();
        volumeBox.addItem("");
        volumeBox.addItem("210M - mCD");
        volumeBox.addItem("700M - CD");
        volumeBox.addItem("1420M - mDVD");
        volumeBox.addItem("4092M - FAT32");
        volumeBox.addItem("4480M - DVD");
        volumeBox.addItem("8128M - DVD/DL");
        volumeBox.addItem("23040M - BD");
        volumeBox.setSelectedItem("");
    }

    private void setPathModeBox() {
        pathModeBox.setEnabled(true);
        pathModeBox.removeAllItems();
        pathModeBox.addItem("Relative pathnames");
        pathModeBox.addItem("Full pathnames");
        pathModeBox.addItem("Absolute pathnames");
    }

    private void setUpdateModeBox() {
        updateModeBox.setEnabled(true);
        updateModeBox.removeAllItems();
        updateModeBox.addItem("Add & replace files");
        updateModeBox.addItem("Update & add files");
        updateModeBox.addItem("Freshen existing files");
        updateModeBox.addItem("Synchronize files");
    }

    private void setEncryptBox() {
        encryptBox.setEnabled(true);
        encryptBox.removeAllItems();
        if (Objects.requireNonNull(formatBox.getSelectedItem()).toString().equals(z7name.toString()))
            encryptBox.addItem("AES-256");
        if (formatBox.getSelectedItem().toString().equals(Format.zip.name())) {
            encryptBox.addItem("ZipCrypto");
            encryptBox.addItem("AES-256");
        }
    }
    void construct() {
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
