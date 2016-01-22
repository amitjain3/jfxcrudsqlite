package pcgabssys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

public class ProvideDB {
    public  static final String DATABASE = "jdbc:sqlite:../base.s3db";
    private final SQLiteConnectionPoolDataSource source;
    private final Logger logger;

    public ProvideDB(){
        this.source = new SQLiteConnectionPoolDataSource();
        this.source.setUrl(ProvideDB.DATABASE);
        this.logger=Logger.getLogger(ProvideDB.class.getName());
    }

    private Connection connect() throws SQLException {
       return this.source.getConnection();
    }

    public final int executeUpdate(final String query){
        int res = -1;
        try (Connection con = this.connect();
             PreparedStatement pst = con.prepareStatement(query) ){
            res=pst.executeUpdate();
        } catch (final SQLException e) {
            this.logger.log(Level.SEVERE,e.getMessage());
        }
        return res;
    }


    public final int delete(final String rid,final String tabname){
        int res = -1;
        try (Connection con = this.connect();
             PreparedStatement pst = con.prepareStatement(String.format("delete from %s where Id = ?", tabname)) ){
            pst.setString(1,rid);
            res=pst.executeUpdate();
        } catch (final SQLException e) {
            this.logger.log(Level.SEVERE,e.getMessage());
        }
        return res;
    }

    public final void maptable(final TableView<ObservableList<Object>> tableview,final Pane pane,final String tbname){
        final String sql = String.format("select * from %s", tbname);
        try (Connection con = this.connect();
             Statement statement = con.createStatement();
             ResultSet resultset = statement.executeQuery(sql)){
            ProvideDB.metatable(tableview,pane,resultset);
            tableview.setItems(ProvideDB.fdata(resultset));
        } catch (final SQLException e) {
            this.logger.log(Level.SEVERE,e.getMessage());
        }
    }

    public static void metatable(final TableView<ObservableList<Object>> tableview,final Pane pane,final ResultSet resultset) throws SQLException {
        final ResultSetMetaData metadata = resultset.getMetaData();
        final int columncount = metadata.getColumnCount();

        TableColumn column=null;
        double lly = 33.0;
        for (int fori = 0; fori < columncount; fori++) {
            final int finalfori = fori;
            String colName = metadata.getColumnName(fori + 1);
            column=new TableColumn(colName);
            column.setId(colName);
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty((String)param.getValue().get(finalfori));
                }
            });
            tableview.getColumns().addAll(column);

            Label label = new Label(colName);
            label.setLayoutX(11.0);
            label.setLayoutY(lly);
            final TextField field = new TextField();
            field.setId(colName);
            field.setLayoutX(90.0);
            field.setLayoutY(lly);
            pane.getChildren().addAll(label,field);
            lly+=33.0;
        }
    }

    private static ObservableList<ObservableList<Object>> fdata(final ResultSet resultset) throws SQLException {
        final ResultSetMetaData metadata = resultset.getMetaData();
        final ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();
        while(resultset.next()){
            final ObservableList<Object> row = FXCollections.observableArrayList();
            for(int fori=1 ; fori<=metadata.getColumnCount(); fori++){
                row.add(resultset.getString(fori));
            }
            data.add(row);
        }
        return data;
    }

    public final ObservableList<ObservableList<Object>> loadTable(final String tbname){
        ObservableList<ObservableList<Object>> data = null;
        final String sql = String.format("select * from %s", tbname);
        try (Connection con = this.connect();
             Statement stm = con.createStatement();
             ResultSet resultset = stm.executeQuery(sql)){
            data = ProvideDB.fdata(resultset);
        } catch (final SQLException e) {
            this.logger.log(Level.SEVERE,e.getMessage());
        }
        return data;
    }

}
