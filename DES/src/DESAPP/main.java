package DESAPP;

import javax.swing.*;

public class main {
    //该程序采用Java Swing + JFormDesigner可视化组件进行开发，IDE为IntelliJ IDEA 2021.1
    public static void main(String[] args)
    {
        //JFrame指一个窗口，构造方法的参数为窗口标题
        JFrame frame  = new DESUI(); //多态
        //设置窗口标题
        frame.setTitle("DES-GUI");
        //设置窗口大小
        frame.setSize(830,355);
        //固定窗口大小
        frame.setResizable(false);
        //当关闭窗口时，退出整个程序
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //显示窗口
        frame.setVisible(true);
    }
}
