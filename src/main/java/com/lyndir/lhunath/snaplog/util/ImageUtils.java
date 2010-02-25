/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.util;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleOp;


/**
 * <h2>{@link ImageUtils}<br>
 * <sub>Image manipulation utilities.</sub></h2>
 * 
 * <p>
 * <i>Jan 6, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class ImageUtils {

    /**
     * Resize the given image to the new dimensions <b>while maintaining the aspect ratio</b>. The old image will be
     * sized to fit within the new dimensions.
     * 
     * @param oldImage
     *            The image to resize.
     * @param newMaxWidth
     *            The maximum width of the new image.
     * @param newMaxHeight
     *            The maximum height of the new image.
     * 
     * @return The resized image.
     */
    public static BufferedImage rescale(BufferedImage oldImage, int newMaxWidth, int newMaxHeight) {

        return resize( oldImage, DimensionConstrain.createMaxDimension( newMaxWidth, newMaxHeight ) );
    }

    /**
     * Resize the given image to the new dimensions <b>while maintaining the aspect ratio</b>.
     * 
     * @param oldImage
     *            The image to resize.
     * @param ratio
     *            The amount to rescale the image by. The new image will have dimensions [ratio]:1.
     * 
     * @return The resized image.
     */
    public static BufferedImage rescale(BufferedImage oldImage, float ratio) {

        return resize( oldImage, DimensionConstrain.createRelativeDimension( ratio ) );
    }

    /**
     * Resize the given image to the new dimensions <b>without maintaining the aspect ratio</b>.
     * 
     * @param oldImage
     *            The image to resize.
     * @param newDimension
     *            The definition of the new image's dimensions.
     * 
     * @return The resized image.
     */
    public static BufferedImage resize(BufferedImage oldImage, DimensionConstrain newDimension) {

        // MultiStepRescaleOp resizeOperation = new MultiStepRescaleOp( newDimension,
        // RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        ResampleOp resizeOperation = new ResampleOp( newDimension );
        // resizeOperation.setUnsharpenMask( UnsharpenMask.Soft );

        return resizeOperation.filter( oldImage, null );
    }

    /**
     * Write out (encode) the given image to the given output stream.
     * 
     * @param image
     *            The image that should be written.
     * @param stream
     *            The stream where the encoded image data should be written to.
     * @param outputTypeMIME
     *            The MIME type of the image encoding to use for encoding the image on the output stream.
     * @param compressionQuality
     *            If non-<code>null</code>, explicitly set the compression quality to use for encoding. Value must be
     *            between (inclusive) 0-1.
     * @param progressive
     *            If non-<code>null</code>, explicitly set whether or not to use progressive encoding. This results in
     *            the image loading through the use of scan lines which makes the image appear in lower quality and
     *            improve in quality as it continues loading.
     * 
     * @throws IOException
     *             If the image could not be encoded or written to the output stream.
     * @throws UnsupportedOperationException
     *             If compression or progressiveness was requested but not supported by the output type.
     */
    public static void write(RenderedImage image, OutputStream stream, String outputTypeMIME, Float compressionQuality,
                             Boolean progressive)
            throws IOException {

        ImageWriter writer = ImageIO.getImageWritersByMIMEType( outputTypeMIME ).next();
        ImageWriteParam params = writer.getDefaultWriteParam();

        // Output image parameters.
        if (compressionQuality != null) {
            params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
            params.setCompressionQuality( compressionQuality );
        }
        if (progressive != null)
            params.setProgressiveMode( progressive? ImageWriteParam.MODE_DEFAULT: ImageWriteParam.MODE_DISABLED );

        writer.setOutput( ImageIO.createImageOutputStream( stream ) );
        writer.write( null, new IIOImage( image, null, null ), params );
        writer.dispose();
    }
}
