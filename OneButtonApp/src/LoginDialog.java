import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class LoginDialog {
	private static Text UsernameText;
	private static Text PasswordText;
	private static Shell shlWelcomeToUse;
	private static Display display;
	private OBAController controller;

	public LoginDialog() {
		controller = OBAController.getInstance();
		display = new Display();
		shlWelcomeToUse = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);

		shlWelcomeToUse.setText("Welcome to VCL One Button App");
		shlWelcomeToUse.setSize(500, 350);
		shlWelcomeToUse.setLayout(new FormLayout());

		// center the dialog screen to the monitor
		Rectangle bounds = display.getBounds();
		Rectangle rect = shlWelcomeToUse.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlWelcomeToUse.setLocation(x, y);

		Label lblLogo = new Label(shlWelcomeToUse, SWT.NONE);
		FormData fd_lblLogo = new FormData();
		fd_lblLogo.bottom = new FormAttachment(0, 96);
		fd_lblLogo.right = new FormAttachment(0, 500);
		fd_lblLogo.top = new FormAttachment(0);
		fd_lblLogo.left = new FormAttachment(0);
		lblLogo.setLayoutData(fd_lblLogo);
		lblLogo.setText("New Label");
		lblLogo.setImage(SWTResourceManager.getImage("./vcl_logo.png"));

		Label lblUsername = new Label(shlWelcomeToUse, SWT.NONE);
		FormData fd_lblUsername = new FormData();
		fd_lblUsername.top = new FormAttachment(lblLogo, 35);
		fd_lblUsername.left = new FormAttachment(0, 70);
		lblUsername.setLayoutData(fd_lblUsername);
		lblUsername.setText("Username: ");

		UsernameText = new Text(shlWelcomeToUse, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(lblUsername, 255, SWT.RIGHT);
		fd_text.top = new FormAttachment(lblLogo, 32);
		fd_text.left = new FormAttachment(lblUsername, 6);
		UsernameText.setLayoutData(fd_text);

		Label lblPassword = new Label(shlWelcomeToUse, SWT.NONE);
		lblPassword.setText(" Password: ");
		FormData fd_lblPassword = new FormData();
		fd_lblPassword.top = new FormAttachment(lblUsername, 12);
		fd_lblPassword.left = new FormAttachment(lblUsername, 0, SWT.LEFT);
		lblPassword.setLayoutData(fd_lblPassword);

		PasswordText = new Text(shlWelcomeToUse, SWT.PASSWORD | SWT.BORDER);
		FormData fd_text2 = new FormData();
		fd_text2.right = new FormAttachment(UsernameText, 0, SWT.RIGHT);
		fd_text2.left = new FormAttachment(UsernameText, 0, SWT.LEFT);
		fd_text2.top = new FormAttachment(UsernameText, 6);
		PasswordText.setLayoutData(fd_text2);

		final Button btnCheckButton = new Button(shlWelcomeToUse, SWT.CHECK);
		FormData fd_btnCheckButton = new FormData();
		fd_btnCheckButton.top = new FormAttachment(PasswordText, 6);
		fd_btnCheckButton.left = new FormAttachment(UsernameText, 0, SWT.LEFT);
		btnCheckButton.setLayoutData(fd_btnCheckButton);
		btnCheckButton.setText("Save Password");

		Button btnExitButton = new Button(shlWelcomeToUse, SWT.NONE);
		btnExitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!shlWelcomeToUse.isDisposed()) {
					shlWelcomeToUse.dispose();
				}
				display.dispose();
				System.exit(0);
			}
		});
		FormData fd_btnExitButton = new FormData();
		fd_btnExitButton.width = 100;
		fd_btnExitButton.right = new FormAttachment(100, -10);
		fd_btnExitButton.bottom = new FormAttachment(100, -10);
		btnExitButton.setLayoutData(fd_btnExitButton);
		btnExitButton.setText("Exit");

		Button btnLoginButton = new Button(shlWelcomeToUse, SWT.NONE);
		btnLoginButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Get username and password
				String username = UsernameText.getText();
				String password = PasswordText.getText();

				if (username.length() <= 0 || password.length() <= 0) {
					MessageBox dialog = new MessageBox(shlWelcomeToUse,
							SWT.ICON_ERROR | SWT.OK);
					dialog.setText("No Username or Password Found!");
					dialog.setMessage("Please enter correct username and password.");
					dialog.open();
					return;
				}
				// We need to use VCL XML RPC to check whether the
				// username/password are correct
				OBALogic oba_inst = new OBALogic(username, password);
				if (!oba_inst.loginCheck()) {
					MessageBox dialog = new MessageBox(shlWelcomeToUse,
							SWT.ICON_ERROR | SWT.OK);
					dialog.setText("Login fail!");
					dialog.setMessage("Login fail! Please enter correct username and password.");
					dialog.open();
					return;
				} else {
					// if user chose to store his password
					if (btnCheckButton.getSelection()) {
						controller.savePasswd(username, password);
					}

					// Now close this login dialog
					if (!shlWelcomeToUse.isDisposed()) {
						shlWelcomeToUse.dispose();
					}
					display.dispose();
					controller.setVCLConnector(username, password);
					// Start the main OBA GUI
					controller.showMainOBA();
				}
			}
		});

		FormData fd_btnLoginButton = new FormData();
		fd_btnLoginButton.bottom = new FormAttachment(100, -10);
		fd_btnLoginButton.width = 100;
		fd_btnLoginButton.right = new FormAttachment(btnExitButton, -10);
		btnLoginButton.setLayoutData(fd_btnLoginButton);
		btnLoginButton.setText("Login");

		shlWelcomeToUse.setDefaultButton(btnLoginButton);
	}

	public void show() {
		shlWelcomeToUse.open();
		shlWelcomeToUse.layout();
		while (!shlWelcomeToUse.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
