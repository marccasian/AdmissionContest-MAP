package GUI;

import domain.ValidatorException;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import domain.Candidat;
import domain.Inscriere;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import controller.ControllerCandidat;
import controller.ControllerInscrieri;
import controller.ControllerSectie;
import utils.Observable;
import utils.Observer;

@SuppressWarnings("unused")
public class InscrieriController implements Observer<Inscriere> {
    private ObservableList<Inscriere> model;

    ControllerInscrieri service;
    ControllerSectie serviceS;
    ControllerCandidat serviceC;
    
    @FXML
    private TableView<Inscriere> inscrieriTable;
    @FXML
    private TableColumn<Inscriere, String> candidatColumn;
    @FXML
    private TableColumn<Inscriere, String> sectieColumn;
    @FXML
    private Pagination pagination ;
    @FXML
    private TextField itemsNrTop;
    
    final private int rowsPerPage = 15;

    
	public InscrieriController(){
      
    }

	public void setService(ControllerInscrieri inscriereService, ControllerCandidat servic, ControllerSectie servis) {
        this.service=inscriereService;
        this.serviceS=servis;
        this.serviceC=servic;
        this.model= FXCollections.observableArrayList((Collection<? extends Inscriere>)inscriereService.getInscrieri());
        this.pagination.setPageCount(getNrPages());
        this.pagination.setCurrentPageIndex(0);
        updateTable(inscrieriTable, 0);
       
    }
	
	private void updateTable(TableView<Inscriere> table, Integer index) {
		this.pagination.setPageCount(getNrPages());
        int start = index * rowsPerPage ;
        int end = start + rowsPerPage;
        if (start + rowsPerPage > this.model.size()){
        	end = this.model.size();
        }
        table.getItems().setAll(FXCollections.observableArrayList(this.model.subList(start, end)));
    }
	
	 /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	candidatColumn.setCellValueFactory(new PropertyValueFactory<>("numec"));
    	sectieColumn.setCellValueFactory(new PropertyValueFactory<>("numes"));//merge fara, defapt nu merge :)))
    	pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> updateTable(inscrieriTable, newIndex.intValue()));
    }
    
    private int getNrPages(){
    	int last_pag = 0;
        if (this.model.size() % rowsPerPage != 0){
        	last_pag = 1;
        }
        return this.model.size() / rowsPerPage + last_pag;
    }
    
    private void refreshTable(){
    	this.pagination.setPageCount(getNrPages());
        updateTable(inscrieriTable, pagination.getCurrentPageIndex());  
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void update(Observable<Inscriere> observable) {
		ControllerInscrieri s=(ControllerInscrieri)observable;
            model.setAll((List)s.getInscrieri());
            refreshTable();
    }

    public void handleAddInscriere()
    {
    	showInscriereSaveDialog();
    }
    
    public void handleTop3()
    {
    	showTop3Dialog();
    }

    static void showMessage(Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.showAndWait();
    }

    static void showErrorMessage(String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.setTitle("Mesaj eroare");
        message.setContentText(text);
        message.showAndWait();
    }

    public void handleDeleteInscriere(ActionEvent actionEvent) {		
		try{
			Inscriere c =inscrieriTable.getSelectionModel().getSelectedItem();
			if (null != c){        	
				Inscriere sters = service.deleteI(c);
				if (sters != null) { 
					showMessage(Alert.AlertType.INFORMATION, "Inscriere stearsa!", "Inscriereul "+sters.toString()+" a fost stearsa cu succes!");
				}
				else{
					showErrorMessage("Stergerea inscrierii a esuat!");
				}
			}
			else{
				showErrorMessage("Nu ati selectat nici o inscriere!");
			}
					
		} catch (ValidatorException | sun.security.validator.ValidatorException e1) {
            showErrorMessage(e1.getMessage());
        }
		refreshTable();
    }
    
    public void save(ActionEvent e){
    	service.saveRepo();
    }
    
    public void showInscriereSaveDialog() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InscrieriController.class.getResource("AddInscriereView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Inscriere");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddInscriereController addInscriereController= loader.getController();
            addInscriereController.setService(service,serviceC,serviceS,dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
    
    public void showTop3Dialog() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InscrieriController.class.getResource("Top3View.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Top 3 sectii");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            RaportController raportController= loader.getController();
            Integer nr = getTopNrItems();
            if (nr > 0){
            	raportController.setService(service,serviceC,serviceS,dialogStage,nr);
            	dialogStage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
    
    private Integer getTopNrItems() {
    	String nrs = itemsNrTop.getText();
    	Integer nr = -1;
    	try{
    		nr = Integer.parseInt(nrs);
    		if (nr >= 0){
    			return nr;
    		}
    		throw new NumberFormatException();
    	}catch (NumberFormatException e) {
        	showErrorMessage("Numarul de intrari in raport trebuie sa fie numar natural!");
        }
    	
    	return nr;
	}

}
