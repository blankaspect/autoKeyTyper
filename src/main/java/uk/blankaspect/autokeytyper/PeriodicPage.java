/*====================================================================*\

PeriodicPage.java

Class: 'periodic' page.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Robot;

import java.lang.invoke.MethodHandles;

import java.time.LocalTime;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Platform;

import javafx.css.PseudoClass;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.stage.Stage;

import javafx.util.Duration;

import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.GraphicButton;
import uk.blankaspect.ui.jfx.button.ImageDataButton;

import uk.blankaspect.ui.jfx.container.PaneStyle;

import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.icon.Icons;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;
import uk.blankaspect.ui.jfx.spinner.SpinnerFactory;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: 'PERIODIC' PAGE


public class PeriodicPage
	implements IPage
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int		MIN_INTERVAL_VALUE		= 1;
	private static final	int		MAX_INTERVAL_VALUE		= 999;
	private static final	int		DEFAULT_INTERVAL_VALUE	= 30;
	private static final	TimeUnit	DEFAULT_INTERVAL_UNIT	= TimeUnit.SECONDS;

	/** The preferred number of columns of the key field. */
	private static final	int		KEY_FIELD_NUM_COLUMNS	= 15;

	/** The maximum number of digits of the interval-value spinner. */
	private static final	int		INTERVAL_VALUE_SPINNER_NUM_DIGITS	= 3;

	/** The horizontal gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The vertical gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_V_GAP	= 8.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(6.0, 4.0, 6.0, 10.0);

	/** The factor by which the size of the default font is multiplied to give the size of the font of the status
		label. */
	private static final	double	STATUS_LABEL_FONT_SIZE_FACTOR	= 1.333;

	/** The suffix of a string representation of a font size in a style property. */
	private static final	String	FONT_SIZE_SUFFIX	= "px";

	/** The padding around the status pane. */
	private static final	Insets	STATUS_PANE_PADDING	= new Insets(8.0, 12.0, 8.0, 12.0);

	/** The padding around a button. */
	private static final	Insets	BUTTON_PADDING	= new Insets(4.0, 16.0, 4.0, 16.0);

	/** The horizontal gap between adjacent buttons of the button pane. */
	private static final	double	BUTTON_PANE_H_GAP	= 12.0;

	/** The vertical gap between adjacent buttons of the button pane. */
	private static final	double	BUTTON_PANE_V_GAP	= 6.0;

	/** The padding around the button pane. */
	private static final	Insets	BUTTON_PANE_PADDING	= new Insets(4.0, 6.0, 0.0, 6.0);

	/** The formatter for the time of the last generated key press. */
	private static final	DateTimeFormatter	TIME_FORMATTER	= DateTimeFormatter.ofPattern("HH:mm:ss");

	/** A list of information about keys. */
	private static final	List<KeyInfo>	KEY_INFOS;

	/** Miscellaneous strings. */
	private static final	String	KEY_STR			= "Key";
	private static final	String	EDIT_KEY_STR	= "Edit key";
	private static final	String	CLEAR_KEY_STR	= "Clear key";
	private static final	String	INTERVAL_STR	= "Interval";
	private static final	String	START_STR		= "Start";
	private static final	String	STOP_STR		= "Stop";

	/** The pseudo-class that is associated with the <i>highlighted</i> state. */
	private static final	PseudoClass	HIGHLIGHTED_PSEUDO_CLASS	=
			PseudoClass.getPseudoClass(PseudoClassKey.HIGHLIGHTED);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.CLEAR_KEY_BUTTON_DISC,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(Icons.StyleClass.CLEAR01_DISC)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.CLEAR_KEY_BUTTON_CROSS,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(Icons.StyleClass.CLEAR01_CROSS)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.CONTROL_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.STATUS_LABEL_TEXT,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.STATUS_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.STATUS_LABEL_TEXT_HIGHLIGHTED,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.STATUS_LABEL).pseudo(PseudoClassKey.HIGHLIGHTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.STATUS_PANE_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.STATUS_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.STATUS_PANE_BACKGROUND_HIGHLIGHTED,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.STATUS_PANE).pseudo(PseudoClassKey.HIGHLIGHTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.STATUS_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.PERIODIC_PAGE)
						.desc(StyleClass.STATUS_LABEL)
						.build())
				.fontSize(Math.round(FontUtils.getSize(STATUS_LABEL_FONT_SIZE_FACTOR)) + FONT_SIZE_SUFFIX)
				.boldFont()
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	CONTROL_PANE	= StyleConstants.CLASS_PREFIX + "control-pane";
		String	PERIODIC_PAGE	= StyleConstants.CLASS_PREFIX + "periodic-page";
		String	STATUS_LABEL	= StyleConstants.CLASS_PREFIX + "status-label";
		String	STATUS_PANE		= StyleConstants.CLASS_PREFIX + "status-pane";
	}

	/** Keys of CSS pseudo-classes. */
	public interface PseudoClassKey
	{
		String	HIGHLIGHTED	= "highlighted";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CLEAR_KEY_BUTTON_CROSS				= PREFIX + "clearKeyButton.cross";
		String	CLEAR_KEY_BUTTON_DISC				= PREFIX + "clearKeyButton.disc";
		String	STATUS_LABEL_TEXT					= PREFIX + "statusLabel.text";
		String	STATUS_LABEL_TEXT_HIGHLIGHTED		= PREFIX + "statusLabel.text.highlighted";
		String	STATUS_PANE_BACKGROUND				= PREFIX + "statusPane.background";
		String	STATUS_PANE_BACKGROUND_HIGHLIGHTED	= PREFIX + "statusPane.background.highlighted";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The saved state of this page. */
	private	State						state;

	/** Information about the current key. */
	private	KeyInfo						keyInfo;

	/** The time of the last generated key press. */
	private	LocalTime					keyPressTime;

	/** The <i>edit key</i> button. */
	private	ImageDataButton				editKeyButton;

	/** The text field in which a key is selected. */
	private	TextField					keyField;

	/** The spinner for the numerical part of the interval between generated key presses. */
	private	Spinner<Integer>			intervalValueSpinner;

	/** The spinner for the unit (seconds or minutes) of the interval between generated key presses. */
	private	CollectionSpinner<TimeUnit>	intervalUnitSpinner;

	/** The label for the time of the last generated key press, {@link #keyPressTime}. */
	private	Label						statusLabel;

	/** The pane that contains {@link #statusLabel}. */
	private	StackPane					statusPane;

	/** The <i>start</i> button. */
	private	Button						startButton;

	/** The <i>stop</i> button. */
	private	Button						stopButton;

	/** The outer pane of this page. */
	private	HBox						pane;

	/** The scene that contains this page. */
	private	Scene						scene;

	/** The interval timer. */
	private	Timeline					timer;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(PeriodicPage.class, COLOUR_PROPERTIES, RULE_SETS,
									   PaneStyle.class);

		// Create list of key information
		KEY_INFOS = new ArrayList<>();
		for (KeyCode keyCode : KeyCode.values())
		{
			int code = keyCode.getCode();
			if (code >= 0)
				KEY_INFOS.add(new KeyInfo(keyCode.name(), keyCode.getName(), code));
		}
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public PeriodicPage()
	{
		// Initialise instance variables
		state = new State();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static KeyInfo keyInfoForId(
		String	id)
	{
		return KEY_INFOS.stream().filter(keyInfo -> keyInfo.id.equals(id)).findFirst().orElse(null);
	}

	//------------------------------------------------------------------

	private static KeyInfo keyInfoForCode(
		int	code)
	{
		return KEY_INFOS.stream().filter(keyInfo -> keyInfo.code == code).findFirst().orElse(null);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the current theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the current theme of the style
	 *         manager, or {@link StyleManager#DEFAULT_COLOUR} if there is no such colour.
	 */

	private static Color getColour(
		String	key)
	{
		return StyleManager.INSTANCE.getColourOrDefault(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IPage interface
////////////////////////////////////////////////////////////////////////

	@Override
	public HBox pane()
	{
		return pane;
	}

	//------------------------------------------------------------------

	@Override
	public MapNode encodeState()
	{
		// Update state
		state.keyInfo = keyInfo;
		state.intervalValue = intervalValueSpinner.getValue();
		state.intervalUnit = intervalUnitSpinner.getItem();

		// Encode state and return result
		return state.encodeTree();
	}

	//------------------------------------------------------------------

	@Override
	public void decodeState(
		MapNode	rootNode)
	{
		state.decodeTree(rootNode);
	}

	//------------------------------------------------------------------

	@Override
	public void onExiting()
	{
		stopButton.fire();
	}

	//------------------------------------------------------------------

	@Override
	public void onWindowShown()
	{
		if (scene == null)
		{
			scene = pane.getScene();

			// Handle 'key pressed' events
			scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
			{
				if (keyField.isEditable() && keyField.isFocused())
				{
					// Get key code
					int code = event.getCode().getCode();

					// Display name of key in field
					keyInfo = keyInfoForCode(code);
					keyField.setText((keyInfo == null) ? "" : keyInfo.name);
					keyField.end();

					// Consume event
					event.consume();
				}
			});

			//
			if (scene.getWindow() instanceof Stage window)
			{
				// Create procedure to start or pause timer
				IProcedure0 updateTimer = () ->
				{
					// Start or pause timer
					if (timer != null)
					{
						if (!window.isIconified() && window.isFocused())
							timer.play();
						else
							timer.pause();
					}

					// Update status
					updateStatus();
				};

				// Start/pause timer when window gains/loses focus
				window.focusedProperty().addListener(observable -> updateTimer.invoke());

				// Start/pause timer when 'iconified' state of window changes
				window.iconifiedProperty().addListener(observable -> updateTimer.invoke());
			}
		}

		intervalValueSpinner.requestFocus();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void init(
		Robot					robot,
		IProcedure1<Boolean>	enableExternalControls)
	{
		// Create control pane
		GridPane controlPane = new GridPane();
		controlPane.setHgap(CONTROL_PANE_H_GAP);
		controlPane.setVgap(CONTROL_PANE_V_GAP);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);
		controlPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER)));
		controlPane.getStyleClass().add(StyleClass.CONTROL_PANE);
		VBox.setVgrow(controlPane, Priority.ALWAYS);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		controlPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		controlPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		controlPane.getColumnConstraints().add(column);

		// Initialise row index
		int row = 0;

		// Text field: key
		keyField = new TextField();
		if (state.keyInfo != null)
		{
			keyInfo = state.keyInfo;
			keyField.setText(keyInfo.name);
		}
		keyField.setPrefColumnCount(KEY_FIELD_NUM_COLUMNS);
		keyField.addEventHandler(KeyEvent.KEY_TYPED, event -> event.consume());
		keyField.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
		{
			if (!keyField.isEditable())
				event.consume();
		});
		HBox.setMargin(keyField, new Insets(0.0, 2.0, 0.0, 0.0));

		// Create procedure to update key field
		IProcedure0 updateKeyField = () ->
		{
			boolean editing = editKeyButton.isSelected();
			keyField.setFocusTraversable(editing);
			keyField.setEditable(editing);
			if (editing)
				keyField.requestFocus();
		};

		// Button: edit key
		editKeyButton = new ImageDataButton(Images.ImageId.PENCIL, EDIT_KEY_STR);
		editKeyButton.setSelectable(true);
		editKeyButton.selectedProperty().addListener(observable -> updateKeyField.invoke());

		// Button: clear key
		Group clearIcon = Icons.clear01(getColour(ColourKey.CLEAR_KEY_BUTTON_DISC),
										getColour(ColourKey.CLEAR_KEY_BUTTON_CROSS));
		GraphicButton clearKeyButton = new GraphicButton(clearIcon, CLEAR_KEY_STR);
		clearKeyButton.setOnAction(event ->
		{
			keyInfo = null;
			keyField.clear();
			if (keyField.isEditable())
				Platform.runLater(() -> keyField.requestFocus());
		});

		// Pane: key
		HBox keyPane = new HBox(2.0, keyField, editKeyButton, clearKeyButton);
		keyPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(KEY_STR), keyPane);

		// Spinner: interval value
		intervalValueSpinner = SpinnerFactory.integerSpinner(MIN_INTERVAL_VALUE, MAX_INTERVAL_VALUE,
															 state.intervalValue, INTERVAL_VALUE_SPINNER_NUM_DIGITS);

		// Spinner: interval unit
		intervalUnitSpinner = CollectionSpinner.leftRightH(HPos.CENTER, true, TimeUnit.class, state.intervalUnit, null,
														   null);

		// Pane: interval
		HBox intervalPane = new HBox(CONTROL_PANE_H_GAP, intervalValueSpinner, intervalUnitSpinner);
		intervalPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(INTERVAL_STR), intervalPane);

		// Create status label
		statusLabel = new Label();
		statusLabel.setFont(FontUtils.boldFont(STATUS_LABEL_FONT_SIZE_FACTOR));
		statusLabel.setTextFill(getColour(ColourKey.STATUS_LABEL_TEXT));
		statusLabel.getStyleClass().add(StyleClass.STATUS_LABEL);

		// Create status pane
		statusPane = new StackPane(statusLabel);
		statusPane.setPadding(STATUS_PANE_PADDING);
		statusPane.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.STATUS_PANE_BACKGROUND)));
		statusPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER)));
		statusPane.getStyleClass().add(StyleClass.STATUS_PANE);
		HBox.setHgrow(statusPane, Priority.ALWAYS);

		// Create procedure to update UI components
		IProcedure0 updateComponents = () ->
		{
			updateStatus();

			boolean timerActive = (timer != null);
			controlPane.setDisable(timerActive);
			startButton.setDisable(timerActive || (keyInfo == null));
			stopButton.setDisable(!timerActive);
		};

		// Create button: start
		startButton = Buttons.hExpansive(START_STR);
		startButton.setPadding(BUTTON_PADDING);
		startButton.setOnAction(event ->
		{
			if (keyInfo != null)
			{
				// Reset time of last key press
				keyPressTime = null;

				// Create timer to generate key presses periodically
				int interval = intervalValueSpinner.getValue();
				if (intervalUnitSpinner.getItem() == TimeUnit.MINUTES)
					interval *= 60;
				timer = new Timeline(new KeyFrame(Duration.seconds((double)interval), event0 ->
				{
					// Generate key press and release
					robot.keyPress(keyInfo.code);
					robot.keyRelease(keyInfo.code);

					// Update time of last key press
					keyPressTime = LocalTime.now();

					// Update status
					updateStatus();
				}));
				timer.setCycleCount(Animation.INDEFINITE);

				// Request focus on 'stop' button
				stopButton.requestFocus();

				// Start timer
				timer.play();

				// Update UI components
				updateComponents.invoke();

				// Disable external controls
				enableExternalControls.invoke(false);
			}
		});

		// Create button: stop
		stopButton = Buttons.hExpansive(STOP_STR);
		stopButton.setPadding(BUTTON_PADDING);
		stopButton.setOnAction(event ->
		{
			// Stop timer and invalidate it
			if (timer != null)
			{
				// Stop timer
				timer.stop();

				// Invalidate timer
				timer = null;
			}

			// Update UI components
			updateComponents.invoke();

			// Enable external controls
			enableExternalControls.invoke(true);
		});

		// Create button pane
		TilePane buttonPane = new TilePane(BUTTON_PANE_H_GAP, BUTTON_PANE_V_GAP, startButton, stopButton);
		buttonPane.setPrefColumns(buttonPane.getChildren().size());
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		buttonPane.setPadding(BUTTON_PANE_PADDING);

		// Create left pane
		VBox leftPane = new VBox(4.0, controlPane, buttonPane);
		leftPane.setAlignment(Pos.TOP_CENTER);

		// Create outer pane
		pane = new HBox(4.0, leftPane, statusPane);
		pane.setAlignment(Pos.CENTER);
		pane.getStyleClass().add(StyleClass.PERIODIC_PAGE);

		// Update UI components when content of key field changes
		keyField.textProperty().addListener(observable -> updateComponents.invoke());

		// Update key field
		updateKeyField.invoke();

		// Update UI components
		updateComponents.invoke();
	}

	//------------------------------------------------------------------

	private void updateStatus()
	{
		// Update status label
		statusLabel.setText(((timer == null) || (keyPressTime == null))
												? " "
												: TIME_FORMATTER.format(keyPressTime));
		boolean timerRunning = (timer != null) && (timer.getStatus() == Animation.Status.RUNNING);
		String colourKey = timerRunning ? ColourKey.STATUS_LABEL_TEXT_HIGHLIGHTED : ColourKey.STATUS_LABEL_TEXT;
		statusLabel.setTextFill(getColour(colourKey));
		statusLabel.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, timerRunning);

		// Update status pane
		colourKey = timerRunning ? ColourKey.STATUS_PANE_BACKGROUND_HIGHLIGHTED : ColourKey.STATUS_PANE_BACKGROUND;
		statusPane.setBackground(SceneUtils.createColouredBackground(getColour(colourKey)));
		statusPane.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, timerRunning);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: TIME UNIT


	private enum TimeUnit
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		SECONDS,
		MINUTES;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	String	text;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TimeUnit()
		{
			// Initialise instance variables
			key = text = name().toLowerCase();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: INFORMATION ABOUT A KEY


	private record KeyInfo(
		String	id,
		String	name,
		int		code)
	{

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public boolean equals(
			Object	obj)
		{
			if (this == obj)
				return true;

			return (obj instanceof KeyInfo other) && (code == other.code);
		}

		//--------------------------------------------------------------

		@Override
		public int hashCode()
		{
			return code;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: STATE


	private static class State
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** Keys of properties. */
		private interface PropertyKey
		{
			String	INTERVAL	= "interval";
			String	KEY			= "key";
			String	UNIT		= "unit";
			String	VALUE		= "value";
		}

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private KeyInfo		keyInfo;
		private int			intervalValue;
		private TimeUnit	intervalUnit;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private State()
		{
			// Initialise instance variables
			intervalValue = DEFAULT_INTERVAL_VALUE;
			intervalUnit = DEFAULT_INTERVAL_UNIT;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private MapNode encodeTree()
		{
			// Create root node
			MapNode rootNode = new MapNode();

			// Encode key name
			if (keyInfo != null)
				rootNode.addString(PropertyKey.KEY, keyInfo.id);

			// Encode interval
			MapNode intervalNode = rootNode.addMap(PropertyKey.INTERVAL);
			intervalNode.addInt(PropertyKey.VALUE, intervalValue);
			intervalNode.addString(PropertyKey.UNIT, intervalUnit.key);

			// Return root node
			return rootNode;
		}

		//--------------------------------------------------------------

		private void decodeTree(
			MapNode	rootNode)
		{
			// Decode key
			String key = PropertyKey.KEY;
			if (rootNode.hasString(key))
				keyInfo = keyInfoForId(rootNode.getString(key));

			// Decode interval
			key = PropertyKey.INTERVAL;
			if (rootNode.hasMap(key))
			{
				MapNode intervalNode = rootNode.getMapNode(key);
				int value = intervalNode.getInt(PropertyKey.VALUE, 0);
				TimeUnit unit = intervalNode.getEnumValue(TimeUnit.class, PropertyKey.UNIT, u -> u.key, null);
				if ((value >= MIN_INTERVAL_VALUE) && (value <= MAX_INTERVAL_VALUE) && (unit != null))
				{
					intervalValue = value;
					intervalUnit = unit;
				}
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
