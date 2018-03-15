package org.altarplanner.app.config;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.domain.DateSpan;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.ServiceType;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServerEditor {

    @FXML private Button removeButton;
    @FXML private ListView<Server> serverListView;
    @FXML private TextField surnameTextField;
    @FXML private TextField forenameTextField;
    @FXML private TextField yearTextField;
    @FXML private CheckComboBox<DayOfWeek> weeklyAbsencesCheckComboBox;
    @FXML private CheckComboBox<Server> pairedWithCheckComboBox;
    @FXML private CheckComboBox<ServiceType> inabilitiesCheckComboBox;
    @FXML private Tab absencesTab;
    @FXML private Button removeAbsenceButton;
    @FXML private DatePicker absenceStartDatePicker;
    @FXML private DatePicker absenceEndDatePicker;
    @FXML private ListView<DateSpan> absencesListView;
    @FXML private Tab assignmentWishesTab;
    @FXML private Button removeAssignmentWishButton;
    @FXML private DatePicker assignmentWishDatePicker;
    @FXML private TextField assignmentWishTimeTextField;
    @FXML private ListView<LocalDateTime> assignmentWishesListView;

    private Config config;
    private Server selectedServer;
    private boolean applyMainChanges;
    private DateSpan selectedAbsence;
    private boolean applyAbsenceChanges;
    private LocalDateTime selectedAssignmentWish;
    private boolean applyAssignmentWishChanges;

    @FXML private void initialize() {
        serverListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Server item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getDesc());
                }
            }
        });

        serverListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyMainChanges = false;
                applyListViews();
                surnameTextField.setText(newValue.getSurname());
                forenameTextField.setText(newValue.getForename());
                yearTextField.setText(String.valueOf(newValue.getYear()));
                weeklyAbsencesCheckComboBox.getCheckModel().clearChecks();
                Optional.ofNullable(newValue.getWeeklyAbsences())
                        .ifPresent(days -> days.forEach(day -> weeklyAbsencesCheckComboBox.getCheckModel().check(day)));
                pairedWithCheckComboBox.getCheckModel().clearChecks();
                pairedWithCheckComboBox.getItems().setAll(serverListView.getItems().filtered(server -> server != newValue));
                Optional.ofNullable(newValue.getPairedWith())
                        .ifPresent(servers -> servers.forEach(server -> pairedWithCheckComboBox.getCheckModel().check(server)));

                absencesListView.getItems().setAll(newValue.getAbsences());
                if (!absencesListView.getItems().isEmpty()) {
                    absencesListView.getSelectionModel().selectFirst();
                    setAbsenceDisable(false);
                } else {
                    setAbsenceDisable(true);
                }

                assignmentWishesListView.getItems().setAll(newValue.getDateTimeOnWishes());
                if (!assignmentWishesListView.getItems().isEmpty()) {
                    assignmentWishesListView.getSelectionModel().selectFirst();
                    setAssignmentWishDisable(false);
                } else {
                    setAssignmentWishDisable(true);
                }

                selectedServer = newValue;
                applyMainChanges = true;
            }
        });

        surnameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyMainChanges) {
                selectedServer.setSurname(newValue);
                serverListView.getItems().sort(Server.getNaturalOrderComparator());
            }
        });

        forenameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyMainChanges) {
                selectedServer.setForename(newValue);
                serverListView.getItems().sort(Server.getNaturalOrderComparator());
            }
        });

        yearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyMainChanges) {
                try {
                    selectedServer.setYear(Integer.parseInt(newValue));
                    yearTextField.getStyleClass().remove("text-input-error");
                } catch (NumberFormatException e) {
                    if (!yearTextField.getStyleClass().contains("text-input-error"))
                        yearTextField.getStyleClass().add("text-input-error");
                }
            }
        });

        weeklyAbsencesCheckComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(DayOfWeek object) {
                return object.getDisplayName(TextStyle.FULL, Locale.getDefault());
            }

            @Override
            public DayOfWeek fromString(String string) {
                return null;
            }
        });

        weeklyAbsencesCheckComboBox.getItems().setAll(DayOfWeek.values());

        weeklyAbsencesCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super DayOfWeek>)c -> {
            if (applyMainChanges)
                selectedServer.setWeeklyAbsences(c.getList().parallelStream().collect(Collectors.toList()));
        });

        pairedWithCheckComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Server object) {
                return object.getDesc();
            }

            @Override
            public Server fromString(String string) {
                return null;
            }
        });

        pairedWithCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Server>) c -> {
            if (applyMainChanges) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        selectedServer.addAllPairedWith(c.getAddedSubList().parallelStream().collect(Collectors.toList()));
                    } else if (c.wasRemoved()) {
                        selectedServer.removeAllPairedWith(c.getRemoved().parallelStream().collect(Collectors.toList()));
                    }
                }
            }
        });

        inabilitiesCheckComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ServiceType object) {
                return object.getDesc();
            }

            @Override
            public ServiceType fromString(String string) {
                return null;
            }
        });

        inabilitiesCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super ServiceType>) c -> {
            if (applyMainChanges) {
                selectedServer.setInabilities(c.getList().parallelStream().collect(Collectors.toList()));
            }
        });

        absencesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DateSpan item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getDesc());
                }
            }
        });

        absencesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyAbsenceChanges = false;
                absenceStartDatePicker.setValue(newValue.getStart());
                absenceEndDatePicker.setValue(newValue.getEnd());
                selectedAbsence = newValue;
                applyAbsenceChanges = true;
            }
        });

        absenceStartDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (applyAbsenceChanges) {
                selectedAbsence.setStart(newValue);
                absencesListView.getItems().sort(DateSpan.getNaturalOrderComparator());
            }
        });

        absenceEndDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (applyAbsenceChanges) {
                selectedAbsence.setEnd(newValue);
                absencesListView.getItems().sort(DateSpan.getNaturalOrderComparator());
            }
        });

        assignmentWishesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)));
                }
            }
        });

        assignmentWishesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyAssignmentWishChanges = false;
                assignmentWishDatePicker.setValue(newValue.toLocalDate());
                assignmentWishTimeTextField.setText(newValue.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                selectedAssignmentWish = newValue;
                applyAssignmentWishChanges = true;
            }
        });

        assignmentWishDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (applyAssignmentWishChanges) {
                LocalDateTime newAssignmentWish = LocalDateTime.of(newValue, selectedAssignmentWish.toLocalTime());
                assignmentWishesListView.getItems().add(newAssignmentWish);
                assignmentWishesListView.getItems().remove(selectedAssignmentWish);
                assignmentWishesListView.getSelectionModel().select(newAssignmentWish);
                assignmentWishesListView.getItems().sort(Comparator.reverseOrder());
            }
        });

        assignmentWishTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyAssignmentWishChanges) {
                try {
                    LocalDateTime newAssignmentWish = LocalDateTime.of(selectedAssignmentWish.toLocalDate(), LocalTime.parse(newValue, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                    assignmentWishTimeTextField.getStyleClass().remove("text-input-error");
                    assignmentWishesListView.getItems().add(newAssignmentWish);
                    assignmentWishesListView.getItems().remove(selectedAssignmentWish);
                    assignmentWishesListView.getSelectionModel().select(newAssignmentWish);
                    assignmentWishesListView.getItems().sort(Comparator.reverseOrder());
                } catch (DateTimeParseException e) {
                    if (!assignmentWishTimeTextField.getStyleClass().contains("text-input-error"))
                        assignmentWishTimeTextField.getStyleClass().add("text-input-error");
                }
            }
        });

    }

    public void initData(Config config) {
        this.config = config;
        inabilitiesCheckComboBox.getItems().setAll(config.getServiceTypes());
        serverListView.getItems().setAll(config.getServers());
        if (!serverListView.getItems().isEmpty())
            serverListView.getSelectionModel().selectFirst();
        else
            setDisable(true);
    }

    private void setDisable(boolean disable) {
        applyMainChanges = false;
        removeButton.setDisable(disable);
        serverListView.setDisable(disable);
        surnameTextField.setDisable(disable);
        forenameTextField.setDisable(disable);
        yearTextField.setDisable(disable);
        weeklyAbsencesCheckComboBox.setDisable(disable);
        pairedWithCheckComboBox.setDisable(disable);
        inabilitiesCheckComboBox.setDisable(disable);
        absencesTab.setDisable(disable);
        assignmentWishesTab.setDisable(disable);
        if (disable) {
            surnameTextField.clear();
            forenameTextField.clear();
            yearTextField.clear();
            weeklyAbsencesCheckComboBox.getCheckModel().clearChecks();
            inabilitiesCheckComboBox.getCheckModel().clearChecks();
            setAbsenceDisable(true);
            setAssignmentWishDisable(true);
        }
    }

    private void setAbsenceDisable(boolean disable) {
        applyAbsenceChanges = !disable;
        removeAbsenceButton.setDisable(disable);
        absenceStartDatePicker.setDisable(disable);
        absenceEndDatePicker.setDisable(disable);
        absencesListView.setDisable(disable);
        if (disable) {
            absencesListView.getItems().clear();
            absenceStartDatePicker.setValue(null);
            absenceEndDatePicker.setValue(null);
        }
    }

    private void setAssignmentWishDisable(boolean disable) {
        applyAssignmentWishChanges = !disable;
        removeAssignmentWishButton.setDisable(disable);
        assignmentWishDatePicker.setDisable(disable);
        assignmentWishTimeTextField.setDisable(disable);
        assignmentWishesListView.setDisable(disable);
        if (disable) {
            assignmentWishesListView.getItems().clear();
            assignmentWishDatePicker.setValue(null);
            assignmentWishTimeTextField.clear();
        }
    }

    private void applyListViews() {
        if (selectedServer != null) {
            selectedServer.setAbsences(absencesListView.getItems().parallelStream().collect(Collectors.toList()));
            selectedServer.setDateTimeOnWishes(assignmentWishesListView.getItems().parallelStream().collect(Collectors.toList()));
        }
    }

    @FXML private void addServer() {
        Server server = new Server();
        serverListView.getItems().add(server);
        setDisable(false);
        serverListView.getSelectionModel().select(server);
        serverListView.getItems().sort(Server.getNaturalOrderComparator());
    }

    @FXML private void removeServer() {
        selectedServer.removeFromAllPairs();
        serverListView.getItems().remove(selectedServer);
        if (serverListView.getItems().isEmpty())
            setDisable(true);
    }

    @FXML private void loadLauncher() throws IOException {
        applyListViews();
        config.setServers(serverListView.getItems().parallelStream().collect(Collectors.toList()));
        config.save();
        Launcher.loadParent("launcher.fxml", launcher -> ((Launcher)launcher).initData(config));
    }

    @FXML private void addAbsence() {
        DateSpan absence = new DateSpan();
        absencesListView.getItems().add(absence);
        setAbsenceDisable(false);
        absencesListView.getSelectionModel().select(absence);
        absencesListView.getItems().sort(DateSpan.getNaturalOrderComparator());
    }

    @FXML private void removeAbsence() {
        absencesListView.getItems().remove(selectedAbsence);
        if (absencesListView.getItems().isEmpty())
            setAbsenceDisable(true);
    }

    @FXML private void addAssignmentWish() {
        LocalDateTime assignmentWish = LocalDateTime.of(LocalDate.now().plusMonths(1), LocalTime.of(11, 0));
        assignmentWishesListView.getItems().add(assignmentWish);
        setAssignmentWishDisable(false);
        assignmentWishesListView.getSelectionModel().select(assignmentWish);
        assignmentWishesListView.getItems().sort(Comparator.reverseOrder());
    }

    @FXML private void removeAssignmentWish() {
        assignmentWishesListView.getItems().remove(selectedAssignmentWish);
        if (assignmentWishesListView.getItems().isEmpty())
            setAssignmentWishDisable(true);
    }

}
