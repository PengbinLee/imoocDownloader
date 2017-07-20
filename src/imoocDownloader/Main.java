package imoocDownloader;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.JLabel;

public class Main {

	public static void main(String[] args) {
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	private static void createAndShowGUI() {
		
		// 得到屏幕的尺寸
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // 创建frame
        JFrame frame = new JFrame("慕课网视频下载器 V1.0");
        // 设置窗口布局
        frame.setLayout(new FlowLayout());
        // 设置窗口大小
        frame.setSize(500, 300);
        // 设置窗口位置
        frame.setLocation((screenSize.width - 500) / 2, (screenSize.height - 300) / 2);
        // 设置是否允许调节窗口大小
        frame.setResizable(false);
        // 设置关闭按钮的响应动作，关闭窗口时退出程序
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口可见性
        frame.setVisible(true);
        
        // 创建inputPanel
        JPanel inputPanel = new JPanel();
        // 设置大小
        inputPanel.setPreferredSize(new Dimension(500, 150));
        // 设置位置
        inputPanel.setLocation((screenSize.width - 500) / 2, (frame.getHeight() - 300) / 2);
        // 将panel添加到frame中
        frame.add(inputPanel);
        
        // 创建resultPanel
        JPanel resultPanel = new JPanel();
        // 设置大小
        resultPanel.setPreferredSize(new Dimension(500, 150));
        // 设置位置
        resultPanel.setLocation((screenSize.width - 500) / 2, (frame.getHeight() - 300) / 2);
        // 将panel添加到frame中
        frame.add(resultPanel);

        // 创建label
        JLabel label = new JLabel("课程地址：");
        // 将label添加到inputPanel中
        inputPanel.add(label);
        
        // 创建textArea
        JTextArea textArea = new JTextArea();
        // 禁止编辑
        textArea.setEditable(false);
        // 将resultLabel添加到resultPanel中
        resultPanel.add(textArea);
        
        // 创建textField		期望输入的是(http://www.imooc.com/video/3665)中的3665
        JTextField textField = new JTextField(25);
        // 将textField添加到inputPanel中
        inputPanel.add(textField);
        
        // 创建copyButton
        JButton copyButton = new JButton("复制");
        // 为copyButton添加点击事件
        copyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection result = new StringSelection(textArea.getText());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(result, result);
			}
		});
        // 将copyButton添加到resultPanel中
        resultPanel.add(copyButton);
        copyButton.setVisible(false);
        
        // 创建button
        JButton button = new JButton("获取下载链接");
        // 为button添加点击事件
        button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (textField.getText().equals("")) {
					createAndShowDialog(frame, "课程地址不能为空！");
				} else if (textField.getText().matches("^http://www.imooc.com/video/+[1-9]\\d*$") == false) {
					createAndShowDialog(frame, "课程地址不合法！");
				} else {
					String link = getDownloadLink(textField);
					textArea.setText(link);
					copyButton.setVisible(true);
				}
			}
		});
        // 将button添加到inputPanel中
        inputPanel.add(button); 
    }
	
	private static void createAndShowDialog(JFrame frame, String string) {
		JLabel label = new JLabel(string);
		JDialog dialog = new JDialog(frame, "", true);
		dialog.setLayout(new FlowLayout());
		dialog.setSize(150, 100);
		dialog.setLocation(frame.getLocation().x +(500 - 150) / 2, frame.getLocation().y + (300 - 100) / 2);
		dialog.add(label);
		dialog.setVisible(true);
	}
	
	private static String getDownloadLink(JTextField textField) {
		//http://www.imooc.com/course/ajaxmediainfo/?mid=12141&mode=flash
		String mid = textField.getText().substring(27);
		String url = "http://www.imooc.com/course/ajaxmediainfo/?mid=" + mid + "&mode=flash";
		String Strjson = null;
		StringBuilder json = new StringBuilder();
        URL oracle;
		try {
			oracle = new URL(url);
			URLConnection yc = oracle.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(),"UTF-8"));
	        String inputLine = null;
	        while ( (inputLine = in.readLine()) != null){
	            json.append(inputLine);
	        }
	        in.close();
	        Strjson = json.toString();
	        
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JSONway(Strjson);
	}
	
	private static String JSONway(String jsonString) {
		
		String link = null;
		JSONObject jsonObject =JSONObject.fromObject(jsonString);
		JSONObject data = jsonObject.getJSONObject("data");
		JSONObject result = data.getJSONObject("result");
		JSONArray mpath = result.getJSONArray("mpath");
		
		link = StringUtils.substringBefore(mpath.get(2).toString(), "?").replace("v2", "v1");
		
		return link;
	}
}
