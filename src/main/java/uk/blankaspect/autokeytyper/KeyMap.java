/*====================================================================*\

KeyMap.java

Class: map from characters to key codes.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Robot;

import java.awt.event.KeyEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Stream;

import uk.blankaspect.common.exception2.BaseException;

//----------------------------------------------------------------------


// CLASS: MAP FROM CHARACTERS TO KEY CODES


public class KeyMap
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int		MIN_NUM_FIELDS	= 2;
	private static final	int		MAX_NUM_FIELDS	= 3;

	private static final	String	COMMENT_PREFIX	= "#";

	private static final	String	CHARACTER_PREFIX	= ".";
	private static final	String	UNICODE_PREFIX		= "U+";

	private static final	int		MIN_UNICODE_SEQUENCE_LENGTH	= 4;

	private static final	int		MAX_CODE_POINT	= 0xFFFF;

	/** The prefix of the name of an AWT key-code field. */
	private static final	String	KEY_CODE_PREFIX	= "VK_";

	/** Error messages. */
	private interface ErrorMsg
	{
		String	MALFORMED_ENTRY				= "The map entry is malformed.";
		String	MALFORMED_CHARACTER			= "The character '%s' is malformed.";
		String	ILLEGAL_CHARACTER			= "'%s' is not a legal character.";
		String	MALFORMED_CODE_POINT		= "The code point '%s' is malformed.";
		String	ILLEGAL_CODE_POINT			= "'%s' is not a legal code point.";
		String	CODE_POINT_OUT_OF_BOUNDS	= "The code point %s is out of bounds.";
		String	ILLEGAL_KEY_CODE			= "'%s' is not a legal key code.";
		String	UNRECOGNISED_KEY_CODE		= "'%s' is not a recognised key code.";
		String	UNRECOGNISED_MODIFIER_KEY	= "'%s' is not a recognised modifier key.";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private KeyMap()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Map<Character, Key> parse(
		List<String>	lines)
		throws ParseException
	{
		// Initialise key map
		Map<Character, Key> keyMap = new LinkedHashMap<>();

		// Parse map entries
		for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++)
		{
			// Get next line and split it into fields
			String[] fields = lines.get(lineIndex).split("\\t+", -1);

			// If last field is empty or starts with comment prefix, ignore it
			int numFields = fields.length;
			String lastField = fields[numFields - 1];
			if (lastField.isEmpty() || lastField.startsWith(COMMENT_PREFIX))
				--numFields;

			// Ignore line with no significant fields
			if (numFields == 0)
				continue;

			// Test for required number of significant fields
			if ((numFields < MIN_NUM_FIELDS) || (numFields > MAX_NUM_FIELDS))
				throw new ParseException(ErrorMsg.MALFORMED_ENTRY, lineIndex);

			// Parse field: character
			char ch = '\0';
			int fieldIndex = 0;
			String charField = fields[fieldIndex++];
			if (charField.startsWith(CHARACTER_PREFIX))
			{
				String charStr = charField.substring(CHARACTER_PREFIX.length());
				if (charStr.length() != 1)
					throw new ParseException(ErrorMsg.MALFORMED_CHARACTER, lineIndex, charField);
				ch = charStr.charAt(0);
			}
			else if (charField.startsWith(UNICODE_PREFIX))
			{
				String unicodeSeq = charField.substring(UNICODE_PREFIX.length());
				int seqLength = unicodeSeq.length();
				if (seqLength < MIN_UNICODE_SEQUENCE_LENGTH)
					throw new ParseException(ErrorMsg.MALFORMED_CODE_POINT, lineIndex, charField);

				try
				{
					int codePoint = Integer.parseUnsignedInt(unicodeSeq, 16);
					if (codePoint > MAX_CODE_POINT)
					{
						throw new ParseException(ErrorMsg.CODE_POINT_OUT_OF_BOUNDS, lineIndex,
												 UNICODE_PREFIX + Integer.toHexString(codePoint).toUpperCase());
					}
					if (seqLength > MIN_UNICODE_SEQUENCE_LENGTH)
						throw new ParseException(ErrorMsg.MALFORMED_CODE_POINT, lineIndex, charField);
					ch = (char)codePoint;
				}
				catch (NumberFormatException e)
				{
					throw new ParseException(ErrorMsg.ILLEGAL_CODE_POINT, lineIndex, charField);
				}
			}
			else
				throw new ParseException(ErrorMsg.ILLEGAL_CHARACTER, lineIndex, charField);

			// Parse field: key code
			int keyCode = KeyEvent.VK_UNDEFINED;
			String keyCodeStr = fields[fieldIndex++];
			if (keyCodeStr.startsWith(KEY_CODE_PREFIX))
			{
				boolean found = false;
				for (Field field : KeyEvent.class.getDeclaredFields())
				{
					int modifiers = field.getModifiers();
					if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && (field.getType() == Integer.TYPE)
							&& field.getName().equals(keyCodeStr))
					{
						try
						{
							keyCode = field.getInt(null);
							found = true;
						}
						catch (Throwable e)
						{
							// ignore
						}
						break;
					}
				}
				if (!found)
					throw new ParseException(ErrorMsg.UNRECOGNISED_KEY_CODE, lineIndex, keyCodeStr);
			}
			else
			{
				try
				{
					keyCode = Integer.parseUnsignedInt(keyCodeStr);
				}
				catch (NumberFormatException e)
				{
					throw new ParseException(ErrorMsg.ILLEGAL_KEY_CODE, lineIndex, keyCodeStr);
				}
			}

			// Parse field: modifier keys
			EnumSet<ModifierKey> modifierKeys = EnumSet.noneOf(ModifierKey.class);
			if (numFields > MIN_NUM_FIELDS)
			{
				for (String key : fields[fieldIndex++].trim().split(" +"))
				{
					ModifierKey modifierKey = ModifierKey.forKey(key);
					if (modifierKey == null)
						throw new ParseException(ErrorMsg.UNRECOGNISED_MODIFIER_KEY, lineIndex, key);
					modifierKeys.add(modifierKey);
				}
			}

			// Add entry to map
			keyMap.put(ch, new Key(keyCode, modifierKeys));
		}

		// Return key map
		return keyMap;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: MODIFIER KEY


	private enum ModifierKey
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		CONTROL
		(
			KeyEvent.VK_CONTROL
		),

		SHIFT
		(
			KeyEvent.VK_SHIFT
		),

		ALT
		(
			KeyEvent.VK_ALT
		),

		META
		(
			KeyEvent.VK_META
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int		keyCode;
		private	String	key;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an enumeration constant for a modifier key.
		 */

		private ModifierKey(
			int	keyCode)
		{
			this.keyCode = keyCode;
			key = name().toLowerCase();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		private static ModifierKey forKey(
			String	key)
		{
			return Stream.of(values())
							.filter(value -> value.key.equals(key))
							.findFirst()
							.orElse(null);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: KEY


	/**
	 * This record encapsulates information about a key in a key map.
	 */

	public record Key(
		int						keyCode,
		EnumSet<ModifierKey>	modifierKeys)
	{

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Generates native key events for this key by using the specified robot.
		 *
		 * @param robot
		 *          the object that will be used to generate native key events for this key.
		 */

		public void type(
			Robot	robot)
		{
			// Generate key-press events for modifier keys
			Deque<Integer> keyCodes = new ArrayDeque<>();
			for (ModifierKey modifierKey : modifierKeys)
			{
				robot.keyPress(modifierKey.keyCode);
				keyCodes.push(modifierKey.keyCode);
			}

			// Generate key-press and key-release events for principal key
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);

			// Generate key-release events for modifier keys
			while (!keyCodes.isEmpty())
				robot.keyRelease(keyCodes.pop());
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: PARSE EXCEPTION


	/**
	 * This class implements an exception that is thrown if an error occurs when parsing a key-map file.
	 */

	public static class ParseException
		extends BaseException
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** Miscellaneous strings. */
		private static final	String	LINE_STR	= "Line";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an exception with the specified message and line index.
		 *
		 * @param message
		 *          the message of the exception.
		 * @param lineIndex
		 *          the zero-based index of the line at which the exception occurred.
		 * @param replacements
		 *          the objects whose string representations will replace placeholders in {@code message}.
		 */

		private ParseException(
			String		message,
			int			lineIndex,
			Object...	replacements)
		{
			// Call superclass constructor
			super(LINE_STR + " " + (lineIndex + 1) + ": " + message, replacements);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
