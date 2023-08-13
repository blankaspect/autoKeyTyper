/*====================================================================*\

AutoKeyTyperApp.java

Class: automatic key-typer application.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.AWTException;
import java.awt.Robot;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

import javafx.stage.Stage;

import javafx.util.Duration;

import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.build.BuildUtils;

import uk.blankaspect.common.cls.ClassUtils;

import uk.blankaspect.common.config.AppAuxDirectory;
import uk.blankaspect.common.config.AppConfig;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.logging.ErrorLogger;

import uk.blankaspect.common.resource.ResourceProperties;
import uk.blankaspect.common.resource.ResourceUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.GraphicButton;

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

import uk.blankaspect.ui.jfx.filler.FillerUtils;

import uk.blankaspect.ui.jfx.font.Fonts;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.IntRangeSpinner;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.TextUtils;

import uk.blankaspect.ui.jfx.window.WindowState;
import uk.blankaspect.ui.jfx.window.WindowUtils;

//----------------------------------------------------------------------


// CLASS: AUTOMATIC KEY-TYPER APPLICATION


/**
 * This class implements a JavaFX application that can generate a sequence of native key events for some chosen input
 * text.  The key events are added to the platform's input queue after a specified delay, which is equivalent to typing
 * the input text on a keyboard.
 * <p>
 * This application may be used to facilitate the entry of text into an input field (for example, a password field on a
 * web page) that does not allow text to be pasted from the system clipboard.
 * </p>
 */

public class AutoKeyTyperApp
	extends Application
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The short name of the application. */
	private static final	String	SHORT_NAME	= "AutoKeyTyper";

	/** The long name of the application. */
	private static final	String	LONG_NAME	= "Automatic key typer";

	/** The key with which the application is associated. */
	private static final	String	NAME_KEY	= StringUtils.firstCharToLowerCase(SHORT_NAME);

	/** The name of the file that contains the build properties of the application. */
	private static final	String	BUILD_PROPERTIES_FILENAME	= "build.properties";

	/** The filename of the CSS style sheet. */
	private static final	String	STYLE_SHEET_FILENAME	= NAME_KEY + "-%02d.css";

	/** The margins that are applied to the visual bounds of each screen when determining whether the saved location of
		the main window is within a screen. */
	private static final	Insets	SCREEN_MARGINS	= new Insets(0.0, 32.0, 32.0, 0.0);

	/** The minimum delay (in seconds) when generating key events. */
	private static final	int		MIN_DELAY		= 1;

	/** The maximum delay (in seconds) when generating key events. */
	private static final	int		MAX_DELAY		= 15;

	/** The default delay (in seconds) when generating key events. */
	private static final	int		DEFAULT_DELAY	= 3;

	/** The preferred number of columns of the input field. */
	private static final	int		INPUT_FIELD_NUM_COLUMNS	= 40;

	/** The factor by which the height of the default font is multiplied to give the dimensions of a <i>clear input</i>
		icon. */
	private static final	double	CLEAR_INPUT_ICON_SIZE_FACTOR	= 0.8;

	/** The factor by which the radius of the disc of the <i>clear input</i> icon is multiplied to give a coordinate of
		the cross of the icon. */
	private static final	double	CLEAR_INPUT_ICON_CROSS_SIZE_FACTOR	= 0.4;

	/** The factor by which the radius of the disc of the <i>clear input</i> icon is multiplied to give the width of the
		strokes of the cross. */
	private static final	double	CLEAR_INPUT_ICON_CROSS_STROKE_WIDTH_FACTOR	= 0.3;

	/** The horizontal gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The vertical gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_V_GAP	= 6.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(8.0, 10.0, 8.0, 12.0);

	/** The border colour of the control pane. */
	private static final	Color	CONTROL_PANE_BORDER_COLOUR	= Color.grayRgb(200);

	/** The horizontal gap between adjacent buttons of the button pane. */
	private static final	double	BUTTON_PANE_H_GAP	= 12.0;

	/** The vertical gap between adjacent buttons of the button pane. */
	private static final	double	BUTTON_PANE_V_GAP	= 6.0;

	/** The padding around the button pane. */
	private static final	Insets	BUTTON_PANE_PADDING	= new Insets(4.0, 12.0, 4.0, 12.0);

	/** The name of the default key-map file. */
	private static final	String	KEY_MAP_FILENAME	= "keyMap.txt";

	/** Miscellaneous strings. */
	private static final	String	CONFIG_ERROR_STR	= "Configuration error";
	private static final	String	INPUT_STR			= "Input";
	private static final	String	CLEAR_INPUT_STR		= "Clear input";
	private static final	String	DELAY_STR			= "Delay";
	private static final	String	SECONDS_STR			= "seconds";
	private static final	String	PREFERENCES_STR		= "Preferences";
	private static final	String	ABORT_STR			= "Abort";
	private static final	String	GENERATE_STR		= "Generate";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.CLEAR_INPUT_BUTTON_DISC,
			CssSelector.builder()
						.cls(StyleClass.AUTO_KEY_TYPER)
						.desc(StyleClass.CLEAR_INPUT_BUTTON_DISC)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.CLEAR_INPUT_BUTTON_CROSS,
			CssSelector.builder()
						.cls(StyleClass.AUTO_KEY_TYPER)
						.desc(StyleClass.CLEAR_INPUT_BUTTON_CROSS)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.id(NodeId.INPUT_FIELD)
									.desc(FxStyleClass.TEXT)
									.build())
						.grayFontSmoothing()
						.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	AUTO_KEY_TYPER				= StyleConstants.CLASS_PREFIX + "auto-key-typer";
		String	CLEAR_INPUT_BUTTON_CROSS	= StyleConstants.CLASS_PREFIX + "clear-input-button-cross";
		String	CLEAR_INPUT_BUTTON_DISC		= StyleConstants.CLASS_PREFIX + "clear-input-button-disc";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	CLEAR_INPUT_BUTTON_CROSS	= "clearInputButton.cross";
		String	CLEAR_INPUT_BUTTON_DISC		= "clearInputButton.disc";
	}

	/** Identifiers of nodes. */
	private interface NodeId
	{
		String	INPUT_FIELD	= "inputField";
	}

	/** Keys of properties. */
	private interface PropertyKey
	{
		String	APPEARANCE	= "appearance";
		String	DELAY		= "delay";
		String	MAIN_WINDOW	= "mainWindow";
		String	THEME		= "theme";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	KEY_MAP					= "keyMap";
		String	USE_STYLE_SHEET_FILE	= "useStyleSheetFile";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	NO_AUXILIARY_DIRECTORY		= "The location of the auxiliary directory could not be determined.";
		String	FAILED_TO_READ_KEY_MAP		= "Failed to read the key map.";
		String	FAILED_TO_CREATE_ROBOT		= "Failed to create a robot.";
		String	CHARACTERS_NOT_IN_KEY_MAP	= "The following characters cannot be mapped to key codes:";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Map<Character, KeyMap.Key>	keyMap	= Collections.emptyMap();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The properties of the build of this application. */
	private	ResourceProperties	buildProperties;

	/** The string representation of the version of this application. */
	private	String				versionStr;

	/** The configuration of this application. */
	private	Configuration		configuration;

	/** User preferences. */
	private	Preferences			preferences;

	/** The state of the main window. */
	private	WindowState			mainWindowState;

	/** The delay (in seconds) when generating key events. */
	private	int					delay;

	/** The object that adds native key events to the platform's input queue. */
	private	Robot				robot;

	/** The main window. */
	private	Stage				primaryStage;

	/** The <i>delay</i> spinner. */
	private	IntRangeSpinner		delaySpinner;

	/** The <i>abort</i> button. */
	private	Button				abortButton;

	/** The <i>generate</i> button. */
	private	Button				generateButton;

	/** The timer that provides a delay of the chosen length when generating key events. */
	private	Timeline			delayTimer;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an automatic key-typer application.
	 */

	public AutoKeyTyperApp()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * This is the main method of the application.
	 *
	 * @param args
	 *          the command-line arguments of the application.
	 */

	public static void main(
		String[]	args)
	{
		launch(args);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns an icon that represents a <i>clear input</i> action.
	 *
	 * @return an icon that represents a <i>clear input</i> action.
	 */

	private static Group createClearInputIcon()
	{
		// Get size of icon from height of default font
		double size = Math.rint(CLEAR_INPUT_ICON_SIZE_FACTOR * TextUtils.textHeight());

		// Create disc
		double radius = 0.5 * size;
		Circle disc = new Circle(radius, getColour(ColourKey.CLEAR_INPUT_BUTTON_DISC));
		disc.getStyleClass().add(StyleClass.CLEAR_INPUT_BUTTON_DISC);

		// Create cross
		double coord = CLEAR_INPUT_ICON_CROSS_SIZE_FACTOR * radius;
		javafx.scene.shape.Path cross = new javafx.scene.shape.Path
		(
			new MoveTo(-coord, -coord),
			new LineTo(coord, coord),
			new MoveTo(-coord, coord),
			new LineTo(coord, -coord)
		);
		cross.setStroke(getColour(ColourKey.CLEAR_INPUT_BUTTON_CROSS));
		cross.setStrokeWidth(radius * CLEAR_INPUT_ICON_CROSS_STROKE_WIDTH_FACTOR);
		cross.setStrokeLineCap(StrokeLineCap.ROUND);
		cross.getStyleClass().add(StyleClass.CLEAR_INPUT_BUTTON_CROSS);

		// Create background rectangle
		Rectangle background = new Rectangle(-radius, -radius, size, size);
		background.setFill(Color.TRANSPARENT);

		// Create and return icon
		return new Group(background, disc, cross);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the selected theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the selected theme of the style
	 *         manager, or {@link StyleManager#DEFAULT_COLOUR} if there is no such colour.
	 */

	private static Color getColour(
		String	key)
	{
		Color colour = StyleManager.INSTANCE.getColour(key);
		return (colour == null) ? StyleManager.DEFAULT_COLOUR : colour;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void start(
		Stage	primaryStage)
	{
		// Log stack trace of uncaught exception
		if (ClassUtils.isFromJar(getClass()))
		{
			Thread.setDefaultUncaughtExceptionHandler((thread, exception) ->
			{
				try
				{
					ErrorLogger.INSTANCE.write(exception);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
		}

		// Initialise instance variables
		preferences = new Preferences();
		mainWindowState = new WindowState(false, true);
		delay = DEFAULT_DELAY;
		this.primaryStage = primaryStage;

		// Read build properties
		try
		{
			buildProperties = new ResourceProperties(ResourceUtils.normalisedPathname(getClass(), BUILD_PROPERTIES_FILENAME));
			versionStr = BuildUtils.versionString(getClass(), buildProperties);
		}
		catch (LocationException e)
		{
			e.printStackTrace();
		}

		// Read configuration file and decode configuration
		BaseException configException = null;
		try
		{
			// Initialise configuration
			configuration = new Configuration();

			// Read configuration file
			configuration.read();

			// Decode configuration
			decodeConfig(configuration.getConfig());
		}
		catch (BaseException e)
		{
			configException = e;
		}

		// Get style manager
		StyleManager styleManager = StyleManager.INSTANCE;

		// Select theme
		String themeId = System.getProperty(StyleManager.SystemPropertyKey.THEME);
		if (StringUtils.isNullOrEmpty(themeId))
			themeId = preferences.getThemeId();
		if (themeId != null)
			styleManager.selectTheme(themeId);

		// Set ID and style-sheet filename on style manager
		if (Boolean.getBoolean(SystemPropertyKey.USE_STYLE_SHEET_FILE))
		{
			styleManager.setId(getClass().getSimpleName());
			styleManager.setStyleSheetFilename(STYLE_SHEET_FILENAME);
		}

		// Register the style properties of this class with the style manager
		styleManager.register(getClass(), COLOUR_PROPERTIES, RULE_SETS);

		// Create robot to generate key events
		try
		{
			robot = new Robot();
		}
		catch (AWTException e)
		{
			ErrorDialog.show(primaryStage, SHORT_NAME, new BaseException(ErrorMsg.FAILED_TO_CREATE_ROBOT, e));
			System.exit(1);
		}

		// Read and parse key-map file
		Path keyMapFile = null;
		try
		{
			// Get location of key-map file from system property
			String pathname = System.getProperty(SystemPropertyKey.KEY_MAP);
			keyMapFile = (pathname == null) ? null : Path.of(PathnameUtils.parsePathname(pathname));

			// Read lines of key-map file
			List<String> keyMapLines = null;
			try
			{
				keyMapLines = (keyMapFile == null) ? ResourceUtils.readLines(getClass(), KEY_MAP_FILENAME)
												   : Files.readAllLines(keyMapFile);
			}
			catch (IOException e)
			{
				throw new BaseException(ErrorMsg.FAILED_TO_READ_KEY_MAP, e);
			}

			// Parse key map
			keyMap = KeyMap.parse(keyMapLines);
		}
		catch (BaseException e)
		{
			e = (keyMapFile == null) ? new LocationException(e, KEY_MAP_FILENAME) : new FileException(e, keyMapFile);
			ErrorDialog.show(primaryStage, SHORT_NAME, e);
			System.exit(1);
		}

		// Create control pane
		GridPane controlPane = new GridPane();
		controlPane.setHgap(CONTROL_PANE_H_GAP);
		controlPane.setVgap(CONTROL_PANE_V_GAP);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);
		controlPane.setBorder(SceneUtils.createSolidBorder(CONTROL_PANE_BORDER_COLOUR, Side.BOTTOM));
		VBox.setVgrow(controlPane, Priority.ALWAYS);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(GridPane.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		controlPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		controlPane.getColumnConstraints().add(column);

		// Initialise row index
		int row = 0;

		// Create text field: input
		TextField inputField = new TextField();
		inputField.setId(NodeId.INPUT_FIELD);
		inputField.setFont(Fonts.monoFont());
		inputField.setPrefColumnCount(INPUT_FIELD_NUM_COLUMNS);
		inputField.setOnAction(event -> generateButton.fire());
		HBox.setHgrow(inputField, Priority.ALWAYS);

		// Create button: clear input
		GraphicButton clearInputButton = new GraphicButton(createClearInputIcon(), CLEAR_INPUT_STR);
		clearInputButton.setOnAction(event ->
		{
			Platform.runLater(() ->
			{
				inputField.clear();
				inputField.requestFocus();
			});
		});

		// Create pane: input
		HBox inputPane = new HBox(2.0, inputField, clearInputButton);
		inputPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(INPUT_STR), inputPane);
		GridPane.setHgrow(inputPane, Priority.ALWAYS);

		// Create spinner: delay
		delaySpinner = IntRangeSpinner.leftRightH(HPos.CENTER, false, MIN_DELAY, MAX_DELAY, delay, "000", null);

		// Create pane: delay
		HBox delayPane = new HBox(5.0, delaySpinner, new Label(SECONDS_STR));
		delayPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(DELAY_STR), delayPane);

		// Create button: preferences
		Button preferencesButton = new Button(PREFERENCES_STR);
		preferencesButton.setOnAction(event -> onEditPreferences());

		// Create procedure to update buttons
		IProcedure0 updateButtons = () ->
		{
			boolean noInput = inputField.getText().isEmpty();
			clearInputButton.setDisable((delayTimer != null) || noInput);
			preferencesButton.setDisable(delayTimer != null);
			abortButton.setDisable(delayTimer == null);
			generateButton.setDisable((delayTimer != null) || noInput);
		};

		// Create button: abort
		abortButton = new Button(ABORT_STR);
		abortButton.setMaxWidth(Double.MAX_VALUE);
		abortButton.setOnAction(event ->
		{
			// Stop delay timer and invalidate it
			if (delayTimer != null)
			{
				// Stop delay timer
				delayTimer.stop();

				// Invalidate delay timer
				delayTimer = null;
			}

			// Update buttons
			updateButtons.invoke();
		});

		// Create button: generate
		generateButton = new Button(GENERATE_STR);
		generateButton.setMaxWidth(Double.MAX_VALUE);
		generateButton.setOnAction(event ->
		{
			// Get text from input field
			String text = inputField.getText();

			// If there is text, generate key presses and releases for its characters after a delay
			if (!text.isEmpty())
			{
				// Create list of characters of input text that do not appear in key map
				List<Character> unmappedChars = new ArrayList<>();
				for (int i = 0; i < text.length(); i++)
				{
					char ch = text.charAt(i);
					if (!keyMap.containsKey(ch))
						unmappedChars.add(ch);
				}

				// Report characters not in key map
				if (!unmappedChars.isEmpty())
				{
					StringBuilder buffer = new StringBuilder(256);
					buffer.append(ErrorMsg.CHARACTERS_NOT_IN_KEY_MAP);
					for (int i = 0; i < unmappedChars.size(); i++)
					{
						if (i % 10 == 0)
							buffer.append('\n');
						else
							buffer.append("  ");
						buffer.append(unmappedChars.get(i));
					}
					ErrorDialog.show(primaryStage, GENERATE_STR, buffer.toString());
					return;
				}

				// Generate key presses and releases after a delay
				delayTimer = new Timeline(new KeyFrame(Duration.seconds((double)delaySpinner.getValue()), event0 ->
				{
					// Invalidate delay timer
					delayTimer = null;

					// Generate key presses and releases
					for (int i = 0; i < text.length(); i++)
					{
						char ch = text.charAt(i);
						KeyMap.Key key = keyMap.get(ch);
						if (key != null)
							key.type(robot);
					}

					// Update buttons
					updateButtons.invoke();
				}));

				// Update buttons
				updateButtons.invoke();

				// Request focus on 'abort' button
				abortButton.requestFocus();

				// Start delay
				delayTimer.playFromStart();
			}
		});

		// Update buttons when content of input field changes
		inputField.textProperty().addListener(observable -> updateButtons.invoke());

		// Update buttons
		updateButtons.invoke();

		// Create right button pane
		TilePane rightButtonPane = new TilePane(BUTTON_PANE_H_GAP, BUTTON_PANE_V_GAP, abortButton, generateButton);
		rightButtonPane.setPrefColumns(rightButtonPane.getChildren().size());
		rightButtonPane.setAlignment(Pos.CENTER);

		// Create button pane
		HBox buttonPane = new HBox(preferencesButton, FillerUtils.hBoxFiller(16.0), rightButtonPane);
		buttonPane.setAlignment(Pos.CENTER);
		buttonPane.setPadding(BUTTON_PANE_PADDING);

		// Create main pane
		VBox mainPane = new VBox(controlPane, buttonPane);
		mainPane.setAlignment(Pos.CENTER);
		mainPane.getStyleClass().add(StyleClass.AUTO_KEY_TYPER);

		// Create scene
		Scene scene = new Scene(mainPane);

		// Add style sheet to scene
		styleManager.addStyleSheet(scene);

		// Set properties of main window
		primaryStage.setTitle(LONG_NAME + " " + versionStr);
		primaryStage.getIcons().addAll(Images.APP_ICON_IMAGES);

		// Set scene on main window
		primaryStage.setScene(scene);

		// Set location and width of main window when it is opening
		primaryStage.setOnShowing(event ->
		{
			// Set location of window
			Point2D location = mainWindowState.getLocation();
			if (location != null)
			{
				primaryStage.setX(location.getX());
				primaryStage.setY(location.getY());
			}

			// Set size/width of window
			Dimension2D size = mainWindowState.getSize();
			if (size == null)
				primaryStage.sizeToScene();
			else
				primaryStage.setWidth(size.getWidth());
		});

		// Set location of main window after it is shown
		primaryStage.setOnShown(event ->
		{
			// Get location of window
			Point2D location = mainWindowState.getLocation();

			// Invalidate location if top centre of window is not within a screen
			double width = primaryStage.getWidth();
			if ((location != null)
					&& !SceneUtils.isWithinScreen(location.getX() + 0.5 * width, location.getY(), SCREEN_MARGINS))
				location = null;

			// If there is no location, centre window within primary screen
			if (location == null)
			{
				location = SceneUtils.centreInScreen(width, primaryStage.getHeight());
				primaryStage.setX(location.getX());
				primaryStage.setY(location.getY());
			}

			// Prevent height of main window from changing
			WindowUtils.preventHeightChange(primaryStage);
		});

		// Write configuration file when main window is closed
		if (configuration != null)
		{
			primaryStage.setOnHiding(event ->
			{
				// Update state of main window
				mainWindowState.restoreAndUpdate(primaryStage);

				// Write configuration
				if (configuration.canWrite())
				{
					try
					{
						// Encode configuration
						encodeConfig(configuration.getConfig());

						// Write configuration file
						configuration.write();
					}
					catch (FileException e)
					{
						ErrorDialog.show(primaryStage, SHORT_NAME + " : " + CONFIG_ERROR_STR, e);
					}
				}
			});
		}

		// Display main window
		primaryStage.show();

		// Report any configuration error
		if (configException != null)
			ErrorDialog.show(primaryStage, SHORT_NAME + " : " + CONFIG_ERROR_STR, configException);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Encodes the configuration of this application to the specified root node.
	 *
	 * @param rootNode
	 *          the root node of the configuration.
	 */

	private void encodeConfig(
		MapNode	rootNode)
	{
		// Clear properties
		rootNode.clear();

		// Encode theme ID
		rootNode.addMap(PropertyKey.APPEARANCE).addString(PropertyKey.THEME, preferences.getThemeId());

		// Encode state of main window
		MapNode windowStateNode = mainWindowState.encodeTree();
		if (!windowStateNode.isEmpty())
			rootNode.add(PropertyKey.MAIN_WINDOW, windowStateNode);

		// Encode delay
		if (delaySpinner != null)
			delay = delaySpinner.getValue();
		rootNode.addInt(PropertyKey.DELAY, delay);
	}

	//------------------------------------------------------------------

	/**
	 * Decodes the configuration of this application from the specified root node.
	 *
	 * @param rootNode
	 *          the root node of the configuration.
	 */

	private void decodeConfig(
		MapNode	rootNode)
	{
		// Decode theme ID
		String key = PropertyKey.APPEARANCE;
		if (rootNode.hasMap(key))
			preferences.setThemeId(rootNode.getMapNode(key).getString(PropertyKey.THEME, StyleManager.DEFAULT_THEME_ID));

		// Decode state of main window
		key = PropertyKey.MAIN_WINDOW;
		if (rootNode.hasMap(key))
			mainWindowState.decodeTree(rootNode.getMapNode(key));

		// Decode delay
		delay = rootNode.getInt(PropertyKey.DELAY, DEFAULT_DELAY);
	}

	//------------------------------------------------------------------

	/**
	 * Displays a modal dialog in which the user preferences of the application may be edited.
	 */

	private void onEditPreferences()
	{
		Preferences result = PreferencesDialog.show(primaryStage, preferences);
		if (result != null)
		{
			// Update instance variable
			preferences = result;

			// Apply theme
			StyleManager styleManager = StyleManager.INSTANCE;
			String themeId = preferences.getThemeId();
			if ((themeId != null) && !themeId.equals(styleManager.getThemeId()))
			{
				// Update theme
				styleManager.selectTheme(themeId);

				// Reapply style sheet to the scenes of all JavaFX windows
				styleManager.reapplyStylesheet();
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: CONFIGURATION


	/**
	 * This class implements the configuration of the application.
	 */

	private static class Configuration
		extends AppConfig
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The identifier of a configuration file. */
		private static final	String	ID	= "AO0O5A1TSWTBGLQQ0AFJ46F5W";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of the configuration of the application.
		 *
		 * @throws BaseException
		 *           if the configuration directory could not be determined.
		 */

		private Configuration()
			throws BaseException
		{
			// Call superclass constructor
			super(ID, NAME_KEY, SHORT_NAME, LONG_NAME);

			// Get location of parent directory of config file
			AppAuxDirectory.Directory directory = AppAuxDirectory.getDirectory(NAME_KEY, AutoKeyTyperApp.class);
			if (directory == null)
				throw new BaseException(ErrorMsg.NO_AUXILIARY_DIRECTORY);

			// Set parent directory of config file
			setDirectory(directory.location());
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
