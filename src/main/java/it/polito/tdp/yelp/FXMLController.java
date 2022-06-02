/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Giornalista;
import it.polito.tdp.yelp.model.Model;
import it.polito.tdp.yelp.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader
    @FXML // fx:id="btnUtenteSimile"
    private Button btnUtenteSimile; // Value injected by FXMLLoader
    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader
    @FXML // fx:id="txtX2"
    private TextField txtX2; // Value injected by FXMLLoader
    @FXML // fx:id="cmbAnno"
    private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader
    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader
    @FXML // fx:id="cmbUtente"
    private ComboBox<User> cmbUtente; // Value injected by FXMLLoader
    @FXML // fx:id="txtX1"
    private TextField txtX1; // Value injected by FXMLLoader
    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) 
    {
    	String n = this.txtN.getText();
    	int min;
    	
    	try
    	{
    		min = Integer.parseInt(n);
    	}
    	catch(NumberFormatException e)
    	{
    		e.printStackTrace();
    		this.txtResult.setText("ERRORE! Inserisci un numero minimo di recensioni (numero intero)!");
    		return;
    	}
    	
    	Integer anno = this.cmbAnno.getValue();
    	
    	if(anno == null)
    	{
    		this.txtResult.setText("ERRORE! Seleziona un anno!");
    		return;
    	}
    	
    	String msg = model.creaGrafo(min, anno);
    	
    	this.txtResult.setText(msg);
    	
    	this.cmbUtente.getItems().clear();
    	this.cmbUtente.getItems().addAll(model.getVertici());
    }

    @FXML
    void doUtenteSimile(ActionEvent event) 
    {
    	User user = this.cmbUtente.getValue();
    	
    	if(user == null)
    	{
    		this.txtResult.setText("ERRORE! Seleziona un utente!");
    		return;
    	}
    	
    	List<User> simili = model.getSimili(user);
    	
    	this.txtResult.setText("Utenti simili a " + user.toString() + ":\n\n");
    	
    	for(User u: simili)
    	{
    		this.txtResult.appendText(u.toString() + "\n");
    	}
    }
    
    @FXML
    void doSimula(ActionEvent event) 
    {
    	Integer numVertici = model.nVertici();
    	Integer x1 = null;
    	Integer x2 = null;
    	
    	try
    	{
    		x1 = Integer.parseInt(this.txtX1.getText());
    		x2 = Integer.parseInt(this.txtX2.getText());
    	}
    	catch (NumberFormatException e)
    	{
    		e.printStackTrace();
    		this.txtResult.setText("ERRORE! Inserisci x1 e x2 in formato numerico!");
    		return;
    	}
    	
    	if(x2 > numVertici)
    	{
    		this.txtResult.setText("ERRORE! x2 deve essere minore del numero di User del grafo = " +numVertici);
    		return;
    	}
    	
    	if(x1 > x2)
    	{
    		this.txtResult.setText("ERRORE! x1 deve essere minore di x2!");
    		return;
    	}
    	
    	model.simula(x1, x2);
    	
    	List<Giornalista> giornalisti = model.getGiornalisti();
    	Integer numGiorni = model.getNumeroGiorniSimulazione();
    	
    	this.txtResult.setText("Simulazione effettuata con successo!\n\n");
    	this.txtResult.appendText(String.format("%d intervistatori <--> %d utenti <--> %d giorni\n\n",
    			x1, x2, numGiorni));
    	
    	this.txtResult.appendText("Elenco intervistatore <-> numero intervistati:\n");
    	
    	for(Giornalista g: giornalisti)
    	{
    		this.txtResult.appendText(g.toString());
    	}
    }
    

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() 
    {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnUtenteSimile != null : "fx:id=\"btnUtenteSimile\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX2 != null : "fx:id=\"txtX2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbUtente != null : "fx:id=\"cmbUtente\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX1 != null : "fx:id=\"txtX1\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

        for(int anno=2005; anno<=2013; anno++)
        {
        	this.cmbAnno.getItems().add(anno);
        }
    }
    
    public void setModel(Model model) 
    {
    	this.model = model;
    }
}
