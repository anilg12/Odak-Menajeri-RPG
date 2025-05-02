import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.time.LocalTime;
import java.util.*;
import java.util.Timer;

public class FocusManagerRPG extends Application {

    private int xp = 0;
    private int level = 1;
    private String rank = "Acemi";
    private String character = "Erkek";

    private Label xpLabel = new Label("XP: 0");
    private Label levelLabel = new Label("Seviye: 1");
    private Label rankLabel = new Label("Rütbe: Acemi");
    private VBox taskList = new VBox(5);
    private Timer timer = new Timer(true);
    private ImageView characterImage = new ImageView();

    private List<Pair<String, LocalTime>> reminders = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ChoiceBox<String> characterChoice = new ChoiceBox<>();
        characterChoice.getItems().addAll("Kadın", "Erkek");
        characterChoice.setValue("Erkek");
        setCharacterImage("Erkek");
        characterChoice.setOnAction(e -> {
            character = characterChoice.getValue();
            setCharacterImage(character);
        });

        TextField taskField = new TextField();
        taskField.setPromptText("Ders/Görev adı");
        TextField purposeField = new TextField();
        purposeField.setPromptText("Amaç");
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 12);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        Button addButton = new Button("Görev Ekle ve Hatırlatıcı Kur");

        addButton.setOnAction(e -> {
            String task = taskField.getText();
            String purpose = purposeField.getText();
            int hour = hourSpinner.getValue();
            int minute = minuteSpinner.getValue();
            if (!task.isEmpty()) {
                Label taskLabel = new Label("📝 " + task + " - " + purpose);
                taskList.getChildren().add(taskLabel);
                reminders.add(new Pair<>(task, LocalTime.of(hour, minute)));
                taskField.clear();
                purposeField.clear();
                addXP(25);
            }
        });

        HBox stats = new HBox(20, xpLabel, levelLabel, rankLabel);
        stats.setAlignment(Pos.CENTER);

        VBox characterBox = new VBox(10, characterImage, new Label("Karakter Seçimi:"), characterChoice);
        characterBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(15,
                characterBox,
                new Label("Görev Adı:"), taskField,
                new Label("Amaç:"), purposeField,
                new Label("Hatırlatma Saati:"), new HBox(5, hourSpinner, new Label(":"), minuteSpinner),
                addButton,
                new Separator(),
                stats,
                new Label("Görev Listesi:"), taskList);

        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 450, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Focus Manager RPG");
        primaryStage.show();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                for (Pair<String, LocalTime> reminder : reminders) {
                    if (reminder.getValue().equals(now)) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Görev Hatırlatıcı");
                            alert.setHeaderText("Zamanı Geldi: " + reminder.getKey());
                            alert.setContentText("Hadi başlayalım!");
                            alert.showAndWait();
                        });
                    }
                }
            }
        }, 0, 60000);
    }

    private void addXP(int amount) {
        xp += amount;
        xpLabel.setText("XP: " + xp);
        level = xp / 100 + 1;
        levelLabel.setText("Seviye: " + level);
        updateRank();
    }

    private void updateRank() {
        if (level >= 6) rank = "Usta";
        else if (level >= 3) rank = "Çalışkan";
        else rank = "Acemi";
        rankLabel.setText("Rütbe: " + rank);
    }

    private void setCharacterImage(String type) {
        String imagePath = type.equals("Kadın") ? "https://i.imgur.com/4Z2yZjX.png" : "https://i.imgur.com/UH3IPXg.png";
        Image img = new Image(imagePath, 100, 100, true, true);
        characterImage.setImage(img);
    }
}
