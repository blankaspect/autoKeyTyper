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

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

import uk.blankaspect.ui.jfx.dropdownlist.SimpleDropDownList;

import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.icon.Icons;

import uk.blankaspect.ui.jfx.popup.ActionLabelPopUp;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.SpinnerFactory;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: 'PERIODIC' PAGE


public class PeriodicPage
	implements IPage
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The minimum interval value. */
	private static final	int		MIN_INTERVAL_VALUE	= 1;

	/** The maximum interval value. */
	private static final	int		MAX_INTERVAL_VALUE	= 999;

	/** The default interval value. */
	private static final	int		DEFAULT_INTERVAL_VALUE	= 30;

	/** The default interval unit. */
	private static final	TimeUnit	DEFAULT_INTERVAL_UNIT	= TimeUnit.SECONDS;

	/** The preferred number of columns of the key field. */
	private static final	int		KEY_FIELD_NUM_COLUMNS	= 15;

	/** The maximum number of digits of the interval-value spinner. */
	private static final	int		INTERVAL_VALUE_SPINNER_NUM_DIGITS	= 3;

	/** The horizontal gap between adjacent components in a container. */
	private static final	double	CONTROL_H_GAP	= 6.0;

	/** The vertical gap between adjacent components in a container. */
	private static final	double	CONTROL_V_GAP	= 8.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(6.0, 6.0, 6.0, 12.0);

	/** The factor by which the size of the default font is multiplied to give the size of the font of the time
		label. */
	private static final	double	TIME_LABEL_FONT_SIZE_FACTOR	= 1.333;

	/** The suffix of a string representation of a font size in a style property. */
	private static final	String	FONT_SIZE_SUFFIX	= "px";

	/** The padding around the time pane. */
	private static final	Insets	TIME_PANE_PADDING	= new Insets(8.0, 12.0, 8.0, 12.0);

	/** The padding around a command button. */
	private static final	Insets	BUTTON_PADDING	= new Insets(4.0, 16.0, 4.0, 16.0);

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
	private static final	String	ERROR_STR		= "Error";
	private static final	String	MOVE_PANE_STR	= "Move pane to the ";
	private static final	String	LEFT_STR		= "left";
	private static final	String	RIGHT_STR		= "right";

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
			ColourKey.TIME_LABEL_TEXT,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.TIME_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.TIME_LABEL_TEXT_HIGHLIGHTED,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.TIME_LABEL).pseudo(PseudoClassKey.HIGHLIGHTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.TIME_PANE_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.TIME_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.TIME_PANE_BACKGROUND_HIGHLIGHTED,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.TIME_PANE).pseudo(PseudoClassKey.HIGHLIGHTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.PERIODIC_PAGE)
					.desc(StyleClass.TIME_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.PERIODIC_PAGE)
						.desc(StyleClass.TIME_LABEL)
						.build())
				.fontSize(Math.round(FontUtils.getSize(TIME_LABEL_FONT_SIZE_FACTOR)) + FONT_SIZE_SUFFIX)
				.boldFont()
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	CONTROL_PANE	= StyleConstants.CLASS_PREFIX + "control-pane";
		String	PERIODIC_PAGE	= StyleConstants.CLASS_PREFIX + "periodic-page";
		String	TIME_LABEL		= StyleConstants.CLASS_PREFIX + "time-label";
		String	TIME_PANE		= StyleConstants.CLASS_PREFIX + "time-pane";
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
		String	TIME_LABEL_TEXT						= PREFIX + "timeLabel.text";
		String	TIME_LABEL_TEXT_HIGHLIGHTED			= PREFIX + "timeLabel.text.highlighted";
		String	TIME_PANE_BACKGROUND				= PREFIX + "timePane.background";
		String	TIME_PANE_BACKGROUND_HIGHLIGHTED	= PREFIX + "timePane.background.highlighted";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	PROBLEM_WITH_KEY_EVENT_GENERATOR	= "There was a problem with the key-event generator.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The saved state of this page. */
	private	State							state;

	/** Information about the current key. */
	private	KeyInfo							keyInfo;

	/** The time of the last generated key press. */
	private	LocalTime						keyPressTime;

	/** The interval timer. */
	private	Timeline						timer;

	/** The <i>edit key</i> button. */
	private	ImageDataButton					editKeyButton;

	/** The text field in which a key is selected. */
	private	TextField						keyField;

	/** The spinner for the numerical part of the interval between generated key presses. */
	private	Spinner<Integer>				intervalValueSpinner;

	/** The drop-down list for the unit (seconds or minutes) of the interval between generated key presses. */
	private	SimpleDropDownList<TimeUnit>	intervalUnitList;

	/** The label for the time of the last generated key press, {@link #keyPressTime}. */
	private	Label							timeLabel;

	/** The pane that contains {@link #timeLabel}. */
	private	StackPane						timePane;

	/** The <i>start</i> button. */
	private	Button							startButton;

	/** The <i>stop</i> button. */
	private	Button							stopButton;

	/** The outer pane of this page. */
	private	HBox							pane;

	/** The scene that contains this page. */
	private	Scene							scene;

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
			if (code > 0)
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
	public List<Button> buttons()
	{
		return List.of(startButton, stopButton);
	}

	//------------------------------------------------------------------

	@Override
	public MapNode encodeState()
	{
		// Update state
		state.keyInfo = keyInfo;
		state.intervalValue = intervalValueSpinner.getValue();
		state.intervalUnit = intervalUnitList.item();

		// Encode state and return result
		return state.encode();
	}

	//------------------------------------------------------------------

	@Override
	public void decodeState(
		MapNode	rootNode)
	{
		state.decode(rootNode);
	}

	//------------------------------------------------------------------

	@Override
	public void onDeselecting()
	{
		stopButton.fire();
	}

	//------------------------------------------------------------------

	@Override
	public void onWindowShown()
	{
		if (scene == null)
		{
			// Get scene
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

			// Start/pause timer when window gains/loses focus or when 'iconified' state of window changes
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

					// Update time
					updateTime();
				};

				// Start/pause timer when window gains/loses focus
				window.focusedProperty().addListener(observable -> updateTimer.invoke());

				// Start/pause timer when 'iconified' state of window changes
				window.iconifiedProperty().addListener(observable -> updateTimer.invoke());
			}
		}

		// Request focus on interval-value spinner
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
		controlPane.setHgap(CONTROL_H_GAP);
		controlPane.setVgap(CONTROL_V_GAP);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);
		controlPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER)));
		controlPane.getStyleClass().add(StyleClass.CONTROL_PANE);
		HBox.setHgrow(controlPane, Priority.ALWAYS);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
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

		// Drop-down list: interval unit
		intervalUnitList = new SimpleDropDownList<>(TimeUnit.values());
		intervalUnitList.item(state.intervalUnit);

		// Pane: interval
		HBox intervalPane = new HBox(CONTROL_H_GAP, intervalValueSpinner, intervalUnitList);
		intervalPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(INTERVAL_STR), intervalPane);

		// Label: time
		timeLabel = new Label();
		timeLabel.setFont(FontUtils.boldFont(TIME_LABEL_FONT_SIZE_FACTOR));
		timeLabel.setMinWidth(Region.USE_PREF_SIZE);
		timeLabel.setPrefWidth(TextUtils.textWidthCeil(timeLabel.getFont(),
													   TIME_FORMATTER.format(LocalTime.of(0, 0, 0))));
		timeLabel.setTextFill(getColour(ColourKey.TIME_LABEL_TEXT));
		timeLabel.getStyleClass().add(StyleClass.TIME_LABEL);

		// Create procedure to update children of outer pane
		IProcedure0 updateOuterPane = () ->
		{
			pane.getChildren().setAll(switch (state.timePanePos)
			{
				case LEFT  -> List.of(timePane, controlPane);
				case RIGHT -> List.of(controlPane, timePane);
			});
		};

		// Pane: time
		timePane = new StackPane(timeLabel);
		timePane.setPadding(TIME_PANE_PADDING);
		timePane.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.TIME_PANE_BACKGROUND)));
		timePane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER)));
		timePane.getStyleClass().add(StyleClass.TIME_PANE);
		timePane.setOnContextMenuRequested(event ->
		{
			if (timer == null)
			{
				// Create pop-up for 'move pane' action
				String text = MOVE_PANE_STR + ((state.timePanePos == TimePanePos.LEFT) ? RIGHT_STR : LEFT_STR);
				String imageId = (state.timePanePos == TimePanePos.LEFT)
						? Images.ImageId.ARROW_RIGHT
						: Images.ImageId.ARROW_LEFT;
				ActionLabelPopUp.GraphicPos graphicPos = (state.timePanePos == TimePanePos.LEFT)
						? ActionLabelPopUp.GraphicPos.RIGHT
						: ActionLabelPopUp.GraphicPos.LEFT;
				ActionLabelPopUp popUp = new ActionLabelPopUp(text, Images.icon(imageId), graphicPos, () ->
				{
					state.timePanePos = switch (state.timePanePos)
					{
						case LEFT  -> TimePanePos.RIGHT;
						case RIGHT -> TimePanePos.LEFT;
					};
					updateOuterPane.invoke();
				});

				// Display context menu
				popUp.show(SceneUtils.getWindow(timePane), event.getScreenX(), event.getScreenY());
			}
		});

		// Create outer pane
		pane = new HBox(4.0);
		pane.setAlignment(Pos.CENTER);
		pane.getStyleClass().add(StyleClass.PERIODIC_PAGE);

		// Add children to outer pane
		updateOuterPane.invoke();

		// Create procedure to update UI components
		IProcedure0 updateComponents = () ->
		{
			updateTime();

			boolean timerActive = (timer != null);
			controlPane.setDisable(timerActive);
			startButton.setDisable(timerActive || (keyInfo == null));
			stopButton.setDisable(!timerActive);
		};

		// Button: start
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
				if (intervalUnitList.item() == TimeUnit.MINUTES)
					interval *= 60;
				timer = new Timeline(new KeyFrame(Duration.seconds((double)interval), event0 ->
				{
					// Generate key press and release
					try
					{
						robot.keyPress(keyInfo.code);
						robot.keyRelease(keyInfo.code);
					}
					catch (Exception e)
					{
						// Stop timer
						timer.stop();

						// Report error
						Platform.runLater(() ->
						{
							// Display error dialog
							ErrorDialog.show(SceneUtils.getWindow(startButton), ERROR_STR,
											 ErrorMsg.PROBLEM_WITH_KEY_EVENT_GENERATOR, e);

							// Fire 'stop' button
							stopButton.fire();
						});

						// Return from event handler
						return;
					}

					// Update time of last key press
					keyPressTime = LocalTime.now();

					// Update time
					updateTime();
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

		// Button: stop
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

		// Update UI components when content of key field changes
		keyField.textProperty().addListener(observable -> updateComponents.invoke());

		// Update key field
		updateKeyField.invoke();

		// Update UI components
		updateComponents.invoke();
	}

	//------------------------------------------------------------------

	private void updateTime()
	{
		// Update time label
		timeLabel.setText(((timer == null) || (keyPressTime == null))
												? " "
												: TIME_FORMATTER.format(keyPressTime));
		boolean timerRunning = (timer != null) && (timer.getStatus() == Animation.Status.RUNNING);
		String colourKey = timerRunning ? ColourKey.TIME_LABEL_TEXT_HIGHLIGHTED : ColourKey.TIME_LABEL_TEXT;
		timeLabel.setTextFill(getColour(colourKey));
		timeLabel.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, timerRunning);

		// Update time pane
		colourKey = timerRunning ? ColourKey.TIME_PANE_BACKGROUND_HIGHLIGHTED : ColourKey.TIME_PANE_BACKGROUND;
		timePane.setBackground(SceneUtils.createColouredBackground(getColour(colourKey)));
		timePane.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, timerRunning);
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


	// ENUMERATION: HORIZONTAL POSITION OF TIME PANE


	private enum TimePanePos
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		LEFT,
		RIGHT;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TimePanePos()
		{
			// Initialise instance variables
			key = name().toLowerCase();
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

		private static final	TimePanePos	DEFAULT_TIME_PANE_POS	= TimePanePos.RIGHT;

		/** Keys of properties. */
		private interface PropertyKey
		{
			String	INTERVAL			= "interval";
			String	KEY					= "key";
			String	TIME_PANE_POSITION	= "timePanePosition";
			String	UNIT				= "unit";
			String	VALUE				= "value";
		}

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private KeyInfo		keyInfo;
		private int			intervalValue;
		private TimeUnit	intervalUnit;
		private	TimePanePos	timePanePos;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private State()
		{
			// Initialise instance variables
			intervalValue = DEFAULT_INTERVAL_VALUE;
			intervalUnit = DEFAULT_INTERVAL_UNIT;
			timePanePos = DEFAULT_TIME_PANE_POS;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private MapNode encode()
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

			// Encode position of time pane
			rootNode.addString(PropertyKey.TIME_PANE_POSITION, timePanePos.key);

			// Return root node
			return rootNode;
		}

		//--------------------------------------------------------------

		private void decode(
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

			// Decode position of time pane
			timePanePos = rootNode.getEnumValue(TimePanePos.class, PropertyKey.TIME_PANE_POSITION, pos -> pos.key,
												DEFAULT_TIME_PANE_POS);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
