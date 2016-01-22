package pcgabssys;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class Controller implements Initializable {

    @FXML
    private TableView<ObservableList<Object>> tableview;
    @FXML
    private Pane pane;
    @FXML
    private Button btnupdate;
    @FXML
    private Button btninsert;
    @FXML
    private Button btndelete;

    private String tablename = null;


    private  ProvideDB providedb = null;

    @Override
    public final void initialize(final URL url,final ResourceBundle rbdl) {
        this.tablename="users";
        this.tableview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.tableview.setEditable(true);
        this.providedb = new ProvideDB();
        this.providedb.maptable(this.tableview, this.pane,this.tablename);
   }

    @FXML
    private void tableClicked(){
        final ObservableList<Object> row = this.tableview.getSelectionModel().getSelectedItems().get(0);
        final ObservableList<TableColumn<ObservableList<Object>, ?>> cols = this.tableview.getColumns();
        for (int fori = 0; fori < cols.size(); fori++) {
            final String colid = cols.get(fori).getId();
            final TextField textfield = (TextField) this.pane.lookup('#' + colid);
            textfield.setText((String) row.get(fori));
        }
    }


    @FXML
    private void updateClicked(final ActionEvent event){
        final ObservableList<TableColumn<ObservableList<Object>, ?>> cols= this.tableview.getColumns();
        final StringBuilder builder =new StringBuilder(1);
        builder.append("update ").append(this.tablename).append(" set ");
        String key =null;
        for (final TableColumn<ObservableList<Object>, ?> col : cols) {
             final String colid = col.getId();
             final TextField textfield = (TextField) this.pane.lookup(String.format("#%s", colid));
            if ("id".equalsIgnoreCase(colid)) {
                key = textfield.getText();
            } else {
                builder.append(colid).append("='").append(textfield.getText()).append("',");
            }
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(" where Id=").append(key);
        this.providedb.executeUpdate(builder.toString());
        this.tableview.setItems(this.providedb.loadTable(this.tablename));
    }

    @FXML
    private void insertClicked(){
        final ObservableList<TableColumn<ObservableList<Object>, ?>> cols= this.tableview.getColumns();
        final StringBuilder builder = new StringBuilder(1);
        builder.append("insert into ").append(this.tablename).append(" values (null,");
        TextField txfield=null;
        for (final TableColumn<ObservableList<Object>, ?> col : cols) {
             final String colid = col.getId();
             final TextField textfield = (TextField) this.pane.lookup(String.format("#%s", colid));
            if ("id".equalsIgnoreCase(colid)) {
                txfield = textfield;
            } else {
                builder.append('\'').append(textfield.getText()).append("',");
            }
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(')');
        final int nid= this.providedb.executeUpdate(builder.toString());
        if (nid>-1){
            this.tableview.setItems(this.providedb.loadTable(this.tablename));
            if (txfield != null) {
                txfield.setText(String.valueOf(nid));
            }
        }
    }

    @FXML
    private void deleteClicked(){
        final TextField textfield = (TextField) this.pane.lookup("#Id");
        this.providedb.delete(textfield.getText(), this.tablename);
        this.tableview.setItems(this.providedb.loadTable(this.tablename));
    }

    public final TableView<ObservableList<Object>> getTableview() {
        return this.tableview;
    }

    public final void setTableview(final TableView<ObservableList<Object>> tview) {
        this.tableview = tview;
    }

    public final Pane getPane() {
        return this.pane;
    }

    public final void setPane(final Pane panel) {
        this.pane = panel;
    }
}
