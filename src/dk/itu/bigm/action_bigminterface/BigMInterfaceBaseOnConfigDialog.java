package dk.itu.bigm.action_bigminterface;

import java.awt.FileDialog;
import java.awt.Frame;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.rqm.util.RQMUtil;

public class BigMInterfaceBaseOnConfigDialog extends TitleAreaDialog {

	private Button browseDM; 
	private int BROWSE_DM_ID = 1000;// bigraph data model file browse

	public BigMInterfaceBaseOnConfigDialog(Shell shell) {
		// TODO Auto-generated constructor stub
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText("Upload testcases");
		setTitle("BigM");
		setMessage("Testing and Analysis -> Upload testcases");

		Composite topComp = new Composite(parent, SWT.NONE);
		topComp.setLayout(new GridLayout());
		topComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group bigDataGen = new Group(topComp, SWT.NONE);
		bigDataGen.setText("Upload testcases");
		bigDataGen.setLayout(new GridLayout(4, false));
		bigDataGen.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(bigDataGen, SWT.NONE).setText("Whether to upload the testcases");
		browseDM = createButton(bigDataGen, BROWSE_DM_ID, "Upload...", false);

		return parent;
	}

	public void buttonPressed(int buttonId) {
		if (buttonId == BROWSE_DM_ID) {
/*			browseDM.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					RQMUtil util = new RQMUtil("config.txt");
					util.createTestCase(
							"title2",
							"C:\\Users\\Rui\\Desktop\\RQMUrlUtil\\SmartAirport.path",
							"C:\\Users\\Rui\\Desktop\\RQMUrlUtil\\SmartAirport.data");
				}
			});*/
			RQMUtil util = new RQMUtil("D:\\eclipse_kepler\\BIGM\\BigM-master\\plugins\\dk.itu.big_red\\src\\config.txt");
			//RQMUtil util = new RQMUtil("bin\\config.txt");
			util.createTestCase(
					"title2",
					"C:\\Users\\Rui\\Desktop\\RQMUrlUtil\\SmartAirport.path",
					"C:\\Users\\Rui\\Desktop\\RQMUrlUtil\\SmartAirport.data");
			
		}
	}
}