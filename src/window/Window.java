package window;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import scraper.Scraper;

import org.eclipse.swt.widgets.Button;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;

public class Window {

	protected Shell shell;

	public static final String title = "Scrapminator";
	public static String tag = "";
	//public static final String[] tags = { "h1", "h2", "h3", "h4", "h5", "h6", "p", "ul" };
	public static String url = "https://mansiondominator.wordpress.com/";
	List<String> scrapedData;
	public static boolean removeEmptyLines;
	public static Scraper scraper;
	private Text txtUrl;
	private Text txtTag;
	private Text txtScrap;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		scraper = new Scraper();
		removeEmptyLines = false;
		try {
			Window window = new Window();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(676, 300);
		shell.setText(title);
		shell.setLayout(new GridLayout(1, false));

		Group outerGroup = new Group(shell, SWT.NONE);
		outerGroup.setLayout(new FillLayout(SWT.VERTICAL));
		outerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		outerGroup.setText("Group");

		Composite inputData = new Composite(outerGroup, SWT.NONE);
		;

		txtUrl = new Text(inputData, SWT.BORDER);
		txtUrl.setLocation(41, 7);
		txtUrl.setSize(243, 21);
		txtUrl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				url = txtUrl.getText();
			}
		});
		txtUrl.setText(url);

		Label lblUrl = new Label(inputData, SWT.NONE);
		lblUrl.setLocation(10, 10);
		lblUrl.setSize(25, 15);
		lblUrl.setText("URL");

		/*Combo combo = new Combo(inputData, SWT.NONE);
		combo.setLocation(339, 7);
		combo.setSize(43, 23);
		combo.setItems(tags);
		combo.select(0);*/
		
		txtTag = new Text(inputData, SWT.BORDER);
		txtTag.setLocation(339, 7);
		txtTag.setSize(43, 23);
		txtTag.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				tag = txtTag.getText();
			}
		});
		txtTag.setText(tag);
		

		Button btnScrap = new Button(inputData, SWT.NONE);
		btnScrap.setLocation(388, 5);
		btnScrap.setSize(63, 25);
		btnScrap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					scrapedData = scraper.scrapUrl(url, tag, removeEmptyLines);
					if (scrapedData.isEmpty()) {
						txtScrap.setText("*****Empty*****");
					} else {
						txtScrap.setText(formatData(scrapedData));
					}
				} catch (IOException e1) {
					System.out.println(e1);
					JOptionPane.showMessageDialog(null, "No ha sido posible scrapear el contenido");
					e1.printStackTrace();
				}
			}
		});
		btnScrap.setText("Scrap");

		Button btnRemoveEmptyLines = new Button(inputData, SWT.CHECK);
		btnRemoveEmptyLines.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeEmptyLines = !removeEmptyLines;
			}
		});
		btnRemoveEmptyLines.setBounds(10, 34, 128, 16);
		btnRemoveEmptyLines.setText("Borrar líneas vacías");

		Label lblTag = new Label(inputData, SWT.NONE);
		lblTag.setBounds(290, 10, 43, 15);
		lblTag.setText("Etiqueta");

		Button btnScraphtml = new Button(inputData, SWT.NONE);
		btnScraphtml.setBounds(559, 5, 75, 25);
		btnScraphtml.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					// scrapedData = scraper.scrapUrl(url);
					txtScrap.setText(scraper.scrapUrl(url));
				} catch (IOException e1) {
					System.out.println(e1);
					JOptionPane.showMessageDialog(null, "No ha sido posible scrapear el contenido");
					e1.printStackTrace();
				}
			}
		});
		btnScraphtml.setText("ScrapHTML");

		txtScrap = new Text(outerGroup, SWT.WRAP | SWT.BORDER);

		Composite exportPanel = new Composite(outerGroup, SWT.NONE);

		Button btnExportCsv = new Button(exportPanel, SWT.NONE);
		btnExportCsv.setBounds(554, 43, 80, 25);
		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exportToCsv();
			}
		});
		btnExportCsv.setText("Exportar CSV");
		
		Button btnCleanUp = new Button(exportPanel, SWT.NONE);
		btnCleanUp.setBounds(10, 43, 75, 25);
		btnCleanUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cleanUp();
			}
		});
		btnCleanUp.setText("Clean up");

	}

	protected void cleanUp() {
		url = "";
		tag = "";
		txtUrl.setText(url);
		txtTag.setText(tag);
		txtScrap.setText("");
	}

	private String formatData(List<String> scrapedData) {
		String text = "";
		for (String data : scrapedData) {
			text = text.concat(data + "\n");
		}
		return text;
	}

	protected void exportToCsv() {
		/*try {
			//scraper.exportToCsv(scrapedData, url);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/

	}
}