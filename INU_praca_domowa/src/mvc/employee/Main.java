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
		// źródło danych
		
		empControler.setEmployees(new EmployeesDAL().getEmployees());

	}

	public static void main(String[] args) {
		launch(args);
	}

	int OraDbConnect() {
		int ret = OraConn.open("jdbc:oracle:thin:@localhost:1521:xe", "hr", "PWPass");
		if (ret > 0) {
			AlertBox.showAndWait(AlertType.ERROR, "Nawi�zanie po��czenia z baz� danych",
					"Nieprawid�owy u�ytkownik lub has�o.\n" + "[" + OraConn.getErr() + "] " + OraConn.getErrMsg());
		}
		return ret;
	}

	void primaryStage_Hiding(WindowEvent e) {
		OraConn.close();
	}

	void primaryStage_CloseRequest(WindowEvent e) {
		Optional<ButtonType> result = AlertBox.showAndWait(AlertType.CONFIRMATION, "Ko�czenie pracy",
				"Czy chcesz	zamkn��	aplikacj�?");
		if (result.orElse(ButtonType.CANCEL) != ButtonType.OK)
			e.consume();
	}

}
