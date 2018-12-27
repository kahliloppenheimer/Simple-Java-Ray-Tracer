package me.kahlil.graphics;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Arrays;
import java.util.List;
import me.kahlil.geometry.Ray3D;
import me.kahlil.scene.Camera;
import me.kahlil.scene.SimpleFrame;

/**
 * A simple anti-aliasing implementation of ray tracing that uses a given {@link AntiAliasingMethod}
 * to generate a set of rays to sample, and then averages their results together to produce one
 * final pixel color.
 *
 * <p>No adaptive anti-aliasing or more complicated combination logic is performed.
 */
final class SimpleAntiAliaser extends RayTracer {

  private final RayTracer rayTracer;
  private final AntiAliasingMethod antiAliasingMethod;
  private final SamplingRadius samplingRadius;

  private final ThreadLocal<Long> numTraces = ThreadLocal.withInitial(() -> 0L);

  SimpleAntiAliaser(
      SimpleFrame frame,
      Camera camera,
      RayTracer rayTracer,
      AntiAliasingMethod antiAliasingMethod) {
    super(frame, camera);
    this.samplingRadius =
        ImmutableSamplingRadius.builder()
            .setWidth(frame.getPixelWidthInCoordinateSpace() * 0.5)
            .setHeight(frame.getPixelHeightInCoordinateSpace() * 0.5)
            .build();
    this.rayTracer = rayTracer;
    this.antiAliasingMethod = antiAliasingMethod;
  }

  @Override
  RenderingResult traceRay(Ray3D ray) {
    Ray3D[] raysToSample = antiAliasingMethod.getRaysToSample(ray, samplingRadius);
    RenderingResult[] renderingResults = new RenderingResult[raysToSample.length];

    // Trace all the sample rays and count the total number of rays traced.
    long numTraces = 0;
    for (int i = 0; i < raysToSample.length; i++) {
      renderingResults[i] = rayTracer.traceRay(raysToSample[i]);
      numTraces += renderingResults[i].getNumRaysTraced();
    }

    // Average each RGBA component of the color results using an unweighted average.
    // Use pass-by-reference array semantics to avoid excess array allocations.
    float[] currentColor = new float[4];
    float[] runningAverage = new float[4];
    for (RenderingResult renderingResult : renderingResults) {
      renderingResult.getColor().getRgbaAsFloats(currentColor);
      incrementAverages(runningAverage, currentColor, 1.0f / renderingResults.length);
    }

    return ImmutableRenderingResult.builder()
        .setColor(new Color(runningAverage))
        .setNumRaysTraced(raysToSample.length)
        .build();
  }

  private void incrementAverages(float[] runningAverage, float[] currentColor, float weight) {
    for (int i = 0; i < 4; i++) {
      runningAverage[i] += currentColor[i] * weight;
    }
  }

  @Override
  long getNumTraces() {
    return numTraces.get();
  }
}
