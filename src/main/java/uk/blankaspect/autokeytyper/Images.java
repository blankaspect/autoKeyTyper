/*====================================================================*\

Images.java

Class: images.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.scene.image.Image;

import uk.blankaspect.ui.jfx.image.ImageCache;
import uk.blankaspect.ui.jfx.image.ImageData;

import uk.blankaspect.ui.jfx.style.AbstractTheme;

//----------------------------------------------------------------------


// CLASS: IMAGES


/**
 * This class provides images for the icons that represent the application.
 */

public class Images
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Image identifiers. */
	public interface ImageId
	{
		String	PREFIX = MethodHandles.lookup().lookupClass().getEnclosingClass().getName() + ".";

		String	PENCIL	= PREFIX + "pencil";
	}

	/** A list of images for the icons that represent the application. */
	public static final		List<Image>	APP_ICON_IMAGES;

	/** The directory that contains image files. */
	private static final	String	IMAGE_DIRECTORY	= "images/";

	/** Filename stems of images that are used in this class. */
	private interface ImageFilename
	{
		String	APP_16	= "app-16x16";
		String	APP_32	= "app-32x32";
		String	APP_48	= "app-48x48";
		String	APP_256	= "app-256x256";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Add images that are created from image data
		ImageData.add(ImageId.PENCIL, AbstractTheme.MONO_IMAGE_KEY, ImgData.PENCIL);

		// Register directory containing images that represent the application
		ImageCache.addDirectory(Images.class, IMAGE_DIRECTORY);

		// Initialise images that represent the application
		APP_ICON_IMAGES = List.of
		(
			ImageCache.getImage(ImageFilename.APP_16),
			ImageCache.getImage(ImageFilename.APP_32),
			ImageCache.getImage(ImageFilename.APP_48),
			ImageCache.getImage(ImageFilename.APP_256)
		);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private	Images()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Ensures that the static initialiser of this class is called.
	 *
	 * @return {@code true}.
	 */

	public static boolean init()
	{
		return !APP_ICON_IMAGES.isEmpty();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Image data
////////////////////////////////////////////////////////////////////////

	/**
	 * PNG image data.
	 */

	private interface ImgData
	{
		// File: mono/pencil
		byte[]	PENCIL	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xA9, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x7D, (byte)0xCE, (byte)0x3F, (byte)0x0A, (byte)0xC2,
			(byte)0x30, (byte)0x14, (byte)0x80, (byte)0xF1, (byte)0x4F, (byte)0x70, (byte)0x14, (byte)0x07,
			(byte)0x71, (byte)0xA8, (byte)0xBB, (byte)0x6B, (byte)0x75, (byte)0x15, (byte)0xC1, (byte)0xA2,
			(byte)0xBD, (byte)0x96, (byte)0x8B, (byte)0x78, (byte)0x02, (byte)0x17, (byte)0xEB, (byte)0x05,
			(byte)0xC4, (byte)0xDD, (byte)0xC1, (byte)0x4E, (byte)0x1E, (byte)0xC0, (byte)0x53, (byte)0x08,
			(byte)0x3A, (byte)0xF9, (byte)0x17, (byte)0x37, (byte)0x07, (byte)0x07, (byte)0xF1, (byte)0x99,
			(byte)0x46, (byte)0x69, (byte)0x1F, (byte)0x49, (byte)0x09, (byte)0x69, (byte)0x42, (byte)0x7F,
			(byte)0x1F, (byte)0xBC, (byte)0xF0, (byte)0xC6, (byte)0xB5, (byte)0x68, (byte)0x52, (byte)0xB3,
			(byte)0x37, (byte)0x4D, (byte)0xE6, (byte)0xE7, (byte)0x90, (byte)0x3B, (byte)0x0F, (byte)0xD6,
			(byte)0x04, (byte)0xCE, (byte)0x40, (byte)0xF8, (byte)0x22, (byte)0xBB, (byte)0xCA, (byte)0x8C,
			(byte)0x2D, (byte)0x15, (byte)0x0F, (byte)0xCB, (byte)0xD9, (byte)0xA0, (byte)0xCF, (byte)0x89,
			(byte)0xB6, (byte)0x9F, (byte)0x7B, (byte)0x4C, (byte)0x65, (byte)0x50, (byte)0xAB, (byte)0xC8,
			(byte)0x03, (byte)0xCE, (byte)0xC4, (byte)0x96, (byte)0x27, (byte)0x5C, (byte)0xCD, (byte)0xBD,
			(byte)0x9C, (byte)0x73, (byte)0x81, (byte)0x9B, (byte)0xFF, (byte)0x81, (byte)0x8F, (byte)0x6D,
			(byte)0x40, (byte)0xC4, (byte)0x93, (byte)0x94, (byte)0xBA, (byte)0x7D, (byte)0xDA, (byte)0xED,
			(byte)0xFB, (byte)0xCC, (byte)0x62, (byte)0x90, (byte)0xB0, (byte)0x62, (byte)0xCE, (byte)0x9E,
			(byte)0x58, (byte)0xF3, (byte)0x2F, (byte)0x38, (byte)0xD2, (byte)0x91, (byte)0xEF, (byte)0x82,
			(byte)0x9D, (byte)0x66, (byte)0x13, (byte)0x10, (byte)0xCA, (byte)0x80, (byte)0x0D, (byte)0x5D,
			(byte)0x96, (byte)0x72, (byte)0x2A, (byte)0xCE, (byte)0x82, (byte)0x31, (byte)0x2F, (byte)0xA1,
			(byte)0x83, (byte)0x0C, (byte)0x19, (byte)0x69, (byte)0xCE, (byte)0x82, (byte)0x44, (byte)0x92,
			(byte)0x50, (byte)0x43, (byte)0x2E, (byte)0x28, (byte)0x5F, (byte)0x1F, (byte)0x8B, (byte)0x52,
			(byte)0xC9, (byte)0x6E, (byte)0x7C, (byte)0x8A, (byte)0xFF, (byte)0x0E, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42,
			(byte)0x60, (byte)0x82
		};
	}

	//==================================================================

}

//----------------------------------------------------------------------
