package DESAPP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import net.miginfocom.swing.*;
/*
 * Created by JFormDesigner on Wed Apr 06 19:41:50 GMT+08:00 2022
 */

public class DESUI extends JFrame {
    //from USER
    String msg;
    String key;

    //用于文件、文件夹的信息存储
    String fileContent;
    String filename;
    String path;
    String frontPath;
    String extension;

    //用于文件名的存储
    String[] fileNames;
    //用于文件绝对路径的存储
    String[] dirFilePath;
    //用于文件夹文件的明文存储
    String[] fileTexts;
    //用于文件夹文件的密文存储
    String[] fileCiphs;

    //from DES
    String cipher;
    String plain;
    long stime;
    long etime;

    DES demo = new DES();

    public DESUI() {
        initComponents();
        button1.addActionListener(new ActionListener(){
        //DES加密按钮事件-匿名内部类
            @Override //实现方法
            public void actionPerformed(ActionEvent e) {
                input();
            }
        });
        //DES解密按钮事件-lambda表达式
        button2.addActionListener(e -> output());
        //打开文件事件-lambda表达式
        button6.addActionListener(e -> fileOpen());
        button7.addActionListener(e -> fileInput());
        button8.addActionListener(e -> fileOutput());
        button3.addActionListener(e -> dirOpen());
        button4.addActionListener(e -> dirInput());
        button5.addActionListener(e -> dirOutput());
    }



    //================字符串DES加密解密====================
    public void input(){
        System.out.println("DES加密"); //调试用，相关语句可删除
        //从文本框获取用户输入，初始化msg、key，进行DES加密，通过 cipher 从文本框返回加密结果
        msg = textField1.getText();
        key = textField4.getText();
        stime = etime = 0;
        stime = System.nanoTime(); //系统纳秒计时
        cipher = demo.encryption(msg, key); //DES加密得到密文
        etime = System.nanoTime();
        label13.setText((etime - stime) / 10E6 +" 毫秒"); //加上单位“毫秒”
        textField3.setText(cipher);
        System.out.println("明文：" +msg);
        System.out.println("密钥：" +key);
        System.out.println("密文：" +cipher);
    }

    public void output(){
        System.out.println("DES解密");
        //进行DES解密，通过 plain 从文本框返回解密结果
        stime = etime = 0;
        stime = System.nanoTime();
        plain = demo.decryption(cipher, key); //DES解密得到明文
        etime = System.nanoTime();
        label14.setText((etime - stime) / 10E6 +" 毫秒"); //加上单位“毫秒”
        textField2.setText(plain);
        System.out.println("明文：" +plain);
    }



    //=================================文件DES加密解密==============================
    public String readFile(String path){
        FileReader reader = null;
        String str = null;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int len = 0;
        //通过chars一次读取一个数组，数组要设置的大一些
        char[] chars = new char[102400];
        while (true){
            try {
                if (!((len = reader.read(chars)) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            //将字符数组转换成字符串
            str = new String(chars,0,len);
        }
        System.out.println(str);
        try {
            reader.close(); //释放资源
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void writeFile(String str,String path){
        FileWriter writer;
        try {
            writer = new FileWriter(path);
            writer.write(str);
            writer.flush(); //清空缓冲区
            writer.close(); //释放资源
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void fileOpen(){
        //创建一个JFrame组件为parent组件
        JFrame file1 = new JFrame("选择文件");
        //创建文件选择器
        JFileChooser choosers = new JFileChooser();
        //设置为只能选文件
        choosers.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //默认一次只能选择一个文件，参数改为"true"后可选择多个文件
        choosers.setMultiSelectionEnabled(true);
        //弹出选择文件对话框
        int flag = choosers.showOpenDialog(file1);
        if(flag == JFileChooser.APPROVE_OPTION) {
            //获取文件的名称
            filename = choosers.getSelectedFile().getName();
            //获取选择文件的路径
            path = choosers.getSelectedFile().getPath();
            //获取文件的扩展名
            extension = path.substring(path.lastIndexOf('.'));
            //获取文件名之前的路径
            frontPath = path.substring(0, path.lastIndexOf('\\')); //需要对'\'进行转义表示
            label17.setText(filename); //显示打开的文件名
            System.out.println("用户选择了文件：" + filename);
            System.out.println("文件绝对路径：" + path);
            System.out.println("文件路径：" + frontPath);
            System.out.println("文件扩展名：" + extension);
            //从源文件中读取字符流，存为字符串
            fileContent = readFile(path);
            //将读到的明文写入文本文档中,在"."之前插入字符串"_Src"
            StringBuilder sb = new StringBuilder(filename);
            writeFile(fileContent, frontPath+"\\"+sb.insert(sb.indexOf("."),"_Src"));
        }
    }

    public void fileInput(){
        System.out.println("文件DES加密");
        stime = etime = 0;
        stime = System.nanoTime(); //系统纳秒计时
        //DES加密得到密文
        cipher = demo.encryption(fileContent, key);
        etime = System.nanoTime();
        //计算加密时间，加上单位“毫秒”
        label11.setText((etime - stime) / 10E6 +" 毫秒");
        System.out.println("明文：" +fileContent);
        System.out.println("密钥：" +key);
        System.out.println("密文：" +cipher);
        //加密好的密文写到文件中,在"."之前插入字符串"_Src"
        StringBuilder sb = new StringBuilder(filename);
        writeFile(cipher,frontPath+"\\"+sb.insert(sb.indexOf("."),"_Enc"));
    }

    public void fileOutput(){
        System.out.println("文件DES解密");
        //读取加密文件，存为cipher字符串
        StringBuilder sb = new StringBuilder(filename);
        cipher = readFile(frontPath+"\\"+sb.insert(sb.indexOf("."),"_Enc"));
        stime = etime = 0;
        stime = System.nanoTime();
        //DES解密得到明文
        plain = demo.decryption(cipher, key);
        etime = System.nanoTime();
        //计算解密时间，加上单位“毫秒”
        label12.setText((etime - stime) / 10E6 +" 毫秒");
        System.out.println("明文：" +plain);
        //解密出来的明文写到新文件中
        sb = new StringBuilder(filename); //当前sb为“XXX_Enc.txt”，需要重新赋值,变为"XXX.txt"
        writeFile(plain,frontPath+"\\"+sb.insert(sb.indexOf("."),"_Dec")+extension);
    }



    //=====================================文件夹DES加密===========================================
    public void dirOpen(){
        //创建一个JFrame组件为parent组件
        JFrame file2 = new JFrame("选择文件");
        //创建文件选择器
        JFileChooser choosers = new JFileChooser();
        //设置为只能选文件
        choosers.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //默认一次只能选择一个文件夹，参数改为"true"后可选择多个文件夹
        choosers.setMultiSelectionEnabled(true);
        //弹出选择文件对话框
        int flag = choosers.showOpenDialog(file2);
        if(flag == JFileChooser.APPROVE_OPTION) {
            //获取文件夹的名称
            filename = choosers.getSelectedFile().getName();
            //获取选择文件夹的路径
            path = choosers.getSelectedFile().getPath();
            frontPath = path.substring(0, path.lastIndexOf('\\'));
            //显示打开的文件夹名
            label18.setText(filename);
            System.out.println("用户选择了文件夹：" + filename);
            System.out.println("文件夹绝对路径：" + path);



            //获取文件夹下的所有文件（一层）
            File fi = choosers.getSelectedFile();
            File[] files = fi.listFiles();
            //将目录下的所有文件名称及文件地址存储到列表中
            List<String> listF = new ArrayList<>();
            List<String> listP = new ArrayList<>();
            for (File file : files) {
                System.out.println(file.getName());
                listF.add(file.getName());
                listP.add(path + "\\" + file.getName());
            }
            //将列表中的内容存到字符串数组中
            fileNames = listF.toArray(new String[listF.size()]);
            dirFilePath = listP.toArray(new String[listP.size()]);
            //在所选文件夹的路径下创建目录DES，如果没有目录DES则新建一个
            File dir = new File(path+"\\DES");
            if(!dir.exists()){
                dir.mkdir();
            }



            //必须先实例化，否则会触发空指针异常:"Cannot invoke "java.util.List.add(Object)" because "this.texts" is null"
            List<String> texts = new ArrayList<>();
            //循环处理遍历得到的文件
            for (int i = 0; i < dirFilePath.length; i++) {
                StringBuilder sb = new StringBuilder(fileNames[i]);
                //得到文件的绝对路径
                System.out.println(dirFilePath[i]);
                //"XXX.txt变为XXX_Src.txt"
                String temp = String.valueOf(sb.insert(sb.indexOf("."), "_Src"));
                //字符串连接得到新文件的绝对路径
                String newFileAbsolute = dirFilePath[i].substring(0, dirFilePath[i].lastIndexOf('\\')) + "\\DES\\" + temp;
                System.out.println(temp);
                System.out.println(newFileAbsolute);
                //将读取到的明文存储到列表中
                fileContent = null;
                fileContent = readFile(dirFilePath[i]);
                texts.add(fileContent);
                //将读到的明文写到新文件中
                writeFile(fileContent, newFileAbsolute);
            }
            //将列表中的内容存到字符串数组中
            fileTexts = texts.toArray(new String[texts.size()]);
        }
    }

    public void dirInput(){
        System.out.println("文件夹DES加密");
        List<String> ciphs = new ArrayList<>();
        stime = etime = 0;
        stime = System.nanoTime(); //系统纳秒计时
        for(int i = 0; i < fileTexts.length; i++){
            cipher = null; //先清空cipher
            //DES加密得到密文
            cipher = demo.encryption(fileTexts[i], key);
            ciphs.add(cipher); //密文存储到列表中
            //加密好的密文写到文件中,在"."之前插入字符串"_Enc"
            StringBuilder sb = new StringBuilder(fileNames[i]);
            String temp1 = String.valueOf(sb.insert(sb.indexOf("."), "_Enc"));
            //直接使用dirFilePath，此时已存值
            String newEncAbsolute = dirFilePath[i].substring(0, dirFilePath[i].lastIndexOf('\\')) + "\\DES\\" + temp1;
            writeFile(cipher,newEncAbsolute);
        }
        etime = System.nanoTime();
        //计算加密时间，加上单位“毫秒”
        label15.setText((etime - stime) / 10E6 +" 毫秒");
        //将列表中的内容存到字符串数组中
        fileCiphs = ciphs.toArray(new String[ciphs.size()]);
        for(int j = 0; j<fileCiphs.length; j++){
            System.out.println("密文数组"+j+"内容:"+fileCiphs[j]);
        }
    }

    public void dirOutput(){
        System.out.println("文件夹DES解密");
        stime = etime = 0;
        stime = System.nanoTime();
        for(int i = 0; i < fileCiphs.length; i++){
            plain = null; //先清空plain
            //DES解密得到明文
            plain = demo.decryption(fileCiphs[i],key);
            StringBuilder sb = new StringBuilder(fileNames[i]);
            String temp2 = String.valueOf(sb.insert(sb.indexOf("."), "_Dec"));
            //直接使用全局变量dirFilePath，此时已存值
            String newDecAbsolute = dirFilePath[i].substring(0, dirFilePath[i].lastIndexOf('\\')) + "\\DES\\" + temp2;
            writeFile(plain,newDecAbsolute);
        }
        etime = System.nanoTime();
        //计算解密时间，加上单位“毫秒”
        label16.setText((etime - stime) / 10E6 +" 毫秒");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        textField2 = new JTextField();
        label5 = new JLabel();
        textField4 = new JTextField();
        label4 = new JLabel();
        label13 = new JLabel();
        button1 = new JButton();
        label8 = new JLabel();
        label14 = new JLabel();
        button2 = new JButton();
        label3 = new JLabel();
        textField3 = new JTextField();
        button6 = new JButton();
        label17 = new JLabel();
        button3 = new JButton();
        label18 = new JLabel();
        button7 = new JButton();
        label6 = new JLabel();
        label11 = new JLabel();
        button4 = new JButton();
        label7 = new JLabel();
        label15 = new JLabel();
        button8 = new JButton();
        label9 = new JLabel();
        label12 = new JLabel();
        button5 = new JButton();
        label10 = new JLabel();
        label16 = new JLabel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(
            0,0,0,0), "JFor\u006dDesi\u0067ner \u0045valu\u0061tion",javax.swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder
            .BOTTOM,new java.awt.Font("Dia\u006cog",java.awt.Font.BOLD,12),java.awt.Color.
            red),dialogPane. getBorder()));dialogPane. addPropertyChangeListener(new java.beans.PropertyChangeListener(){@Override public void propertyChange(java.
            beans.PropertyChangeEvent e){if("bord\u0065r".equals(e.getPropertyName()))throw new RuntimeException();}});
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new MigLayout(
                    "insets dialog,hidemode 3",
                    // columns
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]",
                    // rows
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]"));

                //---- label1 ----
                label1.setText("\u8bf7\u8f93\u5165\u660e\u6587\uff1a");
                contentPanel.add(label1, "cell 0 1 3 1");
                contentPanel.add(textField1, "cell 1 1 12 1");

                //---- label2 ----
                label2.setText("\u5f97\u5230\u7684\u660e\u6587\uff1a");
                contentPanel.add(label2, "cell 17 1");
                contentPanel.add(textField2, "cell 18 1 22 1");

                //---- label5 ----
                label5.setText("\u8bf7\u8f93\u5165\u5bc6\u94a5\uff1a");
                contentPanel.add(label5, "cell 0 2");
                contentPanel.add(textField4, "cell 1 2 12 1");

                //---- label4 ----
                label4.setText("\u5b57\u7b26\u4e32\u52a0\u5bc6\u65f6\u95f4\uff1a");
                contentPanel.add(label4, "cell 17 2");

                //---- label13 ----
                label13.setText("0.00 \u6beb\u79d2");
                contentPanel.add(label13, "cell 20 2");

                //---- button1 ----
                button1.setText("DES\u52a0\u5bc6");
                contentPanel.add(button1, "cell 1 3 3 1");

                //---- label8 ----
                label8.setText("\u5b57\u7b26\u4e32\u89e3\u5bc6\u65f6\u95f4\uff1a");
                contentPanel.add(label8, "cell 17 3");

                //---- label14 ----
                label14.setText("0.00 \u6beb\u79d2");
                contentPanel.add(label14, "cell 20 3");

                //---- button2 ----
                button2.setText("DES\u89e3\u5bc6");
                contentPanel.add(button2, "cell 24 3");

                //---- label3 ----
                label3.setText("\u5f97\u5230\u7684\u5bc6\u6587\uff1a");
                contentPanel.add(label3, "cell 0 5");
                contentPanel.add(textField3, "cell 1 5 39 1");

                //---- button6 ----
                button6.setText("\u6253\u5f00\u6587\u4ef6");
                contentPanel.add(button6, "cell 0 7");

                //---- label17 ----
                label17.setText("\u6587\u4ef6\u540d");
                contentPanel.add(label17, "cell 1 7");

                //---- button3 ----
                button3.setText("\u6253\u5f00\u6587\u4ef6\u5939");
                contentPanel.add(button3, "cell 17 7");

                //---- label18 ----
                label18.setText("\u6587\u4ef6\u5939\u540d");
                contentPanel.add(label18, "cell 20 7");

                //---- button7 ----
                button7.setText("\u52a0\u5bc6\u6587\u4ef6");
                contentPanel.add(button7, "cell 0 8");

                //---- label6 ----
                label6.setText("\u6587\u4ef6\u52a0\u5bc6\u65f6\u95f4\uff1a");
                contentPanel.add(label6, "cell 1 8");

                //---- label11 ----
                label11.setText("0.00 \u6beb\u79d2");
                contentPanel.add(label11, "cell 4 8");

                //---- button4 ----
                button4.setText("\u52a0\u5bc6\u6587\u4ef6\u5939");
                contentPanel.add(button4, "cell 17 8");

                //---- label7 ----
                label7.setText("\u6587\u4ef6\u5939\u52a0\u5bc6\u65f6\u95f4\uff1a");
                contentPanel.add(label7, "cell 20 8");

                //---- label15 ----
                label15.setText("0.00 \u6beb\u79d2");
                contentPanel.add(label15, "cell 24 8");

                //---- button8 ----
                button8.setText("\u89e3\u5bc6\u6587\u4ef6");
                contentPanel.add(button8, "cell 0 9");

                //---- label9 ----
                label9.setText("\u6587\u4ef6\u89e3\u5bc6\u65f6\u95f4\uff1a");
                contentPanel.add(label9, "cell 1 9");

                //---- label12 ----
                label12.setText("0.00 \u6beb\u79d2");
                contentPanel.add(label12, "cell 4 9");

                //---- button5 ----
                button5.setText("\u89e3\u5bc6\u6587\u4ef6\u5939");
                contentPanel.add(button5, "cell 17 9");

                //---- label10 ----
                label10.setText("\u6587\u4ef6\u5939\u89e3\u5bc6\u65f6\u95f4\uff1a");
                contentPanel.add(label10, "cell 20 9");

                //---- label16 ----
                label16.setText("0.00 \u6beb\u79d2");
                contentPanel.add(label16, "cell 24 9");
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JTextField textField2;
    private JLabel label5;
    private JTextField textField4;
    private JLabel label4;
    private JLabel label13;
    private JButton button1;
    private JLabel label8;
    private JLabel label14;
    private JButton button2;
    private JLabel label3;
    private JTextField textField3;
    private JButton button6;
    private JLabel label17;
    private JButton button3;
    private JLabel label18;
    private JButton button7;
    private JLabel label6;
    private JLabel label11;
    private JButton button4;
    private JLabel label7;
    private JLabel label15;
    private JButton button8;
    private JLabel label9;
    private JLabel label12;
    private JButton button5;
    private JLabel label10;
    private JLabel label16;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
