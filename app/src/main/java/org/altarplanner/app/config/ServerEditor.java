package org.altarplanner.app.config;

import java.io.File;
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
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.altarplanner.app.Launcher;
import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.ServiceType;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.util.LocalDateRangeUtil;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.LocalDateRange;

public class ServerEditor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerEditor.class);

  @FXML private Button removeButton;
  @FXML private ListView<Server> serverListView;
  @FXML private TextField surnameTextField;
  @FXML private TextField forenameTextField;
  @FXML private TextField yearTextField;
  @FXML private CheckComboBox<DayOfWeek> weeklyAbsencesCheckComboBox;
  @FXML private CheckComboBox<Server> pairedWithCheckComboBox;
  @FXML private CheckComboBox<ServiceType> inabilitiesCheckComboBox;
  @FXML private Tab absencesTab;
  @FXML private Button addAbsenceButton;
  @FXML private Button removeAbsenceButton;
  @FXML private DatePicker absenceStartDatePicker;
  @FXML private DatePicker absenceEndDatePicker;
  @FXML private ListView<LocalDateRange> absencesListView;
  @FXML private ContextMenu absencesListViewContextMenu;
  @FXML private MenuItem absencesListViewContextMenuItemCut;
  @FXML private MenuItem absencesListViewContextMenuItemCopy;
  @FXML private MenuItem absencesListViewContextMenuItemPaste;
  @FXML private MenuItem absencesListViewContextMenuItemDelete;
  @FXML private Tab assignmentWishesTab;
  @FXML private Button removeAssignmentWishButton;
  @FXML private DatePicker assignmentWishDatePicker;
  @FXML private TextField assignmentWishTimeTextField;
  @FXML private ListView<LocalDateTime> assignmentWishesListView;

  private boolean applyMainChanges;
  private boolean applyAbsenceChanges;
  private boolean applyAssignmentWishChanges;

  @FXML
  private void initialize() {
    serverListView.setCellFactory(
        param ->
            new ListCell<>() {
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

    serverListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              applyListViews(oldValue);

              if (newValue != null) {
                applyMainChanges = false;
                surnameTextField.setText(newValue.getSurname());
                forenameTextField.setText(newValue.getForename());
                yearTextField.setText(String.valueOf(newValue.getYear()));
                weeklyAbsencesCheckComboBox.getCheckModel().clearChecks();
                Optional.ofNullable(newValue.getWeeklyAbsences())
                    .ifPresent(
                        days ->
                            days.forEach(
                                day -> weeklyAbsencesCheckComboBox.getCheckModel().check(day)));
                pairedWithCheckComboBox.getCheckModel().clearChecks();
                pairedWithCheckComboBox
                    .getItems()
                    .setAll(serverListView.getItems().filtered(server -> server != newValue));
                Launcher.CONFIG
                    .getPairedWith(newValue)
                    .forEach(server -> pairedWithCheckComboBox.getCheckModel().check(server));
                inabilitiesCheckComboBox.getCheckModel().clearChecks();
                newValue
                    .getInabilities()
                    .forEach(
                        serviceType -> inabilitiesCheckComboBox.getCheckModel().check(serviceType));

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

    surnameTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyMainChanges) {
                serverListView.getSelectionModel().getSelectedItem().setSurname(newValue);
                serverListView.getItems().sort(Comparator.naturalOrder());
              }
            });

    forenameTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyMainChanges) {
                serverListView.getSelectionModel().getSelectedItem().setForename(newValue);
                serverListView.getItems().sort(Comparator.naturalOrder());
              }
            });

    yearTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyMainChanges) {
                try {
                  serverListView
                      .getSelectionModel()
                      .getSelectedItem()
                      .setYear(Integer.parseInt(newValue));
                  yearTextField.getStyleClass().remove("text-input-error");
                } catch (NumberFormatException e) {
                  if (!yearTextField.getStyleClass().contains("text-input-error")) {
                    yearTextField.getStyleClass().add("text-input-error");
                  }
                }
              }
            });

    weeklyAbsencesCheckComboBox.setConverter(
        new StringConverter<>() {
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

    weeklyAbsencesCheckComboBox
        .getCheckModel()
        .getCheckedItems()
        .addListener(
            (ListChangeListener<? super DayOfWeek>)
                change -> {
                  if (applyMainChanges) {
                    serverListView
                        .getSelectionModel()
                        .getSelectedItem()
                        .setWeeklyAbsences(List.copyOf(change.getList()));
                  }
                });

    pairedWithCheckComboBox.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(Server object) {
            return object.getDesc();
          }

          @Override
          public Server fromString(String string) {
            return null;
          }
        });

    pairedWithCheckComboBox
        .getCheckModel()
        .getCheckedItems()
        .addListener(
            (ListChangeListener<? super Server>)
                change -> {
                  if (applyMainChanges) {
                    while (change.next()) {
                      if (change.wasAdded()) {
                        change
                            .getAddedSubList()
                            .forEach(
                                pairedWith ->
                                    Launcher.CONFIG.addPair(
                                        new PairRequest(
                                            serverListView.getSelectionModel().getSelectedItem(),
                                            pairedWith)));
                      } else if (change.wasRemoved()) {
                        change
                            .getRemoved()
                            .forEach(
                                pairedWith ->
                                    Launcher.CONFIG.removePair(
                                        new PairRequest(
                                            serverListView.getSelectionModel().getSelectedItem(),
                                            pairedWith)));
                      }
                    }
                  }
                });

    inabilitiesCheckComboBox.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(ServiceType object) {
            return object.getDesc();
          }

          @Override
          public ServiceType fromString(String string) {
            return null;
          }
        });

    inabilitiesCheckComboBox
        .getCheckModel()
        .getCheckedItems()
        .addListener(
            (ListChangeListener<? super ServiceType>)
                change -> {
                  if (applyMainChanges) {
                    serverListView
                        .getSelectionModel()
                        .getSelectedItem()
                        .setInabilities(List.copyOf(change.getList()));
                  }
                });

    absencesListView.setCellFactory(
        param ->
            new ListCell<>() {
              @Override
              protected void updateItem(LocalDateRange item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                  setText(null);
                  setGraphic(null);
                } else {
                  setText(
                      item.getStart().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
                          + " - "
                          + item.getEndInclusive()
                              .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
                }
              }
            });

    absencesListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                applyAbsenceChanges = false;
                absenceStartDatePicker.setValue(newValue.getStart());
                absenceEndDatePicker.setValue(newValue.getEndInclusive());
                applyAbsenceChanges = true;
              }
            });

    absencesListView.addEventHandler(
        KeyEvent.KEY_PRESSED,
        event -> {
          if (new KeyCodeCombination(KeyCode.DELETE).match(event)) {
            removeAbsenceButton.fire();
          } else if (new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN).match(event)) {
            addAbsenceButton.fire();
          } else if (new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN).match(event)) {
            copyAbsence();
          } else if (new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN).match(event)) {
            cutAbsence();
          } else if (new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN).match(event)) {
            pasteAbsence();
          }
        });

    absencesListViewContextMenu.setOnShowing(
        event -> {
          final boolean hasSelection =
              absencesListView.getSelectionModel().getSelectedIndex() != -1;
          absencesListViewContextMenuItemCut.setDisable(!hasSelection);
          absencesListViewContextMenuItemCopy.setDisable(!hasSelection);
          absencesListViewContextMenuItemPaste.setDisable(
              !Clipboard.getSystemClipboard().hasString());
          absencesListViewContextMenuItemDelete.setDisable(!hasSelection);
        });

    absenceStartDatePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyAbsenceChanges) {
                replaceSelectedAbsence(
                    LocalDateRange.ofClosed(
                        newValue,
                        newValue.isAfter(absenceEndDatePicker.getValue())
                            ? newValue
                            : absenceEndDatePicker.getValue()));
              }
            });

    absenceEndDatePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyAbsenceChanges) {
                replaceSelectedAbsence(
                    LocalDateRange.ofClosed(
                        newValue.isBefore(absenceStartDatePicker.getValue())
                            ? newValue
                            : absenceStartDatePicker.getValue(),
                        newValue));
              }
            });

    assignmentWishesListView.setCellFactory(
        param ->
            new ListCell<>() {
              @Override
              protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                  setText(null);
                  setGraphic(null);
                } else {
                  setText(
                      item.format(
                          DateTimeFormatter.ofLocalizedDateTime(
                              FormatStyle.FULL, FormatStyle.SHORT)));
                }
              }
            });

    assignmentWishesListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                applyAssignmentWishChanges = false;
                assignmentWishDatePicker.setValue(newValue.toLocalDate());
                assignmentWishTimeTextField.setText(
                    newValue.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                applyAssignmentWishChanges = true;
              }
            });

    assignmentWishDatePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyAssignmentWishChanges) {
                LocalDateTime newAssignmentWish =
                    LocalDateTime.of(
                        newValue,
                        assignmentWishesListView
                            .getSelectionModel()
                            .getSelectedItem()
                            .toLocalTime());
                assignmentWishesListView.getItems().add(newAssignmentWish);
                assignmentWishesListView
                    .getItems()
                    .remove(assignmentWishesListView.getSelectionModel().getSelectedItem());
                assignmentWishesListView.getSelectionModel().select(newAssignmentWish);
                assignmentWishesListView.getItems().sort(Comparator.reverseOrder());
              }
            });

    assignmentWishTimeTextField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (applyAssignmentWishChanges) {
                try {
                  LocalDateTime newAssignmentWish =
                      LocalDateTime.of(
                          assignmentWishesListView
                              .getSelectionModel()
                              .getSelectedItem()
                              .toLocalDate(),
                          LocalTime.parse(
                              newValue, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                  assignmentWishTimeTextField.getStyleClass().remove("text-input-error");
                  assignmentWishesListView.getItems().add(newAssignmentWish);
                  assignmentWishesListView
                      .getItems()
                      .remove(assignmentWishesListView.getSelectionModel().getSelectedItem());
                  assignmentWishesListView.getSelectionModel().select(newAssignmentWish);
                  assignmentWishesListView.getItems().sort(Comparator.reverseOrder());
                } catch (DateTimeParseException e) {
                  if (!assignmentWishTimeTextField.getStyleClass().contains("text-input-error")) {
                    assignmentWishTimeTextField.getStyleClass().add("text-input-error");
                  }
                }
              }
            });

    inabilitiesCheckComboBox.getItems().setAll(Launcher.CONFIG.getServiceTypes());
    serverListView.getItems().setAll(Launcher.CONFIG.getServers());
    if (!serverListView.getItems().isEmpty()) {
      serverListView.getSelectionModel().selectFirst();
    } else {
      setDisable(true);
    }
  }

  private void replaceSelectedAbsence(LocalDateRange replacingAbsence) {
    absencesListView.getItems().remove(absencesListView.getSelectionModel().getSelectedItem());
    absencesListView.getItems().add(replacingAbsence);
    absencesListView.getItems().sort(LocalDateRangeUtil.RECENCY_COMPARATOR);
    absencesListView.getSelectionModel().select(replacingAbsence);
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

  private void applyListViews(Server server) {
    if (server != null) {
      server.setAbsences(List.copyOf(absencesListView.getItems()));
      server.setDateTimeOnWishes(List.copyOf(assignmentWishesListView.getItems()));
    }
  }

  @FXML
  private void addServer() {
    Server server = new Server();
    serverListView.getItems().add(server);
    setDisable(false);
    serverListView.getSelectionModel().select(server);
    serverListView.getItems().sort(Comparator.naturalOrder());
  }

  @FXML
  private void removeServer() {
    Launcher.CONFIG.removeAllPairsWith(serverListView.getSelectionModel().getSelectedItem());
    serverListView.getItems().remove(serverListView.getSelectionModel().getSelectedItem());
    if (serverListView.getItems().isEmpty()) {
      setDisable(true);
    }
  }

  @FXML
  private void importServers() throws IOException {
    final FileChooser serverWorkbookFileChooser = new FileChooser();
    serverWorkbookFileChooser.setTitle(
        Launcher.RESOURCE_BUNDLE.getString("fileChooserTitle.openServerWorkbook"));
    serverWorkbookFileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("Excel 2007–2019 (.xlsx)", "*.xlsx"));
    final File serverWorkbookFile =
        serverWorkbookFileChooser.showOpenDialog(removeButton.getScene().getWindow());

    if (serverWorkbookFile != null) {
      Launcher.loadParent(
          "config/serverImporter.fxml",
          false,
          serverImporter -> {
            ((ServerImporter) serverImporter).setInputFile(serverWorkbookFile.toPath());
            ((ServerImporter) serverImporter).setServers(List.copyOf(serverListView.getItems()));
            ((ServerImporter) serverImporter)
                .setServersConsumer(
                    servers -> {
                      serverListView.getItems().setAll(servers);
                      if (!serverListView.getItems().isEmpty()) {
                        setDisable(false);
                        serverListView.getItems().sort(Comparator.naturalOrder());
                        serverListView.getSelectionModel().selectFirst();
                      }
                    });
          });
    } else {
      LOGGER.info("No server workbook has been selected");
    }
  }

  @FXML
  private void saveAndBack() throws IOException {
    applyListViews(serverListView.getSelectionModel().getSelectedItem());
    Launcher.CONFIG.setServers(List.copyOf(serverListView.getItems()));
    Launcher.CONFIG.marshal(Launcher.CONFIG_PATH);
    Launcher.loadParent("launcher.fxml", true);
  }

  @FXML
  private void addAbsence() {
    LocalDateRange absence =
        LocalDateRange.ofClosed(LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1));
    absencesListView.getItems().add(absence);
    setAbsenceDisable(false);
    absencesListView.getSelectionModel().select(absence);
    absencesListView.getItems().sort(LocalDateRangeUtil.RECENCY_COMPARATOR);
  }

  @FXML
  private void removeAbsence() {
    absencesListView.getItems().remove(absencesListView.getSelectionModel().getSelectedItem());
    if (absencesListView.getItems().isEmpty()) {
      setAbsenceDisable(true);
    }
  }

  @FXML
  private void cutAbsence() {
    copyAbsence();
    removeAbsence();
  }

  @FXML
  private void copyAbsence() {
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent clipboardContent = new ClipboardContent();
    clipboardContent.putString(absencesListView.getSelectionModel().getSelectedItem().toString());
    clipboard.setContent(clipboardContent);
  }

  @FXML
  private void pasteAbsence() {
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    if (clipboard.hasString()) {
      final String content = clipboard.getString();
      try {
        final LocalDateRange dateRange = LocalDateRange.parse(content);
        absencesListView.getItems().add(dateRange);
        setAbsenceDisable(false);
        absencesListView.getSelectionModel().select(dateRange);
        absencesListView.getItems().sort(LocalDateRangeUtil.RECENCY_COMPARATOR);
      } catch (DateTimeParseException e) {
        LOGGER.info("'{}' is no valid date range", content);
      }
    }
  }

  @FXML
  private void addAssignmentWish() {
    LocalDateTime assignmentWish =
        LocalDateTime.of(LocalDate.now().plusMonths(1), LocalTime.of(11, 0));
    assignmentWishesListView.getItems().add(assignmentWish);
    setAssignmentWishDisable(false);
    assignmentWishesListView.getSelectionModel().select(assignmentWish);
    assignmentWishesListView.getItems().sort(Comparator.reverseOrder());
  }

  @FXML
  private void removeAssignmentWish() {
    assignmentWishesListView
        .getItems()
        .remove(assignmentWishesListView.getSelectionModel().getSelectedItem());
    if (assignmentWishesListView.getItems().isEmpty()) {
      setAssignmentWishDisable(true);
    }
  }
}
