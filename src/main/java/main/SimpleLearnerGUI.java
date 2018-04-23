package main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Pdf.EvaluationsPdf;
import com.itextpdf.text.DocumentException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sql.SqlImplementation;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author stefan
 */
public class SimpleLearnerGUI extends Application {

    private SqlImplementation sql;
    private String dbURL = "dbc:mysql://localhost:3306/SimpleLearner?useSSL=true";

    public SimpleLearnerGUI() {
        sql = new SqlImplementation();
    }

    @Override
    public void start(Stage primaryStage) {
        // lade CSS-Datei(-en)  //Z.511
        loadStyleSheets();
        // initialisiere AnmeldungPane
        buildLoginPane();
        setBtnLogin(mainStage);
        scene.setRoot(getLoginPane());

        primaryStage = mainStage;
        primaryStage.setTitle("SimpleLearner");
        primaryStage.setScene(scene);
        primaryStage.show();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

// AnmeldungPane
    TextField loginName = new TextField();
    PasswordField loginPassword = new PasswordField();
    Button btnLogin = new Button("Einloggen");
    BorderPane loginPane = new BorderPane();
    BorderPane loginWindow = new BorderPane();
    VBox loginContainer = new VBox();
    boolean isTeacher;
    String currentUser = null;

    String gName = null;

    String getGName() {
        return gName;
    }

    void setGName(String gName) {
        this.gName = gName;
    }

    boolean isTeacher() {
        return isTeacher;
    }

    private void buildLoginPane() {
        Label loginLabel = new Label("Anmeldung");
        loginLabel.setId("loginLabel");
        loginLabel.setAlignment(Pos.CENTER);
        loginLabel.setTextAlignment(TextAlignment.CENTER);

        Label nameLabel = new Label("Name");
        Label passLabel = new Label("Passwort");
        nameLabel.setId("nameLabel");
        passLabel.setId("passLabel");

        loginName.setId("nameTextField");
        loginName.setPrefHeight(35);
        loginName.setMaxWidth(235);

        loginPassword.setId("passTextField");
        loginPassword.setMaxWidth(235);
        loginPassword.setPrefHeight(35);

        btnLogin.setId("btnLogin");
        btnLogin.setAlignment(Pos.CENTER);
        btnLogin.setTextAlignment(TextAlignment.CENTER);
        btnLogin.setDefaultButton(true);

        BorderPane tempMain = new BorderPane();

        BorderPane temp = new BorderPane();

        BorderPane loginTop = new BorderPane();
        loginTop.setPrefHeight(20);
        loginTop.setCenter(loginLabel);

        loginContainer = new VBox(8);
        loginContainer.setPadding(new Insets(10, 50, 50, 50));
        loginContainer.setId("loginContainer");
        loginContainer.getChildren().addAll(loginLabel, nameLabel, loginName, passLabel, loginPassword, btnLogin);
        loginContainer.setMinSize(300, 300);
        loginContainer.setMaxSize(300, 300);
        loginContainer.setMargin(loginLabel, new Insets(20, 0, 0, 65));
        loginContainer.setMargin(btnLogin, new Insets(20, 0, 0, 0));

        temp.setTop(loginTop);
        temp.setCenter(loginContainer);

        Pane a1 = new Pane();
        a1.setPrefHeight((scene.getHeight() /* -btnLogout.getHeight() */) / 2.5);

        Pane a2 = new Pane();
        a2.setPrefHeight((scene.getHeight() /* -btnLogout.getHeight() */) / 2.5);

        Pane b1 = new Pane();
        b1.setPrefWidth(150);

        Pane b2 = new Pane();
        b2.setPrefWidth(150);

        tempMain.setTop(a1);
        tempMain.setBottom(a2);
        tempMain.setLeft(b1);
        tempMain.setRight(b2);
        tempMain.setCenter(temp);
        loginPane = tempMain;
    }

    void setBtnLogin(Stage tempStage) {
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("------------------------------");
                System.out.println("Benutzer wird eingeloggt");
                System.out.println("    Name: " + loginName.getText());
                System.out.println("    Passwort: " + loginPassword.getText());
                //System.out.println("------------------------------");   
                boolean[] check = new boolean[2];
                try {
                    Properties userInfo = new Properties();
                    userInfo.put("username", "simple-learner-tes");
                    userInfo.put("password", "SimpleLearner");

                    Connection connection = DriverManager.getConnection(dbURL, userInfo);

                    check = sql.checkLogin(loginName.getText(), loginPassword.getText(), connection);
                    if (check[0]) {
                        if (check[1]) {//LehrerPane erstellen
                            isTeacher = check[1];
                            System.out.println("Anmeldung Lehrer >>> true");
                        } else { //SchülerPane erstellen
                            isTeacher = check[1];
                            System.out.println("Anmeldung Lehrer >>> false");
                        }

                        currentUser = sql.getCurrentUser();
                        hString = "Kategorie";
                        scene.setRoot(getMainContainer());
                        tempStage.setScene(scene);
                        tempStage.setTitle("SimpleLearner - " + loginName.getText());
                        tempStage.show();
                        // initialisere MeldungPane
                        // initialisiere AufgabenPane
                        setBtnConfirmAnswer();
                        setBtnLastTask(mainStage);
                        setBtnNextTask(mainStage);
                        setBtnConfirmQuiz(mainStage);
                        buildTaskPane();
                        setBtnBlockName();
                        setBtnBack(mainStage);
                        setBtnNewQuestionText();
                        setVBoxTaskContainer();
                        setBtnLoadAnswerChoice();
                        //initialisiere mainContainer  
                        setTopMenu();
                        //setHauptLeft(); setHauptRight(); setHauptBottom(); //zurzeit nicht beötigt
                        setMainContainer();
                        //changeHauptCenter(getAufgabenPane()); //Funktion auskommentiert (Z.214)
                        //setScene(getMainContainer());
                        buildMainPane();
                        fillKategorie();
                    } else {
                        System.out.println("Falsche Eingabe");
                    }
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }

                System.out.println("------------------------------");
            }
        });
    }

    String getName() {
        return loginName.getText();
    }

    String getPasswort() {
        return loginPassword.getText();
    }

    BorderPane getLoginPane() {
        return loginPane;
    }

// mainContainer
    Label labelDirectory = new Label("");
    TextField filter = new TextField();
    Button btnBack;
    BorderPane menuContainer = new BorderPane();
    Pane hauptLeft = new Pane();
    BorderPane hauptRight = new BorderPane();
    BorderPane hauptBottom = new BorderPane();
    BorderPane listContainer = new BorderPane();
    VBox centralList = new VBox();
    Button btnNeuesElement = new Button("Neues Element");
    BorderPane mainContainer = new BorderPane();

    //SpeicherStrings
    String kategorieString = null;
    String modulString = null;
    String hString = null;

    void setTopMenu() {
        menuContainer.setId("mainTopMenu");
        menuContainer.setPrefHeight(50);

        labelDirectory.setId("labelDirectory");

        filter.setPromptText("Filter");
        filter.setPrefHeight(30);
        filter.setPrefWidth(200);
        filter.setFocusTraversable(false);

        filter.setOnKeyReleased(e -> {
            if (hString.equals("Kategorie")) {
                System.out.println("Kategoriefilter");
                fillKategorie();
            } else if (hString.equals("Modul")) {
                System.out.println("modulfilter");
                fillModul(kategorieString);
            } else if (hString.equals("Verzeichnis")) {
                System.out.println("verzeichnisfilter");
                fillVerzeich();
            }
        });
        Image logoutImage = new Image(getClass().getResourceAsStream("logout20.png"));
        Button btnLogout = new Button("Abmelden", new ImageView(logoutImage));
        btnLogout.setId("btnLogout");
        btnLogout.getStyleClass().add("btn");

        Image back = new Image(getClass().getResourceAsStream("back20.png"));
        btnBack = new Button("Zurück", new ImageView(back));
        btnBack.setDisable(true);
        btnBack.setId("btnBack");
        btnBack.getStyleClass().add("btn");
        btnBack.setAlignment(Pos.CENTER);
        HBox hBoxRight = new HBox(8);
        hBoxRight.getChildren().addAll(filter, btnLogout);
        menuContainer.setLeft(btnBack);
        menuContainer.setCenter(labelDirectory);
        menuContainer.setRight(hBoxRight);
        menuContainer.setMargin(hBoxRight, new Insets(10, 10, 10, 10));
        menuContainer.setMargin(btnBack, new Insets(10, 10, 10, 10));
        btnLogout.setOnAction((ActionEvent e) -> {
            //DislogFenster für Namenseingabe
            //-> Erstellung einer neuen Aufgabe
            System.out.println("-----------------");
            System.out.println("Abmeldung startet");
            System.out.println("-----------------");

            labelDirectory.setText("");
            loginName.setText("");
            loginPassword.setText("");
            scene.setRoot(getLoginPane());

            Stage tempStage = mainStage;
            tempStage.setTitle("SimpleLearner - " + loginName.getText());
            tempStage.setScene(scene);
            tempStage.setTitle("SimpleLearner");

            tempStage.show();
        });
        btnBack.setOnAction((ActionEvent e) -> {
            filter.clear();
            if (hString.equals("Modul")) {
                labelDirectory.setText(null); //Label zurücksetzen
                fillKategorie();
                hString = "Kategorie";
                btnBack.setDisable(true);
            } else if (hString.equals("Verzeichnis")) {
                labelDirectory.setText(kategorieString); //Label zurücksetzen
                fillModul(kategorieString);
                hString = "Modul";
            }
        });
    }

    void setMainContainer() {
        listContainer.setId("listContainer");
        centralList.setSpacing(10);
        listContainer.setCenter(centralList);
    }

    class BtnNewElement {

        Button btnNewElement;

        BtnNewElement(String input) {
            btnNewElement = new Button();
            if (input.equals("Modul")) {
                btnNewElement.setText("Neue Kategorie");
            }
            if (input.equals("Verzeich")) {
                btnNewElement.setText("Neuer Aufgabenblock");
            }
            setBtnNewElement(input);
        }

        void setBtnNewElement(String input) {
            btnNewElement.setPrefWidth(scene.getWidth());
            btnNewElement.setId("btnNewElement");
            btnNewElement.setPrefHeight(30);
            btnNewElement.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    //DislogFenster für Namenseingabe
                    //-> Erstellung einer neuen Aufgabe

                    if (input.equals("Modul")) {
                        //fillModul(kategorieString);
                        Stage tempStage = new Stage();
                        tempStage.setTitle("Neue Kategorie");

                        BorderPane borderPane = new BorderPane();
                        TextField textFieldNewCategory = new TextField();
                        textFieldNewCategory.setPromptText("Hier Kategorienamen eintragen");
                        Button btnCancelCategory = new Button("Abbrechen");
                        Button btnConfirmCategory = new Button("Bestätigen");
                        btnCancelCategory.getStyleClass().add("cancelButton");
                        btnConfirmCategory.getStyleClass().add("okButton");

                        btnCancelCategory.setOnAction(e2 -> {
                            tempStage.close();
                        });
                        btnConfirmCategory.setOnAction(e3 -> {
                            if (textFieldNewCategory.getText().trim().length() > 0) {
                                try {
                                    sql.createKategorie(textFieldNewCategory.getText(), kategorieString);
                                    fillModul(kategorieString);
                                } catch (SQLException exc) {
                                    System.out.println(exc.getMessage());
                                }
                                tempStage.close();
                            }
                        });
                        borderPane.setTop(textFieldNewCategory);
                        borderPane.setLeft(btnCancelCategory);
                        borderPane.setRight(btnConfirmCategory);

                        Scene tempScene = new Scene(borderPane, 300, 300);
                        tempScene.getStylesheets().add("SimpleLearnerGUI.css");
                        tempStage.setScene(tempScene);
                        tempStage.show();
                    } else if (input.equals("Verzeich")) {
                        //fillVerzeich();
                        Stage tempStage = new Stage();
                        tempStage.setTitle("Neuer Block");

                        BorderPane borderPane = new BorderPane();
                        TextField textFieldNewQuiz = new TextField();
                        textFieldNewQuiz.setPromptText("Hier Blocknamen eintragen");
                        Button btnCancelQuiz = new Button("Abbrechen");
                        Button btnConfirmNewQuiz = new Button("Bestätigen");
                        btnCancelQuiz.getStyleClass().add("cancelButton");
                        btnConfirmNewQuiz.getStyleClass().add("okButton");

                        btnCancelQuiz.setOnAction(e2 -> {
                            tempStage.close();
                        });
                        btnConfirmNewQuiz.setOnAction(e3 -> {
                            if (textFieldNewQuiz.getText().trim().length() > 0) {
                                try {
                                    sql.createBlock(textFieldNewQuiz.getText(), loginName.getText(), modulString);
                                    fillVerzeich();
                                } catch (SQLException exc) {
                                    System.out.println(exc.getMessage());
                                }
                                tempStage.close();
                            }
                        });
                        borderPane.setTop(textFieldNewQuiz);
                        borderPane.setLeft(btnCancelQuiz);
                        borderPane.setRight(btnConfirmNewQuiz);

                        Scene tempScene = new Scene(borderPane, 300, 300);
                        tempScene.getStylesheets().add("SimpleLearnerGUI.css");
                        tempStage.setScene(tempScene);
                        tempStage.show();
                    }
                }
            });
        }

        Button getBtnNeuesElement() {
            return btnNewElement;
        }
    }

    void buildMainPane() {
        ScrollPane sPane = new ScrollPane();
        sPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sPane.setVvalue(0.1);
        sPane.setContent(listContainer);
        mainContainer.setTop(menuContainer);
        mainContainer.setLeft(hauptLeft);
        mainContainer.setRight(hauptRight);
        mainContainer.setBottom(hauptBottom);
        mainContainer.setCenter(sPane);

        fillKategorie();
    }

    void fillKategorie() {
        //tempStage.setTitle("SimpleLearner - Kategorienverzeichnis");

        // Liste leeren
        centralList.getChildren().setAll();

        // Liste füllen
        if (isTeacher) {
            if (filter.getText().isEmpty()) {
                try {
                    sql.loadFaecher(getName());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                try {
                    sql.loadFilteredFaecher(getName(), filter.getText());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            }
            for (int i = 0; i < sql.getSubjects().size(); i++) {
                centralList.getChildren().add(new SubjectButton(sql.getSubjects().get(i), i).getKategorieButton());
            }
        } else {
            if (this.filter.getText().isEmpty()) {
                try {
                    sql.loadFaecher();
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                try {
                    sql.loadFilteredFaecher(filter.getText());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            }
            for (int i = 0; i < sql.getSubjects().size(); i++) {
                centralList.getChildren().add(new SubjectButton(sql.getSubjects().get(i), i).getKategorieButton());
            }
        }

        //centerListe.getChildren().add(new SubjectButton("Kategorie 0", 0).getKategorieButton());
        //centerListe.getChildren().add(new BtnNewElement("Kategorie").getBtnNeuesElement());
    }

    void fillVerzeich() { //Parameterübergabe für Anzahl der Aufgaben
        // -> Anzahl aus Datenbank
        // Liste leeren (Liste soll sich neu füllen, nicht erweitern)

        centralList.getChildren().setAll();

        if (isTeacher) {
            if (filter.getText().isEmpty()) {
                try {
                    sql.loadBloeckeLehrer(getGName(), getName());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                try {
                    sql.loadFilteredBloeckeLehrer(getGName(), getName(), filter.getText());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            }
            for (int i = 0; i < sql.getTaskBlocks().size(); i++) {
                centralList.getChildren().add(new QuizButton(sql.getTaskBlocks().get(i), i).getQuizButton()); // ersetze ("SimpleLearnerGUI "+i) mit Aufgabenname
            }
            centralList.getChildren().add(new BtnNewElement("Verzeich").getBtnNeuesElement());
        } else {
            if (filter.getText().isEmpty()) {
                try {
                    sql.loadBloeckeSchueler(getGName(), getName());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                try {
                    sql.loadFilteredBloeckeSchueler(getGName(), getName(), filter.getText());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            }
            for (int i = 0; i < sql.getTaskBlocks().size(); i++) {
                centralList.getChildren().add(new QuizButton(sql.getTaskBlocks().get(i), i).getQuizButton()); // ersetze ("SimpleLearnerGUI "+i) mit Aufgabenname
            }
        }

    }

    void fillModul(String kategorieString) { //Parameterübergabe für Anzahl der Aufgaben
        // -> Anzahl aus Datenbank
        // Liste leeren (Liste soll sich neu füllen, nicht erweitern)

        centralList.getChildren().setAll();

        if (filter.getText().isEmpty()) {
            try {
                sql.loadKategorien(kategorieString);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        } else {
            try {
                sql.loadFilteredKategorien(kategorieString, filter.getText());
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        }
        for (int i = 0; i < sql.getCategories().size(); i++) {
            centralList.getChildren().add(new CategoryButton(sql.getCategories().get(i), i).getCategoryButton()); // ersetze ("SimpleLearnerGUI "+i) mit Aufgabenname
        }
        //centerListe.getChildren().add(new CategoryButton("Modul 0", 0).getCategoryButton());
        //centerListe.getChildren().add(new CategoryButton("Modul 1", 1).getCategoryButton());
        //centerListe.getChildren().add(new CategoryButton("Modul 2", 2).getCategoryButton());

        if (isTeacher) {
            centralList.getChildren().add(new BtnNewElement("Modul").getBtnNeuesElement());
        }
    }

    BorderPane getMainContainer() {
        return mainContainer;

    }

// Verzeichnis-Element
//
//
//
    class QuizButton {

        String btnQuizName;
        Button btnQuiz;
        int taskNumber;

        QuizButton(String input, int nummer) {
            btnQuizName = input;
            taskNumber = nummer;
            btnQuiz = new Button(input);
            btnQuiz.getStyleClass().add("btnQuiz");
            btnQuiz.setPrefWidth(scene.getWidth());
            btnQuiz.setMinWidth(listContainer.getWidth()/*-btnLöschen.getPrefWidth()*/);
            setBtnName(mainStage);
        }

        void setBtnName(Stage tempStage) {
            ContextMenu cMenu = new ContextMenu();
            MenuItem itemDelete = new MenuItem("Lösche Aufgabenblock");
            MenuItem itemShow = new MenuItem("Zeige Schüler");
            cMenu.getItems().addAll(itemDelete, itemShow);

            itemDelete.setOnAction(ae -> {
                try {
                    sql.deleteBlock(btnQuizName, loginName.getText(), modulString);
                    fillVerzeich();
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            });
            itemShow.setOnAction(ae2 -> {
                Stage pdfStage = new Stage();
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.getStyleClass().add("popupPane");
                VBox vbStudents = new VBox();
                scrollPane.setContent(vbStudents);
                //VBox vbMehr = new VBox();
                //bp.setLeft(vbMehr);
                try {
                    sql.loadAbsolvierteSchueler(btnQuizName, loginName.getText());
                    if (sql.getStudentsSolved().size() > 0) {
                        for (int i = 0; i < sql.getStudentsSolved().size(); i++) {
                            Button btnStudentName = new Button(sql.getStudentsSolved().get(i));
                            btnStudentName.setPrefWidth(250);
                            btnStudentName.setId("btnSchueler");
                            vbStudents.setMargin(btnStudentName, new Insets(0, 0, 0, 0));
                            btnStudentName.setOnAction(e -> {
                                FileChooser fc = new FileChooser();
                                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*pdf"));
                                File f = fc.showSaveDialog(new Stage());
                                if (f != null && !f.getName().contains(".")) {
                                    f = new File(f.getAbsolutePath() + ".pdf");
                                }
                                if (f != null) {
                                    try {
                                        EvaluationsPdf pdf = new EvaluationsPdf(sql, f);
                                        //Vor- und Nachname aus dem Button filtern
                                        String vorname = "";
                                        String nachname = "";
                                        int j = 0;
                                        for (int k = 0; k < btnStudentName.getText().length(); k++) {
                                            if (Character.isWhitespace(btnStudentName.getText().charAt(k))) {
                                                j = k + 1;
                                                break;
                                            }
                                            vorname += btnStudentName.getText().charAt(k);
                                        }
                                        for (int l = j; l < btnStudentName.getText().length(); l++) {
                                            nachname += btnStudentName.getText().charAt(l);
                                        }
                                        pdf.createTable(btnQuizName, loginName.getText(), vorname, nachname);
                                    } catch (IOException | DocumentException | SQLException exc) {
                                        System.out.println(exc.getMessage());
                                    }
                                }
                            });
                            vbStudents.getChildren().add(btnStudentName);
                        }
                        Scene scene = new Scene(scrollPane, 250, 300);
                        scene.getStylesheets().add("SimpleLearnerGUI.css");
                        pdfStage.setScene(scene);
                        pdfStage.show();
                    }
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            });
            btnQuiz.setOnMousePressed((MouseEvent e) -> {
                if (isTeacher && e.isSecondaryButtonDown()) {
                    cMenu.show(tempStage, e.getScreenX(), e.getScreenY());
                } else {
                    start = System.currentTimeMillis();
                    taskNumber = 0;
                    try {
                        sql.loadFragen(btnQuizName);
                    } catch (SQLException exc) {
                        System.out.println(exc.getMessage());
                    }
                    if (sql.getQuestions().size() > 0) {
                        setBlockPar(btnQuizName);
                        setFragePar(sql.getQuestions().indexOf(sql.getQuestions().get(taskNumber)));
                        blockNameLabel.setText(btnQuizName);
                        fillAntwortAuswahl(btnQuizName, sql.getQuestions().get(taskNumber));
                        taskText.setText(sql.getQuestions().get(taskNumber));
                    } else {
                        setBlockPar(btnQuizName);
                        blockNameLabel.setText(btnQuizName);
                        taskText.setText(null);
                        fillAntwortAuswahl(null, null);
                    }
                    //buildAufgabenPane();// Parameter
                    if (isTeacher || (!isTeacher && sql.getQuestions().size() > 0)) {
                        scene.setRoot(getAufgabenPane());
                        tempStage.setScene(scene);
                        tempStage.show();
                    }
                }
            });
        }

        BorderPane getQuizButton() {
            BorderPane temp = new BorderPane();
            btnQuiz.setPrefWidth(scene.getWidth());
            temp.setLeft(btnQuiz);
            return temp;
        }
    }

//Kategorie-Element
//
//
//
    class SubjectButton {

        String btnSubjectName;
        Button btnSubject;
        int taskNumber;

        SubjectButton(String input, int nummer) {
            btnSubjectName = input;
            taskNumber = nummer;
            btnSubject = new Button(input);
            btnSubject.getStyleClass().add("subjectButton");
            setBtnName(mainStage);

            //btnLoeschen.setPrefWidth(100);
        }

        void setBtnName(Stage tempStage) {
            btnSubject.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {

                    btnBack.setDisable(false);
                    taskNumber = 0;
                    setGName(btnSubjectName);
                    labelDirectory.setText(btnSubjectName);
                    kategorieString = btnSubjectName;
                    hString = "Modul";
                    fillModul(kategorieString);
                    tempStage.show();
                }
            });
        }

        BorderPane getKategorieButton() {
            BorderPane temp = new BorderPane();
            //temp.setLeft(btnQuiz);

            /*if (isTeacher()) {
                btnQuiz.setPrefWidth(scene.getWidth() - 100);
                btnLogout.setLeft(btnQuiz);
                btnLoeschen.setPrefWidth(100);
                btnLogout.setRight(btnLoeschen);
            } else {*/
            btnSubject.setPrefWidth(scene.getWidth());
            temp.setLeft(btnSubject);
            //}
            return temp;
        }
    }

// Kategorie-Element    
//
//
//
    class CategoryButton {

        String btnCategoryName;
        Button btnCategory;
        int aufgabenNummer;

        CategoryButton(String input, int nummer) {
            btnCategoryName = input;
            aufgabenNummer = nummer;
            btnCategory = new Button(input);
            btnCategory.getStyleClass().add("btnCategory");
            btnCategory.setPrefWidth(scene.getWidth());
            btnCategory.setMinWidth(listContainer.getWidth()/*-btnLöschen.getPrefWidth()*/);
            setBtnName(mainStage);

        }

        String getBtnName() {
            return btnCategory.getText();
        }

        void setBtnName(Stage tempStage) {
            btnCategory.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    aufgabenNummer = 0;

                    //buildAufgabenPane();// Parameter
                    setGName(btnCategoryName);
                    labelDirectory.setText(btnCategoryName);
                    modulString = btnCategoryName;
                    hString = "Verzeichnis";
                    fillVerzeich();
                    //setScene(getAufgabenPane());
                    //tempStage.setScene(scene);
                    tempStage.show();
                }
            });
        }

        BorderPane getCategoryButton() {
            BorderPane temp = new BorderPane();
            temp.setLeft(btnCategory);
            /*
            if(isStudent){
                btnLogout.setLeft(btnQuiz);
            }else{
                btnLogout.setLeft(btnQuiz);
                btnLogout.setRight(btnLöschen);
            }
             */
            return temp;
        }
    }

//AufgabenPane
//
//
//
    String blockPar = null;
    int nummerFragePar = 0;
    long start;
    long end;

    BorderPane topContainerTask = new BorderPane();
    HBox hbBlockName = new HBox();
    Label blockNameLabel = new Label("");
    Button btnChangeBlockName = new Button("Blocknamen ändern");
    Button btnBlockZurueck = new Button("Zurück");

    BorderPane AufgabenPane = new BorderPane();
    BorderPane tempPane = new BorderPane(); // Ausgabe des Aufgabentextes
    Label taskText = new Label();
    Button btnChangeTaskText = new Button("Fragetext ändern");
    VBox vBoxTaskText = new VBox();

    BorderPane AnswerPane = new BorderPane();
    VBox antwortAuswahl = new VBox();
    Button btnAntwortenAuswahl = new Button("Antwortenauswahl ändern");
    ToggleGroup AntwortGroup = new ToggleGroup();
    Button btnDeleteAntwort = new Button("Lösche Aufgabe");
    Button btnNeueAntwort = new Button("Neue Aufgabe");
    GridPane navigator = new GridPane();
    //Button btnZurück = new Button("Zurück");
    //Button btnNächste = new Button("Nächste");
    Label auswertungAntwort = new Label();
    Button btnBestaetigen = new Button("Bestätigen");
    Button btnNaechsteAufgabe = new Button("Nächste");
    Button btnBeenden = new Button("Beenden");
    Button btnZurueck = new Button("Zurück");

    void setVBoxTaskContainer() {
        vBoxTaskText.setMargin(btnChangeTaskText, new Insets(5, 5, 5, 5));
        vBoxTaskText.setMargin(taskText, new Insets(5, 5, 5, 5));
        vBoxTaskText.setId("vBoxTaskText");
        vBoxTaskText.setMaxHeight(250);
        vBoxTaskText.setAlignment(Pos.TOP_CENTER);
        vBoxTaskText.getChildren().clear();
        vBoxTaskText.getChildren().add(taskText);
        if (isTeacher) {
            vBoxTaskText.getChildren().add(btnChangeTaskText);
        }
    }

    void setBlockPar(String par) {
        blockPar = par;
    }

    void setFragePar(int par) {
        nummerFragePar = par;
    }

    void buildTaskPane() {
        btnBlockZurueck.getStyleClass().add("btn");
        btnChangeBlockName.getStyleClass().add("btn");
        btnChangeTaskText.getStyleClass().add("btn");
        btnAntwortenAuswahl.getStyleClass().add("btn");
        btnBestaetigen.getStyleClass().add("btn");
        btnBestaetigen.setPrefWidth(120);
        btnNaechsteAufgabe.getStyleClass().add("btn");
        btnNaechsteAufgabe.setPrefWidth(120);
        btnBeenden.getStyleClass().add("btn");
        btnBeenden.setPrefWidth(120);

        tempPane.setId("aufgabePane");
        AnswerPane.setId("answerPane");
        topContainerTask.setId("topContainerTask");
        topContainerTask.setPrefHeight(50);
        topContainerTask.setMargin(btnBlockZurueck, new Insets(10, 10, 10, 10));

        blockNameLabel.setId("blockNameLabel");
        hbBlockName.getChildren().clear();
        hbBlockName.getChildren().add(blockNameLabel);
        hbBlockName.setAlignment(Pos.CENTER_RIGHT);

        hbBlockName.setMargin(btnChangeBlockName, new Insets(10, 10, 10, 10));
        hbBlockName.setMargin(blockNameLabel, new Insets(10, 10, 10, 10));

        if (isTeacher) {
            hbBlockName.getChildren().add(btnChangeBlockName);
            btnChangeBlockName.setAlignment(Pos.CENTER_RIGHT);
        }
        topContainerTask.getChildren().clear();
        topContainerTask.setRight(hbBlockName);
        if (isTeacher) {
            topContainerTask.setLeft(btnBlockZurueck);
        }

        tempPane.setPrefWidth(scene.getWidth() - 250);

        tempPane.setPrefHeight(250);
        tempPane.setId("tempPaneTest");

        taskText.setWrapText(true);
        taskText.setId("taskText");
        taskText.setAlignment(Pos.CENTER);
        taskText.setFont(Font.font(16)); //aufgabeText.setText("ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
        tempPane.setCenter(vBoxTaskText);
        tempPane.setMaxWidth(400);

        AufgabenPane.setCenter(tempPane);
        AufgabenPane.setRight(AnswerPane);
        AufgabenPane.setTop(topContainerTask);

        AnswerPane.setPrefWidth(500);
        antwortAuswahl.setSpacing(5);

        AnswerPane.setAlignment(antwortAuswahl, Pos.CENTER);
        VBox antworten = new VBox(8);
        antworten.setMargin(btnAntwortenAuswahl, new Insets(10, 10, 10, 10));
        antworten.setMargin(antwortAuswahl, new Insets(15, 15, 15, 15));
        antworten.getChildren().add(antwortAuswahl);
        if (isTeacher) {
            antworten.getChildren().add(btnAntwortenAuswahl);
        }
        AnswerPane.setCenter(antworten);//antwortAuswahl
        //setBtnNeueAufgabe();    //Funktion auskommentiert (Z.338)
        //antwortAuswahl.setBottom(btnNeueAntwort);
        AnswerPane.setAlignment(navigator, Pos.CENTER);
        AnswerPane.setBottom(navigator);
        auswertungAntwort.setFont(Font.font(20));
        navigator.getChildren().clear();
        navigator.add(auswertungAntwort, 0, 0);
        if (!isTeacher) {
            navigator.add(btnBestaetigen, 0, 1);
        } else {
            navigator.add(btnNaechsteAufgabe, 0, 1);
        }
        if (isTeacher) {
            navigator.add(btnZurueck, 1, 1);
        }
        navigator.setMargin(btnNaechsteAufgabe, new Insets(10, 10, 10, 10));
        navigator.setMargin(btnBestaetigen, new Insets(10, 10, 10, 10));
    }

    void fillAntwortAuswahl(String block, String frage) {
        if (block == null && frage == null) {
            antwortAuswahl.getChildren().clear();
        } else {
            AntwortGroup = new ToggleGroup();
            antwortAuswahl.getChildren().setAll();
            try {
                sql.loadAntworten(block, frage);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
            for (int i = 0; i < sql.getAnswersTemp().size()/*anzahl der Antworten*/; i++) {
                // hole Aufgabenname der i.ten Aufgabeneinheit
                // Übergebe AufgabenName
                antwortAuswahl.getChildren().add(new btnAntwort(sql.getAnswersTemp().get(i)).getBtnAntwort());
            }
        }
    }

    void setBtnLoadAnswerChoice() {

        btnAntwortenAuswahl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (taskText.getText() != null) {
                    Stage tempStage = new Stage();
                    tempStage.setTitle("Neue Antwortauswahl");

                    ToggleGroup tempToggleGroup = new ToggleGroup();
                    VBox vBox = new VBox(8);
                    HBox hBoxButtonsBottom = new HBox(8);
                    hBoxButtonsBottom.setPrefHeight(40);
                    Button btnCancel = new Button("Abbrechen");
                    btnCancel.getStyleClass().add("cancelButton");
                    hBoxButtonsBottom.setMargin(btnCancel, new Insets(10, 10, 10, 50));

                    Button btnConfirm = new Button("Bestätigen");
                    btnConfirm.getStyleClass().add("okButton");
                    hBoxButtonsBottom.setMargin(btnConfirm, new Insets(10, 10, 10, 50));

                    for (int i = 0; i < antwortAuswahl.getChildren().size(); i++) {
                        RadioButton rb = (RadioButton) antwortAuswahl.getChildren().get(i);
                        String text = rb.getText();
                        RadioButton neuRb = new RadioButton();
                        neuRb.setToggleGroup(tempToggleGroup);
                        TextField neuTf = new TextField(text);
                        HBox hb = new HBox();
                        hb.getChildren().addAll(neuRb, neuTf);
                        vBox.getChildren().add(hb);
                    }

                    VBox vBox2 = new VBox();
                    HBox hBoxFunc = new HBox();
                    Button btnNeueAnwort = new Button("Neue Antwort");
                    vBox2.getChildren().add(vBox);
                    hBoxFunc.getChildren().addAll(btnDeleteAntwort, btnNeueAntwort);
                    vBox2.getChildren().add(hBoxFunc);

                    hBoxButtonsBottom.getChildren().addAll(btnConfirm, btnCancel);

                    BorderPane tempPane = new BorderPane();
                    tempPane.setCenter(vBox2);
                    tempPane.setBottom(hBoxButtonsBottom);

                    Scene tempScene = new Scene(tempPane, 400, 300);
                    tempScene.getStylesheets().add("SimpleLearnerGUI.css");
                    tempStage.setScene(tempScene);

                    tempStage.show();

                    btnDeleteAntwort.setOnAction(e2 -> {
                        if (vBox.getChildren().size() > 0) {
                            vBox.getChildren().remove(vBox.getChildren().size() - 1);
                        }
                    });
                    // btnNeueAntwort definieren
                    btnNeueAntwort.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {

                            RadioButton radioBtn = new RadioButton();
                            radioBtn.setToggleGroup(tempToggleGroup);

                            HBox h = new HBox();
                            h.getChildren().add(radioBtn);
                            h.getChildren().add(new TextField());
                            vBox.getChildren().add(h);

                        }
                    });

                    // btnAbbr definieren
                    btnCancel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            tempStage.close();
                        }
                    });

                    btnConfirm.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            if (vBox.getChildren().size() > 0 && tempToggleGroup.getSelectedToggle() != null) {

                                try {
                                    sql.updateAnswers(blockPar, taskText.getText(), loginName.getText(), vBox);
                                    setBtnLoadAnswerChoice();
                                    fillAntwortAuswahl(blockPar, taskText.getText());
                                    //hier die Antworten direkt neu Laden in der VBox
                                } catch (SQLException exc) {
                                    System.out.println(exc.getMessage());
                                }
                                /*
                           Anweisungen hier
                                 */
                                // Auslesen aller TextFelder
                                for (int i = 0; i < vBox.getChildren().size(); i++) {
                                    HBox hb = (HBox) vBox.getChildren().get(i);
                                    for (int j = 0; j < hb.getChildren().size(); j++) {
                                        if (hb.getChildren().get(j) instanceof TextField) {
                                            TextField tf = (TextField) hb.getChildren().get(j);
                                            String ausgabeString = tf.getText();
                                        }
                                    }
                                }
                                tempStage.close();
                            }
                        }
                    });

                }
            }
        });
    }

    void setBtnBlockName() {
        btnChangeBlockName.setOnAction(e -> {
            Stage tempStage = new Stage();
            tempStage.setTitle("Neuer Quizname");

            BorderPane borderPane = new BorderPane();
            TextField tf = new TextField();
            tf.setPrefHeight(30);
            tf.setPromptText("Hier Blocknamen eintragen");
            Button btnAbbrechen = new Button("Abbrechen");
            Button btnBestaetigen = new Button("Bestätigen");
            btnAbbrechen.getStyleClass().add("cancelButton");
            btnBestaetigen.getStyleClass().add("okButton");
            btnAbbrechen.setPrefWidth(120);
            btnBestaetigen.setPrefWidth(120);
            btnAbbrechen.setOnAction(e2 -> {
                tempStage.close();
            });
            btnBestaetigen.setOnAction(e3 -> {
                if (tf.getText().trim().length() > 0) {
                    try {
                        sql.updateQuiz(blockPar, loginName.getText(), tf.getText());
                        blockPar = tf.getText();
                        blockNameLabel.setText(tf.getText());
                    } catch (SQLException exc) {
                        System.out.println(exc.getMessage());
                    }
                    tempStage.close();
                }
            });
            borderPane.setTop(tf);
            borderPane.setLeft(btnBestaetigen);
            borderPane.setRight(btnAbbrechen);
            borderPane.getStyleClass().add("popupPane");
            borderPane.setMargin(btnAbbrechen, new Insets(10, 10, 10, 20));
            borderPane.setMargin(btnBestaetigen, new Insets(10, 20, 10, 10));
            Scene tempScene = new Scene(borderPane, 300, 80);
            tempScene.getStylesheets().add("SimpleLearnerGUI.css");

            tempStage.setScene(tempScene);
            tempStage.show();
        });
    }

    void setBtnBack(Stage tempStage) {//öffnet nächste Aufgabe
        btnBlockZurueck.setOnAction((ActionEvent e) -> {
            //sql.endAufgabe();
            fillVerzeich();
            scene.setRoot(getMainContainer());
            tempStage.setScene(scene);
            tempStage.show();
        });
    }

    void setBtnNewQuestionText() {

        btnChangeTaskText.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                Stage tempStage = new Stage();
                tempStage.setTitle("Neuer Aufgabentext");

                HBox hBox = new HBox();
                Button btnCancel = new Button("Abbrechen");
                Button btnConfirm = new Button("Bestätigen");
                btnCancel.setId("newTaskTextCancelButton");
                btnCancel.getStyleClass().add("btn");
                btnCancel.setPrefWidth(120);
                btnConfirm.getStyleClass().add("btn");
                btnConfirm.setId("newTaskTextConfirmButton");

                btnConfirm.setPrefWidth(120);
                btnCancel.getStyleClass().add("cancelButton");
                btnConfirm.getStyleClass().add("okButton");

                hBox.setMargin(btnCancel, new Insets(10, 10, 10, 10));
                hBox.setMargin(btnConfirm, new Insets(10, 10, 10, 50));
                hBox.setPrefHeight(50);
                hBox.setId("newTaskTextHBox");
                hBox.getChildren().addAll(btnConfirm, btnCancel);

                TextArea tempTextArea = new TextArea();
                tempTextArea.setPromptText("Neuen Aufgabentext hier eintragen");
                tempTextArea.setText(taskText.getText());
                tempTextArea.setId("newTaskTextArea");
                tempTextArea.setMinWidth(300);
                tempTextArea.setMinHeight(200);
                tempTextArea.setWrapText(true);

                BorderPane tempPane = new BorderPane();
                tempPane.setCenter(tempTextArea);
                tempPane.setBottom(hBox);

                Scene tempScene = new Scene(tempPane, 350, 250);
                tempScene.getStylesheets().add("SimpleLearnerGUI.css");
                tempStage.setScene(tempScene);
                tempStage.show();

                // btnAbbr definieren
                btnCancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        tempStage.close();
                    }
                });

                btnConfirm.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        if (tempTextArea.getText().trim().length() > 0) {
                            try {
                                sql.updateTask(blockPar, taskText.getText(), tempTextArea.getText());
                                taskText.setText(tempTextArea.getText());
                            } catch (SQLException e2) {
                                System.out.println(e2.getMessage());
                            }
                            tempStage.close();
                        }
                    }
                });

            }
        });
    }

    void setBtnConfirmAnswer() {//wertet den bewählte Button aus und gibt diese aus
        btnBestaetigen.setPrefWidth(75);
        btnBestaetigen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String antwort = null;
                if (AntwortGroup.getSelectedToggle() != null) {
                    antwort = AntwortGroup.getSelectedToggle().getUserData().toString();
                }

                //checkAnswer(antwort);
                if (!isTeacher) {
                    if (antwort != null) {
                        try {
                            sql.startBlock(blockPar, loginName.getText());
                            end = System.currentTimeMillis() - start;
                            if (sql.checkAnswer(blockPar, loginName.getText(), sql.getQuestions().get(nummerFragePar), antwort) == true) {
                                System.out.println("richtig");
                                auswertungAntwort.setText("richtig");
                            } else {
                                System.out.println("falsch");
                                auswertungAntwort.setText("falsch");
                            }
                            System.out.println("------------------------------");
                        } catch (SQLException exc) {
                            System.out.println(exc.getMessage());
                        }
                    }
                }
                //ersetze "Bestätigen"-Button mit "Nächste"-Button
                if (!isTeacher && antwort != null) {
                    if (nummerFragePar < sql.getQuestions().size() - 1) {
                        navigator.add(btnNaechsteAufgabe, 0, 1);
                    } else {
                        navigator.getChildren().clear();
                        navigator.add(auswertungAntwort, 0, 0);
                        navigator.add(btnBeenden, 0, 1);
                    }
                }
            }
        });
    }

    void setBtnLastTask(Stage tempStage) {//öffnet nächste Aufgabe
        btnZurueck.setPrefWidth(75);
        btnZurueck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                start = System.currentTimeMillis();
                // exception durch erneutes Einfügen von btnBestätigen
                // -> btnBestätigen löschen und erneut einfügen
                navigator.getChildren().removeAll(btnBestaetigen, btnNaechsteAufgabe, btnZurueck); // btnBestätigen und btnNächsteAufgabe löschen
                navigator.add(btnNaechsteAufgabe, 0, 1); // btnBestätigen ein
                navigator.add(btnZurueck, 1, 1);
                if (nummerFragePar - 1 >= 0) {
                    taskText.setText(sql.getQuestions().get(nummerFragePar - 1));
                    nummerFragePar--;
                    fillAntwortAuswahl(blockPar, sql.getQuestions().get(nummerFragePar));
                    auswertungAntwort.setText("");
                }

                tempStage.show();
            }
        });
    }

    void setBtnNextTask(Stage tempStage) {//öffnet nächste Aufgabe
        btnNaechsteAufgabe.setPrefWidth(75);
        btnNaechsteAufgabe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!(taskText.getText() != null && antwortAuswahl.getChildren().size() == 0)) {

                    start = System.currentTimeMillis();
                    // exception durch erneutes Einfügen von btnBestätigen
                    // -> btnBestätigen löschen und erneut einfügen
                    navigator.getChildren().removeAll(btnBestaetigen, btnNaechsteAufgabe, btnZurueck); // btnBestätigen und btnNächsteAufgabe löschen
                    if (!isTeacher) {
                        navigator.add(btnBestaetigen, 0, 1);
                    } else {
                        navigator.add(btnNaechsteAufgabe, 0, 1);
                    }
                    if (isTeacher) {
                        navigator.add(btnZurueck, 1, 1);
                    }
                    if (isTeacher && (nummerFragePar + 1 >= sql.getQuestions().size())) {
                        taskText.setText(null);
                        fillAntwortAuswahl(null, null);
                    } else {
                        taskText.setText(sql.getQuestions().get(nummerFragePar + 1));
                        nummerFragePar++;
                        fillAntwortAuswahl(blockPar, sql.getQuestions().get(nummerFragePar));
                    }
                    auswertungAntwort.setText("");

                    tempStage.show();
                }
            }
        });
    }

    void setBtnConfirmQuiz(Stage tempStage) {//öffnet nächste Aufgabe
        btnBeenden.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //sql.endAufgabe();
                auswertungAntwort.setText("");
                navigator.getChildren().clear();
                navigator.add(btnBestaetigen, 0, 1);
                fillVerzeich();
                scene.setRoot(getMainContainer());
                tempStage.setScene(scene);
                tempStage.show();
            }
        });
    }

    BorderPane getAufgabenPane() {
        return AufgabenPane;

    }

    class btnAntwort {

        RadioButton btn;
        String btnLabel;

        btnAntwort(String input) {
            btnLabel = input;
            btn = new RadioButton(btnLabel);
            btn.setUserData(btnLabel);
            btn.setToggleGroup(AntwortGroup);
        }

        void setBtnAntwort() {
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println(btnLabel);
                }
            });
        }

        RadioButton getBtnAntwort() {
            return btn;
        }
    }
// Scene    
// getMainContainer();
// getAufgabenPane();
//Scene scene = new Scene(changeMeldungPane(getLoginPane()), 600, 600);
    Scene scene = new Scene(getLoginPane(), 600, 600);

    void loadStyleSheets() {
        scene.getStylesheets().add("SimpleLearnerGUI.css");
    }

    Stage mainStage = new Stage();

    void setPrimaryStage() {
        mainStage.setScene(scene);
    }
}
