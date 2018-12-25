package me.kahlil.graphics;

import me.kahlil.geometry.Ray3D;
import me.kahlil.scene.Camera3D;
import me.kahlil.scene.SimpleFrame;

/**
 * An object which traces a single ray and returns the corresponding color that should be rendered,
 * as defined by the ray tracing algorithm.
 */
abstract class RayTracer {

  private final Camera3D camera;
  private final SimpleFrame frame;

  RayTracer(
      Camera3D camera,
      SimpleFrame frame
  ) {
    this.camera = camera;
    this.frame = frame;
  }

  /**
   * Traces the given ray, returning the corresponding color. Note, this is called with a ray that
   * points to the middle of a given pixel during the main ray tracing algorithm.
   */
  abstract Color traceRay(Ray3D ray);

  /**
   * Traces a ray through ith and jth pixel, returning a color for that pixel.
   */
  final Color traceRay(int i, int j) {
    return traceRay(new Ray3D(camera.getLocation(),
        frame.getBottomLeftCorner()
            .translate(
                // Translate into coordinate space and center
                i * frame.getPixelWidthInCoordinateSpace()
                    + 0.5 * frame.getPixelWidthInCoordinateSpace(),
                j * frame.getPixelHeightInCoordinateSpace()
                    + 0.5 * frame.getPixelHeightInCoordinateSpace())));
  }

}
