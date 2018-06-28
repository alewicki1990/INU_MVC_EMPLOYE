package mvc.employee;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mvc.employee.dal.OraConn;
import mvc.employee.dal.DepartmentsDAL;
import mvc.employee.dal.EmployeesDAL;
import mvc.employee.model.Department;
import mvc.employee.view.AlertBox;
import mvc.employee.view.EmployeeController;
import mvc.employee.view.MainController;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {

		String oracleURL = "jdbc:oracle:thin:@localhost:1521:xe";
		String userName = "hr";
		String userPass = "PWPass";

		if (OraConn.open(oracleURL, userName, userPass) > 0)
			return;
		
		ViewLoader<BorderPane, Object> viewLoader = new ViewLoader<BorderPane, Object>("view/Main.fxml");//("/mvc/employee/view/Main.fxml");
		BorderPane borderPane = viewLoader.getLayout();
		Scene scene = new Scene(borderPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Pracownicy");
		primaryStage.setOnHiding(e -> primaryStage_Hiding(e));
		primaryStage.setOnCloseRequest(e -> primaryStage_CloseRequest(e));
		primaryStage.show();


		ViewLoader<AnchorPane, EmployeeController> viewLoaderEmp = new ViewLoader<AnchorPane, EmployeeController>(
				"view/EmployeeData.fxml");
		AnchorPane anchorPaneEmp = viewLoaderEmp.getLayout();
		borderPane.setCenter(anchorPaneEmp);
		((MainController) viewLoader.getController()).setStage(primaryStage);
		EmployeeController empControler = viewLoaderEmp.getController();
		((MainController) viewLoader.getController()).setStage(primaryStage);
		((MainController) viewLoader.getController()).setEmployeeFXML(viewLoaderEmp);
		// ÅºrÃ³dÅ‚o danych
		
		empControler.setEmployees(new EmployeesDAL().getEmployees());

	}

	public static void main(String[] args) {
		launch(args);
	}

	int OraDbConnect() {
		int ret = OraConn.open("jdbc:oracle:thin:@localhost:1521:xe", "hr", "PWPass");
		if (ret > 0) {
			AlertBox.showAndWait(AlertType.ERROR, "Nawi¹zanie po³¹czenia z baz¹ danych",
					"Nieprawid³owy u¿ytkownik lub has³o.\n" + "[" + OraConn.getErr() + "] " + OraConn.getErrMsg());
		}
		return ret;
	}

	void primaryStage_Hiding(WindowEvent e) {
		OraConn.close();
	}

	void primaryStage_CloseRequest(WindowEvent e) {
		Optional<ButtonType> result = AlertBox.showAndWait(AlertType.CONFIRMATION, "Koñczenie pracy",
				"Czy chcesz	zamkn¹æ	aplikacjê?");
		if (result.orElse(ButtonType.CANCEL) != ButtonType.OK)
			e.consume();
	}

}
