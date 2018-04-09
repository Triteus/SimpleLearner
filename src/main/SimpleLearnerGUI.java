package main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Pdf.EvaluationPdf;
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
import sql.SqlConnection;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author stefan
 */
public class SimpleLearnerGUI extends Application {

    private SqlConnection sql;
    private TextField loginName = new TextField();
    private PasswordField loginPassword = new PasswordField();
    private Button btnLogin = new Button("Einloggen");
    private BorderPane loginPane = new BorderPane();
    private BorderPane loginWindow = new BorderPane();
    private VBox loginContainer = new VBox();
    private boolean isTeacher;
    private String currentUser = null;

    private String gName = null;
    private Label labelDirectory = new Label("");
    private TextField filter = new TextField();
    private Button btnBack;
    private BorderPane menuContainer = new BorderPane();
    private Pane hauptLeft = new Pane();
    private BorderPane hauptRight = new BorderPane();
    private BorderPane hauptBottom = new BorderPane();
    private BorderPane listContainer = new BorderPane();
    private VBox centralList = new VBox();
    private Button btnNeuesElement = new Button("Neues Element");
    private BorderPane mainContainer = new BorderPane();

    private String categoryString = null;
    private String moduleString = null;
    private String hString = null;

    private String blockPar = null;
    private int numberQuestionPar = 0;
    private long start;
    private long end;

    private BorderPane topContainerTask = new BorderPane();
    private HBox hbSectionName = new HBox();
    private Label sectionNameLabel = new Label("");
    private Button btnChangeSectionName = new Button("Blocknamen ändern");
    private Button btnSectionBack = new Button("Zurück");

    private BorderPane TaskPane = new BorderPane();
    private BorderPane tempPane = new BorderPane(); // Ausgabe des Aufgabentextes
    private Label taskText = new Label();
    private Button btnChangeTaskText = new Button("Fragetext ändern");
    private VBox vBoxTaskText = new VBox();

    private BorderPane AnswerPane = new BorderPane();
    private VBox answerSelection = new VBox();
    private Button btnAnswersSelction = new Button("Antwortenauswahl ändern");
    private ToggleGroup AntwortGroup = new ToggleGroup();
    private Button btnDeleteAntwort = new Button("Lösche Aufgabe");
    private Button btnNeueAntwort = new Button("Neue Aufgabe");
    private GridPane navigator = new GridPane();
    //Button btnZurück = new Button("Zurück");
    //Button btnNächste = new Button("Nächste");
    private Label auswertungAntwort = new Label();
    private Button btnConfirm = new Button("Bestätigen");
    private Button btnNextTask = new Button("Nächste");
    private Button btnFinish = new Button("Beenden");
    private Button btnZurueck = new Button("Zurück");


    public SimpleLearnerGUI() {
        sql = new SqlConnection();
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
        Label loginLabel = new Label("Login");
        loginLabel.setId("loginLabel");
        loginLabel.setAlignment(Pos.CENTER);
        loginLabel.setTextAlignment(TextAlignment.CENTER);

        Label nameLabel = new Label("Name");
        Label passLabel = new Label("Password");
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
                System.out.println("Logging in...");
                System.out.println("    Name: " + loginName.getText());
                System.out.println("    Password: " + loginPassword.getText());
                //System.out.println("------------------------------");   
                boolean[] check = new boolean[2];
                try {
                    check = sql.checkLogin(loginName.getText(), loginPassword.getText());
                    if (check[0] == true) {
                        if (check[1] == true) {//LehrerPane erstellen
                            isTeacher = check[1];
                            System.out.println("Anmeldung Lehrer >>> true");
                        } else if (check[1] == false) { //SchülerPane erstellen
                            isTeacher = check[1];
                            System.out.println("Anmeldung Lehrer >>> false");
                        }

                        currentUser = sql.getCurrentUser();
                        hString = "Category";
                        scene.setRoot(getMainContainer());
                        tempStage.setScene(scene);
                        tempStage.setTitle("SimpleLearner - " + loginName.getText());
                        tempStage.show();
                        // initialisere MeldungPane
                        // initialisiere TaskPane
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
                        //changeHauptCenter(getTaskPane()); //Funktion auskommentiert (Z.214)
                        //setScene(getMainContainer());
                        buildMainPane();
                        fillCategory();
                    } else {
                        System.out.println("Flawed Input");
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


    void setTopMenu() {
        menuContainer.setId("mainTopMenu");
        menuContainer.setPrefHeight(50);

        labelDirectory.setId("labelDirectory");

        filter.setPromptText("Filter");
        filter.setPrefHeight(30);
        filter.setPrefWidth(200);
        filter.setFocusTraversable(false);

        filter.setOnKeyReleased(e -> {
            if (hString.equals("Category")) {
                System.out.println("Category filter");
                fillCategory();
            } else if (hString.equals("Module")) {
                System.out.println("module filter");
                fillModule(categoryString);
            } else if (hString.equals("Directory")) {
                System.out.println("Directory filter");
                fillDirectory();
            }
        });
        Image logoutImage = new Image(getClass().getResourceAsStream("logout20.png"));
        Button btnLogout = new Button("Logout", new ImageView(logoutImage));
        btnLogout.setId("btnLogout");
        btnLogout.getStyleClass().add("btn");

        Image back = new Image(getClass().getResourceAsStream("back20.png"));
        btnBack = new Button("Back", new ImageView(back));
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
            System.out.println("Login");
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
            if (hString.equals("Module")) {
                labelDirectory.setText(null); //Label zurücksetzen
                fillCategory();
                hString = "Category";
                btnBack.setDisable(true);
            } else if (hString.equals("Directory")) {
                labelDirectory.setText(categoryString); //Label zurücksetzen
                fillModule(categoryString);
                hString = "Module";
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
            if (input.equals("Module")) {
                btnNewElement.setText("New category");
            }
            if (input.equals("Directory")) {
                btnNewElement.setText("New task block");
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
                        //fillModule(categoryString);
                        Stage tempStage = new Stage();
                        tempStage.setTitle("New category");

                        BorderPane borderPane = new BorderPane();
                        TextField textFieldNewCategory = new TextField();
                        textFieldNewCategory.setPromptText("Insert category names here");
                        Button btnCancelCategory = new Button("Cancel");
                        Button btnConfirmCategory = new Button("Confirm");
                        btnCancelCategory.getStyleClass().add("cancelButton");
                        btnConfirmCategory.getStyleClass().add("okButton");

                        btnCancelCategory.setOnAction(e2 -> {
                            tempStage.close();
                        });
                        btnConfirmCategory.setOnAction(e3 -> {
                            if (textFieldNewCategory.getText().trim().length() > 0) {
                                try {
                                    sql.createKategorie(textFieldNewCategory.getText(), categoryString);
                                    fillModule(categoryString);
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
                        tempScene.getStylesheets().add("style/SimpleLearnerGUI.css");
                        tempStage.setScene(tempScene);
                        tempStage.show();
                    } else if (input.equals("Directory")) {
                        //fillDirectory();
                        Stage tempStage = new Stage();
                        tempStage.setTitle("New block");

                        BorderPane borderPane = new BorderPane();
                        TextField textFieldNewQuiz = new TextField();
                        textFieldNewQuiz.setPromptText("Insert block name here");
                        Button btnCancelQuiz = new Button("Cancel");
                        Button btnConfirmNewQuiz = new Button("Confirm");
                        btnCancelQuiz.getStyleClass().add("cancelButton");
                        btnConfirmNewQuiz.getStyleClass().add("okButton");

                        btnCancelQuiz.setOnAction(e2 -> {
                            tempStage.close();
                        });
                        btnConfirmNewQuiz.setOnAction(e3 -> {
                            if (textFieldNewQuiz.getText().trim().length() > 0) {
                                try {
                                    sql.createBlock(textFieldNewQuiz.getText(), loginName.getText(), moduleString);
                                    fillDirectory();
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
                        tempScene.getStylesheets().add("style/SimpleLearnerGUI.css");
                        tempStage.setScene(tempScene);
                        tempStage.show();
                    }
                }
            });
        }

        Button getBtnNewElement() {
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

        fillCategory();
    }

    void fillCategory() {
        //tempStage.setTitle("SimpleLearner - Kategorienverzeichnis");

        // Liste leeren
        centralList.getChildren().setAll();

        // Liste füllen
        if (isTeacher) {
            if (filter.getText().isEmpty()) {
                try {
                    sql.loadSubjects(getName());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                try {
                    sql.loadFilteredSubjects(getName(), filter.getText());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            }
            for (int i = 0; i < sql.getSubjects().size(); i++) {
                centralList.getChildren().add(new SubjectButton(sql.getSubjects().get(i), i).getCategoryButton());
            }
        } else {
            if (this.filter.getText().isEmpty()) {
                try {
                    sql.loadSubjects();
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                try {
                    sql.loadFilteredSubjects(filter.getText());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            }
            for (int i = 0; i < sql.getSubjects().size(); i++) {
                centralList.getChildren().add(new SubjectButton(sql.getSubjects().get(i), i).getCategoryButton());
            }
        }

        //centerListe.getChildren().add(new SubjectButton("Kategorie 0", 0).getCategoryButton());
        //centerListe.getChildren().add(new BtnNewElement("Kategorie").getBtnNewElement());
    }

    void fillDirectory() { //Parameterübergabe für Anzahl der Aufgaben
        // -> Anzahl aus Datenbank
        // Liste leeren (Liste soll sich neu füllen, nicht erweitern)

        centralList.getChildren().setAll();

        if (isTeacher) {
            if (filter.getText().isEmpty()) {
                try {
                    sql.loadSectionsTeacher(getGName(), getName());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                try {
                    sql.loadFilteredSectionsTeacher(getGName(), getName(), filter.getText());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            }
            for (int i = 0; i < sql.getTaskSections().size(); i++) {
                centralList.getChildren().add(new QuizButton(sql.getTaskSections().get(i), i).getQuizButton()); // ersetze ("SimpleLearnerGUI "+i) mit Aufgabenname
            }
            centralList.getChildren().add(new BtnNewElement("Directory").getBtnNewElement());
        } else {
            if (filter.getText().isEmpty()) {
                try {
                    sql.loadSectionsStudent(getGName(), getName());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                try {
                    sql.loadFilteredSectionsStudent(getGName(), getName(), filter.getText());
                } catch (SQLException exc) {
                    System.out.println(exc.getMessage());
                }
            }
            for (int i = 0; i < sql.getTaskSections().size(); i++) {
                centralList.getChildren().add(new QuizButton(sql.getTaskSections().get(i), i).getQuizButton()); // ersetze ("SimpleLearnerGUI "+i) mit Aufgabenname
            }
        }

    }

    void fillModule(String categoryString) { //Parameterübergabe für Anzahl der Aufgaben
        // -> Anzahl aus Datenbank
        // Liste leeren (Liste soll sich neu füllen, nicht erweitern)

        centralList.getChildren().setAll();

        if (filter.getText().isEmpty()) {
            try {
                sql.loadCategories(categoryString);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        } else {
            try {
                sql.loadFilteredCategories(categoryString, filter.getText());
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
            centralList.getChildren().add(new BtnNewElement("Module").getBtnNewElement());
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

        QuizButton(String input, int number) {
            btnQuizName = input;
            taskNumber = number;
            btnQuiz = new Button(input);
            btnQuiz.getStyleClass().add("btnQuiz");
            btnQuiz.setPrefWidth(scene.getWidth());
            btnQuiz.setMinWidth(listContainer.getWidth()/*-btnLöschen.getPrefWidth()*/);
            setBtnName(mainStage);
        }

        void setBtnName(Stage tempStage) {
            ContextMenu cMenu = new ContextMenu();
            MenuItem itemDelete = new MenuItem("Delete task block");
            MenuItem itemShow = new MenuItem("Show students");
            cMenu.getItems().addAll(itemDelete, itemShow);

            itemDelete.setOnAction(ae -> {
                try {
                    sql.deleteBlock(btnQuizName, loginName.getText(), moduleString);
                    fillDirectory();
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
                    sql.loadStudentsSolvedTask(btnQuizName, loginName.getText());
                    if (sql.getStudentsSolvedTask().size() > 0) {
                        for (int i = 0; i < sql.getStudentsSolvedTask().size(); i++) {
                            Button btnStudentName = new Button(sql.getStudentsSolvedTask().get(i));
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
                                        EvaluationPdf pdf = new EvaluationPdf(sql, f);
                                        //Vor- und Nachname aus dem Button filtern
                                        String firstName = "";
                                        String lastName = "";
                                        int j = 0;
                                        for (int k = 0; k < btnStudentName.getText().length(); k++) {
                                            if (Character.isWhitespace(btnStudentName.getText().charAt(k))) {
                                                j = k + 1;
                                                break;
                                            }
                                            firstName += btnStudentName.getText().charAt(k);
                                        }
                                        for (int l = j; l < btnStudentName.getText().length(); l++) {
                                            lastName += btnStudentName.getText().charAt(l);
                                        }
                                        pdf.createTable(btnQuizName, loginName.getText(), firstName, lastName);
                                    } catch (IOException | DocumentException | SQLException exc) {
                                        System.out.println(exc.getMessage());
                                    }
                                }
                            });
                            vbStudents.getChildren().add(btnStudentName);
                        }
                        Scene scene = new Scene(scrollPane, 250, 300);
                        scene.getStylesheets().add("style/SimpleLearnerGUI.css");
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
                        sql.loadQuestions(btnQuizName);
                    } catch (SQLException exc) {
                        System.out.println(exc.getMessage());
                    }
                    if (sql.getQuestions().size() > 0) {
                        setSectionPar(btnQuizName);
                        setQuestionPar(sql.getQuestions().indexOf(sql.getQuestions().get(taskNumber)));
                        sectionNameLabel.setText(btnQuizName);
                        fillAnswerSelection(btnQuizName, sql.getQuestions().get(taskNumber));
                        taskText.setText(sql.getQuestions().get(taskNumber));
                    } else {
                        setSectionPar(btnQuizName);
                        sectionNameLabel.setText(btnQuizName);
                        taskText.setText(null);
                        fillAnswerSelection(null, null);
                    }
                    //buildAufgabenPane();// Parameter
                    if (isTeacher || (!isTeacher && sql.getQuestions().size() > 0)) {
                        scene.setRoot(getTaskPane());
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

        SubjectButton(String input, int number) {
            btnSubjectName = input;
            taskNumber = number;
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
                    categoryString = btnSubjectName;
                    hString = "Module";
                    fillModule(categoryString);
                    tempStage.show();
                }
            });
        }

        BorderPane getCategoryButton() {
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
        int taskNumber;

        CategoryButton(String input, int number) {
            btnCategoryName = input;
            taskNumber = number;
            btnCategory = new Button(input);
            btnCategory.getStyleClass().add("btnCategory");
            btnCategory.setPrefWidth(scene.getWidth());
            btnCategory.setMinWidth(listContainer.getWidth()/*-btnLöschen.getPrefWidth()*/);
            setBtnName(mainStage);

            //btnLöschen = new Button("Loeschen");
            //btnLöschen.setStyle("-fx-background-color:rgb(255,50,50)");
            //btnLöschen.setPrefWidth(100);

            /*
            btnLöschen.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e){
                    //
                    
                    //
                }
            });
             */
        }

        String getBtnName() {
            return btnCategory.getText();
        }

        void setBtnName(Stage tempStage) {
            btnCategory.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    taskNumber = 0;

                    //buildAufgabenPane();// Parameter
                    setGName(btnCategoryName);
                    labelDirectory.setText(btnCategoryName);
                    moduleString = btnCategoryName;
                    hString = "Directory";
                    fillDirectory();
                    //setScene(getTaskPane());
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

    void setSectionPar(String par) {
        blockPar = par;
    }

    void setQuestionPar(int par) {
        numberQuestionPar = par;
    }

    void buildTaskPane() {
        btnSectionBack.getStyleClass().add("btn");
        btnChangeSectionName.getStyleClass().add("btn");
        btnChangeTaskText.getStyleClass().add("btn");
        btnAnswersSelction.getStyleClass().add("btn");
        btnConfirm.getStyleClass().add("btn");
        btnConfirm.setPrefWidth(120);
        btnNextTask.getStyleClass().add("btn");
        btnNextTask.setPrefWidth(120);
        btnFinish.getStyleClass().add("btn");
        btnFinish.setPrefWidth(120);

        tempPane.setId("aufgabePane");
        AnswerPane.setId("answerPane");
        topContainerTask.setId("topContainerTask");
        topContainerTask.setPrefHeight(50);
        topContainerTask.setMargin(btnSectionBack, new Insets(10, 10, 10, 10));

        sectionNameLabel.setId("sectionNameLabel");
        hbSectionName.getChildren().clear();
        hbSectionName.getChildren().add(sectionNameLabel);
        hbSectionName.setAlignment(Pos.CENTER_RIGHT);

        hbSectionName.setMargin(btnChangeSectionName, new Insets(10, 10, 10, 10));
        hbSectionName.setMargin(sectionNameLabel, new Insets(10, 10, 10, 10));

        if (isTeacher) {
            hbSectionName.getChildren().add(btnChangeSectionName);
            btnChangeSectionName.setAlignment(Pos.CENTER_RIGHT);
        }
        topContainerTask.getChildren().clear();
        topContainerTask.setRight(hbSectionName);
        if (isTeacher) {
            topContainerTask.setLeft(btnSectionBack);
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

        TaskPane.setCenter(tempPane);
        TaskPane.setRight(AnswerPane);
        TaskPane.setTop(topContainerTask);

        AnswerPane.setPrefWidth(500);
        answerSelection.setSpacing(5);

        AnswerPane.setAlignment(answerSelection, Pos.CENTER);
        VBox answer = new VBox(8);
        answer.setMargin(btnAnswersSelction, new Insets(10, 10, 10, 10));
        answer.setMargin(answerSelection, new Insets(15, 15, 15, 15));
        answer.getChildren().add(answerSelection);
        if (isTeacher) {
            answer.getChildren().add(btnAnswersSelction);
        }

        //Rename completed until here TODO!!!

        AnswerPane.setCenter(answer);//answerSelection
        //setBtnNeueAufgabe();    //Funktion auskommentiert (Z.338)
        //answerSelection.setBottom(btnNeueAntwort);
        AnswerPane.setAlignment(navigator, Pos.CENTER);
        AnswerPane.setBottom(navigator);
        auswertungAntwort.setFont(Font.font(20));
        navigator.getChildren().clear();
        navigator.add(auswertungAntwort, 0, 0);
        if (!isTeacher) {
            navigator.add(btnConfirm, 0, 1);
        } else {
            navigator.add(btnNextTask, 0, 1);
        }
        if (isTeacher) {
            navigator.add(btnZurueck, 1, 1);
        }
        navigator.setMargin(btnNextTask, new Insets(10, 10, 10, 10));
        navigator.setMargin(btnConfirm, new Insets(10, 10, 10, 10));
    }

    void fillAnswerSelection(String block, String frage) {
        if (block == null && frage == null) {
            answerSelection.getChildren().clear();
        } else {
            AntwortGroup = new ToggleGroup();
            answerSelection.getChildren().setAll();
            try {
                sql.loadAntworten(block, frage);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
            for (int i = 0; i < sql.getAntwortenTemp().size()/*anzahl der Antworten*/; i++) {
                // hole Aufgabenname der i.ten Aufgabeneinheit
                // Übergebe AufgabenName
                answerSelection.getChildren().add(new btnAntwort(sql.getAntwortenTemp().get(i)).getBtnAntwort());
            }
        }
    }

    void setBtnLoadAnswerChoice() {

        btnAnswersSelction.setOnAction(new EventHandler<ActionEvent>() {
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

                    for (int i = 0; i < answerSelection.getChildren().size(); i++) {
                        RadioButton rb = (RadioButton) answerSelection.getChildren().get(i);
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
                    tempScene.getStylesheets().add("style/SimpleLearnerGUI.css");
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
                                    fillAnswerSelection(blockPar, taskText.getText());
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
        btnChangeSectionName.setOnAction(e -> {
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
                        sectionNameLabel.setText(tf.getText());
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
            tempScene.getStylesheets().add("style/SimpleLearnerGUI.css");

            tempStage.setScene(tempScene);
            tempStage.show();
        });
    }

    void setBtnBack(Stage tempStage) {//öffnet nächste Aufgabe
        btnSectionBack.setOnAction((ActionEvent e) -> {
            //sql.endAufgabe();
            fillDirectory();
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
                tempScene.getStylesheets().add("style/SimpleLearnerGUI.css");
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
        btnConfirm.setPrefWidth(75);
        btnConfirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String antwort = null;
                if (AntwortGroup.getSelectedToggle() != null) {
                    antwort = AntwortGroup.getSelectedToggle().getUserData().toString();
                }

                //checkAntwort(antwort);
                if (!isTeacher) {
                    if (antwort != null) {
                        try {
                            sql.startBlock(blockPar, loginName.getText());
                            end = System.currentTimeMillis() - start;
                            if (sql.checkAntwort(blockPar, loginName.getText(), sql.getQuestions().get(numberQuestionPar), antwort) == true) {
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
                    if (numberQuestionPar < sql.getQuestions().size() - 1) {
                        navigator.add(btnNextTask, 0, 1);
                    } else {
                        navigator.getChildren().clear();
                        navigator.add(auswertungAntwort, 0, 0);
                        navigator.add(btnFinish, 0, 1);
                    }
                }
                /*else {
                    Stage stage = new Stage();
                    Label labelDirectory = new Label("Sie haben das Quiz vollständig bearbeitet.");
                    Button btnStudentName = new Button("Zurück zu den Aufgaben");
                    VBox vbStudents = new VBox();
                    vbStudents.getChildren().addAll(labelDirectory, btnStudentName);
                    Scene scene = new Scene(vbStudents);
                    stage.setScene(scene);
                    stage.show();
                }*/

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
                navigator.getChildren().removeAll(btnConfirm, btnNextTask, btnZurueck); // btnBestätigen und btnNächsteAufgabe löschen
                navigator.add(btnNextTask, 0, 1); // btnBestätigen ein
                navigator.add(btnZurueck, 1, 1);
                if (numberQuestionPar - 1 >= 0) {
                    taskText.setText(sql.getQuestions().get(numberQuestionPar - 1));
                    numberQuestionPar--;
                    fillAnswerSelection(blockPar, sql.getQuestions().get(numberQuestionPar));
                    auswertungAntwort.setText("");
                }

                tempStage.show();
            }
        });
    }

    void setBtnNextTask(Stage tempStage) {//öffnet nächste Aufgabe
        btnNextTask.setPrefWidth(75);
        btnNextTask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!(taskText.getText() != null && answerSelection.getChildren().size() == 0)) {

                    start = System.currentTimeMillis();
                    // exception durch erneutes Einfügen von btnBestätigen
                    // -> btnBestätigen löschen und erneut einfügen
                    navigator.getChildren().removeAll(btnConfirm, btnNextTask, btnZurueck); // btnBestätigen und btnNächsteAufgabe löschen
                    if (!isTeacher) {
                        navigator.add(btnConfirm, 0, 1);
                    } else {
                        navigator.add(btnNextTask, 0, 1);
                    }
                    if (isTeacher) {
                        navigator.add(btnZurueck, 1, 1);
                    }
                    if (isTeacher && (numberQuestionPar + 1 >= sql.getQuestions().size())) {
                        taskText.setText(null);
                        fillAnswerSelection(null, null);
                    } else {
                        taskText.setText(sql.getQuestions().get(numberQuestionPar + 1));
                        numberQuestionPar++;
                        fillAnswerSelection(blockPar, sql.getQuestions().get(numberQuestionPar));
                    }
                    auswertungAntwort.setText("");

                    tempStage.show();
                }
            }
        });
    }

    void setBtnConfirmQuiz(Stage tempStage) {//öffnet nächste Aufgabe
        btnFinish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //sql.endAufgabe();
                auswertungAntwort.setText("");
                navigator.getChildren().clear();
                navigator.add(btnConfirm, 0, 1);
                fillDirectory();
                scene.setRoot(getMainContainer());
                tempStage.setScene(scene);
                tempStage.show();
            }
        });
    }

    BorderPane getTaskPane() {
        return TaskPane;

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
// getTaskPane();
//Scene scene = new Scene(changeMeldungPane(getLoginPane()), 600, 600);
    Scene scene = new Scene(getLoginPane(), 600, 600);

    void loadStyleSheets() {
        scene.getStylesheets().add("style/SimpleLearnerGUI.css");
    }

    Stage mainStage = new Stage();

    void setPrimaryStage() {
        mainStage.setScene(scene);
    }
}
