package dk.itu.bigm.editors.simulation_spec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import dk.itu.bigm.preferences.BigMPreferences;
import dk.itu.bigm.utilities.ui.UI;
import ss.pku.utils.dotRelated.GraphViz;
import sun.text.resources.cldr.en.FormatData_en_US_POSIX;

import org.bigraph.bigsim.ReachChecker;

public class ExportResults {
	private String results;
	
	public ExportResults(String results) {
		this.results = results;
	}
	
	public void SaveDotFile() {
//		FileDialog d = new FileDialog(getShell(),
//				SWT.SAVE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
//			d.setText("Save As");
//			
//			String filename = d.open();
//			if (filename != null) {
//				try {
//					FileWriter fw = new FileWriter(filename);
//					fw.write(resultsText.getText());
//					fw.close();
//					setMessage("Saved to \"" + filename + "\".",
//						IMessageProvider.INFORMATION);
//				} catch (IOException x) {
//					setErrorMessage(x.getLocalizedMessage());
//				}
//			}
	}
	
	public String GetFrom(String fileContent) {
		PrintWriter out = null;
		
		String result = "";
		String content = "";
		String params = "";
		try {
			content = URLEncoder.encode(fileContent, "utf-8");
			params = URLEncoder.encode("str", "utf-8") + "=" + content;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Properties pps = new Properties();
		try {
			pps.load(new FileInputStream("dot.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = pps.getProperty("url");

		BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // post request 
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod(pps.getProperty("method", "POST"));         
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
            		"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.setDoInput(true); // 读取数据  
            connection.setDoOutput(true);

//			methond one
//            byte[] b = params.toString().getBytes();
//            connection.getOutputStream().write(b, 0, b.length);
//            connection.getOutputStream().flush();
//            connection.getOutputStream().close();

//			method two
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(params);
            // flush输出流的缓冲
            out.flush();            

            // 建立实际的连接
            connection.connect();
//            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;            
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
            return "sending get request using too much time " + e;
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
	}

	public void PostToGetDot(String fileContent) {
//	    FileWriter fw = null;
//	    System.out.println("post request");
//	    Date now = new Date();
//	    String postFileName = Long.toString(now.getTime());
//	    String postFileRoot = "D://bigraphtools//"+ postFileName + ".bgm";
//	    String remoteAddress = "http://192.168.1.146:9000/readBgm";
//													//	    String remoteAddress = "https://www.baidu.com";
////	    String remoteAddress = "http://localhost:3000";
//	    
//	    String dotFileName = "";
//	    
//	    try {
//	    	fw = new FileWriter(postFileRoot);
//	        fw.write(fileContent);   
//            fw.close();   
//            
//
//	    } catch (IOException e) {
//	      // TODO Auto-generated catch block
//	      e.printStackTrace();
//	    }   
// 
//	    File postFile = new File(postFileRoot);
//	    PostMethod filePost = new PostMethod(remoteAddress);
//	    HttpClient client = new HttpClient();
//
//	    try {
//	       // 通过以下方法可以模拟页面参数提交
////	       filePost.setParameter("userName", "Kevin");
////	       filePost.setParameter("passwd", "123");
//
//	       Part[] parts = { new FilePart(postFile.getName(), postFile) };
//	       
////	       filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
//	       
//	       client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
//	       
//	       int status = client.executeMethod(filePost);
//	       filePost.getResponseBody();
//	       File storeFile = new File(dotFileName);
//	       FileOutputStream output = new FileOutputStream(storeFile);
//
//	       // 得到网络资源的字节数组,并写入文件
//	       output.write(filePost.getResponseBody());
////	       filePost.getResponseBody();
//	       if (status == HttpStatus.SC_OK) {
//	           System.out.println("上传成功");
//	       } else {
//	           System.out.println("上传失败");
//	       }
//	    } catch (Exception ex) {
//	       ex.printStackTrace();
//	    } finally {
//	       filePost.releaseConnection();
//	    }
	    
	}
	
	public class ToolSelectorDialog extends Dialog {
		private Combo tools;
		
		private String results;
		
		public ToolSelectorDialog(Shell parentShell, String results) {
			super(parentShell);
			this.results = results;
		}

		private void setProgress(boolean progress) {
			UI.setEnabled(progress, tools);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite c = new Composite(parent, SWT.NONE);
			c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			GridLayout gl = new GridLayout(1, true);
			gl.marginLeft = gl.marginRight = gl.marginTop = 10;
			c.setLayout(gl);
			
			tools = new Combo(c, SWT.NONE);
			tools.setLayoutData(
					new GridData(SWT.FILL, SWT.TOP, true, false));
			tools.setItems(BigMPreferences.getExternalTools());
			if (tools.getItemCount() > 0) {
				tools.select(0);
				setProgress(true);
			} else setProgress(false);
			
			return tools;
		}

		@Override
		protected void buttonPressed(int buttonId) {
			if (buttonId == IDialogConstants.OK_ID) {
				new ProcessDialog(getShell(),
						new ProcessBuilder(tools.getText().split(" ")))
					.setInput(results).open();
				okPressed();
			} else if (buttonId == IDialogConstants.CANCEL_ID) {
				cancelPressed();
			}
		}
	}

	public class ExportResultsDialog extends TitleAreaDialog {
		private Text resultsText;

		public ExportResultsDialog(Shell parentShell) {
			super(parentShell);
			//!TODO unremak
//			ReachChecker rc = new ReachChecker(results);
//			String rcresult = rc.check();
//			System.out.println(rcresult);
//			if(!rcresult.equals("Unreached Reaction Rules:\n")){
//				MessageDialog.openInformation(parentShell, "Unreached Reaction Rules QAQ!", rcresult);
//			}
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite c = new Composite(parent, SWT.NONE);
			c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			GridLayout gl = new GridLayout(1, true);
			gl.marginLeft = gl.marginRight = gl.marginTop = 10;
			c.setLayout(gl);
			
			resultsText =
				new Text(c, SWT.MULTI | SWT.BORDER | SWT.WRAP |
						SWT.V_SCROLL);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.widthHint = 400;
			gd.heightHint = 500;
			resultsText.setLayoutData(gd);
			resultsText.setText(results);
			
			return c;
		}
		
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.OK_ID, "OK", true);
			createButton(parent, BasicCommandLineInteractionManager.TO_TOOL_ID, BasicCommandLineInteractionManager.TO_TOOL_LABEL, false);
			createButton(parent, BasicCommandLineInteractionManager.SAVE_ID, BasicCommandLineInteractionManager.SAVE_LABEL, false);
			createButton(parent, BasicCommandLineInteractionManager.COPY_ID, BasicCommandLineInteractionManager.COPY_LABEL, false);
			createButton(parent, BasicCommandLineInteractionManager.TO_BIGSIM_ID, BasicCommandLineInteractionManager.TO_BIGSIM_LABEL, false);
			createButton(parent, BasicCommandLineInteractionManager.GET_PNG_ID, BasicCommandLineInteractionManager.GET_PNG_LABEL, false);
		}

		@Override
		protected void buttonPressed(int buttonId) {
			if (buttonId == BasicCommandLineInteractionManager.TO_TOOL_ID) {
				new ToolSelectorDialog(getShell(), resultsText.getText()).
					open();
			} else if (buttonId == BasicCommandLineInteractionManager.SAVE_ID) {
				FileDialog d = new FileDialog(getShell(),
					SWT.SAVE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
				d.setText("Save As");
				
				String filename = d.open();
				if (filename != null) {
					try {
						FileWriter fw = new FileWriter(filename);
						fw.write(resultsText.getText());
						fw.close();
						setMessage("Saved to \"" + filename + "\".",
							IMessageProvider.INFORMATION);
					} catch (IOException x) {
						setErrorMessage(x.getLocalizedMessage());
					}
				}
			} else if (buttonId == BasicCommandLineInteractionManager.COPY_ID) {
				UI.setClipboardText(resultsText.getText());
				setMessage("Copied to the clipboard.",
						IMessageProvider.INFORMATION);
			} else if (buttonId == BasicCommandLineInteractionManager.TO_BIGSIM_ID) {
				String dotContent = GetFrom(resultsText.getText());
				// save dot file
				FileDialog d = new FileDialog(getShell(),
						SWT.SAVE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
					d.setText("Save Dot File As");
					String filename = d.open();
					if (filename != null) {
						try {
							FileWriter fw = new FileWriter(filename);
							fw.write(dotContent);
							fw.close();
							setMessage("Saved to \"" + filename + "\".",
								IMessageProvider.INFORMATION);
						} catch (IOException x) {
							setErrorMessage(x.getLocalizedMessage());
						}
					}
			} else if (buttonId == BasicCommandLineInteractionManager.GET_PNG_ID) {
				Properties pps = new Properties();
				try {
					pps.load(new FileInputStream("dot.properties"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Enumeration enum1 = pps.propertyNames();//得到配置文件的名字
				while(enum1.hasMoreElements()) {
				    String strKey = (String) enum1.nextElement();
				    String strValue = pps.getProperty(strKey);
				    System.out.println(strKey + "=" + strValue);
				}				
				
				
				String tempDir = pps.getProperty("tempDir");
				String executable = pps.getProperty("executable");
				GraphViz gv = new GraphViz(executable,tempDir);
				
				// from local
//				gv.addln(gv.start_graph());
//				gv.addln("A -> B;");
//				gv.addln("A -> C;");
//				gv.addln(gv.end_graph());
//				System.out.println(gv.getDotSource());
//				String dotContent = gv.getDotSource();
//				gv.increaseDpi();   // 106 dpi
				
				// from the remote 
				String bgmContent = resultsText.getText();
				String dotContent = GetFrom(bgmContent);
			
				
	            String type = pps.getProperty("type");
	            String representationType = pps.getProperty("representationType");
	            
				FileDialog d = new FileDialog(getShell(),
						SWT.SAVE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
					d.setText("Save PNG As");
					String filename = d.open();
					if (filename != null) {
						File out = new File(filename);
//						tempDir = "d:/bigraphtools/";
						
						gv.increaseDpi();

						gv.writeGraphToFile( gv.getGraph(dotContent, type, representationType), out);							
						
						setMessage("Saved to \"" + filename + "\".",
							IMessageProvider.INFORMATION);
					}
			} super.buttonPressed(buttonId);
		}

		@Override
		protected Control createButtonBar(Composite parent) {
			Control c = super.createButtonBar(parent);
			c.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
			return c;
		}

		@Override
		public void create() {
			super.create();
			
			setTitle("Export complete");
			setMessage("The document has been exported.");
		}
	}
}