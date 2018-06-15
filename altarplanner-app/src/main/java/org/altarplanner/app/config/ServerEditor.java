package org.altarplanner.app.config;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Config;
import org.altarplanner.core.util.LocalDateInterval;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.xml.UnknownJAXBException;
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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
    @FXML private ListView<LocalDateInterval> absencesListView;
    @FXML private Tab assignmentWishesTab;
    @FXML private Button removeAssignmentWishButton;
    @FXML private DatePicker assignmentWishDatePicker;
    @FXML private TextField assignmentWishTimeTextField;
    @FXML private ListView<LocalDateTime> assignmentWishesListView;

    private Config config;
    private boolean applyMainChanges;
    private LocalDateInterval selectedAbsence;
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
                config.getPairedWith(newValue).forEach(server -> pairedWithCheckComboBox.getCheckModel().check(server));
                inabilitiesCheckComboBox.getCheckModel().clearChecks();
                newValue.getInabilities().forEach(serviceType -> inabilitiesCheckComboBox.getCheckModel().check(serviceType));

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

                applyMainChanges = true;
            }
        });

        surnameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyMainChanges) {
                serverListView.getSelectionModel().getSelectedItem().setSurname(newValue);
                serverListView.getItems().sort(Server.getDescComparator());
            }
        });

        forenameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyMainChanges) {
                serverListView.getSelectionModel().getSelectedItem().setForename(newValue);
                serverListView.getItems().sort(Server.getDescComparator());
            }
        });

        yearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (applyMainChanges) {
                try {
                    serverListView.getSelectionModel().getSelectedItem().setYear(Integer.parseInt(newValue));
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

        weeklyAbsencesCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super DayOfWeek>) change -> {
            if (applyMainChanges)
                serverListView.getSelectionModel().getSelectedItem().setWeeklyAbsences(List.copyOf(change.getList()));
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

        pairedWithCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Server>) change -> {
            if (applyMainChanges) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        change.getAddedSubList().forEach(pairedWith -> config.addPair(new PairRequest(serverListView.getSelectionModel().getSelectedItem(), pairedWith)));
                    } else if (change.wasRemoved()) {
                        change.getRemoved().forEach(pairedWith -> config.removePair(new PairRequest(serverListView.getSelectionModel().getSelectedItem(), pairedWith)));
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

        inabilitiesCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super ServiceType>) change -> {
            if (applyMainChanges) {
                serverListView.getSelectionModel().getSelectedItem().setInabilities(List.copyOf(change.getList()));
            }
        });

        absencesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(LocalDateInterval item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
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
                LocalDateInterval replaceAbsence = selectedAbsence;
                absencesListView.getItems().remove(replaceAbsence);
                replaceAbsence = LocalDateInterval.of(newValue, replaceAbsence.getEnd());
                absencesListView.getItems().add(replaceAbsence);
                absencesListView.getItems().sort(LocalDateInterval.getRecencyComparator());
                absencesListView.getSelectionModel().select(replaceAbsence);
            }
        });

        absenceEndDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (applyAbsenceChanges) {
                LocalDateInterval replaceAbsence = selectedAbsence;
                absencesListView.getItems().remove(replaceAbsence);
                replaceAbsence = LocalDateInterval.of(replaceAbsence.getStart(), newValue);
                absencesListView.getItems().add(replaceAbsence);
                absencesListView.getItems().sort(LocalDateInterval.getRecencyComparator());
                absencesListView.getSelectionModel().select(replaceAbsence);
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
        if (serverListView.getSelectionModel().getSelectedItem() != null) {
            serverListView.getSelectionModel().getSelectedItem().setAbsences(List.copyOf(absencesListView.getItems()));
            serverListView.getSelectionModel().getSelectedItem().setDateTimeOnWishes(List.copyOf(assignmentWishesListView.getItems()));
        }
    }

    @FXML private void addServer() {
        Server server = new Server();
        serverListView.getItems().add(server);
        setDisable(false);
        serverListView.getSelectionModel().select(server);
        serverListView.getItems().sort(Server.getDescComparator());
    }

    @FXML private void removeServer() {
        config.removeAllPairsWith(serverListView.getSelectionModel().getSelectedItem());
        serverListView.getItems().remove(serverListView.getSelectionModel().getSelectedItem());
        if (serverListView.getItems().isEmpty())
            setDisable(true);
    }

    @FXML private void saveAndBack() throws IOException, UnknownJAXBException {
        applyListViews();
        config.setServers(List.copyOf(serverListView.getItems()));
        config.save();
        Launcher.loadParent("launcher.fxml", true, launcher -> ((Launcher)launcher).initData(config));
    }

    @FXML private void addAbsence() {
        LocalDateInterval absence = LocalDateInterval.of(LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1));
        absencesListView.getItems().add(absence);
        setAbsenceDisable(false);
        absencesListView.getSelectionModel().select(absence);
        absencesListView.getItems().sort(LocalDateInterval.getRecencyComparator());
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
