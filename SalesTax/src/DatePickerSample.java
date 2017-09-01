import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
        import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
        import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
        import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
        import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.text.WordUtils;

import javax.swing.*;

public class DatePickerSample extends Application {

    private final String JD_NAME = "Jurisdiction_Name";
    private final String JD_LEVEL = "Jurisdiction_Level";
    private final String TAX_AMT = "Taxable_Amount";
    private HashMap<String,Float> cityAmounts = new HashMap<String,Float>();
    private HashMap<String,Float> countyAmounts = new HashMap<String,Float>();

    private Stage stage;
    private DatePicker checkInDatePicker;
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("salesTaxBreakdown.txt")), true));
        }catch(Exception e){System.out.println("can't write file");}
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Sales Tax Tool");
        initUI();
        stage.show();
    }

    private void importFile(File file){
        try {
            HashMap<String,Integer> headings = new HashMap<String,Integer>();

            List<String> fileLines = Files.readAllLines(file.toPath());
            for (Iterator<String> iter = fileLines.iterator();iter.hasNext();) {
                String[] line = iter.next().split(",");
                if(headings.size() == 0){
                    String[] headers = line;
                    int counter = 0;
                    for(String header : headers){
                        headings.put(noQuotes(header),counter);
                        counter++;
                    }
                    continue;
                }
                if(noQuotes(line[headings.get(JD_LEVEL)]).equals("District")) //Skipping districts because we don't need them for this really
                    continue;
                String[] state = line;
                String[] city = iter.next().split(",");
                String[] county = iter.next().split(",");
                if(state[headings.get(JD_NAME)].equals("\"NY\"")){
                    float amt = Float.valueOf(noQuotes(state[headings.get(TAX_AMT)]));
                    String cityName = noQuotes(city[headings.get(JD_NAME)]);
                    String countyName = noQuotes(county[headings.get(JD_NAME)]);

                    float cityAmt = cityAmounts.getOrDefault(cityName,0f);
                    float countyAmt = countyAmounts.getOrDefault(countyName,0f);
                    cityAmounts.put(cityName,cityAmt + amt);
                    countyAmounts.put(countyName,countyAmt + amt);
                }
            }

        }catch(java.io.IOException e){

        }
    }

    private String noQuotes(String s){
        return s.replace("\"","");
    }

    private float getJDValue(String countyName,String[] exceptionCities){
        float totalAmt = countyAmounts.getOrDefault(countyName,0f);
        float countyAmt = totalAmt;
        Float[] individualAmts = new Float[exceptionCities.length];
        String outline = countyName.equals("NOT APPLICABLE") ? "NYC Combined" : WordUtils.capitalizeFully(countyName);
        for(int i = 0; i<exceptionCities.length;i++){
            String city = exceptionCities[i];
            individualAmts[i] = cityAmounts.getOrDefault(city,0f);
            countyAmt -=individualAmts[i];
            if(i==0)
                outline += " except "+ WordUtils.capitalizeFully(city);
            else
                outline += ", " + WordUtils.capitalizeFully(city);
        }
        System.out.println(outline +": "+(int)Math.floor(countyAmt));
        for(int i = 0; i<exceptionCities.length;i++){
            String city = exceptionCities[i];
            System.out.println("\t"+WordUtils.capitalizeFully(city)+": "+(int)Math.floor(individualAmts[i]));
        }
        return (int)totalAmt;
    }

    private void createFile(){
        float total = 0;
        total+=getJDValue("ALBANY",new String[]{});
        total+=getJDValue("ALLEGANY",new String[]{});
        total+=getJDValue("BROOME",new String[]{});
        total+=getJDValue("CATTARAUGUS",new String[]{"OLEAN","SALAMANCA"});
        total+=getJDValue("CAYUGA",new String[]{"AUBURN"});
        total+=getJDValue("CHAUTAUQUA",new String[]{});
        total+=getJDValue("CHEMUNG",new String[]{});
        total+=getJDValue("CHENANGO",new String[]{"NORWICH"});
        total+=getJDValue("CLINTON",new String[]{});
        total+=getJDValue("COLUMBIA",new String[]{});
        total+=getJDValue("CORTLAND",new String[]{});
        total+=getJDValue("DELAWARE",new String[]{});
        total+=getJDValue("DUTCHESS",new String[]{});
        total+=getJDValue("ERIE",new String[]{});
        total+=getJDValue("ESSEX",new String[]{});
        total+=getJDValue("FRANKLIN",new String[]{});
        total+=getJDValue("FULTON",new String[]{"GLOVERSVILLE","JOHNSTOWN"});
        total+=getJDValue("GENESEE",new String[]{});
        total+=getJDValue("GREENE",new String[]{});
        total+=getJDValue("HAMILTON",new String[]{});
        total+=getJDValue("HERKIMER",new String[]{});
        total+=getJDValue("JEFFERSON",new String[]{});
        total+=getJDValue("LEWIS",new String[]{});
        total+=getJDValue("LIVINGSTON",new String[]{});
        total+=getJDValue("MADISON",new String[]{"ONEIDA"});
        total+=getJDValue("MONROE",new String[]{});
        total+=getJDValue("MONTGOMERY",new String[]{});
        total+=getJDValue("NASSAU",new String[]{});
        total+=getJDValue("NIAGARA",new String[]{});
        total+=getJDValue("ONEIDA",new String[]{"ROME","UTICA"});
        total+=getJDValue("ONONDAGA",new String[]{});
        total+=getJDValue("ONTARIO",new String[]{});
        total+=getJDValue("ORANGE",new String[]{});
        total+=getJDValue("ORLEANS",new String[]{});
        total+=getJDValue("OSWEGO",new String[]{"OSWEGO"});
        total+=getJDValue("OTSEGO",new String[]{});
        total+=getJDValue("PUTNAM",new String[]{});
        total+=getJDValue("RENSSELAER",new String[]{});
        total+=getJDValue("ROCKLAND",new String[]{});
        total+=getJDValue("ST LAWRENCE",new String[]{});
        total+=getJDValue("SARATOGA",new String[]{"SARATOGA SPRINGS"});
        total+=getJDValue("SCHENECTADY",new String[]{});
        total+=getJDValue("SCHOHARIE",new String[]{});
        total+=getJDValue("SCHUYLER",new String[]{});
        total+=getJDValue("SENECA",new String[]{});
        total+=getJDValue("STEUBEN",new String[]{});
        total+=getJDValue("SUFFOLK",new String[]{});
        total+=getJDValue("SULLIVAN",new String[]{});
        total+=getJDValue("TIOGA",new String[]{});
        total+=getJDValue("TOMPKINS",new String[]{"ITHACA"});
        total+=getJDValue("ULSTER",new String[]{});
        total+=getJDValue("WARREN",new String[]{"GLENS FALLS"});
        total+=getJDValue("WASHINGTON",new String[]{});
        total+=getJDValue("WAYNE",new String[]{});
        total+=getJDValue("WESTCHESTER",new String[]{"MOUNT VERNON","NEW ROCHELLE","WHITE PLAINS","YONKERS"});
        total+=getJDValue("WYOMING",new String[]{});
        total+=getJDValue("YATES",new String[]{});
        total+=getJDValue("NOT APPLICABLE",new String[]{});
        System.out.println("Total: " + total);
        JOptionPane.showMessageDialog(new Frame(),
                "File \"salesTaxBreakdown.txt\" updated.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void initUI() {
        VBox vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(vbox, 100, 100);
        stage.setScene(scene);

        checkInDatePicker = new DatePicker();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label checkInlabel = new Label("Check-In Date:");
        //gridPane.add(checkInlabel, 0, 0);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Button button1 = new Button();
        button1.setText("Import File");
        EventHandler importEvent =
                new EventHandler<MouseEvent>() {
                    public File file;
                    @Override public void handle(MouseEvent e) {
                        file = fileChooser.showOpenDialog(stage);
                        importFile(file);
                    }
                };
        button1.addEventHandler(MouseEvent.MOUSE_CLICKED,importEvent);
        gridPane.add(button1,0,1);

        Button button2 = new Button();
        button2.setText("Create Report");
        EventHandler importEvent2 =
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        createFile();
                    }
                };
        button2.addEventHandler(MouseEvent.MOUSE_CLICKED,importEvent2);
        gridPane.add(button2,0,2);

        GridPane.setHalignment(checkInlabel, HPos.LEFT);

        //gridPane.add(checkInDatePicker, 0, 1);
        vbox.getChildren().add(gridPane);
    }
}